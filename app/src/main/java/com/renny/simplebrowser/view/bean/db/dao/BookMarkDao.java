package com.renny.simplebrowser.view.bean.db.dao;

import android.support.annotation.NonNull;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.renny.simplebrowser.view.bean.db.entity.BookMark;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Renny on 2018/1/5.
 */

public class BookMarkDao extends BaseDao<BookMark> {

    public BookMarkDao() {
        super(BookMark.class);
    }

    /**
     * 删除一条记录
     */
    public void delete(@NonNull String url) {
        try {
            DeleteBuilder<BookMark, Integer> deleteBuilder = getDao().deleteBuilder();
            deleteBuilder.where().eq("url", url);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询一条记录
     */
    public boolean query(@NonNull String url) {
        List<BookMark> markList = null;
        try {
            markList = getDao().queryBuilder().where().eq("url", url).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return markList != null && markList.size() > 0;
    }


}
