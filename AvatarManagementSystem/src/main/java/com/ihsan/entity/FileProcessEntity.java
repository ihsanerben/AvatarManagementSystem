package com.ihsan.entity;

import java.io.Serializable;

public class FileProcessEntity  implements Serializable {

    private String process;

    private FileEntity file;

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }

    public FileProcessEntity() {
    }

    public FileProcessEntity(String process, FileEntity file) {
        this.process = process;
        this.file = file;
    }
}
