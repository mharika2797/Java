using System;
using System.Data;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using Microsoft.Azure.Functions.Worker;
using Microsoft.Data.SqlClient;
using Microsoft.Extensions.Logging;

namespace AzureSolution.Subscriber
{
    public class SubscriberFunction
    {
        private readonly ILogger<SubscriberFunction> _logger;
        private readonly string _sqlConnectionString;

        public SubscriberFunction(ILogger<SubscriberFunction> logger, IConfiguration config)
        {
            _logger = logger;
            _sqlConnectionString = config["SqlConnectionString"]
                ?? throw new InvalidOperationException("SqlConnectionString not configured.");
        }

        /// <summary>
        /// Queue-triggered function. Fires for every message on "data-queue".
        /// Decodes the base64 envelope, maps it to a DB row, and upserts into [dbo].[DataRecords].
        /// </summary>
        [Function("Subscribe")]
        public async Task Run(
            [QueueTrigger("%QueueName%", Connection = "AzureWebJobsStorage")] string base64Message,
            FunctionContext context)
        {
            _logger.LogInformation("Subscriber triggered at {Time}", DateTime.UtcNow);

            // 1. Decode base64 → JSON envelope
            string json;
            try
            {
                json = Encoding.UTF8.GetString(Convert.FromBase64String(base64Message));
            }
            catch (FormatException ex)
            {
                _logger.LogError("Failed to decode base64 message: {Error}", ex.Message);
                throw; // poison-message: let the queue SDK handle retries / dead-letter
            }

            // 2. Deserialise envelope
            QueueMessageEnvelope envelope;
            try
            {
                envelope = JsonSerializer.Deserialize<QueueMessageEnvelope>(json,
                    new JsonSerializerOptions { PropertyNameCaseInsensitive = true })
                    ?? throw new InvalidOperationException("Deserialised envelope is null.");
            }
            catch (JsonException ex)
            {
                _logger.LogError("Failed to deserialise queue message: {Error}", ex.Message);
                throw;
            }

            _logger.LogInformation("Processing MessageId={MsgId}, DataId={DataId}", envelope.MessageId, envelope.DataId);

            // 3. Persist to SQL
            await UpsertToSqlAsync(envelope);

            _logger.LogInformation("MessageId={MsgId} persisted successfully.", envelope.MessageId);
        }

        // -------------------------------------------------------------------------
        // SQL persistence
        // -------------------------------------------------------------------------
        private async Task UpsertToSqlAsync(QueueMessageEnvelope envelope)
        {
            const string upsertSql = @"
                MERGE [dbo].[DataRecords] AS target
                USING (SELECT @DataId AS DataId) AS source ON target.DataId = source.DataId
                WHEN MATCHED THEN
                    UPDATE SET
                        MessageId   = @MessageId,
                        Source      = @Source,
                        Payload     = @Payload,
                        EnqueuedAt  = @EnqueuedAt,
                        ProcessedAt = @ProcessedAt,
                        Status      = @Status
                WHEN NOT MATCHED THEN
                    INSERT (DataId, MessageId, Source, Payload, EnqueuedAt, ProcessedAt, Status)
                    VALUES (@DataId, @MessageId, @Source, @Payload, @EnqueuedAt, @ProcessedAt, @Status);";

            await using var conn = new SqlConnection(_sqlConnectionString);
            await conn.OpenAsync();

            await using var cmd = new SqlCommand(upsertSql, conn);
            cmd.Parameters.Add("@DataId",      SqlDbType.NVarChar, 256).Value = envelope.DataId;
            cmd.Parameters.Add("@MessageId",   SqlDbType.NVarChar, 256).Value = envelope.MessageId;
            cmd.Parameters.Add("@Source",      SqlDbType.NVarChar, 256).Value = envelope.Source;
            cmd.Parameters.Add("@Payload",     SqlDbType.NVarChar, -1 ).Value = envelope.Payload.ToString();
            cmd.Parameters.Add("@EnqueuedAt",  SqlDbType.DateTime2     ).Value = envelope.EnqueuedAt;
            cmd.Parameters.Add("@ProcessedAt", SqlDbType.DateTime2     ).Value = DateTime.UtcNow;
            cmd.Parameters.Add("@Status",      SqlDbType.NVarChar, 50  ).Value = "Processed";

            await cmd.ExecuteNonQueryAsync();
        }
    }

    // DTO matching the publisher envelope
    public record QueueMessageEnvelope
    {
        public string      MessageId  { get; init; } = string.Empty;
        public string      DataId     { get; init; } = string.Empty;
        public string      Source     { get; init; } = string.Empty;
        public JsonElement Payload    { get; init; }
        public DateTime    EnqueuedAt { get; init; }
    }
}
