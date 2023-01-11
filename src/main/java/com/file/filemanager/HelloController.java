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
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class HelloController {
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

    private final ObservableList<FileModel> filesInfoList = FXCollections.observableArrayList();

    public void initialize() {
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

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onNewFileClick() {
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

        factory.close();
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

    public void onTreeShow() {
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
}