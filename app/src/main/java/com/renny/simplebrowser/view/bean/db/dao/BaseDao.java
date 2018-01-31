package com.renny.simplebrowser.view.bean.db.dao;

import android.support.annotation.NonNull;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.renny.simplebrowser.App;
import com.renny.simplebrowser.view.bean.db.DBHelper;
import com.renny.simplebrowser.business.log.Logs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renny on 2018/1/5.
 */

public class BaseDao<entity> implements IDao<entity> {

    private Dao<entity, Integer> mDao;

    BaseDao(Class cls) {
        try {
            mDao = DBHelper.getInstance(App.getContext()).getMyDao(cls);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    Dao<entity, Integer> getDao() {
        return mDao;
    }


    @Override
    public long getCount() {
        try {
            return mDao.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void addEntity(@NonNull entity entity) {
        try {
            mDao.create(entity);
        } catch (SQLException e) {
            Logs.base.e(e);
        }

    }

    @Override
    public void addEntityList(@NonNull List<entity> entityList) {
        try {
            mDao.create(entityList);
        } catch (SQLException e) {
            Logs.base.e(e);
        }
    }

    /**
     * 删除一条记录
     */
    public void delete(@NonNull String url) {
        try {
            DeleteBuilder<entity, Integer> deleteBuilder = mDao.deleteBuilder();
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
    public List<entity> queryForAll() {
        List<entity> entityList = new ArrayList<>();
        try {
            entityList = mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entityList;
    }

    @Override
    public List<entity> queryForPage(long offset, long limit) {
        return null;
    }

    @Override
    public void deleteAll() {
        try {
            DeleteBuilder builder = mDao.deleteBuilder();
            builder.delete();
        } catch (SQLException e) {
            Logs.base.e(e);
        }
    }
}
