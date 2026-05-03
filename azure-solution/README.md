# Azure Solution – Publisher / Subscriber / ETL

## Architecture Overview

```
                        ┌─────────────────────────────────────────────────────┐
                        │              Azure Resource Group                   │
                        │                                                     │
  HTTP POST             │  ┌──────────────────┐    Azure Storage Queue       │
  /api/publish ────────►│  │  Publisher        │──────────────────────────►  │
                        │  │  Function App     │       "data-queue"           │
                        │  │  (C# / .NET 8)   │                              │
                        │  └──────────────────┘         │                    │
                        │                               │ Queue Trigger       │
                        │  ┌──────────────────┐         ▼                    │
                        │  │  Subscriber       │◄─────────────────────────── │
                        │  │  Function App     │                              │
                        │  │  (C# / .NET 8)   │────────────────────────────► │
                        │  └──────────────────┘     Azure SQL DB              │
                        │                           [dbo].[DataRecords]       │
                        │                                                     │
                        │  ┌──────────────────────────────────────────────┐  │
                        │  │  Azure Data Factory                          │  │
                        │  │                                              │  │
                        │  │  Pipeline: ETL_TransformAndPublish           │  │
                        │  │                                              │  │
                        │  │  1. Lookup ──► [dbo].[RawData] (Pending)    │  │
                        │  │  2. ForEach record:                         │  │
                        │  │     a. Data Flow: Transform + Normalise     │  │
                        │  │     b. WebActivity ──► Publisher /api/publish│  │
                        │  │     c. SP: usp_MarkRawDataProcessed         │  │
                        │  └──────────────────────────────────────────────┘  │
                        │                                                     │
                        │  Supporting: App Insights · Key Vault · Log Analytics│
                        └─────────────────────────────────────────────────────┘
```

## Components

| Component | Type | Purpose |
|-----------|------|---------|
| `Publisher Function` | Azure Function (HTTP) | Accepts JSON via POST, validates, enqueues to Storage Queue |
| `Subscriber Function` | Azure Function (Queue Trigger) | Dequeues messages, upserts into `[dbo].[DataRecords]` |
| `ETL Pipeline` | Azure Data Factory | Reads `[dbo].[RawData]`, transforms via Data Flow, calls Publisher Function |
| `Azure SQL DB` | Azure SQL Serverless | Stores raw data, transformed data, and processed records |
| `Storage Queue` | Azure Storage | Durable message bus between Publisher and Subscriber |
| `Key Vault` | Azure Key Vault | Stores SQL password secret |
| `Application Insights` | Azure Monitor | Telemetry for both Function Apps |

---

## Project Structure

```
azure-solution/
├── publisher-function/
│   ├── PublisherFunction.cs     # HTTP trigger – validate & enqueue
│   ├── Program.cs               # DI / startup
│   ├── PublisherFunction.csproj
│   ├── host.json
│   └── local.settings.json      # local dev config (never commit secrets)
│
├── subscriber-function/
│   ├── SubscriberFunction.cs    # Queue trigger – dequeue & persist to SQL
│   ├── Program.cs
│   ├── SubscriberFunction.csproj
│   ├── host.json
│   └── local.settings.json
│
├── etl-dataflow/
│   ├── pipeline-definition.json # ADF pipeline + Data Flow definition (reference)
│   └── schema.sql               # SQL tables, indexes, stored procedures, seed data
│
├── bicep/
│   ├── main.bicep               # All Azure resources (IaC)
│   └── main.dev.bicepparam      # Dev environment parameter file
│
├── deploy.sh                    # One-shot CLI deployment script
└── README.md
```

---

## Prerequisites

- Azure CLI ≥ 2.55  (`az --version`)
- .NET SDK 8.0      (`dotnet --version`)
- Bicep CLI         (`az bicep install`)
- Azure Subscription with Contributor rights

---

## Quick Start

### 1. Clone & configure

```bash
git clone <your-repo>
cd azure-solution

# Edit bicep/main.dev.bicepparam and set sqlAdminPassword
```

### 2. Deploy infrastructure + code

```bash
chmod +x deploy.sh
./deploy.sh dev rg-azure-solution-dev <your-subscription-id>
```

### 3. Apply SQL schema

Connect to the Azure SQL DB (use Azure Data Studio or SSMS):

```bash
sqlcmd -S <sql-fqdn> -d AzureSolutionDB -U sqladmin -P <password> -i etl-dataflow/schema.sql
```

### 4. Test the Publisher Function

```bash
curl -X POST https://azsoln-dev-func-publisher.azurewebsites.net/api/publish \
  -H "Content-Type: application/json" \
  -H "x-functions-key: <your-function-key>" \
  -d '{"id":"test-001","source":"curl-test","data":{"product":"Widget","qty":1}}'
```

Expected response:
```json
{
  "status": "enqueued",
  "messageId": "...",
  "dataId": "test-001",
  "queuedAt": "2024-..."
}
```

### 5. Verify Subscriber processed the message

```sql
SELECT * FROM [dbo].[DataRecords] WHERE DataId = 'test-001';
```

### 6. Run the ETL Pipeline

```bash
az datafactory pipeline create-run \
  --factory-name azsoln-dev-adf \
  --name ETL_TransformAndPublish \
  --resource-group rg-azure-solution-dev \
  --parameters '{"PublisherFunctionBaseUrl":"https://azsoln-dev-func-publisher.azurewebsites.net","PublisherFunctionKey":"<key>"}'
```

---

## Data Flow – Step by Step

```
ETL trigger
  │
  ├─ 1. ADF Lookup: SELECT * FROM [dbo].[RawData] WHERE Status = 'Pending'
  │
  ├─ 2. ForEach record (parallel, batch=10)
  │      │
  │      ├─ a. Data Flow (DF_TransformRecord)
  │      │      - Trim strings
  │      │      - Derive ProcessingKey = DataId + timestamp
  │      │      - Filter null DataIds
  │      │      - Sink → [dbo].[TransformedData]
  │      │
  │      ├─ b. WebActivity → POST /api/publish  ← Publisher Function
  │      │      Body: { "id": "<DataId>", "source": "ADF-ETL", "data": {...} }
  │      │
  │      │         Publisher Function
  │      │            └─ Validates JSON
  │      │            └─ Wraps in QueueMessage envelope
  │      │            └─ Sends base64(envelope) → Storage Queue "data-queue"
  │      │
  │      │                  Subscriber Function (Queue Trigger)
  │      │                     └─ Decodes base64 → envelope
  │      │                     └─ MERGE upsert → [dbo].[DataRecords]
  │      │
  │      └─ c. Stored Proc: usp_MarkRawDataProcessed(@DataId, 'Published'|'Failed')
```

---

## Local Development

### Publisher Function

```bash
cd publisher-function
# Install Azurite (local Storage emulator)
npm install -g azurite
azurite &

func start
```

### Subscriber Function

```bash
cd subscriber-function
# Requires a real SQL connection OR LocalDB
# Update local.settings.json → SqlConnectionString
func start
```

---

## Security Checklist

- [ ] `local.settings.json` files are in `.gitignore`
- [ ] SQL password is stored in Key Vault; not hardcoded
- [ ] Function App host keys are rotated after first deploy
- [ ] ADF pipeline key reference uses AKV Linked Service (not plaintext)
- [ ] Storage Account has `allowBlobPublicAccess: false`
- [ ] Functions run with Managed Identity (RBAC) for production SQL access

---

## Extending the Solution

| Goal | How |
|------|-----|
| Schedule ETL automatically | Add a Trigger to ADF pipeline (Tumbling Window or Schedule) |
| Dead-letter failed queue messages | Enable Queue poison message handling (already set: maxDequeueCount=5) |
| Add more transforms | Extend `DF_TransformRecord` in ADF Data Flow studio |
| Scale up SQL | Change `sku` in `main.bicep` from Serverless to Business Critical |
| Add authentication | Enable Azure AD auth on Function Apps; use Managed Identity for SQL |
