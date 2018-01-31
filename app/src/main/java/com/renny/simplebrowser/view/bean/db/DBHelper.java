package com.renny.simplebrowser.view.bean.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.renny.simplebrowser.view.bean.db.entity.BookMark;
import com.renny.simplebrowser.view.bean.db.entity.History;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Renny on 2018/1/5.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String TABLE_NAME = "sqlite-renny.db";
    private static DBHelper instance;
    /**
     * userDao ，每张表对于一个
     */
    private List<Class> mClassList = new ArrayList<>();
    private HashMap<Class, Dao> mDaoHashMap = new HashMap<>();

    private DBHelper(Context context) {
        super(context, TABLE_NAME, null, 5);
        mClassList.add(BookMark.class);
        mClassList.add(History.class);
    }

    /**
     * 单例获取该Helper
     */
    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null)
                    instance = new DBHelper(context);
            }
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {
            for (Class aClass : mClassList) {
                TableUtils.createTable(connectionSource, aClass);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            for (Class aClass : mClassList) {
                TableUtils.dropTable(connectionSource, aClass, true);
            }
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /**
     * 获得userDao
     *
     * @return
     * @throws SQLException
     */
    public Dao getMyDao(Class cls) throws SQLException {
        Dao dao = mDaoHashMap.get(cls);
        if (dao == null) {
            dao = getDao(cls);
            mDaoHashMap.put(cls, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (Map.Entry<Class, Dao> entry : mDaoHashMap.entrySet()) {
            Dao dao = entry.getValue();
            dao = null;
        }
    }

}  
