package project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class FileUtils {

    public static void deleteDirectory(File file) throws IOException{
        if (file != null && file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteDirectory(f);
                    } else {
                        f.delete();
                    }
                }
            }
            file.delete();
        }
    }

    public static void addLineToFile(String s, File file) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
        bw.write(s);
        bw.newLine();
        bw.close();
    }
}
