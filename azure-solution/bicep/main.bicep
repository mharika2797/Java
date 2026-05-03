// =============================================================================
// main.bicep  –  Azure Solution Infrastructure
// Resources:
//   - Storage Account (Queues)
//   - Azure SQL Server + Database
//   - App Service Plan (Linux, Consumption)
//   - Publisher Function App
//   - Subscriber Function App
//   - Azure Data Factory + Linked Services + Pipeline
//   - Application Insights + Log Analytics Workspace
//   - Key Vault (for secrets)
// =============================================================================

targetScope = 'resourceGroup'

// ---------------------------------------------------------------------------
// Parameters
// ---------------------------------------------------------------------------
@description('Short environment tag: dev | uat | prod')
@allowed(['dev', 'uat', 'prod'])
param environment string = 'dev'

@description('Azure region for all resources')
param location string = resourceGroup().location

@description('SQL administrator login name')
param sqlAdminLogin string = 'sqladmin'

@description('SQL administrator password – store in Key Vault, never hardcode')
@secure()
param sqlAdminPassword string

@description('Object ID of the ADF managed identity (set after first deploy, for AKV access policy)')
param adfPrincipalId string = ''

// ---------------------------------------------------------------------------
// Variables
// ---------------------------------------------------------------------------
var prefix      = 'azsoln-${environment}'
var storageName = replace('st${prefix}', '-', '')   // storage names: lowercase, no dashes, max 24 chars
var storageSafe = length(storageName) > 24 ? substring(storageName, 0, 24) : storageName

// ---------------------------------------------------------------------------
// Log Analytics Workspace
// ---------------------------------------------------------------------------
resource logAnalytics 'Microsoft.OperationalInsights/workspaces@2022-10-01' = {
  name:     '${prefix}-law'
  location: location
  properties: {
    sku: { name: 'PerGB2018' }
    retentionInDays: 30
  }
}

// ---------------------------------------------------------------------------
// Application Insights
// ---------------------------------------------------------------------------
resource appInsights 'Microsoft.Insights/components@2020-02-02' = {
  name:     '${prefix}-ai'
  location: location
  kind:     'web'
  properties: {
    Application_Type:             'web'
    WorkspaceResourceId:          logAnalytics.id
    IngestionMode:                'LogAnalytics'
    publicNetworkAccessForQuery:  'Enabled'
    publicNetworkAccessForIngestion: 'Enabled'
  }
}

// ---------------------------------------------------------------------------
// Key Vault
// ---------------------------------------------------------------------------
resource keyVault 'Microsoft.KeyVault/vaults@2023-07-01' = {
  name:     '${prefix}-kv'
  location: location
  properties: {
    sku:               { family: 'A', name: 'standard' }
    tenantId:          subscription().tenantId
    enableSoftDelete:  true
    softDeleteRetentionInDays: 7
    accessPolicies:    []   // Managed via role assignments below
  }
}

resource kvSqlSecret 'Microsoft.KeyVault/vaults/secrets@2023-07-01' = {
  parent: keyVault
  name:   'SqlAdminPassword'
  properties: { value: sqlAdminPassword }
}

// ---------------------------------------------------------------------------
// Storage Account + Queue
// ---------------------------------------------------------------------------
resource storageAccount 'Microsoft.Storage/storageAccounts@2023-01-01' = {
  name:     storageSafe
  location: location
  sku:      { name: 'Standard_LRS' }
  kind:     'StorageV2'
  properties: {
    minimumTlsVersion:        'TLS1_2'
    allowBlobPublicAccess:    false
    supportsHttpsTrafficOnly: true
  }
}

resource queueService 'Microsoft.Storage/storageAccounts/queueServices@2023-01-01' = {
  parent: storageAccount
  name:   'default'
}

resource dataQueue 'Microsoft.Storage/storageAccounts/queueServices/queues@2023-01-01' = {
  parent: queueService
  name:   'data-queue'
}

// ---------------------------------------------------------------------------
// Azure SQL Server + Database
// ---------------------------------------------------------------------------
resource sqlServer 'Microsoft.Sql/servers@2023-05-01-preview' = {
  name:     '${prefix}-sql'
  location: location
  properties: {
    administratorLogin:         sqlAdminLogin
    administratorLoginPassword: sqlAdminPassword
    version:                    '12.0'
    publicNetworkAccess:        'Enabled'
  }
}

resource sqlFirewallAzure 'Microsoft.Sql/servers/firewallRules@2023-05-01-preview' = {
  parent: sqlServer
  name:   'AllowAzureServices'
  properties: {
    startIpAddress: '0.0.0.0'
    endIpAddress:   '0.0.0.0'
  }
}

resource sqlDatabase 'Microsoft.Sql/servers/databases@2023-05-01-preview' = {
  parent:   sqlServer
  name:     'AzureSolutionDB'
  location: location
  sku: {
    name:     'GP_S_Gen5_1'   // Serverless Gen5 – cost-efficient for dev/uat
    tier:     'GeneralPurpose'
    family:   'Gen5'
    capacity: 1
  }
  properties: {
    collation:           'SQL_Latin1_General_CP1_CI_AS'
    maxSizeBytes:        34359738368   // 32 GB
    autoPauseDelay:      60            // minutes (serverless)
    minCapacity:         '0.5'
    zoneRedundant:       false
    requestedBackupStorageRedundancy: 'Local'
  }
}

// ---------------------------------------------------------------------------
// App Service Plan (Consumption / Serverless for Functions)
// ---------------------------------------------------------------------------
resource appServicePlan 'Microsoft.Web/serverfarms@2023-01-01' = {
  name:     '${prefix}-asp'
  location: location
  sku:      { name: 'Y1', tier: 'Dynamic' }
  kind:     'functionapp'
  properties: { reserved: false }
}

// ---------------------------------------------------------------------------
// Shared Function App settings helper
// ---------------------------------------------------------------------------
var sharedFunctionSettings = [
  { name: 'FUNCTIONS_EXTENSION_VERSION',        value: '~4'                                  }
  { name: 'FUNCTIONS_WORKER_RUNTIME',            value: 'dotnet-isolated'                     }
  { name: 'WEBSITE_RUN_FROM_PACKAGE',            value: '1'                                   }
  { name: 'AzureWebJobsStorage',                 value: 'DefaultEndpointsProtocol=https;AccountName=${storageAccount.name};AccountKey=${storageAccount.listKeys().keys[0].value};EndpointSuffix=core.windows.net' }
  { name: 'APPLICATIONINSIGHTS_CONNECTION_STRING', value: appInsights.properties.ConnectionString }
  { name: 'QueueName',                           value: 'data-queue'                          }
]

// ---------------------------------------------------------------------------
// Publisher Function App
// ---------------------------------------------------------------------------
resource publisherFunctionApp 'Microsoft.Web/sites@2023-01-01' = {
  name:     '${prefix}-func-publisher'
  location: location
  kind:     'functionapp'
  identity: { type: 'SystemAssigned' }
  properties: {
    serverFarmId: appServicePlan.id
    httpsOnly:    true
    siteConfig: {
      netFrameworkVersion: 'v8.0'
      appSettings:         sharedFunctionSettings
      ftpsState:           'Disabled'
      minTlsVersion:       '1.2'
    }
  }
}

// ---------------------------------------------------------------------------
// Subscriber Function App
// ---------------------------------------------------------------------------
resource subscriberFunctionApp 'Microsoft.Web/sites@2023-01-01' = {
  name:     '${prefix}-func-subscriber'
  location: location
  kind:     'functionapp'
  identity: { type: 'SystemAssigned' }
  properties: {
    serverFarmId: appServicePlan.id
    httpsOnly:    true
    siteConfig: {
      netFrameworkVersion: 'v8.0'
      appSettings: concat(sharedFunctionSettings, [
        {
          name:  'SqlConnectionString'
          value: 'Server=tcp:${sqlServer.properties.fullyQualifiedDomainName},1433;Database=AzureSolutionDB;User ID=${sqlAdminLogin};Password=${sqlAdminPassword};Encrypt=True;Connection Timeout=30;'
        }
      ])
      ftpsState:     'Disabled'
      minTlsVersion: '1.2'
    }
  }
}

// ---------------------------------------------------------------------------
// Azure Data Factory
// ---------------------------------------------------------------------------
resource dataFactory 'Microsoft.DataFactory/factories@2018-06-01' = {
  name:     '${prefix}-adf'
  location: location
  identity: { type: 'SystemAssigned' }
  properties: {
    publicNetworkAccess: 'Enabled'
  }
}

// Linked Service: Azure SQL
resource adfLinkedServiceSQL 'Microsoft.DataFactory/factories/linkedservices@2018-06-01' = {
  parent: dataFactory
  name:   'LS_AzureSQL'
  properties: {
    type: 'AzureSqlDatabase'
    typeProperties: {
      connectionString: 'Server=tcp:${sqlServer.properties.fullyQualifiedDomainName},1433;Database=AzureSolutionDB;User ID=${sqlAdminLogin};Password=${sqlAdminPassword};Encrypt=True;Connection Timeout=30;'
    }
  }
}

// Linked Service: Azure Storage (for staging)
resource adfLinkedServiceStorage 'Microsoft.DataFactory/factories/linkedservices@2018-06-01' = {
  parent: dataFactory
  name:   'LS_AzureStorage'
  properties: {
    type: 'AzureBlobStorage'
    typeProperties: {
      connectionString: 'DefaultEndpointsProtocol=https;AccountName=${storageAccount.name};AccountKey=${storageAccount.listKeys().keys[0].value};EndpointSuffix=core.windows.net'
    }
  }
}

// Dataset: RawData table
resource adfDatasetRawData 'Microsoft.DataFactory/factories/datasets@2018-06-01' = {
  parent: dataFactory
  name:   'DS_AzureSQL_RawData'
  properties: {
    linkedServiceName: { referenceName: adfLinkedServiceSQL.name, type: 'LinkedServiceReference' }
    type:              'AzureSqlTable'
    typeProperties: {
      schema: 'dbo'
      table:  'RawData'
    }
  }
}

// Dataset: TransformedData table
resource adfDatasetTransformed 'Microsoft.DataFactory/factories/datasets@2018-06-01' = {
  parent: dataFactory
  name:   'DS_AzureSQL_TransformedData'
  properties: {
    linkedServiceName: { referenceName: adfLinkedServiceSQL.name, type: 'LinkedServiceReference' }
    type:              'AzureSqlTable'
    typeProperties: {
      schema: 'dbo'
      table:  'TransformedData'
    }
  }
}

// ADF Pipeline – ETL Transform & Publish
resource adfPipeline 'Microsoft.DataFactory/factories/pipelines@2018-06-01' = {
  parent: dataFactory
  name:   'ETL_TransformAndPublish'
  properties: {
    description: 'Reads pending rows, transforms, and calls Publisher Function.'
    parameters: {
      PublisherFunctionBaseUrl: { type: 'string', defaultValue: 'https://${publisherFunctionApp.properties.defaultHostName}' }
      PublisherFunctionKey:     { type: 'string', defaultValue: '' }
    }
    activities: [
      {
        name:        'LookupPendingRecords'
        type:        'Lookup'
        dependsOn:   []
        typeProperties: {
          source: {
            type:           'AzureSqlSource'
            sqlReaderQuery: 'SELECT DataId, RawPayload, CreatedAt FROM [dbo].[RawData] WHERE Status = \'Pending\''
            queryTimeout:   '02:00:00'
          }
          dataset:       { referenceName: adfDatasetRawData.name, type: 'DatasetReference' }
          firstRowOnly:  false
        }
      }
      {
        name: 'ForEachPendingRecord'
        type: 'ForEach'
        dependsOn: [{ activity: 'LookupPendingRecords', dependencyConditions: ['Succeeded'] }]
        typeProperties: {
          isSequential: false
          batchCount:   10
          items: { value: '@activity(\'LookupPendingRecords\').output.value', type: 'Expression' }
          activities: [
            {
              name: 'CallPublisherFunction'
              type: 'WebActivity'
              typeProperties: {
                url:    { value: '@concat(pipeline().parameters.PublisherFunctionBaseUrl, \'/api/publish\')', type: 'Expression' }
                method: 'POST'
                headers: {
                  'Content-Type':    'application/json'
                  'x-functions-key': { value: '@pipeline().parameters.PublisherFunctionKey', type: 'Expression' }
                }
                body: {
                  value: '@json(concat(\'{"id":"\', item().DataId, \'","source":"ADF-ETL","data":{\', \'\"rawPayload\":\"\', item().RawPayload, \'\"}}\')'  )
                  type:  'Expression'
                }
              }
            }
            {
              name: 'MarkProcessed'
              type: 'SqlServerStoredProcedure'
              dependsOn: [{ activity: 'CallPublisherFunction', dependencyConditions: ['Succeeded'] }]
              linkedServiceName: { referenceName: adfLinkedServiceSQL.name, type: 'LinkedServiceReference' }
              typeProperties: {
                storedProcedureName: '[dbo].[usp_MarkRawDataProcessed]'
                storedProcedureParameters: {
                  DataId: { value: { value: '@item().DataId', type: 'Expression' }, type: 'String' }
                  Status: { value: 'Published', type: 'String' }
                }
              }
            }
            {
              name: 'MarkFailed'
              type: 'SqlServerStoredProcedure'
              dependsOn: [{ activity: 'CallPublisherFunction', dependencyConditions: ['Failed'] }]
              linkedServiceName: { referenceName: adfLinkedServiceSQL.name, type: 'LinkedServiceReference' }
              typeProperties: {
                storedProcedureName: '[dbo].[usp_MarkRawDataProcessed]'
                storedProcedureParameters: {
                  DataId: { value: { value: '@item().DataId', type: 'Expression' }, type: 'String' }
                  Status: { value: 'Failed', type: 'String' }
                }
              }
            }
          ]
        }
      }
    ]
  }
}

// ---------------------------------------------------------------------------
// Outputs
// ---------------------------------------------------------------------------
output publisherFunctionUrl     string = 'https://${publisherFunctionApp.properties.defaultHostName}/api/publish'
output subscriberFunctionName   string = subscriberFunctionApp.name
output sqlServerFqdn            string = sqlServer.properties.fullyQualifiedDomainName
output storageAccountName       string = storageAccount.name
output dataFactoryName          string = dataFactory.name
output appInsightsKey           string = appInsights.properties.InstrumentationKey
output keyVaultUri              string = keyVault.properties.vaultUri
