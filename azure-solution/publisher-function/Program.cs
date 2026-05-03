using Azure.Storage.Queues;
using Microsoft.Azure.Functions.Worker;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;

var host = new HostBuilder()
    .ConfigureFunctionsWebApplication()
    .ConfigureServices((ctx, services) =>
    {
        services.AddApplicationInsightsTelemetryWorkerService();
        services.ConfigureFunctionsApplicationInsights();

        // Register QueueClient as singleton – reads connection string from app settings
        var connStr    = ctx.Configuration["AzureWebJobsStorage"]
                         ?? throw new InvalidOperationException("AzureWebJobsStorage not set.");
        var queueName  = ctx.Configuration["QueueName"] ?? "data-queue";

        services.AddSingleton(_ => new QueueClient(connStr, queueName));
    })
    .Build();

await host.RunAsync();
