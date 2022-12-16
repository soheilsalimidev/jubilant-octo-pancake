package com.file.filemanager.Part1;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Part1 {
    List<File> files = new ArrayList<>();
    List<File> dir = new ArrayList<>();
    ArrayList<FileModel> filesInfoList = new ArrayList<FileModel>();
    String path = "C:\\Users\\User\\Desktop\\Folder1\\";
    takeOutFiles(files, filesInfoList, dir, path);
    groupFile(files, dir, path);
}

public static void listf(String directoryName, List<File> files, List<File> dir) {
    File directory = new File(directoryName);
    File[] fList = directory.listFiles();
    if (fList != null) {
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files, dir);
                dir.add(file);
            }
        }
    }
}

public static void takeOutFiles(List<File> files, ArrayList<FileModel> filesInfoList, List<File> dir, String path) throws IOException {
    listf("C:\\Users\\User\\Desktop\\Folder1", files, dir);
    File rootFile = new File(path + "root\\");
    if (!rootFile.exists())
        rootFile.mkdir();
    for (File file : files) {
        filesInfoList.add(new FileModel(file.getName().split("\\.")[0], Integer.parseInt(file.getName().split("\\.")[1]), file.getName().split("\\.")[2]));
        Files.copy(file.toPath(), (new File(path + "root\\" + file.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
        file.delete();
    }
    for (File dirFile : dir) {
        dirFile.delete();
    }
    for (File file : Objects.requireNonNull(rootFile.listFiles())) {
        Files.copy(file.toPath(), (new File(path + file.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
        file.delete();
    }
    rootFile.delete();
}

public static void addFile(String name, String date, String format, List<File> files, ArrayList<FileModel> filesInfoList) throws IOException {
    files.add(new File("C:\\Users\\User\\Desktop\\Folder1\\" + name + "." + date + "." + format));
    filesInfoList.add(new FileModel(name, Integer.parseInt(date), format));
    new File("C:\\Users\\User\\Desktop\\Folder1\\" + name + "." + date + "." + format).createNewFile();
}

public static void removeFile(String name, List<File> files, ArrayList<FileModel> filesInfoList) {
    int x = -1;
    for (FileModel file : filesInfoList) {
        if (file.getName().equals(name)) {
            x = filesInfoList.indexOf(file);
            files.get(filesInfoList.indexOf(file)).delete();
            filesInfoList.remove(file);
            break;
        }
    }
    if (x != -1)
        files.remove(x);
}
//sorting methods
public static Comparator<FileModel> sortByDate = (FileModel a, FileModel b) -> b.getdate() - a.getdate();
public static Comparator<FileModel> sortByFormat = Comparator.comparing(FileModel::getFormat);

public static void groupFile(List<File> files, List<File> dir, String path) throws IOException {
    for (File file : files) {
        File x = new File(path + file.getName().split("\\.")[1] + "\\");
        if (!x.exists()){
            x.mkdirs();
        }
        File y = new File(path + file.getName().split("\\.")[1] + "\\" + file.getName().split("\\.")[2] + "\\");
        if (!y.exists()){
            y.mkdirs();
        }
        String newPath = path + file.getName();
        Files.copy(Paths.get(newPath), (new File(path + file.getName().split("\\.")[1] + "\\" + file.getName().split("\\.")[2] + "\\" + file.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
        File newFile = new File(newPath);
        newFile.delete();
    }
}
