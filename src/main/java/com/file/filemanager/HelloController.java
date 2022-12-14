package com.file.filemanager;

import com.file.filemanager.Utils.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class HelloController {
    String rootPath = "";
    @FXML
    private MenuItem newFile;
    @FXML
    private MenuItem sortByYear;
    @FXML
    private MenuItem sortByFormat;
    @FXML
    private TreeTableView treeView;

    @FXML
    protected void onNewFileClick() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("zip files (*.zip)", "*.zip");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(newFile.getParentPopup().getOwnerWindow());

        if (file != null)
            try {
                rootPath = Util.extractFolder(file.getPath(), file.getParent() + "//root//");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
}