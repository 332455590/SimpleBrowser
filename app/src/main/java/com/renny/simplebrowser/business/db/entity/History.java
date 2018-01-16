package com.renny.simplebrowser.business.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Renny on 2018/1/15.
 */
@DatabaseTable(tableName = "tb_history")
public class History {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "time")
    private long time;
    @DatabaseField(columnName = "url")
    private String url;
    @DatabaseField(columnName = "title")
    private String title;

    public History() {
    }

    public History(long time, String url, String title) {
        this.time = time;
        this.url = url;
        this.title = title;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
