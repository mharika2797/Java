# Amazon SQS (Simple Queue Service)

## What is SQS?
Amazon SQS is a fully managed **point-to-point** message queuing service by AWS.
- Decouples producers and consumers
- Guarantees at-least-once delivery
- Messages persist until explicitly deleted by the consumer
- No need to manage infrastructure

---

## Queue Types

### 1. Standard Queue
- **Throughput**: Nearly unlimited TPS (transactions per second)
- **Ordering**: Best-effort ordering (NOT guaranteed)
- **Delivery**: At-least-once (duplicates possible)
- **Use case**: High-throughput tasks where order doesn't matter (image processing, log ingestion)

### 2. FIFO Queue (First-In-First-Out)
- **Throughput**: 300 TPS (3,000 with batching)
- **Ordering**: Strict ordering guaranteed
- **Delivery**: Exactly-once processing (deduplication built in)
- **Use case**: Financial transactions, order processing, inventory updates

---

## Key Concepts

### Message Visibility Timeout
- When a consumer reads a message, it becomes **invisible** to other consumers for a set duration (default: 30s, max: 12h)
- If the consumer crashes before deleting it, the message reappears and is retried
- Prevents duplicate processing in normal flows

### Long Polling vs Short Polling
| | Short Polling | Long Polling |
|---|---|---|
| Behavior | Returns immediately (even if empty) | Waits up to 20s for a message |
| Cost | Higher (many empty responses) | Lower (fewer API calls) |
| Recommended | No | Yes |

### Message Retention
- Default: 4 days
- Range: 1 minute to 14 days

### Max Message Size
- 256 KB per message
- For larger payloads: use **SQS Extended Client Library** (stores body in S3, sends reference)

### Delay Queues
- Postpone delivery of new messages by 0–900 seconds
- Consumer won't see the message until the delay expires

### Message Attributes
- Metadata attached to a message (up to 10 attributes)
- Examples: MessageType, Priority, CorrelationId

---

## Dead Letter Queue (DLQ)
- After `maxReceiveCount` failed delivery attempts, SQS moves the message to a DLQ
- Allows investigation without blocking the main queue
- Must be same type as source (Standard → Standard DLQ, FIFO → FIFO DLQ)
- See `05_Dead_Letter_Queue.md` for full details

---

## Access Control
- **Queue Policy** (resource-based): Allow other AWS accounts/services to send/receive
- **IAM Policy** (identity-based): Grant users/roles access to SQS actions
- Encryption: SSE (Server-Side Encryption) using AWS KMS

---

## Common Use Cases
| Use Case | Queue Type |
|---|---|
| Email notification dispatch | Standard |
| Order processing pipeline | FIFO |
| Microservice decoupling | Standard |
| Financial ledger updates | FIFO |
| Background job queue | Standard |

---

## SQS vs SNS
- SQS = **pull-based** (consumer polls the queue)
- SNS = **push-based** (broker pushes to all subscribers)
- Common pattern: **SNS → SQS fan-out** (SNS broadcasts, each SQS queue handles independently)

---

## Pricing
- First 1 million requests/month: **Free**
- Standard: ~$0.40 per million requests
- FIFO: ~$0.50 per million requests
- Data transfer within same region: Free
