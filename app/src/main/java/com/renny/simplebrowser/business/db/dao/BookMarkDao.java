package com.renny.simplebrowser.business.db.dao;

import android.support.annotation.NonNull;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.renny.simplebrowser.business.db.entity.BookMark;
import com.renny.simplebrowser.business.log.Logs;

import java.sql.SQLException;
import java.util.ArrayList;
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

    /**
     * 查询所有记录
     */
    @Override
    public List<BookMark> queryForAll() {
        List<BookMark> markList = new ArrayList<>();
        try {
            markList = getDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return markList;
    }


    @Override
    public long getCount() {
        try {
            return getDao().countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void addEntity(@NonNull BookMark entity) {
        if (!query(entity.getUrl())) {
            try {
                getDao().create(entity);
            } catch (SQLException e) {
                Logs.base.e(e);
            }
        }
    }

    @Override
    public void addEntityList(@NonNull List<BookMark> entityList) {
        try {
            getDao().create(entityList);
        } catch (SQLException e) {
            Logs.base.e(e);
        }
    }

    @Override
    public List<BookMark> queryForPage(long offset, long limit) {
        return null;
    }

    @Override
    public void deleteAll() {

    }
}
