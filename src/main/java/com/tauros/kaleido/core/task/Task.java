package com.tauros.kaleido.core.task;

import com.tauros.kaleido.core.exception.KaleidoUnsupportedException;

/**
 * Created by tauros on 2016/4/9.
 */
public interface Task {

    /**
     * 任务是否已经准备就绪
     *
     * @return
     */
    boolean isReady();

    /**
     * 任务是否已经执行
     *
     * @return
     */
    boolean isProcessed();

    /**
     * 任务是否成功
     *
     * @return
     */
    boolean isSuccess();

    /**
     * 执行任务
     *
     * @return 是否执行成功
     */
    boolean process();

    /**
     * 获取一个相同的任务
     *
     * @return
     */
    Task obtain();

    /**
     * 是否支持取消
     *
     * @return
     */
    boolean isCancelSupported();

    /**
     * 取消任务
     */
    void cancel() throws KaleidoUnsupportedException;

    /**
     * 是否支持回滚
     *
     * @return
     */
    boolean isRollbackSupported();

    /**
     * 回滚
     */
    void rollback() throws KaleidoUnsupportedException;

    /**
     * 更新状态
     */
    void updateStatus();

    /**
     * 绑定状态监听器
     *
     * @param taskStatusListener
     */
    void registerStatusListener(TaskStatusListener taskStatusListener);

    /**
     * 解绑状态监听器
     */
    void unRegisterStatusListener();

    /**
     * 是否可重试
     *
     * @return
     */
    boolean isRetryAble();
}
