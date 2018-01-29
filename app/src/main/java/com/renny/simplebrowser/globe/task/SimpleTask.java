package com.renny.simplebrowser.globe.task;


import com.renny.simplebrowser.business.base.ILoading;

/**
 * 应用通用callback,可以在task和Api通用
 * <p>
 * 如果是在Task里使用:
 * ----1.任务执行时调用doBackground
 * ----2.任务执行成功调用onComplete
 * ----3.任务执行异常调用onException
 * ----4.任务被取消会调用onCancelled
 * <p>
 * 如果是在Api里调用
 * ----1.请求正常返会回调onResponse
 * ----2.请求异常返会回调onException
 * ----3.返回结果success==true会回调onComplete
 * ----4.返回结果success=false会回调onFailure
 * <p>
 * 如果要更新进度条请在doBackground手动调用progress方法更新进度
 * <p>
 */
public abstract class SimpleTask<Result> extends SimpleCallback<Result> implements ITask<Result> {
    public SimpleTask() {
    }

    public SimpleTask(ILoading iLoading) {
        setLoading(iLoading);
    }
}
