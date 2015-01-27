package org.priorityhealth.stab.pdiff.service;

import java.io.*;

/**
 * File manipulation service.
 */
public class TextFileService {
    /**
     * Save the contents of a string to a file.
     *
     * @param content   Content of file.
     * @param path      Path to file.
     */
    public static void saveStringToFile(String content, String path){
        File file = new File(path);
        FileWriter fileWriter = null;
        try {
            File parent = file.getParentFile();
            if(!parent.exists() && !parent.mkdirs()){
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
        } catch (IOException ex) {
            LogService.Info("TextFileService", "IOException: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException ex) {
                    // Do nothing.
                }
            }
        }
    }

    /**
     * Read project resource text file.
     *
     * @param object Object from project
     * @param path path to resource
     * @return resource contents
     */
    public static String readTextResource(Object object, String path) {
        LogService.Info(TextFileService.class, "Loading: " + path);
        InputStream inputStream = object.getClass().getResourceAsStream(path);
        return readTextFromInputStream(inputStream);
    }

    /**
     *
     * @param inputStream Text input stream
     * @return Text from file
     */
    public static String readTextFromInputStream(InputStream inputStream) {
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try {
            if (inputStream == null) {
                throw new IOException("Input Stream is null.");
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            LogService.Info("TextFileService", "Loaded text from input stream.");
        } catch (IOException ex) {
            LogService.Info("TextFileService", "IOException: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // Do nothing.
                }
            }
        }

        return stringBuilder.toString();
    }

    /**
     *
     * @param path Source path
     * @return InputStream
     */
    public static InputStream createInputStreamFromFile(String path) {
        File initialFile = new File(path);
        InputStream result = null;
        try {
            result = new FileInputStream(initialFile);
        } catch (IOException ex) {
            LogService.Info("TextFileService", "IOException: " + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param path Source path
     * @return Document text
     */
    public static String readTextFromFile(String path) {
        File file = new File(path);
        FileReader fileReader = null;
        String result = null;
        try {
            fileReader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            fileReader.read(chars);
            result = new String(chars);
        } catch (IOException ex) {
            LogService.Info("TextFileService", "IOException: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    // Do nothing.
                }
            }
        }
        return result;
    }
}
