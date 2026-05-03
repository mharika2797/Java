# When to Use Which Messaging Service

## Quick Decision Guide

```
Do you need pub/sub (one message → many consumers)?
  YES → Do you need enterprise features (filtering, sessions, DLQ)?
          YES → Azure Service Bus Topics  (Azure)
                OR Amazon SNS + SQS      (AWS)
                OR Kafka                 (very high throughput / event sourcing)
          NO  → Azure Storage Queues won't do pub/sub
                Use SNS (AWS) or Service Bus Topics (Azure)

  NO (point-to-point queue)?
    Do you need enterprise features (FIFO, DLQ, transactions)?
      YES → SQS FIFO (AWS) or Azure Service Bus Queue (Azure)
      NO  → SQS Standard (AWS) or Azure Storage Queue (Azure)

Do you need event streaming / replay / audit log?
  → Apache Kafka (or Amazon Kinesis / Azure Event Hubs)
```

---

## Detailed Comparison Table

| Feature | SQS Standard | SQS FIFO | SNS | Azure Storage Q | Service Bus Q | Service Bus Topic | Kafka |
|---|---|---|---|---|---|---|---|
| Model | Queue | Queue | Pub/Sub | Queue | Queue | Pub/Sub | Event Log |
| Ordering | Best-effort | Strict | Best-effort | Best-effort | With sessions | With sessions | Per partition |
| Throughput | Unlimited | 3K/sec | Unlimited | Unlimited | High | High | Millions/sec |
| Replay | No | No | No | No | No | No | Yes |
| Built-in DLQ | Yes | Yes | Per sub | No | Yes | Yes | No |
| Exactly-once | No | Yes | No | No | Yes | Yes | Yes (EOS) |
| Max msg size | 256 KB | 256 KB | 256 KB | 64 KB | 100 MB (Prem) | 100 MB (Prem) | 1 MB (default) |
| Retention | 14 days | 14 days | No | 7 days | 14 days | 14 days | Unlimited |
| Filter | No | No | Yes (SQL) | No | No | Yes (SQL/Correlation) | No |
| Transactions | No | No | No | No | Yes | Yes | Yes |
| Protocol | HTTPS | HTTPS | HTTPS | HTTPS | AMQP/HTTPS | AMQP/HTTPS | TCP (custom) |
| Cloud | AWS | AWS | AWS | Azure | Azure | Azure | Any |

---

## Scenario-Based Recommendations

### "I want to process background jobs (send email, resize image)"
→ **SQS Standard** (AWS) or **Azure Storage Queue** (Azure)
- Simple, cheap, high throughput
- Jobs are independent, order doesn't matter

### "I need to process financial transactions in order"
→ **SQS FIFO** (AWS) or **Azure Service Bus Queue with Sessions** (Azure)
- Strict ordering, exactly-once delivery
- Transactions support (Service Bus)

### "I want to notify multiple services when an order is placed"
→ **SNS + SQS fan-out** (AWS) or **Azure Service Bus Topics** (Azure)
- One publish, multiple independent consumers
- Each service gets its own queue/subscription

### "I need real-time analytics on clickstream data (millions of events/sec)"
→ **Apache Kafka** or **Amazon Kinesis** or **Azure Event Hubs**
- Extremely high throughput
- Retain raw events for later analysis
- Multiple consumers reading same data independently

### "I want an audit trail / ability to replay events from 3 months ago"
→ **Apache Kafka** (with long retention)
- Only Kafka retains messages after consumption
- SQS/Service Bus delete after acknowledgment

### "Microservices on Azure, need pub/sub with SQL filtering"
→ **Azure Service Bus Topics**
- SQL filter expressions route messages to correct subscriptions
- Example: only `priority = 'HIGH'` messages go to the express-processing subscription

### "I need to fan-out and then also have each subscriber independently consume at their own pace"
→ **SNS → SQS** (AWS) or **Service Bus Topics → Subscriptions** (Azure)
- SNS/Topics push to each SQS queue / subscription independently
- Each queue acts as a buffer for that consumer

### "We're on AWS, need simple decoupling, don't want to manage infrastructure"
→ **SQS**
- Fully managed, auto-scales, no servers to run
- 99.9% SLA

### "We need cross-cloud or on-prem + cloud messaging"
→ **Apache Kafka** (self-hosted or Confluent Cloud)
- Cloud-agnostic
- Can bridge on-prem and cloud systems

---

## Cost Considerations

| Service | Free Tier | Paid |
|---|---|---|
| SQS Standard | 1M requests/month | $0.40/million |
| SQS FIFO | 1M requests/month | $0.50/million |
| SNS | 1M publishes/month | $0.50/million |
| Azure Storage Queue | 2M ops/month (Storage free tier) | $0.004 per 10K ops |
| Azure Service Bus Basic | None | ~$0.05/million ops |
| Azure Service Bus Standard | 10M ops/month | ~$0.01/million ops |
| Kafka (Confluent Cloud) | None | From $0.11/GB ingested |
| Amazon MSK | None | EC2 + storage costs |

---

## Mental Model Summary

| Service | Think of it as... |
|---|---|
| SQS Standard | A post box — drop messages, someone collects later |
| SQS FIFO | A numbered ticket queue at the DMV — strict order |
| SNS | A broadcast announcement — everyone hears it at once |
| Azure Storage Queue | A simple sticky-note pile — cheap, basic |
| Service Bus Queue | An enterprise inbox — rich features, guaranteed delivery |
| Service Bus Topic | A company newsletter — filtered copies for each department |
| Kafka | A river — data flows continuously, you can drink from any point |
