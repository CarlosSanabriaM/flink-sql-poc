package org.myorg.quickstart.udfs.scalar;

import org.apache.flink.table.functions.ScalarFunction;

// https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/functions/udfs.html#scalar-functions
@SuppressWarnings("unused")
public class GetDirectorsMoviesIdFunction extends ScalarFunction {
    public String eval(String directorId, String movieId) {
        return directorId + ":" + movieId;
    }
}
