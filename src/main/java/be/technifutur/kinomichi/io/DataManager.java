package be.technifutur.kinomichi.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static be.technifutur.kinomichi.util.ConsoleUtil.printError;
import static be.technifutur.kinomichi.util.ConsoleUtil.printSuccess;

public class DataManager {
    private final String basePath;

    public DataManager(String basePath) {
        this.basePath = basePath;
        if (new File(basePath).mkdirs()) {
            printSuccess("Création du répertoire de sauvegarde.");
        }
    }

    public <T> void save(List<T> data, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(data);
        } catch (IOException e) {
            printError("Erreur lors de la sauvegarde !");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> load(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
