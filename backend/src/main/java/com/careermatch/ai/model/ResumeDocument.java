package com.careermatch.ai.model;

import java.time.LocalDateTime;

public class ResumeDocument {
    private String fileName;
    private String fileId;
    private String rawText;
    private int version;
    private long fileSize;
    private LocalDateTime uploadTime;

    public ResumeDocument(String fileName, String fileId, String rawText, int version, long fileSize) {
        this.fileName = fileName;
        this.fileId = fileId;
        this.rawText = rawText;
        this.version = version;
        this.fileSize = fileSize;
        this.uploadTime = LocalDateTime.now();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }
}
