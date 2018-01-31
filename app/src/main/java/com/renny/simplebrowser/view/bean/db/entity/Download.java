package com.renny.simplebrowser.view.bean.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Renny on 2018/1/5.
 */
@DatabaseTable(tableName = "tb_download")
public class Download {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "fileName")
    private String fileName;
    @DatabaseField(columnName = "downLink")
    private String downLink;

    @DatabaseField(columnName = "filePath")
    private String filePath;

    @DatabaseField(columnName = "mimeType")
    private String mimeType;

    public Download() {
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownLink() {
        return downLink;
    }

    public void setDownLink(String url) {
        this.downLink = url;
    }
}
