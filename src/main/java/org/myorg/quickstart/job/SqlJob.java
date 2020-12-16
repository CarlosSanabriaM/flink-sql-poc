package org.myorg.quickstart.job;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.myorg.quickstart.utils.Utils;

import java.time.Duration;

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
		//region Set up environments
		// Set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// Specify Blink Planner
		// https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/common.html#main-differences-between-the-two-planners
		EnvironmentSettings envSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();

		// Create a TableEnvironment
		// https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/common.html#create-a-tableenvironment
		final TableEnvironment tableEnv = StreamTableEnvironment.create(env, envSettings);
		//endregion

		//region Optionally specify TableConfig
		// All configuration: https://ci.apache.org/projects/flink/flink-docs-stable/dev/table/config.html#configuration
		// See docs/interesting-table-config-options.md for information about some interesting TableConfig options

		// access flink configuration
		TableConfig tableConfig = tableEnv.getConfig();
		Configuration configuration = tableConfig.getConfiguration();
		// Set common or important configuration options (TableConfig provides getters and setters)
		tableConfig.setIdleStateRetention(Duration.ZERO);
		// Set low-level key-value options
		configuration.setString("table.exec.mini-batch.enabled", "true");
		configuration.setString("table.exec.mini-batch.allow-latency", "5 s");
		configuration.setString("table.exec.mini-batch.size", "5000");
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


		// Create Temporary Upsert Kafka tables
		// TODO: There is no TEMPORARY keyword in CREATE TABLE SQL statement (is this OK?)
		// https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/sql/create.html#run-a-create-statement
		// https://ci.apache.org/projects/flink/flink-docs-release-1.12/dev/table/connectors/upsert-kafka.html#full-example

		// Movies Table
		tableEnv.executeSql(Utils.getResourcesFileContent("ddl/create-table-movies.sql"));
		// Directors Table
		tableEnv.executeSql(Utils.getResourcesFileContent("ddl/create-table-directors.sql"));
		//endregion

		//region Run SQL Query
		// Run a SQL query on the Table and retrieve the result as a new Table
		Table queryTable = tableEnv.sqlQuery(Utils.getResourcesFileContent("dml/join-query.sql"));
		TableResult queryTableResult = queryTable.execute();

		// Print the results
		queryTableResult.print();
		//endregion

		// Execute Flink program
		env.execute("Flink SQL Job");
	}
}
