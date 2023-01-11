module com.file.filemanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires jakarta.persistence;
    requires java.naming;
    requires jakarta.xml.bind;
    requires org.hibernate.orm.core;
    requires com.fasterxml.classmate;
    requires net.bytebuddy;
    requires org.hibernate.validator;
    requires org.hibernate.commons.annotations;
    requires java.desktop;
    opens com.file.filemanager to javafx.fxml, org.hibernate.orm.core;
    opens com.file.filemanager.Models;
    exports com.file.filemanager;
    exports com.file.filemanager.Utils;
    opens com.file.filemanager.Utils to javafx.fxml, org.hibernate.orm.core;
}