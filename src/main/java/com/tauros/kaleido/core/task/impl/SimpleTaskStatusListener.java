package com.tauros.kaleido.core.task.impl;

import com.tauros.kaleido.core.exception.KaleidoIllegalStateException;
import com.tauros.kaleido.core.task.TaskInfo;
import com.tauros.kaleido.core.task.TaskLogManager;
import com.tauros.kaleido.core.task.TaskStatusListener;

/**
 * Created by tauros on 2016/4/10.
 */
public class SimpleTaskStatusListener implements TaskStatusListener {

	private long     taskId;
	private TaskInfo taskInfo;

	@Override
	public void onRegister(long taskId) {
		this.taskId = taskId;
		TaskLogManager.registerListener(this);
	}

	@Override
	public void update(TaskInfo taskInfo) {
		if (taskInfo == null) {
			throw new KaleidoIllegalStateException("taskInfo is null");
		}
		this.taskInfo = taskInfo;
	}

	@Override
	public TaskInfo fetchInfo() {
		return this.taskInfo;
	}

	@Override
	public void onUnRegister() {
		TaskLogManager.unRegisterListener(this);
	}

	@Override
	public long getTaskId() {
		return this.taskId;
	}
}
