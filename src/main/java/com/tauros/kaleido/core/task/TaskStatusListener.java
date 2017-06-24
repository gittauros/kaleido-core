package com.tauros.kaleido.core.task;

/**
 * Created by tauros on 2016/4/9.
 */
public interface TaskStatusListener {

    /**
     * 绑定监听器时触发
     */
    void onRegister(long taskId);

    /**
     * 更新状态信息
     *
     * @param taskInfo
     */
    void update(TaskInfo taskInfo);

    /**
     * 获取任务状态信息
     *
     * @return
     */
    TaskInfo fetchInfo();

    /**
     * 解绑监听器时触发
     */
    void onUnRegister();

    /**
     * 获取监听器所监听的taskId
     *
     * @return
     */
    long getTaskId();

}
