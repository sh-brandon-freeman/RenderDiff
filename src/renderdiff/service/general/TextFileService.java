package renderdiff.service.general;

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
            System.out.println("IOException: " + ex.getMessage());
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
        InputStream inputStream = object.getClass().getResourceAsStream(path);
        return readTextFromInputStream(inputStream);
    }

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
            System.out.println("Loaded text from input stream.");
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
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

    public static InputStream createInputStreamFromFile(String path) {
        File initialFile = new File(path);
        InputStream result = null;
        try {
            result = new FileInputStream(initialFile);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

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
            System.out.println("IOException: " + ex.getMessage());
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
