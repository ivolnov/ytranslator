package com.ivolnov.ytranslator.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility method for reading json request string from file.
 * @see <a href="http://stackoverflow.com/a/13357785/4003403">stackoverflow</a>
 */
public class JsonFileReader {

    private static String convertStreamToString(InputStream is) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        final StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    /**
     *
     * @param filename a name of json file from the resources directory.
     * @return a string with file content.
     */
    public static String getStringFromFile (String filename) {
        String ret;

        try {
            final ClassLoader cl = JsonFileReader.class.getClassLoader();
            final InputStream is = cl.getResource(filename).openStream();
            ret = convertStreamToString(is);
            is.close();
        } catch (IOException e) {
            ret = e.getMessage();
        }
        return ret;
    }
}
