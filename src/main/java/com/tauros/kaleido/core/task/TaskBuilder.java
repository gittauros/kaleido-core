package com.tauros.kaleido.core.task;

import com.tauros.kaleido.core.exception.KaleidoIllegalStateException;

/**
 * Created by tauros on 2016/4/9.
 */
public interface TaskBuilder<E extends Task> {

    /**
     * 构建一个任务
     *
     * @return
     */
    E build() throws KaleidoIllegalStateException;
}
