package com.renny.simplebrowser.globe.task;


import com.renny.simplebrowser.BuildConfig;
import com.renny.simplebrowser.business.http.request.HttpRequest;
import com.renny.simplebrowser.business.http.response.HttpResultParse;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.globe.http.bean.Result;
import com.renny.simplebrowser.globe.http.reponse.IResult;
import com.renny.simplebrowser.globe.http.request.Api;
import com.renny.simplebrowser.globe.lang.Maps;
import com.renny.simplebrowser.globe.thread.task.AsyncTaskInstance;
import com.renny.simplebrowser.globe.thread.task.IGroup;
import com.renny.simplebrowser.globe.thread.task.ITaskBackground;
import com.renny.simplebrowser.globe.thread.task.ITaskCallback;
import com.renny.simplebrowser.globe.thread.task.TaskScheduler;

import java.io.IOException;
import java.util.Map;

/**
 * Created by LuckyCrystal on 2017/9/26.
 */

public class TaskHelper {
    private static TaskScheduler taskScheduler;

    static {
        taskScheduler = TaskScheduler.instance();
        if (BuildConfig.DEBUG) {
            taskScheduler.setOnTaskLogInterface(new TaskScheduler.OnTaskLogInterface() {
                @Override
                public void onTaskLog(int dualPolicy, String taskName) {
                    Logs.defaults.d("task name=%s, policy=%d", taskName, dualPolicy);
                }
            });
        }
    }

    public static void cancelGroup(String groupName) {
        taskScheduler.cancleGroup(groupName);
    }


    /**
     * 向默认分组提交任务
     * 不会自动取消分组的任务
     *
     * @param taskName
     * @param task
     * @param <T>
     */
    public static <T> AsyncTaskInstance<T> submitTask(String taskName, ITask<T> task) {
        AsyncTaskInstance<T> asyncTask = AsyncTaskInstance.build(task, task)
                .taskName(taskName)
                .groupName(IGroup.DEFAULT_GROUP_NAME)
                .serialExecute(false);
        taskScheduler.submit(asyncTask);
        return asyncTask;
    }

    public static <T> AsyncTaskInstance<T> submitTask(String taskName, String groupName, ITaskBackground<T> task, ITaskCallback<T> callback) {
        AsyncTaskInstance<T> asyncTask = AsyncTaskInstance.build(task, callback)
                .taskName(taskName)
                .groupName(groupName)
                .serialExecute(false);
        taskScheduler.submit(asyncTask);
        return asyncTask;
    }

    /**
     * 串行提交任务
     *
     * @param taskName
     * @param task
     * @param <T>
     */
    public static <T> AsyncTaskInstance<T> submitTaskSerial(String taskName, String groupName, ITaskBackground<T> task, ITaskCallback<T> callback) {
        AsyncTaskInstance<T> asyncTask = AsyncTaskInstance.build(task, callback)
                .taskName(taskName)
                .groupName(groupName)
                .serialExecute(true);
        taskScheduler.submit(asyncTask);
        return asyncTask;
    }

    /**
     * 在默认分组请求api
     *
     * @param iApi
     * @param apiCallback
     */
    public static <T> AsyncTaskInstance apiCall(final Api iApi, final Map<String, String> params, final ITaskCallback<IResult<T>> apiCallback) {
        String taskName = "execute " + iApi.getUrl();
        AsyncTaskInstance asyncTask = AsyncTaskInstance.build(new ITaskBackground<IResult<T>>() {
            @Override
            public IResult<T> onBackground() throws Exception {
                okhttp3.Response response;
                try {
                    HttpRequest request = new HttpRequest(iApi, params);
                    response = request.Call();
                    return HttpResultParse.parseResult(iApi, response);
                } catch (IOException e) {
                    e.printStackTrace();
                    return Result.fail("  ", "IOException");
                }

            }
        }, apiCallback)
                .taskName(taskName)
                .serialExecute(false);
        taskScheduler.submit(asyncTask);
        return asyncTask;
    }

    /**
     * api请求
     *
     * @param iGroup      分组
     * @param iApi
     * @param params      参数
     * @param apiCallback
     */
    public static <T> AsyncTaskInstance apiCall(final IGroup iGroup, final Api iApi,
                                                final Map<String, String> params,
                                                final ITaskCallback apiCallback) {
        String taskName = "execute " + iApi.getUrl();
        AsyncTaskInstance asyncTask = AsyncTaskInstance.build(new ITaskBackground<IResult<T>>() {
            @Override
            public IResult onBackground() throws Exception {
                okhttp3.Response response;
                try {
                    HttpRequest request = new HttpRequest(iApi, params);
                    response = request.Call();
                    return HttpResultParse.parseResult(iApi, response);
                } catch (IOException e) {
                    e.printStackTrace();
                    Result.fail("  ", "IOException");
                }
                return Result.fail(" ", " ");
            }
        }, apiCallback)
                .taskName(taskName)
                .groupName(iGroup.groupName())
                .serialExecute(false);
        taskScheduler.submit(asyncTask);
        return asyncTask;
    }

    /**
     * 在默认分组请求api
     *
     * @param iApi
     * @param apiCallback
     */
    public static <T> AsyncTaskInstance apiCall(final Api iApi, final ITaskCallback<IResult<T>> apiCallback) {
        String taskName = "execute " + iApi.getUrl();
        AsyncTaskInstance asyncTask = AsyncTaskInstance.build(new ITaskBackground<IResult<T>>() {
            @Override
            public IResult<T> onBackground() throws Exception {
                okhttp3.Response response;
                try {
                    HttpRequest request = new HttpRequest(iApi, Maps.mapNull);
                    response = request.Call();
                    return HttpResultParse.parseResult(iApi, response);
                } catch (IOException e) {
                    e.printStackTrace();
                    return Result.fail("  ", "IOException");
                }

            }
        }, apiCallback)
                .taskName(taskName)
                .serialExecute(false);
        taskScheduler.submit(asyncTask);
        return asyncTask;
    }


}
