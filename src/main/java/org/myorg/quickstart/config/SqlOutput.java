package org.myorg.quickstart.config;

/**
 * Specifies how the output of the SQL query should be emitted.
 */
public enum SqlOutput {
    SQL_TABLE,
    RETRACT_STREAM,
    UPSERT_STREAM
}
