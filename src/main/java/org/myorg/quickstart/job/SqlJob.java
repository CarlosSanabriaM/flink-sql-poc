package org.myorg.quickstart.job;

import org.aeonbits.owner.ConfigFactory;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.myorg.quickstart.config.JobConfig;
import org.myorg.quickstart.model.DirectorsMovies;
import org.myorg.quickstart.operators.map.DirectorsSetMap;
import org.myorg.quickstart.udfs.scalar.GetDirectorsMoviesIdFunction;
import org.myorg.quickstart.utils.Utils;

import java.util.Properties;

/**
 * Flink SQL Job.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the pom.xml file (simply search for 'mainClass').
 */
public class SqlJob {

    public static void main(String[] args) throws Exception {
        //region Obtain configuration from properties file
        String pathToConfigurationFile = Utils.getPathToConfigurationFileFromArgs(args);
        Properties configurationProperties = Utils.getPropertiesFromPropertiesFile(pathToConfigurationFile);
        JobConfig jobConfig = ConfigFactory.create(JobConfig.class, configurationProperties);
        //endregion

        //region Set up environments
        // Set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Set parallelism to 1
        env.setParallelism(1);

        // Specify Blink Planner
        // https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/common.html#main-differences-between-the-two-planners
        EnvironmentSettings envSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();

        // Create a TableEnvironment
        // https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/common.html#create-a-tableenvironment
        final StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, envSettings);
        //endregion

        //region Optionally specify TableConfig
        // All configuration: https://ci.apache.org/projects/flink/flink-docs-stable/dev/table/config.html#configuration
        // See docs/interesting-table-config-options.md for information about some interesting TableConfig options

        // access flink configuration
        //TableConfig tableConfig = tableEnv.getConfig();
        //Configuration configuration = tableConfig.getConfiguration();
        // Set common or important configuration options (TableConfig provides getters and setters)
        //tableConfig.setIdleStateRetention(Duration.ZERO);
        // Set low-level key-value options
        //configuration.setString("table.exec.mini-batch.enabled", "true");
        //configuration.setString("table.exec.mini-batch.allow-latency", "5 s");
        //configuration.setString("table.exec.mini-batch.size", "5000");
        //endregion

        //region Define Catalog and Database (not needed)
        // A TableEnvironment maintains a map of catalogs of tables which are created with an identifier.
        // Each identifier consists of 3 parts: catalog name, database name and object name.
        //tableEnv.useCatalog("custom_catalog");
        //tableEnv.useDatabase("custom_database");
        //endregion

        //region Create Tables
        // Tables can be either virtual (VIEWS) or regular (TABLES).
        // * VIEWS can be created from an existing Table object, usually the result of a Table API or SQL query.
        // * TABLES describe external data, such as a file, database table, or message queue.

        // Tables may either be temporary, and tied to the lifecycle of a single Flink session, or permanent,
        // and visible across multiple Flink sessions and clusters.
        // * Temporary tables are always stored in memory and only exist for the duration of the Flink session they are created within.
        //   They are not bound to any catalog or database but can be created in the namespace of one.
        // https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/common.html#temporary-vs-permanent-tables


        // Create Temporary Upsert Kafka tables:

        // * Input tables
        // 		1. Movies Table
        tableEnv.executeSql(Utils.getResourcesFileContent("ddl/create-table-input-movies.sql"));
        // 		2. Directors Table
        tableEnv.executeSql(Utils.getResourcesFileContent("ddl/create-table-input-directors.sql"));

        // * Output tables
        // 		1. DirectorsMovies Table
        tableEnv.executeSql(Utils.getResourcesFileContent("ddl/create-table-output-directors-movies.sql"));
        //endregion

        //region Create UDFs
        tableEnv.createTemporarySystemFunction("GetDirectorsMoviesIdFunction", GetDirectorsMoviesIdFunction.class);
        //endregion

        //region Run SQL Query
        // Evaluate the SQL query and retrieve the result as a Table
        // This table can be converted into a DataStream
        Table queryTable = tableEnv.sqlQuery(Utils.getResourcesFileContent("dml/join-query.sql"));

        // Execute the query and insert the results in the output Table
        // This table is registered with the upsert-kafka connector, so events will be sent to Kafka
        queryTable.executeInsert("DirectorsMovies");

        // Execute the query and show the results in the stdout (OPTIONAL)
        TableResult queryTableResult = queryTable.execute();
        // Only print Table if DataStreams are NOT printed, because both cannot be printed at the same time
        if (!jobConfig.convertSqlOutputToDatastream())
            queryTableResult.print();

        // https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/streaming/dynamic_tables.html#table-to-stream-conversion
        // This implementation gives as a result a retract stream:
        // A dynamic table is converted into a retract stream by encoding:
        // * An INSERT change as add message (+I)
        // * A DELETE change as a retract message (-D)
        // * An UPDATE change as a retract message for the updated (previous) row (-U), and an additional message for the updating (new) row (+U)
        //
        // A dynamic table that is converted into a retract stream DOES NOT need a unique primary key,
        // because each update generates a retract message of the previous value for that event.
        //endregion

        //region Convert query Table into a Retract stream
        // https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/common.html#convert-a-table-into-a-datastream

        if (jobConfig.convertSqlOutputToDatastream()) {
            // Convert the Table into a retract DataStream of POJO.
            //   A retract stream of type X is a DataStream<Tuple2<Boolean, X>>.
            //   The boolean field indicates the type of the change.
            //   True is INSERT, false is DELETE.
            DataStream<Tuple2<Boolean, DirectorsMovies>> queryRetractStream =
                    tableEnv.toRetractStream(queryTable, DirectorsMovies.class);
            queryRetractStream.print("queryRetractStream");

            // Do some transformations on the DataStream, to demonstrate how both APIs can be easily integrated
            //   Based on the boolean field (f0)
            printCountOfDeleteEvents(queryRetractStream);
            //   Based on the POJO (f1)
            printTenetDirectors(queryRetractStream);
        }
        //endregion

        //region Convert query Table into an Upsert stream (NOT POSSIBLE)
        //
        // A dynamic table that is converted into an upsert stream requires a (possibly composite) unique key,
        // because UPDATES do not retract previous events and then create a new event with the new value.
        // UPDATES simply override the previous value of that unique key.
        //
        // A dynamic table with a unique key is transformed into a stream by encoding:
        // * INSERT and UPDATE changes as upsert messages (*U)
        // * DELETE changes as delete messages (-D)
        //
        // The stream consuming operator needs to be aware of the unique key attribute to apply messages correctly.
        // The main difference to a retract stream is that UPDATE changes are encoded with a single message and hence more efficient
        //
        // Only append and retract streams are supported when converting a dynamic table into a DataStream.
        //endregion

        // Execute Flink program
        env.execute("Flink SQL Job");
    }

    private static void printCountOfDeleteEvents(DataStream<Tuple2<Boolean, DirectorsMovies>> queryRetractStream) {
        queryRetractStream
                .filter(event -> !event.f0) // only let DELETE events pass the filter
                .map(event -> 1) // transform all events to the Integer 1
                .keyBy(event -> event) // group all events in the same node (all events have the same value (1))
                .sum(0) // sum the received integers (keeping the previous sum value in state)
                .print("numDeleteEventsStream");
    }

    private static void printTenetDirectors(DataStream<Tuple2<Boolean, DirectorsMovies>> queryRetractStream) {
        queryRetractStream
                .filter(event -> event.f0) // only let NON DELETE events pass the filter
                .map(event -> event.f1) // simplify the stream by keeping only the DirectorsMovies object
                .filter(event -> event.getMovie().equals("Tenet")) // keep only events of the movie "Tenet"
                .keyBy(DirectorsMovies::getMovie) // group all events in the same node (all events have the same movie (Tenet))
                .map(new DirectorsSetMap()) // use keyed state to store all the distinct directors of the movie
                .print("tenetDirectors");
    }

}
