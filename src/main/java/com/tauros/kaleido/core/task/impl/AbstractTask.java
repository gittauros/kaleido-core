package com.tauros.kaleido.core.task.impl;

import com.tauros.kaleido.core.exception.KaleidoIllegalStateException;
import com.tauros.kaleido.core.exception.KaleidoUnsupportedException;
import com.tauros.kaleido.core.task.Task;
import com.tauros.kaleido.core.task.TaskInfo;
import com.tauros.kaleido.core.task.TaskStatusListener;
import com.tauros.kaleido.core.task.TaskManager;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tauros on 2016/4/9.
 */
public abstract class AbstractTask implements Task {

	private static AtomicLong curTaskId = new AtomicLong(0);

	private long taskId;
	protected boolean ready = false;
	protected boolean processed = false;
	protected TaskStatusListener taskStatusListener;

	{
		taskId = curTaskId.incrementAndGet();
	}

	@Override
	public boolean isReady() {
		return this.ready;
	}

	@Override
	public boolean isProcessed() {
		return this.processed;
	}

	@Override
	public boolean isCancelSupported() {
		return false;
	}

	@Override
	public void cancel() throws KaleidoUnsupportedException {
		throw new KaleidoUnsupportedException("cancel is not supported for Task");
	}

	@Override
	public boolean isRollbackSupported() {
		return false;
	}

	@Override
	public void rollback() throws KaleidoUnsupportedException {
		throw new KaleidoUnsupportedException("rollback is not supported for Task");
	}

	@Override
	public void updateStatus() {
		if (this.taskStatusListener == null) {
			return;
		}
		this.taskStatusListener.update(fetchInfo());
	}

	public final TaskInfo fetchInfo() {
		TaskInfo<TaskInfo> taskInfo = new TaskInfo<>();
		taskInfo.setThreadName(Thread.currentThread().getName());
		taskInfo.setTaskId(taskId);
		taskInfo.setStatusListenerExist(this.taskStatusListener != null);
		setTaskInfo(taskInfo);
		return taskInfo;
	}

	protected abstract void setTaskInfo(TaskInfo<TaskInfo> taskInfo);

	@Override
	public final void registerStatusListener(TaskStatusListener taskStatusListener) {
		if (taskStatusListener == null) {
			return;
		}
		if (this.taskStatusListener != null) {
			throw new KaleidoIllegalStateException("task already has statusListener");
		}
		this.taskStatusListener = taskStatusListener;
		this.taskStatusListener.onRegister(this.taskId);
	}

	@Override
	public final void unRegisterStatusListener() {
		if (this.taskStatusListener == null) {
			return;
		}
		TaskStatusListener taskStatusListener = this.taskStatusListener;
		this.taskStatusListener = null;
		taskStatusListener.onUnRegister();
	}

	public long getTaskId() {
		return taskId;
	}
}
