package com.renny.simplebrowser.globe.task;


import com.renny.simplebrowser.globe.http.reponse.IResult;

/**
 * Created by yh on 2016/4/28.
 */
public interface IApiCallback<T> {
    /**
     * 成功
     *
     * @param result
     */
    void onSuccess(IResult<T> result);

    /**
     * 失败
     *
     * @param result
     */
    void onFailure(IResult<String> result);

}