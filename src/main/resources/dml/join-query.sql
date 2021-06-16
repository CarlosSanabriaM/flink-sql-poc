SELECT
  -- The id has the following format: "directorId:movieId"
  -- There are 2 ways of obtaining the id in that format:
  --   1. With a UDF (user defined function):
  GetDirectorsMoviesIdFunction(Directors.id, Movies.id) as id,  -- used here only to show how they work
  --   2. With a system (built-in) function:
  -- CONCAT(Directors.id, ':', Movies.id) as id,  -- better solution. no need to code the function.

  -- Other fields
  Directors.name AS director,
  Movies.name AS movie,
  Movies.nominatedToOscar AS nominatedToOscar, -- TODO: Remove?

  -- Metadata
  --  The creation of ROWs is positional. The 1st element is "eventTimestamp" and the 2nd element is "updatedBy".
  --  https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/functions/systemFunctions.html#value-construction-functions
  ROW(
    -- eventTimestamp (max timestamp between the movie and the director that generated the current event)
    --  Example usage of the CASE built-in function
    CASE
      WHEN Movies.eventTimestamp > Directors.eventTimestamp THEN Movies.eventTimestamp
      ELSE Directors.eventTimestamp
    END,

    -- updatedBy (the table whose update generated this event)
    --  Example usage of the IF built-in function
    IF(
      Movies.eventTimestamp > Directors.eventTimestamp, -- condition
      Movies.metadata.tableName, -- return this if the condition is true
      Directors.metadata.tableName -- return this if not
    )
  ) AS metadata
FROM
  Movies
  INNER JOIN
    Directors
    ON Movies.directorId = Directors.id
WHERE
  Movies.nominatedToOscar = TRUE