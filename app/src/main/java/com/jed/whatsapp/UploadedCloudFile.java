package com.jed.whatsapp;

import java.util.Date;

public class UploadedCloudFile {

    private Date lastModified;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}