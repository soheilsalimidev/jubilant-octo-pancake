package com.file.filemanager.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "files")
public class FileModel {

    public FileModel(String name, String format, int year) {
        this.name = name;
        this.format = format;
        this.year = year;
    }

    long id;
    @Column(name = "name", length = 255, nullable = true)
    String name;
    @Column(name = "format", length = 255, nullable = true)
    String format;
    @Column(name = "year", length = 255, nullable = true)
    int year;

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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
