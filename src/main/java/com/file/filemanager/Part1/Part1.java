package com.file.filemanager.Part1;

import com.file.filemanager.Models.FileModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Part1 {
    private final String rootFolderPath;
    private final List<File> files = new ArrayList<>();
    private final List<File> dir = new ArrayList<>();
    private final ArrayList<FileModel> filesInfoList = new ArrayList<>();

    public Part1(String rootFolderPath) {
        this.rootFolderPath = rootFolderPath;
    }

    public void listOfFiles(String directoryName) {
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    listOfFiles(file.getAbsolutePath());
                    dir.add(file);
                }
            }
        }
    }

    public void takeOutFiles(String path) throws IOException {
        listOfFiles(rootFolderPath);
        File rootFile = new File(path + "root\\");
        if (!rootFile.exists())
            rootFile.mkdir();
        for (File file : files) {
            filesInfoList.add(new FileModel(file.getName().split("\\.")[0], file.getName().split("\\.")[2], Integer.parseInt(file.getName().split("\\.")[1])));
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

    public void addFile(String name, String date, String format) throws IOException {
        files.add(new File(rootFolderPath + name + "." + date + "." + format));
        filesInfoList.add(new FileModel(name, format, Integer.parseInt(date)));
        new File(rootFolderPath + name + "." + date + "." + format).createNewFile();
    }

    public void removeFile(String name) {
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

    /**
     * @implSpec groupFile(files, dir, path);
     * public static Comparator<FileModel> sortByDate = (FileModel a, FileModel b) -> b.getdate() - a.getdate();
     * public static Comparator<FileModel> sortByFormat = Comparator.comparing(FileModel::getFormat);
     */
    public void groupFile(String path) throws IOException {
        for (File file : files) {
            File x = new File(path + file.getName().split("\\.")[1] + "\\");
            if (!x.exists()) x.mkdirs();
            File y = new File(path + file.getName().split("\\.")[1] + "\\" + file.getName().split("\\.")[2] + "\\");
            if (!y.exists()) y.mkdirs();
            String newPath = path + file.getName();
            Files.copy(Paths.get(newPath), (new File(path + file.getName().split("\\.")[1] + "\\" + file.getName().split("\\.")[2] + "\\" + file.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
            File newFile = new File(newPath);
            newFile.delete();
        }
    }

    public ArrayList<FileModel> getFilesInfoList() {
        return filesInfoList;
    }

    public void printMaxHeap(String rootFolderPath) throws FileNotFoundException {
        File rootFolder = new File(rootFolderPath);
        for (File folder : Objects.requireNonNull(rootFolder.listFiles())) {
            File textFolder = new File(rootFolderPath + "\\" + folder.getName() + "\\txt");
            if (textFolder.listFiles() == null)
                continue;
            for (File file : Objects.requireNonNull(textFolder.listFiles())) {
                Scanner readFile = new Scanner(file);
                String line = readFile.nextLine();
                ArrayList<Integer> arr = new ArrayList<>();
                for (int i = 0; i < line.split("\\,").length; i++) {
                    arr.add(Integer.valueOf(line.split("\\,")[i]));
                }
                System.out.print(file.getName() + ": ");
                MaxHeap maxHeap = new MaxHeap(arr.size());
                maxHeap.getArr(arr);
                maxHeap.print();
                System.out.println();
            }
        }
    }

    class Node {
        int data = -1;
    }

    class MaxHeap {
        private Node[] Heap;
        private int size;
        private int maxsize;

        public MaxHeap(int maxsize) {
            this.maxsize = maxsize;
            this.size = 0;
            Heap = new Node[this.maxsize];
            for (int i = 0; i < maxsize; i++)
                Heap[i] = new Node();
        }

        private int parent(int pos) {
            return (pos - 1) / 2;
        }

        public void getArr(ArrayList<Integer> arr) {
            for (int i = 0; i < arr.size(); i++) {
                insert(arr.get(i));
            }
        }

        public void insert(int element) {
            Heap[size].data = element;
            int current = size;
            while (Heap[current].data > Heap[parent(current)].data) {
                swap(current, parent(current));
                current = parent(current);
            }
            size++;
        }

        private void swap(int fpos, int spos) {
            Node tmp;
            tmp = Heap[fpos];
            Heap[fpos] = Heap[spos];
            Heap[spos] = tmp;
        }

        public void print() {
            int x = 1;
            while (x < Heap.length / 2)
                x *= 2;
            x -= 1;
            while (x < Heap.length) {
                System.out.print(Heap[x].data);
                x++;
            }
        }
    }
}