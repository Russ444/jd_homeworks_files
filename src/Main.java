import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {

        String gamesPath = "C://Games";


        File gameDir = new File(gamesPath);
        File srcDir = new File(gameDir, "src");
        File resDir = new File(gameDir, "res");
        File savegamesDir = new File(gameDir, "savegames");
        File tempDir = new File(gameDir, "temp");

        File mainDir = new File(srcDir, "main");
        File testDir = new File(srcDir, "test");
        File drawablesDir = new File(resDir, "drawables");
        File vectorsDir = new File(resDir, "vectors");
        File iconsDir = new File(resDir, "icons");

        File mainFile = new File(mainDir, "Main.java");
        File utilsFile = new File(mainDir, "Utils.java");
        File tempFile = new File(tempDir, "temp.txt");

        StringBuilder log = new StringBuilder();

        try {
            srcDir.mkdirs();
            resDir.mkdirs();
            savegamesDir.mkdirs();
            tempDir.mkdirs();
            mainDir.mkdirs();
            testDir.mkdirs();
            drawablesDir.mkdirs();
            vectorsDir.mkdirs();
            iconsDir.mkdirs();
            mainFile.createNewFile();
            utilsFile.createNewFile();
            tempFile.createNewFile();

            log.append("Все директории и файлы успешно созданы");
        } catch (IOException e) {
            e.printStackTrace();
            log.append("При создании директории и файлов произошла ошибка: ").append(e.getMessage());
        } finally {
            writeToTempFile(tempFile, log.toString());
        }

        GameProgress gameSave1 = new GameProgress(100, 100, 2, 103.2);
        GameProgress gameSave2 = new GameProgress(90, 90, 3, 300.6);
        GameProgress gameSave3 = new GameProgress(76, 77, 5, 763.3);

        String pathSave1 = savegamesDir.getPath() + File.separator + "save1.dat";
        String pathSave2 = savegamesDir.getPath() + File.separator + "save2.dat";
        String pathSave3 = savegamesDir.getPath() + File.separator + "save3.dat";

        try {
            saveGame(pathSave1, gameSave1);
            saveGame(pathSave2, gameSave2);
            saveGame(pathSave3, gameSave3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String zipFilePath = savegamesDir.getAbsolutePath() + File.separator + "zip.zip";
        List<String> filesToZip = List.of(pathSave1, pathSave2, pathSave3);

        try {
            zipFiles(zipFilePath, filesToZip);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File delSave1 = new File(pathSave1);
        File delSave2 = new File(pathSave2);
        File delSave3 = new File(pathSave3);

        delSave1.delete();
        delSave2.delete();
        delSave3.delete();

        openZip(zipFilePath, savegamesDir.getAbsolutePath());

        try {
            gameSave1 = openProgress(pathSave1);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println(gameSave1.toString());
    }

    public static void writeToTempFile(File file, String message) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveGame(String path, GameProgress gameSave) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameSave);
        }
    }

    public static void zipFiles(String pathFileZip, List<String> listFiles) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(pathFileZip);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            for (String file : listFiles) {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    File fileToZip = new File(file);
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fileInputStream.read(buffer)) > 0) {
                        zipOut.write(buffer, 0, length);
                    }
                    zipOut.closeEntry();
                }
            }
        }
    }

    public static void openZip(String pathZip, String pathSavegames) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zin = new ZipInputStream(new
                FileInputStream(pathZip))) {
            ZipEntry zipEntry = zin.getNextEntry();

            while (zipEntry != null) {
                String entryName = zipEntry.getName();
                File newFile = new File(pathSavegames + File.separator + entryName);
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int lenght;
                    while ((lenght = zin.read(buffer)) > 0) {
                        fos.write(buffer, 0, lenght);
                    }
                }
                zipEntry = zin.getNextEntry();
            }
        }
    }

    public static GameProgress openProgress(String pathSaveGame) throws IOException, ClassNotFoundException {

        try (FileInputStream fis = new FileInputStream(pathSaveGame);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (GameProgress) ois.readObject();
        }
    }
}