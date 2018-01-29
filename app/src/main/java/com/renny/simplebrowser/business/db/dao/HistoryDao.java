package com.renny.simplebrowser.business.db.dao;

import android.support.annotation.NonNull;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.renny.simplebrowser.business.db.entity.History;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renny on 2018/1/5.
 */

public class HistoryDao extends BaseDao<History> {

    public HistoryDao() {
        super(History.class);
    }

    /**
     * 删除一条记录
     */
    public void delete(@NonNull String url) {
        try {
            DeleteBuilder<History, Integer> deleteBuilder = getDao().deleteBuilder();
            deleteBuilder.where().eq("url", url);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询所有记录
     */
    @Override
    public List<History> queryForAll() {
        List<History> entityList = new ArrayList<>();
        try {
            QueryBuilder<History, Integer> queryBuilder = getDao().queryBuilder();
            entityList = queryBuilder.orderBy("id", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entityList;
    }

    /**
     * 查询今天记录
     */
    public void queryToheavy(String date, String url) {
        List<History> entityList;
        try {
            QueryBuilder<History, Integer> queryBuilder = getDao().queryBuilder();
            queryBuilder.where().eq("date", date).and().eq("url", url);
            entityList = queryBuilder.query();
            if (entityList != null && !entityList.isEmpty()){
                DeleteBuilder<History, Integer> deleteBuilder = getDao().deleteBuilder();
                deleteBuilder.where().eq("date", date).and().eq("url", url);
                deleteBuilder.delete();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<History> queryForPage(long offset, long limit) {
        return null;
    }


}
