package org.myorg.quickstart.config;

import org.aeonbits.owner.Config;

public interface JobConfig extends Config {
    @Key("convert-sql-output-to-datastream")
    boolean convertSqlOutputToDatastream();
}
