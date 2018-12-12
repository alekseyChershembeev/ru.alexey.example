package model;







import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.ArrayList;
import java.util.HashMap;


import java.util.concurrent.Callable;

import java.util.logging.Logger;

import static model.Config.ENCODING;
import static model.Config.BUFFER;

//здесь стоит кодировка по умолчанию стандартная под windows, чтобы можно было использовать кирилицу и латиницу .Если файлы в другой кодировке, то надо менять кодировку filecontent

//надо обязательно чтобы была выбрана вкладка из treeview и tab, тогда работает поиск по файлу


public class Search  implements Callable<HashMap<File, ArrayList<Long>>>{
    private  final Logger LOGGER =Logger.getLogger(Search.class.getSimpleName());


    public   boolean booleanSearchText(File file, String text) {
        LOGGER.info("method booleanSearchTex started " +file.getName());


         int buffer = BUFFER;


        byte[] textInBytes = text.getBytes();



        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")){

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
            LOGGER.warning("method booleanSearchTex with " +file.getName()+" Exception "+ex);
        }

        return false;
    }


    private static long positions =0;//статик не должно быть в релизе





    public  String searchStringTextForward(File file, String text) {
        LOGGER.info("method searchStringTextForward started " +file.getName());


         int buffer = BUFFER;


        double countCurrentFile = (Math.ceil((double)(file.length())/buffer));

        int countBuffers =  (int)countCurrentFile;//количество буферов в файле, включая неполный

        int count = 0;//сколько буфров просмотрели перед тем как найти нужный

        byte[] textInBytes = text.getBytes();
        String s = "";


        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")){

            byte[] fileContent = new byte[buffer];

            for (  long position = positions; position < file.length(); position += buffer - textInBytes.length) {
                count++;
                randomAccessFile.seek(position);
                randomAccessFile.read(fileContent);
                s = new String(fileContent, ENCODING);
                if (s.indexOf(text) >= 0) {


                    if (positions<countBuffers) {
                        positions += count * buffer;

                        return s;
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.warning("searchStringTextForward " +file.getName()+" Exception "+ex);
        }


        return s;
    }
    public  String searchStringTextBack(File file, String text) {
        LOGGER.info("method searchStringTextBack started "+file.getName());
         int buffer = BUFFER;


        int count = 0;//сколько буфров просмотрели перед тем как найти нужный

        byte[] textInBytes = text.getBytes();
        String s = "";


        try (RandomAccessFile randomAccessFile  = new RandomAccessFile(file, "r") ){

            byte[] fileContent = new byte[buffer];

            for (  long position = positions; position < file.length(); position += buffer - textInBytes.length) {
                count++;
                randomAccessFile.seek(position);
                randomAccessFile.read(fileContent);
                s = new String(fileContent, ENCODING);
                if (s.indexOf(text) >= 0) {
                    if (positions>0) {
                        positions -= count * buffer;



                        return s;
                    }

                }
            }
        } catch (IOException ex) {
            LOGGER.warning("searchStringTextBack " +file.getName()+ " Exception "+ex);

        }


        return s;
    }
    public  String searchString(File file, String text) {
        LOGGER.info("method searchString started "+file.getName());
         int buffer = BUFFER;


      //  double countCurrentFile = (Math.ceil((double)(file.length())/buffer));



        int count = 0;//сколько буфров просмотрели перед тем как найти нужный

        byte[] textInBytes = text.getBytes();
        String s = "";

        try(FileInputStream fileInputStream  = new FileInputStream(file) ) {

            byte[] fileContent = new byte[buffer];

            for (  long position = 0; position < file.length(); position += buffer - textInBytes.length) {
                count++;

                fileInputStream.read(fileContent);
                s = new String(fileContent, ENCODING);
                if (s.indexOf(text) >= 0) {

                      return s;
                }

            }
        } catch (IOException ex) {
            LOGGER.warning("searchString " +file.getName()+" Exception "+ex);

        }


        return s;
    }


    public ArrayList<Long>  futureSearchString(File file, String text) {

        ArrayList<Long>longs = new ArrayList<>();
        LOGGER.info("method futureSearchString started "+file.getName());
        int buffer = BUFFER;


       // double countCurrentFile = (Math.ceil((double)(file.length())/buffer));



        int count = 0;//сколько буфров просмотрели перед тем как найти нужный

        byte[] textInBytes = text.getBytes();
        String s = "";

        try(FileInputStream fileInputStream  = new FileInputStream(file) ) {

            byte[] fileContent = new byte[buffer];

            for (  long position = 0; position < file.length(); position += buffer - textInBytes.length) {
                count++;


                fileInputStream.read(fileContent);
                s = new String(fileContent, ENCODING);
                if (s.indexOf(text) >= 0) {
                    longs.add(position);


                }

            }

        } catch (IOException ex) {
            LOGGER.warning("futureSearchString " +file.getName()+" Exception "+ex);

        }

        return longs;

    }
    @Override

    public HashMap<File, ArrayList<Long>> call() {

//здесь должен был быть новый поток, но так и не вышло XD







        return null;
    }




}



