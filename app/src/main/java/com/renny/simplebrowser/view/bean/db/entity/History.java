package com.renny.simplebrowser.view.bean.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.renny.simplebrowser.globe.helper.DateUtil;

/**
 * Created by Renny on 2018/1/15.
 */
@DatabaseTable(tableName = "tb_history")
public class History {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "date")
    private String date;
    @DatabaseField(columnName = "url")
    private String url;
    @DatabaseField(columnName = "title")
    private String title;

    public History() {
    }

    public History(String date, String url, String title) {
        this.date = date;
        this.url = url;
        this.title = title;
    }


    public String getDate() {
        String year= String.valueOf(DateUtil.getSysYear());
        if (date.startsWith(year))
            return date.substring(5);
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
