# Apache Kafka

## What is Kafka?
Apache Kafka is a distributed **event streaming platform** — NOT just a message queue.
- Built for **high-throughput, fault-tolerant, real-time** data streaming
- Messages are **retained** even after consumption (consumers read at their own pace)
- Designed to handle millions of events per second
- Originally built at LinkedIn, now open-source (Apache Foundation)

---

## Core Architecture

```
Producers → [Kafka Cluster] → Consumers
              │
    ┌─────────┴──────────┐
    │  Broker 1  Broker 2  Broker 3  │  ← Kafka Cluster
    └─────────────────────────────────┘
              │
         ZooKeeper / KRaft (metadata)
```

---

## Key Concepts

### Topic
- A named category/feed to which producers write messages
- Logical grouping of related events
- Example: `orders`, `user-clicks`, `payments`

### Partition
- A topic is split into **partitions** (horizontal sharding)
- Each partition is an **ordered, immutable log**
- Enables parallelism — more partitions = more consumers can read in parallel
- Messages within a partition are ordered; across partitions, no order guarantee

```
Topic: "orders"
Partition 0: [msg1] [msg4] [msg7] ...
Partition 1: [msg2] [msg5] [msg8] ...
Partition 2: [msg3] [msg6] [msg9] ...
```

### Offset
- Each message in a partition has a unique sequential **offset** number (0, 1, 2, ...)
- Consumers track their position using offsets
- Consumers can **replay** messages by resetting to an earlier offset
- This is a fundamental difference from SQS/Service Bus (messages aren't deleted after consumption)

### Broker
- A single Kafka server
- Stores partitions and serves producer/consumer requests
- A Kafka cluster = multiple brokers
- Each partition has one **leader** broker and multiple **replica** brokers

### Replication
- Each partition is replicated across `N` brokers (replication factor)
- Leader handles reads/writes; replicas are hot standbys
- If leader fails, a replica is elected as the new leader
- Common: replication factor = 3 (survives 2 broker failures)

### ZooKeeper / KRaft
- **ZooKeeper** (legacy): Manages cluster metadata, leader election
- **KRaft** (Kafka 2.8+): Kafka's own consensus protocol, replaces ZooKeeper dependency

---

## Producers

### How Producers Work
1. Producer sends a message (key, value, headers, timestamp) to a topic
2. Kafka determines which partition to write to:
   - **No key**: Round-robin across partitions
   - **With key**: `hash(key) % numPartitions` — same key always goes to same partition
3. Message is appended to the partition log

### Key-based Partitioning (Ordering)
```
key = "user-123" → always goes to Partition 1
key = "user-456" → always goes to Partition 0
```
- All events for `user-123` are ordered within Partition 1
- Useful for: per-user event ordering, per-order event ordering

### Acknowledgments (acks)
| acks | Meaning | Risk |
|---|---|---|
| 0 | Fire and forget (no ack) | Message loss possible |
| 1 | Leader confirms write | Loss if leader fails before replication |
| all (-1) | All replicas confirm | Safest, slightly slower |

---

## Consumers

### Consumer Groups
- Consumers are organized into **Consumer Groups**
- Each partition is assigned to exactly ONE consumer in a group
- Different consumer groups read INDEPENDENTLY (each gets all messages)

```
Topic "orders" — 3 partitions

Consumer Group A (Billing Service):
  Consumer A1 → Partition 0
  Consumer A2 → Partition 1
  Consumer A3 → Partition 2

Consumer Group B (Shipping Service):
  Consumer B1 → Partition 0, 1
  Consumer B2 → Partition 2
```

- Adding consumers to a group = scales out processing
- More consumers than partitions → some consumers are idle

### Offset Management
- Consumers commit their offset to Kafka (topic `__consumer_offsets`)
- **Auto-commit**: Kafka periodically commits offset automatically (risk of data loss on crash)
- **Manual commit**: Consumer commits after successful processing (safer)
- **Replay**: Reset offset to beginning to reprocess all historical data

---

## Message Retention
- Messages are retained for a configurable period regardless of consumption
- Default: 7 days
- Can also set size-based retention (e.g., keep up to 1 TB per partition)
- `log.retention.ms` / `log.retention.bytes`
- This enables **event sourcing** and **audit trails**

---

## Kafka Streams & KSQL
- **Kafka Streams**: Java library for real-time stream processing on Kafka data
- **KSQL / ksqlDB**: SQL interface for stream processing without writing code
- Examples: windowed aggregations, joins between streams, filtering

---

## Exactly-Once Semantics (EOS)
- Kafka supports exactly-once delivery (Kafka 0.11+)
- Requires:
  - **Idempotent producer**: Deduplicates retried sends (enable.idempotence=true)
  - **Transactions**: Atomic writes across partitions
- Guarantees: a message is processed exactly once, even on failures

---

## Dead Letter Queue in Kafka
- Kafka has NO built-in DLQ (unlike SQS/Service Bus)
- Common pattern: create a separate topic (e.g., `orders-dlq`) and route failed messages there manually in consumer code
- Libraries like **Kafka Connect** and **Spring Kafka** have DLQ support built in
- See `05_Dead_Letter_Queue.md` for full details

---

## Kafka vs Traditional Message Queues

| Feature | Kafka | SQS / Service Bus |
|---|---|---|
| Model | Event log (append-only) | Queue (delete after consume) |
| Retention | Time/size based (days) | Until consumed or TTL |
| Replay | Yes (reset offset) | No |
| Throughput | Millions/sec | Thousands–hundreds of thousands/sec |
| Ordering | Per partition | FIFO queue or sessions |
| Consumers | Many independently | Competing consumers |
| Use case | Streaming, event sourcing | Task queue, decoupling |

---

## Managed Kafka Services
| Service | Provider |
|---|---|
| Amazon MSK | AWS |
| Azure Event Hubs (Kafka API) | Azure |
| Confluent Cloud | Confluent (multi-cloud) |
| HDInsight Kafka | Azure |
| Google Cloud Pub/Sub (Kafka compat) | GCP |

---

## Common Use Cases
| Use Case | Why Kafka |
|---|---|
| Real-time analytics dashboards | High throughput, retain raw events |
| CDC (Change Data Capture) | Debezium + Kafka captures DB changes |
| Microservice event bus | Durable, replayable event log |
| Log aggregation | All services → Kafka → ELK/Splunk |
| Machine learning feature pipelines | Stream features to model serving |
| Financial fraud detection | Low-latency stream processing |
