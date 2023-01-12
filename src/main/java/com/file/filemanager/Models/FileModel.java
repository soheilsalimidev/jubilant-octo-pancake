package com.file.filemanager.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.File;

@Entity
@Table(name = "files")
public class FileModel {
    public FileModel(String name, String format, int date, File path) {
        this.name = name;
        this.format = format;
        this.date = date;
        this.path = path;
    }

    public FileModel(String name, String format, int date) {
        this.name = name;
        this.format = format;
        this.date = date;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public File getPath() {
        return path;
    }

    long id;
    @Column(name = "name", length = 255, nullable = true)
    String name;
    @Column(name = "format", length = 255, nullable = true)
    String format;
    @Column(name = "date", length = 255, nullable = true)
    int date;
    File path;

    public FileModel() {
    }

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", format='" + format + '\'' +
                ", date=" + date +
                '}';
    }
}
