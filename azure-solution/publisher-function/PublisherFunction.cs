using System;
using System.IO;
using System.Text.Json;
using System.Threading.Tasks;
using Azure.Storage.Queues;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.Functions.Worker;
using Microsoft.Azure.Functions.Worker.Http;
using Microsoft.Extensions.Logging;

namespace AzureSolution.Publisher
{
    public class PublisherFunction
    {
        private readonly ILogger<PublisherFunction> _logger;
        private readonly QueueClient _queueClient;

        public PublisherFunction(ILogger<PublisherFunction> logger, QueueClient queueClient)
        {
            _logger = logger;
            _queueClient = queueClient;
        }

        /// <summary>
        /// HTTP POST endpoint: receives a JSON payload and enqueues it.
        /// POST /api/publish
        /// Body: { "id": "...", "source": "...", "data": { ... } }
        /// </summary>
        [Function("Publish")]
        public async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Function, "post", Route = "publish")] HttpRequest req)
        {
            _logger.LogInformation("Publisher triggered at {Time}", DateTime.UtcNow);

            string requestBody;
            using (var reader = new StreamReader(req.Body))
            {
                requestBody = await reader.ReadToEndAsync();
            }

            if (string.IsNullOrWhiteSpace(requestBody))
                return new BadRequestObjectResult("Request body is empty.");

            // Validate JSON
            QueueMessage message;
            try
            {
                using var doc = JsonDocument.Parse(requestBody);
                var root = doc.RootElement;

                // Ensure required "id" field exists
                if (!root.TryGetProperty("id", out var idProp) || string.IsNullOrWhiteSpace(idProp.GetString()))
                    return new BadRequestObjectResult("Payload must contain a non-empty 'id' field.");

                message = new QueueMessage
                {
                    MessageId  = Guid.NewGuid().ToString(),
                    DataId     = idProp.GetString()!,
                    Source     = root.TryGetProperty("source", out var src) ? src.GetString() ?? "unknown" : "unknown",
                    Payload    = JsonDocument.Parse(requestBody).RootElement.Clone(),
                    EnqueuedAt = DateTime.UtcNow
                };
            }
            catch (JsonException ex)
            {
                _logger.LogWarning("Invalid JSON payload: {Error}", ex.Message);
                return new BadRequestObjectResult($"Invalid JSON: {ex.Message}");
            }

            // Serialize the envelope and send to queue
            var envelope = JsonSerializer.Serialize(message);
            var base64   = Convert.ToBase64String(System.Text.Encoding.UTF8.GetBytes(envelope));

            await _queueClient.CreateIfNotExistsAsync();
            await _queueClient.SendMessageAsync(base64);

            _logger.LogInformation("Message {MsgId} (DataId={DataId}) enqueued successfully.", message.MessageId, message.DataId);

            return new OkObjectResult(new
            {
                status    = "enqueued",
                messageId = message.MessageId,
                dataId    = message.DataId,
                queuedAt  = message.EnqueuedAt
            });
        }
    }

    /// <summary>Envelope written to the queue.</summary>
    public record QueueMessage
    {
        public string          MessageId  { get; init; } = string.Empty;
        public string          DataId     { get; init; } = string.Empty;
        public string          Source     { get; init; } = string.Empty;
        public JsonElement     Payload    { get; init; }
        public DateTime        EnqueuedAt { get; init; }
    }
}
