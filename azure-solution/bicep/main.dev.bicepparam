// bicep/main.dev.bicepparam
using './main.bicep'

param environment       = 'dev'
param location          = 'eastus'
param sqlAdminLogin     = 'sqladmin'
param sqlAdminPassword  = '<replace-with-secure-password-or-use-AKV-reference>'
param adfPrincipalId    = ''   // fill after first deploy: az datafactory show --name ... --query identity.principalId
