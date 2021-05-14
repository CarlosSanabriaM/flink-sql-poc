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
    Movies.nominatedToOscar AS nominatedToOscar
FROM
    Movies
    INNER JOIN
        Directors
        ON Movies.directorId = Directors.id
WHERE
    Movies.nominatedToOscar = TRUE