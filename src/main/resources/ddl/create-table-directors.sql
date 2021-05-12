CREATE TABLE Directors (
  id STRING,
  name STRING,
  PRIMARY KEY (id) NOT ENFORCED
) WITH (
  'connector' = 'upsert-kafka',
  'topic' = 'directors',
  'properties.bootstrap.servers' = 'localhost:9092',
  'properties.group.id' = 'flink-sql-poc',
  -- This connector requires both a key and value format
  -- where the key fields are derived from the PRIMARY KEY constraint
  'key.format' = 'raw',
  'value.format' = 'json'
)