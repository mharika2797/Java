# Amazon SNS (Simple Notification Service)

## What is SNS?
Amazon SNS is a fully managed **pub/sub (publish-subscribe)** messaging service.
- One message published to a **Topic** is **pushed** to ALL subscribers simultaneously
- Supports multiple protocol types for subscribers
- No consumer polling — push-based delivery

---

## Core Components

### Topic
- A logical access point / communication channel
- Producers publish messages TO a topic
- Subscribers receive messages FROM a topic

### Publisher
- Any entity that sends a message to a topic
- Examples: Lambda, EC2, API Gateway, CloudWatch Alarm, S3 event

### Subscriber
- Receives every message published to the topic
- Supported endpoints:
  - **SQS** (most common — fan-out pattern)
  - **Lambda**
  - **HTTP/HTTPS**
  - **Email / Email-JSON**
  - **SMS**
  - **Mobile Push** (APNS for iOS, FCM for Android)

---

## Topic Types

### Standard Topic
- Best-effort ordering
- At-least-once delivery
- High throughput (unlimited)
- Supports all subscriber types

### FIFO Topic
- Strict message ordering
- Exactly-once delivery
- Lower throughput (300 TPS)
- **Only SQS FIFO queues** can subscribe
- Use case: financial systems, inventory management

---

## Key Concepts

### Fan-Out Pattern
The most powerful SNS pattern:
```
         ┌──────────────────┐
         │   SNS Topic      │
         └──────┬───────────┘
                │ publishes once
        ┌───────┼───────────┐
        ▼       ▼           ▼
    SQS Queue  Lambda   HTTP endpoint
   (Service A) (Service B) (Service C)
```
- Publish once, deliver to many independent consumers
- Each consumer processes at its own pace
- Loose coupling between services

### Message Filtering
- By default ALL subscribers receive ALL messages
- **Subscription Filter Policy**: JSON policy that filters messages per subscriber
- Example: Only send `orderType = "ELECTRONICS"` to the electronics SQS queue

```json
{
  "orderType": ["ELECTRONICS"]
}
```

### Message Attributes
- Key-value pairs attached to the message
- Used by filter policies to route messages

### Message Size
- Max 256 KB
- For larger payloads: use SNS + S3 (store payload in S3, send S3 reference)

### Delivery Retries
- HTTP/HTTPS endpoints: SNS retries with exponential backoff (up to 23 times over 23 days)
- SQS/Lambda: immediate retry handled by those services

---

## Dead Letter Queue for SNS
- SNS can have a DLQ configured per **subscription** (not the topic itself)
- Only for Lambda and HTTP/HTTPS subscriptions
- Failed deliveries land in the DLQ for debugging
- SQS subscriptions handle DLQ at the SQS level

---

## Message Encryption
- SSE using AWS KMS (encrypts at rest)
- In-transit encryption via HTTPS

---

## Access Control
- **Topic Policy** (resource-based): Who can publish/subscribe
- **IAM Policy** (identity-based): Fine-grained user permissions

---

## Common Use Cases
| Use Case | Reason |
|---|---|
| Sending alerts to multiple teams | Fan-out to email, Slack webhook, PagerDuty |
| Microservice event broadcasting | Fan-out to multiple SQS queues |
| Mobile push notifications | Native APNS/FCM support |
| CloudWatch alarms | Direct SNS integration |
| Order placed → trigger billing + shipping + inventory | Fan-out pattern |

---

## SNS vs SQS Summary
| Feature | SNS | SQS |
|---|---|---|
| Model | Pub/Sub (push) | Queue (pull) |
| Consumers | Many simultaneously | One at a time |
| Persistence | No (fire and forget) | Yes (up to 14 days) |
| Ordering | Optional (FIFO topic) | Optional (FIFO queue) |
| Filtering | Yes (per subscription) | No |
| Use when | Broadcasting events | Task processing |

---

## Pricing
- First 1 million publishes/month: **Free**
- Standard: $0.50 per million publishes
- SMS: varies by country
- Mobile push: $1.00 per million notifications
