CREATE TABLE Movies (
  id DECIMAL,
  name STRING,
  nominatedToOscar BOOLEAN,
  directorId DECIMAL,
  PRIMARY KEY (id) NOT ENFORCED
) WITH (
  'connector' = 'upsert-kafka',
  'topic' = 'movies',
  'properties.bootstrap.servers' = 'localhost:9092',
  -- This connector requires both a key and value format
  -- where the key fields are derived from the PRIMARY KEY constraint
  'key.format' = 'json',
  'value.format' = 'json'
)