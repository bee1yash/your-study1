package com.example.yourstudy.pdf;

import androidx.annotation.NonNull;

public class putPDF {
    private String name;
    private String url;
    private String fileExtension;

    public putPDF() {
    }

    public putPDF(String name, String url, String fileExtension) {
        this.name = name;
        this.url = url;
        this.fileExtension = fileExtension;
    }

    public String getName() {
        return name;
    }
    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}
