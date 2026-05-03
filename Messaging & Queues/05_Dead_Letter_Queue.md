# Dead Letter Queue (DLQ) — Complete Guide

## What is a Dead Letter Queue?
A Dead Letter Queue (DLQ) is a **secondary queue** where messages are sent when they **cannot be processed successfully**.

Think of it as a quarantine zone:
- Messages that keep failing don't block the main queue
- They're preserved for investigation, debugging, and potential reprocessing
- Without a DLQ, failed messages would either be lost or loop forever

---

## Why Do Messages End Up in a DLQ?

| Reason | Description |
|---|---|
| **Max delivery/retry exceeded** | Consumer failed too many times (e.g., 3–10 retries) |
| **Message TTL expired** | Message sat in queue longer than allowed |
| **Poison message** | Message content is malformed/unparseable |
| **Consumer exception** | Unhandled exception in processing logic |
| **Schema mismatch** | Deserializer can't read the message format |
| **Business rule violation** | Consumer explicitly rejects the message |
| **Filter evaluation error** | SNS/Service Bus filter expression failed |
| **Consumer unavailable** | Downstream service is down for too long |

---

## DLQ Per Service

### Amazon SQS DLQ
- Configured as a **Redrive Policy** on the source queue
- `maxReceiveCount`: Number of delivery attempts before moving to DLQ (1–1000)
- DLQ must be same type: Standard → Standard, FIFO → FIFO
- DLQ lives in same AWS account/region

**Configuration:**
```json
{
  "deadLetterTargetArn": "arn:aws:sqs:us-east-1:123456789:MyQueue-DLQ",
  "maxReceiveCount": 5
}
```

**Flow:**
```
Producer → SQS Queue → Consumer (fails)
                  ↑ retry (up to maxReceiveCount)
                  ↓ after max retries
              SQS DLQ
```

**Redrive (reprocessing):**
- AWS Console supports "Start DLQ Redrive" — moves messages back to source queue
- Or write a Lambda that reads DLQ and republishes to source queue

---

### Amazon SNS DLQ
- Configured per **subscription** (not the topic)
- Only for HTTP/HTTPS and Lambda subscriptions
- SQS subscriptions use the SQS queue's own DLQ
- Stores failed delivery attempts

**Failure reasons tracked in DLQ message attributes:**
- `ERROR_TYPE`: e.g., `LAMBDA_INVOKE_FAILED`
- `ERROR_CODE`: HTTP response code or Lambda error code
- `ERROR_MESSAGE`: Detailed failure description

---

### Azure Service Bus DLQ
- **Built-in**, automatically created: `<queue-or-subscription>/$DeadLetterQueue`
- No separate resource to create
- Fully accessible via SDK for reading/reprocessing

**Triggers:**
| Trigger | Description |
|---|---|
| `MaxDeliveryCount` exceeded | Default is 10 attempts |
| `TimeToLive` expired | Message TTL passed |
| `DeadLetterOnMessageExpiration` | Config flag to DLQ expired TTL messages |
| Explicit `DeadLetter()` call | Consumer code manually dead-letters |
| Filter evaluation error | Topic subscription filter throws |

**DLQ message contains:**
- `DeadLetterReason`: e.g., `MaxDeliveryCountExceeded`
- `DeadLetterErrorDescription`: Detailed error text
- Original message body and all properties preserved

**Reading DLQ in C#:**
```csharp
var client = new ServiceBusClient(connectionString);
var receiver = client.CreateReceiver(
    queueName,
    new ServiceBusReceiverOptions {
        SubQueue = SubQueue.DeadLetter
    });

ServiceBusReceivedMessage msg = await receiver.ReceiveMessageAsync();
```

---

### Apache Kafka DLQ
- **No built-in DLQ** — must be implemented manually
- Convention: create a topic named `<original-topic>-dlq` or `<original-topic>-dlt`

**Spring Kafka implementation:**
```java
@Bean
public DefaultErrorHandler errorHandler(KafkaOperations<String, Object> template) {
    var recoverer = new DeadLetterPublishingRecoverer(template,
        (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));

    var backoff = new FixedBackOff(1000L, 3); // 3 retries, 1s apart
    return new DefaultErrorHandler(recoverer, backoff);
}
```

**DLT (Dead Letter Topic) message headers (Spring Kafka):**
- `kafka_dlt-original-topic`
- `kafka_dlt-original-partition`
- `kafka_dlt-original-offset`
- `kafka_dlt-exception-message`
- `kafka_dlt-exception-stacktrace`

**Reprocessing:**
- Write a consumer for the DLQ topic
- Fix the issue, then republish to original topic
- Or reset offsets on the DLQ topic to reprocess

---

## DLQ Best Practices

### 1. Always Configure a DLQ
Never leave a queue/topic without a DLQ. Without it:
- Messages are silently lost after max retries (SQS)
- Or they loop forever consuming resources

### 2. Set Appropriate maxReceiveCount / MaxDeliveryCount
- Too low (1–2): Transient failures (network blip) cause unnecessary DLQ routing
- Too high (50+): Genuinely broken messages waste processing time
- Recommended: **3–5** for most systems; **10** for systems with significant transient failures

### 3. Set DLQ Retention Higher Than Source Queue
- Source queue: 4 days
- DLQ: 14 days
- Gives engineers time to investigate and fix

### 4. Alert on DLQ Messages
- Set up a CloudWatch Alarm (SQS) or Azure Monitor Alert (Service Bus) when DLQ depth > 0
- Any DLQ message is a signal something needs attention

### 5. Preserve Original Message Context
- When reprocessing DLQ messages, preserve original headers (correlation IDs, timestamps)
- Log the original error alongside the message

### 6. Make Consumers Idempotent
- Before reprocessing DLQ messages, ensure the consumer handles duplicate processing safely
- Use idempotency keys, database upserts, or check-before-insert patterns

### 7. Categorize DLQ Failure Reasons
- **Transient failures** (network, timeout) → retry via redrive
- **Poison messages** (bad format, schema mismatch) → fix producer, reprocess
- **Business logic failures** → may need manual correction before reprocessing

---

## DLQ Monitoring & Alerting

### AWS (SQS DLQ)
```
CloudWatch Metric: ApproximateNumberOfMessagesVisible
Alarm: > 0 messages in DLQ → SNS alert → PagerDuty / Slack
```

### Azure (Service Bus DLQ)
```
Azure Monitor Metric: Dead-lettered messages
Alert: Count > 0 → Action Group → Email / Teams webhook
```

### Kafka (DLT)
```
Consumer Lag on DLT topic → Prometheus/Grafana alert
Or: Log message count to monitoring system on DLT consumer
```

---

## DLQ Reprocessing Patterns

### Pattern 1: Manual Redrive (AWS)
- Console → SQS → Source Queue → Start DLQ Redrive
- Moves all DLQ messages back to source queue
- Risk: if the root cause isn't fixed, they'll DLQ again

### Pattern 2: Lambda Reprocessor
```
DLQ → CloudWatch Event (scheduled) → Lambda → fix + republish → Source Queue
```

### Pattern 3: DLQ Consumer Service
- Dedicated microservice reads DLQ
- Applies transformation / fix logic
- Republishes to original queue/topic

### Pattern 4: Manual Inspection + Selective Requeue
- Engineer reads DLQ message
- Determines root cause
- Fixes data and manually republishes specific messages

---

## Summary Comparison: DLQ Across Services

| Feature | SQS DLQ | Service Bus DLQ | Kafka DLT |
|---|---|---|---|
| Built-in | Yes | Yes | No (manual) |
| Configuration | Redrive policy | Auto-created | Custom topic + code |
| Failure metadata | dequeue count | DLQ reason + description | Headers (Spring Kafka) |
| Reprocessing | Console redrive / Lambda | SDK consumer | Custom consumer |
| Alert integration | CloudWatch | Azure Monitor | Prometheus / custom |
| Max retries config | maxReceiveCount | MaxDeliveryCount | Retry policy in code |
