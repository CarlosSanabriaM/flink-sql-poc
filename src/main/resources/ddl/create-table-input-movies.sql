CREATE TABLE Movies (
  id STRING,
  name STRING,
  nominatedToOscar BOOLEAN,
  directorId STRING,

  -- Metadata
  --  Connector metadata
  --   Available metadata: https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/connectors/kafka.html#available-metadata
  eventTimestamp TIMESTAMP(3) METADATA FROM 'timestamp',
  --  Metadata object present in the incoming events
  metadata ROW<tableName STRING>, -- fields present in nested json objects must be typed as ROW<field1Name field1Type, ...>

  -- PK
  --  ENFORCED/NOT ENFORCED controls if the constraint checks are performed on the incoming/outgoing data.
  --  Flink does not own the data. Therefore, the only supported mode is the NOT ENFORCED mode.
  PRIMARY KEY (id) NOT ENFORCED
) WITH (
  'connector' = 'upsert-kafka',
  'topic' = 'movies',
  'properties.bootstrap.servers' = 'localhost:9092',
  'properties.group.id' = 'flink-sql-poc',
  -- This connector requires both a key and value format
  -- where the key fields are derived from the PRIMARY KEY constraint
  'key.format' = 'raw',
  'value.format' = 'json',
  'value.json.fail-on-missing-field' = 'true', -- Throw exception is a field is missing
  'value.json.timestamp-format.standard' = 'ISO-8601' -- "yyyy-MM-ddTHH:mm:ss.s{precision}'Z'" format
)