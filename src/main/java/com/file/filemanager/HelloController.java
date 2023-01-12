package com.file.filemanager;

import com.file.filemanager.Models.FileModel;
import com.file.filemanager.Part1.Part1;
import com.file.filemanager.Utils.HibernateUtil;
import com.file.filemanager.Utils.Util;
import com.file.filemanager.part3.Faz3;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class HelloController {
    @FXML
    public ProgressIndicator progress;
    String rootPath = "";
    String rootUnChangedPath = "";
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

    int state = 0;
    private final ObservableList<FileModel> filesInfoList = FXCollections.observableArrayList();

    public void initialize() {
        progress.setVisible(false);
        part1 = new Part1(rootPath);
        filesInfoList.addAll(getAllFiles());
        addFilesToTree();
    }

    @FXML
    protected void onNewZipFileClick() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("zip files (*.zip)", "*.zip");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(newFile.getParentPopup().getOwnerWindow());
        if (file != null) try {
            rootUnChangedPath = Util.extractFolder(file.getPath(), file.getParent() + "//rootUnChanged//");
            rootPath = Util.extractFolder(file.getPath(), file.getParent() + "//root//");
            part1 = new Part1(rootPath);
            part1.takeOutFiles();
            hqlTruncate();
            saveFilesInfoInDatabase(part1.getFilesInfoList());
            treeView.setRoot(null);
            treeView.refresh();
            filesInfoList.removeAll();
            filesInfoList.addAll(part1.getFilesInfoList());
            addFilesToTree();
            state = 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onNewFileClick() {
        checkTheState(1);
        Dialog<FileModel> dialog = new Dialog<>();
        dialog.setTitle("فایل جدید");
        dialog.setHeaderText("اطلاعات فایل خود را وارد کنید ؟");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField nameField = new TextField("MyFile");
        TextField dateField = new TextField();
        TextField formatField = new TextField("mp4");

        dateField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, change -> {
            NumberFormat numberFormat = NumberFormat.getIntegerInstance();
            ParsePosition position = new ParsePosition(0);
            Object obj = numberFormat.parseObject(change.getControlNewText(), position);
            if (obj != null && position.getIndex() == change.getControlNewText().length()) {
                return change;
            }
            return null;
        }));

        dialogPane.setContent(new VBox(8, nameField, dateField, formatField));
        Platform.runLater(nameField::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new FileModel(nameField.getText(), formatField.getText(), Integer.parseInt(dateField.getText()));
            }
            return null;
        });
        Optional<FileModel> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((FileModel results) -> {
            try {
                part1.addFile(results.getName(), String.valueOf(results.getDate()), results.getFormat());
                filesInfoList.add(results);
                saveFileInfoInDatabase(results);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @FXML
    protected void onDeleteFileClick() {
        checkTheState(1);
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

    private void saveFileInfoInDatabase(FileModel file) {
        Session session = factory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.persist(file);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            session.close();
        }

        // factory.close();
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

        // factory.close();
    }


    public void hqlTruncate() {
        Session session = factory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.createNativeQuery("delete from file.files where 1", FileModel.class).executeUpdate();
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

    public List<FileModel> getAllFiles() {

        try (Session session = factory.openSession()) {
            return session.createNativeQuery("Select * from file.files", FileModel.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    void addFilesToTree() {
        if (state == 2)
            findFiles(new FileModel("root", "dir", 0, new File(rootPath)), null);
        else {
            TreeItem<FileModel> root = new TreeItem<>(new FileModel("root", "dir", 0));
            root.setExpanded(true);
            filesInfoList.forEach(file -> root.getChildren().add(new TreeItem<>(file)));
            filesInfoList.addListener((ListChangeListener<FileModel>) c -> {
                if (treeView.getRoot() == null) {
                    TreeItem<FileModel> newRoot = new TreeItem<>(new FileModel("root", "dir", 0));
                    root.setExpanded(true);
                    treeView.setRoot(newRoot);
                }

                while (c.next()) {
                    if (c.wasAdded()) {
                        for (FileModel element : c.getAddedSubList()) {
                            TreeItem<FileModel> temp = new TreeItem<>(element);
                            treeView.getRoot().getChildren().add(temp);
                        }
                    }
                }
            });
            treeView.setRoot(root);
        }
    }

    private void findFiles(FileModel dir, TreeItem<FileModel> parent) {
        TreeItem<FileModel> root = new TreeItem<>(new FileModel(dir.getName(), "dir", 0));
        root.setExpanded(true);
        File[] files = dir.getPath().listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory())
                findFiles(new FileModel(file.getName(), "dir", 0, file), root);
            else
                root.getChildren().add(new TreeItem<>(new FileModel(file.getName().split("\\.")[0], file.getName().split("\\.")[2], Integer.parseInt(file.getName().split("\\.")[1]))));

        }
        if (parent == null) {
            treeView.setRoot(root);
        } else {
            parent.getChildren().add(root);
        }
    }

    public void onTreeShow() {
        checkTheState(1);
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("درخت");
        dialog.setHeaderText("اسم فولدر را وارد کنید");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField nameField = new TextField("root");

        dialogPane.setContent(new VBox(8, nameField));
        Platform.runLater(nameField::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return nameField.getText();
            }
            return null;
        });

        Optional<String> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((String results) -> {
            Faz3 faz3 = new Faz3(rootUnChangedPath, results);

            Dialog<String> alert = new Dialog<>();
            alert.setTitle("درخت");
            alert.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            TextFlow flow = new TextFlow();

            Text text1 = new Text("pre Order \n");
            text1.setStyle("-fx-font-weight: bold");

            Text text2 = new Text(faz3.getPreOrder());
            text2.setStyle("-fx-font-weight: regular");

            Text text3 = new Text("\nin Order \n");
            text1.setStyle("-fx-font-weight: bold");

            Text text4 = new Text(faz3.getInOrder());
            text2.setStyle("-fx-font-weight: regular");

            Text text5 = new Text("\npost Order \n");
            text1.setStyle("-fx-font-weight: bold");

            Text text6 = new Text(faz3.getPostOrder());
            text2.setStyle("-fx-font-weight: regular");


            flow.getChildren().addAll(text1, text2, text3, text4, text5,
                    text6);

            alert.getDialogPane().setContent(flow);
            alert.setResultConverter((ButtonType button) -> null);
            alert.showAndWait();

        });
    }

    public void onHeapShow() {

        checkTheState(2);
        Dialog<String> alert = new Dialog<>();
        alert.setTitle("درخت");
        alert.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextFlow flow = new TextFlow();

        Text text1 = new Text("Heap max \n");
        text1.setStyle("-fx-font-weight: bold");
        Text text2 = null;
        try {
            //System.out.println(part1.printMaxHeap());
            text2 = new Text(part1.printMaxHeap());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
//        text2.setStyle("-fx-font-weight: ");


        flow.getChildren().addAll(text1, text2);

        alert.getDialogPane().setContent(flow);
        alert.setResultConverter((ButtonType button) -> null);
        alert.showAndWait();

    }

    public void onSortFolders() {
        if (Objects.equals(rootPath, "")) onNewZipFileClick();
        progress.setVisible(true);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                try {
                    part1.groupFile(rootPath);
                    progress.setVisible(false);
                    treeView.getRoot().getChildren().clear();
                    treeView.setRoot(null);
                    addFilesToTree();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }).start();
        state = 2;

    }


    public void checkTheState(int requiredState) {
        if (Objects.equals(rootPath, "")) onNewZipFileClick();
        if (state == requiredState) return;
        else if (requiredState == 1)
            try {
                progress.setVisible(true);
                part1.takeOutFiles();
                treeView.getRoot().getChildren().clear();
                treeView.setRoot(null);
                addFilesToTree();
                progress.setVisible(false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        else
            onSortFolders();
        state = requiredState;
    }
}