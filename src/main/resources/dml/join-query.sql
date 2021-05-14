SELECT
    GetDirectorsMoviesIdFunction(Directors.id, Movies.id) as id,
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