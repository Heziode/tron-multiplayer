package fr.unicaen.info.l3.tron.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for saving information to a file
 */
public final class SaveData {

    /**
     * Private constructor to prevent instantiation
     */
    private SaveData() {}

    /**
     * Save the Dot formatted tree to a file
     *
     * @param path Path and filename
     * @param data Data to write to file
     */
    public static void saveInFile(String path, String data) {
        try {
            Path pathToFile = Paths.get(path);
            Files.createDirectories(pathToFile.getParent());

            BufferedWriter file = new BufferedWriter(new FileWriter(path));
            file.write(data);
            file.newLine();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
