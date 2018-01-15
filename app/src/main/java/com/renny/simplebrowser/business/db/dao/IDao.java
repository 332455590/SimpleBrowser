package com.renny.simplebrowser.business.db.dao;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by LuckyCrystal on 2017/8/21.
 */

public interface IDao<T> {

    long getCount();

    void addEntity(@NonNull T entity);

    void addEntityList(@NonNull List<T> entityList);

    List<T> queryForPage(long offset, long limit);

    List<T> queryForAll();

    void deleteAll();
}
