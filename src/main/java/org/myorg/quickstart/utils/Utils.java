package org.myorg.quickstart.utils;

import org.apache.flink.api.java.utils.ParameterTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Utils {

    /**
     * Returns the content of a file under the resources/ folder.
     *
     * @param filePathRelativeToResourcesFolder Path relative to the location of the resources/ folder.
     */
    public static String getResourcesFileContent(String filePathRelativeToResourcesFolder) throws IOException {
        final String basePath = "src/main/resources/";
        final File file = new File(basePath + filePathRelativeToResourcesFolder);
        final String fileAbsolutePath = file.getAbsolutePath();
        return new String(Files.readAllBytes(Paths.get(fileAbsolutePath)));
    }

    //region Config file

    /**
     * Obtains from the "--conf" parameter of the CLI call the path to the configuration file.
     *
     * @param args Arguments passed to the Java application.
     * @return The path to the configuration file, as passed to the "--conf" parameter.
     * If "--conf" parameter has no value, an IllegalArgumentException is thrown.
     */
    public static String getPathToConfigurationFileFromArgs(String[] args) {
        // Obtain arguments passed to the command line invocation
        ParameterTool parameters = ParameterTool.fromArgs(args);
        // Obtain path to the configuration file from the arguments
        String pathToConfigurationFile = parameters.get("conf");
        if (pathToConfigurationFile == null) {
            throw new IllegalArgumentException(
                    "The program must be called with the path to the configuration file: " +
                            "'flink run <jar-file> --conf <path-to-conf.properties>'"
            );
        }
        return pathToConfigurationFile;
    }

    /**
     * Creates a Properties object with all the properties specified in a .properties file.
     *
     * @param pathToConfigurationFile Path to the .properties file.
     * @return a Properties object with all the properties specified in the .properties file.
     */
    public static Properties getPropertiesFromPropertiesFile(String pathToConfigurationFile) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(pathToConfigurationFile));
        return properties;
    }

    //endregion

}
