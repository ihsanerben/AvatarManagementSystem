package com.ihsan.dto;

public class FilePhotoDTO {
    private byte[] fileData;
    private byte[] thumbnailData;

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public byte[] getThumbnailData() {
        return thumbnailData;
    }

    public void setThumbnailData(byte[] thumbnailData) {
        this.thumbnailData = thumbnailData;
    }

    public FilePhotoDTO() {
    }

    public FilePhotoDTO(byte[] fileData, byte[] thumbnailData) {
        this.fileData = fileData;
        this.thumbnailData = thumbnailData;
    }
}
