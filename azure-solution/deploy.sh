#!/usr/bin/env bash
# deploy.sh – One-shot deploy script for the Azure Solution
# Usage: ./deploy.sh <environment> <resource-group> <subscription-id>
# Example: ./deploy.sh dev rg-azure-solution-dev 00000000-0000-0000-0000-000000000000

set -euo pipefail

ENV=${1:-dev}
RG=${2:-"rg-azure-solution-${ENV}"}
SUB=${3:-$(az account show --query id -o tsv)}

echo "=========================================="
echo "  Azure Solution Deployment"
echo "  Environment : $ENV"
echo "  Resource Grp: $RG"
echo "  Subscription: $SUB"
echo "=========================================="

az account set --subscription "$SUB"

# 1. Create resource group if it doesn't exist
az group create --name "$RG" --location eastus --output none
echo "[1/4] Resource group ready: $RG"

# 2. Deploy Bicep template
DEPLOYMENT=$(az deployment group create \
  --name "deploy-${ENV}-$(date +%Y%m%d%H%M%S)" \
  --resource-group "$RG" \
  --template-file bicep/main.bicep \
  --parameters bicep/main.${ENV}.bicepparam \
  --query "properties.outputs" \
  --output json)

echo "[2/4] Infrastructure deployed."
echo "$DEPLOYMENT" | jq .

PUBLISHER_URL=$(echo "$DEPLOYMENT" | jq -r '.publisherFunctionUrl.value')
SQL_FQDN=$(echo      "$DEPLOYMENT" | jq -r '.sqlServerFqdn.value')
ADF_NAME=$(echo      "$DEPLOYMENT" | jq -r '.dataFactoryName.value')

# 3. Deploy Publisher Function App
echo "[3/4] Deploying Publisher Function..."
pushd publisher-function
  dotnet publish -c Release -o ./publish
  cd publish && zip -r ../publisher.zip . && cd ..
  az functionapp deployment source config-zip \
    --resource-group "$RG" \
    --name "azsoln-${ENV}-func-publisher" \
    --src publisher.zip
popd

# 4. Deploy Subscriber Function App
echo "[4/4] Deploying Subscriber Function..."
pushd subscriber-function
  dotnet publish -c Release -o ./publish
  cd publish && zip -r ../subscriber.zip . && cd ..
  az functionapp deployment source config-zip \
    --resource-group "$RG" \
    --name "azsoln-${ENV}-func-subscriber" \
    --src subscriber.zip
popd

echo ""
echo "=========================================="
echo "  ✅  Deployment Complete!"
echo "  Publisher endpoint : $PUBLISHER_URL"
echo "  SQL Server FQDN    : $SQL_FQDN"
echo "  ADF Name           : $ADF_NAME"
echo ""
echo "  Next steps:"
echo "  1. Run the SQL schema:  etl-dataflow/schema.sql  against $SQL_FQDN"
echo "  2. Get Publisher function key and update ADF pipeline parameter."
echo "  3. Trigger ADF pipeline: az datafactory pipeline create-run --factory-name $ADF_NAME --name ETL_TransformAndPublish --resource-group $RG"
echo "=========================================="
