package com.jed.whatsapp;

import java.util.Date;

public class UploadedCloudFile {

    private Date dateCreated;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public Date getCreated() {
        return dateCreated;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}