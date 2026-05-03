# Azure Queues & Topics (Azure Messaging)

Azure provides two distinct queuing services depending on complexity needs:

---

## Part 1: Azure Storage Queues

### What is it?
- Simple, cost-effective message queue built on top of **Azure Storage**
- Designed for **large volumes of messages** with basic queuing semantics
- REST-based API, accessible via HTTP/HTTPS

### Key Characteristics
| Property | Value |
|---|---|
| Max message size | 64 KB |
| Max queue size | 500 TB |
| Max retention | 7 days |
| Delivery | At-least-once |
| Ordering | Best-effort (NOT guaranteed) |
| Protocol | HTTP/HTTPS (REST) |

### How It Works
1. Producer sends message to a named queue in a Storage Account
2. Consumer **polls** the queue (pull-based)
3. Message becomes invisible during processing (visibility timeout)
4. Consumer deletes message after successful processing
5. If not deleted within timeout → reappears for retry

### Visibility Timeout
- Default: 30 seconds (max: 7 days)
- Same concept as SQS visibility timeout
- Prevents concurrent processing of the same message

### Poison Messages
- No built-in DLQ (unlike Service Bus)
- Messages that fail repeatedly stay in queue until retention expires
- You must implement your own poison message handling:
  - Track dequeue count (`DequeueCount` property)
  - Move to a separate "poison" queue manually when count exceeds threshold

### When to Use Azure Storage Queues
- Simple, high-volume queuing (millions of messages)
- Need queue size > 80 GB
- Need audit trail of messages
- Low cost is priority
- No need for advanced features (ordering, topics, sessions)

---

## Part 2: Azure Service Bus

### What is it?
- **Enterprise-grade** fully managed message broker
- Supports both **Queues** (point-to-point) and **Topics** (pub/sub)
- Rich feature set: FIFO, sessions, DLQ, transactions, duplicate detection

---

## Azure Service Bus — Queues

### Key Characteristics
| Property | Value |
|---|---|
| Max message size | 256 KB (Standard) / 100 MB (Premium) |
| Max queue size | 80 GB |
| Max retention | 14 days |
| Delivery | At-least-once or At-most-once |
| Ordering | FIFO with Sessions |
| Protocols | AMQP, HTTP, SBMP |

### Key Features

#### Sessions (FIFO Guarantee)
- Group related messages with a `SessionId`
- All messages with the same SessionId are processed by one consumer in order
- Example: All messages for `OrderId=12345` processed sequentially

#### Duplicate Detection
- If enabled, Service Bus deduplicates messages within a time window
- Uses `MessageId` to detect duplicates
- Window: 20 seconds to 7 days

#### Scheduled Messages
- Send a message but delay its availability until a specific time
- Example: Send "subscription renewal" message to appear in 30 days

#### Message Lock
- Consumer locks a message during processing (similar to visibility timeout)
- Lock duration: up to 5 minutes (renewable)
- If lock expires → message reappears

#### Transactions
- Group sends/receives/completes into atomic transactions
- Either all succeed or all fail
- Critical for financial systems

#### Dead Letter Queue (DLQ)
- Built-in DLQ per queue: `<queue-name>/$DeadLetterQueue`
- Triggered by:
  - `MaxDeliveryCount` exceeded (default: 10)
  - Message TTL expired
  - Filter evaluation errors
  - Explicit `DeadLetter()` call by consumer
- See `05_Dead_Letter_Queue.md` for full details

---

## Azure Service Bus — Topics & Subscriptions

### What is it?
- **Pub/Sub model** (equivalent to SNS in AWS)
- Publisher sends to a **Topic**
- Multiple **Subscriptions** receive independently filtered copies

### Architecture
```
         ┌──────────────────┐
         │   Service Bus    │
         │      Topic       │
         └──────┬───────────┘
                │
        ┌───────┼───────────┐
        ▼       ▼           ▼
  Subscription  Subscription  Subscription
   (Billing)   (Shipping)   (Analytics)
      │              │           │
   SB Queue       Function    Logic App
```

### Subscription Filters
Three types of filters per subscription:

| Filter Type | Description |
|---|---|
| **Boolean filter** | TrueFilter (all) or FalseFilter (none) |
| **SQL filter** | SQL-like expression on message properties: `color = 'red' AND quantity > 5` |
| **Correlation filter** | Match on specific properties (more efficient than SQL) |

### Subscription Actions
- Can modify message properties before delivery (add/set/remove properties)

### Key Characteristics
| Property | Value |
|---|---|
| Max subscriptions per topic | 2,000 |
| Max topic size | 80 GB |
| Max message size | 256 KB (Standard) / 100 MB (Premium) |
| DLQ | Built-in per subscription |

---

## Azure Storage Queues vs Azure Service Bus

| Feature | Storage Queues | Service Bus Queues | Service Bus Topics |
|---|---|---|---|
| Max message size | 64 KB | 256 KB / 100 MB | 256 KB / 100 MB |
| Queue size | 500 TB | 80 GB | 80 GB |
| FIFO guarantee | No | With Sessions | With Sessions |
| Duplicate detection | No | Yes | Yes |
| Transactions | No | Yes | Yes |
| DLQ | Manual | Built-in | Built-in |
| Pub/Sub | No | No | Yes |
| Cost | Very cheap | Moderate | Moderate |
| Use when | Simple, large volume | Enterprise point-to-point | Enterprise pub/sub |

---

## Service Bus Tiers

| Tier | Max Message Size | Features |
|---|---|---|
| Basic | 256 KB | Queues only, no topics |
| Standard | 256 KB | Queues + Topics |
| Premium | 100 MB | Dedicated resources, VNet, Geo-DR |
