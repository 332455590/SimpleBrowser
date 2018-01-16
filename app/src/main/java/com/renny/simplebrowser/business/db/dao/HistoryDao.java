package com.renny.simplebrowser.business.db.dao;

import android.support.annotation.NonNull;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.renny.simplebrowser.business.db.entity.History;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Renny on 2018/1/5.
 */

public class HistoryDao extends BaseDao<History>{

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

    @Override
    public List<History> queryForPage(long offset, long limit) {
        return null;
    }




}
