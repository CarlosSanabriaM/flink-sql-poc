package org.myorg.quickstart.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

}
