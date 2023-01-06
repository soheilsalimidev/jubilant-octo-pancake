package com.file.filemanager;

import com.file.filemanager.Models.FileModel;
import com.file.filemanager.Part1.Part1;
import com.file.filemanager.Utils.HibernateUtil;
import com.file.filemanager.Utils.Util;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.stage.FileChooser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class HelloController {
    String rootPath = "";
    @FXML
    private MenuItem newFile;
    //    @FXML
//    private MenuItem sortByYear;
//    @FXML
//    private MenuItem removeFile;
    @FXML
    private TreeTableView<FileModel> treeView;
    SessionFactory factory = HibernateUtil.getSessionFactory();
    Part1 part1;

    private final ObservableList<FileModel> filesInfoList = FXCollections.observableArrayList();

    public void initialize() {
        part1 = new Part1(rootPath);
        filesInfoList.addAll(getAllFiles());
        addFilesToTree();
    }

    @FXML
    protected void onNewFileClick() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("zip files (*.zip)", "*.zip");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(newFile.getParentPopup().getOwnerWindow());
        if (file != null)
            try {
                rootPath = Util.extractFolder(file.getPath(), file.getParent() + "//root//");
                part1.takeOutFiles();
                hqlTruncate();
                saveFilesInfoInDatabase(part1.getFilesInfoList());
                filesInfoList.removeAll();
                filesInfoList.addAll(part1.getFilesInfoList());
                addFilesToTree();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    @FXML
    protected void onDeleteFileClick() {
        TreeItem<FileModel> c = treeView.getSelectionModel().getSelectedItem();
        c.getParent().getChildren().remove(c);
        filesInfoList.remove(c.getValue());
        part1.removeFile(c.getValue().getName());
        Session session = factory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.remove(c.getValue());
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            session.close();
        }
        System.out.println("Remove");
    }

    private void saveFilesInfoInDatabase(List<FileModel> files) {
        Session session = factory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            files.forEach(session::persist);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            session.close();
        }

        factory.close();
    }

//    @FXML
//    protected void sortOnDate() {
//        filesInfoList.removeAll();
//        try (Session session = factory.openSession()) {
//            session.createNativeQuery("select * from file.files order by date desc", FileModel.class).stream().forEach(filesInfoList::add);
//        } catch (Exception e) {
//            System.out.println("ERROR: " + e.getMessage());
//        }
//        addFilesToTree();
//    }

    public void hqlTruncate() {
        Session session = factory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.createNativeQuery("delete from file.files", FileModel.class);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    public List<FileModel> getAllFiles() {

        try (Session session = factory.openSession()) {
            return session.createNativeQuery("Select * from file.files", FileModel.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    void addFilesToTree() {
        TreeItem<FileModel> root = new TreeItem<>(new FileModel("root", "dir", 0));
        root.setExpanded(true);

        filesInfoList.forEach(file -> root.getChildren().add(new TreeItem<>(file)));
        filesInfoList.addListener((ListChangeListener<FileModel>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    TreeItem<FileModel> temp = new TreeItem(c);
                    root.getChildren().add(temp);
                }
            }
        });

        treeView.setRoot(root);
    }
}