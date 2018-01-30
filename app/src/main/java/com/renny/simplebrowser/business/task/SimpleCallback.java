package com.renny.simplebrowser.business.task;


import com.renny.simplebrowser.BuildConfig;
import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.ILoading;
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.business.toast.ToastHelper;
import com.renny.simplebrowser.globe.exception.ViewNotBindException;
import com.renny.simplebrowser.globe.helper.ThreadHelper;
import com.renny.simplebrowser.globe.thread.task.ITaskCallback;


public class SimpleCallback<T> implements ITaskCallback<T> {

    public ILoading iLoading;

    public void setLoading(ILoading iLoading) {
        this.iLoading = iLoading;
    }

    /**
     * 向ui线程抛进度信息
     *
     * @param progressInfo
     */
    public void postProgressInfo(final Object progressInfo) {
        ThreadHelper.postMain(new Runnable() {
            @Override
            public void run() {
                try {
                    onProgressInfo(progressInfo);
                } catch (ViewNotBindException throwable) {
                    Logs.defaults.i("task is cancel");
                } catch (Throwable throwable) {
                    Logs.defaults.e(throwable);
                }
            }
        });
    }


    /**
     * 在ui线程处理进度的信息
     *
     * @param progressInfo
     */
    public void onProgressInfo(Object progressInfo) {

    }

    /**
     * callback被执行前调用
     */
    @Override
    public void onBeforeCall() {
        if (iLoading != null) {
            iLoading.showLoading(getLoadingTip());
        }
    }

    /**
     * callback被执行后调用
     * 无论执行成功或失败此方法都将被调用
     */
    @Override
    public void onAfterCall() {
        if (iLoading != null) {
            iLoading.dismissLoading();
        }
    }

    @Override
    public String getLoadingTip() {
        return UIHelper.getString(R.string.default_loading_msg);
    }

    /**
     * 任务执行完毕或Api调用成功的回调方法，在主线程里执行
     *
     * @param result
     */
    @Override
    public void onComplete(T result) {

    }

    /**
     * 任务执行失败的回调方法，在主线程里执行
     *
     * @param throwable
     */
    @Override
    public void onException(Throwable throwable) {
    }

    /**
     * 任务执行被取消的回调方法，在主线程里执行
     *
     * @param
     */
    @Override
    public void onCancelled() {
        if (BuildConfig.DEBUG) {
            ToastHelper.makeToast("task cancelled");
            Logs.defaults.i("task is cancel");
        }
    }

}
