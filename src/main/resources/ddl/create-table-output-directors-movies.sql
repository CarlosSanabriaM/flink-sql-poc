CREATE TABLE DirectorsMovies (
  id STRING,
  director STRING,
  movie STRING,
  nominatedToOscar BOOLEAN,

  -- Metadata
  metadata ROW<
    eventTimestamp TIMESTAMP(3), -- max timestamp between the movie and the director that generated the current event
    updatedBy STRING -- the table whose update generated this event
  >,

  -- PK
  --  ENFORCED/NOT ENFORCED controls if the constraint checks are performed on the incoming/outgoing data.
  --  Flink does not own the data. Therefore, the only supported mode is the NOT ENFORCED mode.
  PRIMARY KEY (id) NOT ENFORCED
) WITH (
  'connector' = 'upsert-kafka',
  'topic' = 'directors-movies',
  'properties.bootstrap.servers' = 'localhost:9092',
  'properties.group.id' = 'flink-sql-poc',
  -- This connector requires both a key and value format
  -- where the key fields are derived from the PRIMARY KEY constraint
  'key.format' = 'raw',
  'value.format' = 'json',
  'value.json.timestamp-format.standard' = 'ISO-8601' -- "yyyy-MM-ddTHH:mm:ss.s{precision}'Z'" format
)