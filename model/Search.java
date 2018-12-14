package model;



import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import static model.Config.ENCODING;
import static model.Config.BUFFER;






public class Search  implements Callable<HashMap<File, ArrayList<Long>>> {

    public static final HashMap<File,String>positions = new HashMap<>();
    public static final HashMap<File,ArrayList<Long>>positionsAll = new HashMap<>();

    private final Logger LOGGER = Logger.getLogger(Search.class.getSimpleName());


    /*
    method for searching the 1st occurrence of the search text, return boolean
    */
    public boolean booleanSearchText(File file, String text) {
        LOGGER.info("method booleanSearchText started " + file.getName());

        int buffer = BUFFER;

        byte[] textInBytes = text.getBytes();

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {

            byte[] fileContent = new byte[buffer];
            for (long position = 0; position < file.length();
                 position += buffer - textInBytes.length) {
                randomAccessFile.seek(position);
                randomAccessFile.read(fileContent);
                if (new String(fileContent, ENCODING).indexOf(text) >= 0) {
                    return true;
                }
            }


        } catch (IOException ex) {
            LOGGER.warning("method booleanSearchTex with " + file.getName() + " Exception " + ex);
        }

        return false;
    }

    /*
    method to find the 1st occurrence of the search text, return String
    */
    public String searchString(File file, String text) {
        LOGGER.info("method searchString started " + file.getName());

        int buffer = BUFFER;


        byte[] textInBytes = text.getBytes();
        String s = "";

        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            byte[] fileContent = new byte[buffer];

            for (long position = 0; position < file.length(); position += buffer - textInBytes.length) {

                fileInputStream.read(fileContent);
                s = new String(fileContent, ENCODING);
                if (s.indexOf(text) >= 0) {

                    return s;
                }

            }
        } catch (IOException ex) {
            LOGGER.warning("searchString " + file.getName() + " Exception " + ex);

        }

        return s;
    }

    /*
    method for searching all positions of the search text in files
    */
    private ArrayList<Long> futureSearchString(File file, String text) {

        ArrayList<Long> longs = new ArrayList<>();

        LOGGER.info("method futureSearchString started " + file.getName());

        final int buffer = BUFFER;



        byte[] textInBytes = text.getBytes();
        String s = "";

        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            byte[] fileContent = new byte[buffer];

            for (long position = 0; position < file.length(); position += buffer - textInBytes.length) {

                fileInputStream.read(fileContent);
                s = new String(fileContent, ENCODING);

                if (s.indexOf(text) >= 0) {
                    longs.add(position);
                }

            }

        } catch (IOException ex) {

            LOGGER.warning("futureSearchString " + file.getName() + " Exception " + ex);

        }

        return longs;

    }

    public byte[] readSomeDataFromFile(File file, Long pos, int buff) throws IOException {

        RandomAccessFile fileAF = new RandomAccessFile(file, "r");
        fileAF.seek(pos);
        byte[] byteToRead = new byte[buff];
        fileAF.read(byteToRead);
        fileAF.close();
        return byteToRead;
    }

    /*
    method that defines the work of the future thread
    */
    @Override
    public HashMap<File, ArrayList<Long>> call() {
        LOGGER.info("Thread future start ");

        for (Map.Entry entry : positions.entrySet()) {
            File file = (File) entry.getKey();
            String text = (String) entry.getValue();
            ArrayList<Long> list = futureSearchString(file, text);
            positionsAll.put(file, list);

        }

        LOGGER.info("Thread future done ");


        return null;
    }

    }



