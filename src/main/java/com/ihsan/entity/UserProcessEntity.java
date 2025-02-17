package com.ihsan.entity;

import java.io.Serializable;

public class UserProcessEntity implements Serializable {

    private String process;

    private UserEntity user;

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public UserProcessEntity() {
    }

    public UserProcessEntity(String process, UserEntity user) {
        this.process = process;
        this.user = user;
    }
}
