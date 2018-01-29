package com.renny.simplebrowser.globe.thread.task;


import com.renny.simplebrowser.globe.exception.NetworkNotAvailableException;
import com.renny.simplebrowser.globe.helper.LogHelper;
import com.renny.simplebrowser.globe.helper.ThreadHelper;
import com.renny.simplebrowser.globe.lang.Chars;
import com.renny.simplebrowser.globe.thread.TheadLocalHelper;

import java.net.SocketException;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public abstract class AbstractTaskInstance<Result> extends FutureTask<Result> implements ITaskInstance, IGroupedTaskInstance, IPriorityTask {
    protected String taskName = DEFAULT_TASK_NAME;
    protected String groupName = DEFAULT_GROUP_NAME;
    protected ITaskCallback<Result> callback;
    protected boolean serialExecute;
    protected int dualPolicy = DISCARD_NEW;
    protected int priority = PRIOR_NORMAL;
    int status = STATUS_NEW;
    /**
     * 任务提交的时间
     */
    private long submitTime;
    /**
     * 开始执行时间
     */
    private long beginExecute;
    /**
     * 执行结束时间
     */
    private long endExecute;

    public AbstractTaskInstance(final ITaskBackground<Result> callable, ITaskCallback<Result> callback) {
        super(new Callable<Result>() {
            @Override
            public Result call() throws Exception {
                return callable.onBackground();
            }
        });
        this.callback = callback;
    }

    public AbstractTaskInstance(Runnable runnable) {
        super(runnable, null);
    }

    private void onBeforeCall() {
        ThreadHelper.postMain(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onBeforeCall();
                } catch (Throwable throwable) {
                    onException("onBeforeCall", throwable);
                }
            }
        });
    }

    private void onAfterCall() {
        try {
            callback.onAfterCall();
        } catch (Throwable throwable) {
            onException("onAfterCall", throwable);
        }
    }

    private void onComplete() {
        try {
            Result result = get();
            if (result != null) {
                callback.onComplete(result);
            } else {
                LogHelper.task().d("task(%s) result is null,callback.onComplete will not call",
                        taskName());
            }
        } catch (Throwable throwable) {
            onException("onComplete", throwable);
        }
    }

    private void onException(String period, Throwable throwable) {
        Throwable cause;
        if (throwable instanceof ExecutionException) {
            ExecutionException executionException = (ExecutionException) throwable;
            cause = executionException.getCause();
        } else {
            cause = throwable;
        }
        if (cause instanceof NetworkNotAvailableException) {
            LogHelper.task().e("网络未开启");
        } else if (throwable instanceof SocketException) {
            LogHelper.task().e("网络异常");
        } else if (throwable instanceof CancellationException) {
            LogHelper.task().e("网请求取消");
        } else {
            LogHelper.task().e(cause, "task(%s) exception at period= %s", taskName(), period);
        }
        try {
            callback.onException(cause);
        } catch (Throwable throwable1) {
            LogHelper.task().e(throwable1, "exception on onException");
        }
    }

    private void onCancelled() {
        LogHelper.task().d("task(%s) cancel", taskName());
        try {
            callback.onCancelled();
        } catch (Throwable throwable) {
            //onException("onCancelled", throwable);
        }
    }

    public void onSubmit() {
        submitTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (callback != null) {
            onBeforeCall();
        }
        LogHelper.task().d("task(%s) execute start", taskName());
        TheadLocalHelper.setTaskInfo(groupName(), taskName());
        beginExecute = System.currentTimeMillis();
        try {
            super.run();
        } catch (final Throwable throwable) {
            ThreadHelper.postMain(new Runnable() {
                @Override
                public void run() {
                    onException("onBackground", throwable);
                }
            });
        }
    }

    /**
     * 任务执行结束时将会调用此方法
     * 不论是正常结束，异常结束，还是被取消此方法都将被调用
     */
    @Override
    protected void done() {
        endExecute = System.currentTimeMillis();
        if (callback != null) {
            ThreadHelper.postMain(new Runnable() {
                @Override
                public void run() {
                    onAfterCall();
                    if (isCancelled()) {
                        onCancelled();
                    } else {
                        onComplete();
                    }
                }
            });
        }
        long waitTime = beginExecute - submitTime;
        long runTime = endExecute - beginExecute;
        long totalTime = endExecute - submitTime;
        LogHelper.task().d("task(%s)execute end.waitTime=%d,runTime=%d,totalTime=%d",
                taskName, waitTime, runTime, totalTime);
    }

//    @Override
//    public void start() {
//        TaskScheduler.instance().submit(this);
//    }

//    @Override
//    public void stop() {
//        this.cancel(true);//手动取消也会会触发一次done()
//        this.setStatus(STATUS_CANCEL);
//        TaskScheduler.instance().removeEndTask(this);
//        LogUtil.w("task was canceled, \ntask=%s");
//    }

    @Override
    protected void setException(Throwable t) {
        super.setException(t);
        this.endExecute = System.currentTimeMillis();//为了更好的打印日志，此时任务还未结束此字段为0
        LogHelper.task().e("execute task exception \nexception: %s \ntaskName: %s", t.getMessage(), taskName());
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String taskName() {
        return taskName;
    }

    @Override
    public String groupName() {
        return groupName;
    }

    /**
     * 依次和PriorityBlockingQueue中的对象比较，
     * priority值越大越早执行
     *
     * @param another
     * @return 返回负数表示优先级较高
     */
    @Override
    public int compareTo(IPriorityTask another) {
        return another.getPriority() - priority;
    }

    @Override
    public boolean serialExecute() {
        return serialExecute;
    }

    @Override
    public int dualPolicy() {
        return dualPolicy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractTaskInstance that = (AbstractTaskInstance) o;

        return taskName.equals(that.taskName) && groupName.equals(that.groupName);
    }

    @Override
    public int hashCode() {
        int result = taskName.hashCode();
        result = 31 * result + groupName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('{')
                .append("waitTime").append(Chars.EQUAL).append(beginExecute - submitTime)
                .append(Chars.WRAP).append("runTime").append(Chars.EQUAL).append(endExecute - beginExecute)
                .append(Chars.WRAP).append("totalTime").append(Chars.EQUAL).append(endExecute - submitTime)

                .append(Chars.WRAP).append("taskName").append(Chars.EQUAL)
                .append(Chars.SIGLE_QUOTE).append(taskName).append(Chars.SIGLE_QUOTE)
                .append(Chars.WRAP).append("groupName").append(Chars.EQUAL)
                .append(Chars.SIGLE_QUOTE).append(groupName).append(Chars.SIGLE_QUOTE)

                .append(Chars.WRAP).append(Chars.WRAP).append("serialExecute")
                .append(Chars.EQUAL).append(serialExecute)
                .append(Chars.WRAP).append("priority").append(Chars.EQUAL).append(priority)
                .append(Chars.WRAP).append("status").append(Chars.EQUAL).append(status).append('}');
        return stringBuilder.toString();
    }
}
