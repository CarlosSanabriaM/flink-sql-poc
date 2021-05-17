CREATE TABLE DirectorsMovies (
  id STRING,
  director STRING,
  movie STRING,
  nominatedToOscar BOOLEAN,
  -- ENFORCED/NOT ENFORCED controls if the constraint checks are performed on the incoming/outgoing data.
  -- Flink does not own the data. Therefore, the only supported mode is the NOT ENFORCED mode.
  PRIMARY KEY (id) NOT ENFORCED
) WITH (
  'connector' = 'upsert-kafka',
  'topic' = 'directors-movies',
  'properties.bootstrap.servers' = 'localhost:9092',
  'properties.group.id' = 'flink-sql-poc',
  -- This connector requires both a key and value format
  -- where the key fields are derived from the PRIMARY KEY constraint
  'key.format' = 'raw',
  'value.format' = 'json'
)