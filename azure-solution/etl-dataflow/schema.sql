-- =============================================================================
-- Azure SQL Database Schema
-- Database: AzureSolutionDB
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. RawData  – source table read by the ADF ETL pipeline
-- -----------------------------------------------------------------------------
CREATE TABLE [dbo].[RawData]
(
    [Id]         INT IDENTITY(1,1) PRIMARY KEY,
    [DataId]     NVARCHAR(256)  NOT NULL UNIQUE,
    [RawPayload] NVARCHAR(MAX)  NOT NULL,                -- raw JSON from any upstream source
    [Status]     NVARCHAR(50)   NOT NULL DEFAULT 'Pending', -- Pending | Published | Failed
    [CreatedAt]  DATETIME2(7)   NOT NULL DEFAULT SYSUTCDATETIME(),
    [UpdatedAt]  DATETIME2(7)   NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE INDEX IX_RawData_Status ON [dbo].[RawData] ([Status]) INCLUDE ([DataId]);

-- -----------------------------------------------------------------------------
-- 2. TransformedData  – written by ADF Data Flow after transformation
-- -----------------------------------------------------------------------------
CREATE TABLE [dbo].[TransformedData]
(
    [Id]             INT IDENTITY(1,1) PRIMARY KEY,
    [DataId]         NVARCHAR(256)  NOT NULL UNIQUE,
    [RawPayload]     NVARCHAR(MAX)  NOT NULL,
    [ProcessingKey]  NVARCHAR(512)  NOT NULL,
    [ETLVersion]     NVARCHAR(20)   NOT NULL,
    [ETLProcessedAt] DATETIME2(7)   NOT NULL,
    [CreatedAt]      DATETIME2(7)   NOT NULL DEFAULT SYSUTCDATETIME()
);

-- -----------------------------------------------------------------------------
-- 3. DataRecords  – written by the Subscriber Function after dequeuing
-- -----------------------------------------------------------------------------
CREATE TABLE [dbo].[DataRecords]
(
    [Id]          INT IDENTITY(1,1) PRIMARY KEY,
    [DataId]      NVARCHAR(256)  NOT NULL UNIQUE,
    [MessageId]   NVARCHAR(256)  NOT NULL,
    [Source]      NVARCHAR(256)  NOT NULL,
    [Payload]     NVARCHAR(MAX)  NOT NULL,               -- full JSON envelope payload
    [EnqueuedAt]  DATETIME2(7)   NOT NULL,
    [ProcessedAt] DATETIME2(7)   NOT NULL,
    [Status]      NVARCHAR(50)   NOT NULL DEFAULT 'Processed'
);

CREATE INDEX IX_DataRecords_MessageId ON [dbo].[DataRecords] ([MessageId]);
CREATE INDEX IX_DataRecords_ProcessedAt ON [dbo].[DataRecords] ([ProcessedAt] DESC);

-- -----------------------------------------------------------------------------
-- 4. Stored procedure – called by ADF after each successful / failed publish
-- -----------------------------------------------------------------------------
CREATE OR ALTER PROCEDURE [dbo].[usp_MarkRawDataProcessed]
    @DataId NVARCHAR(256),
    @Status NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE [dbo].[RawData]
    SET    [Status]    = @Status,
           [UpdatedAt] = SYSUTCDATETIME()
    WHERE  [DataId]    = @DataId;
END;
GO

-- -----------------------------------------------------------------------------
-- 5. Seed: sample pending row for local testing
-- -----------------------------------------------------------------------------
INSERT INTO [dbo].[RawData] ([DataId], [RawPayload], [Status])
VALUES
    ('item-001', '{"product":"Widget A","qty":10,"price":9.99}', 'Pending'),
    ('item-002', '{"product":"Widget B","qty":5, "price":19.99}', 'Pending');
