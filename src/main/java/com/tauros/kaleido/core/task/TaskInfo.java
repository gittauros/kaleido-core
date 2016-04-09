package com.tauros.kaleido.core.task;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tauros on 2016/4/9.
 */
public class TaskInfo<E extends TaskInfo> {

	private static AtomicLong curInfoId = new AtomicLong(0);

	private long    infoId;
	private long    taskId;
	private boolean statusListenerExist;
	private String  threadName;
	private E       extraInfo;

	{
		infoId = curInfoId.incrementAndGet();
	}

	public boolean isStatusListenerExist() {
		return statusListenerExist;
	}

	public void setStatusListenerExist(boolean statusListenerExist) {
		this.statusListenerExist = statusListenerExist;
	}

	protected String selfInfo() {
		StringBuilder selfInfoBuilder = new StringBuilder("");
		selfInfoBuilder.append("task").append(taskId).append(": ")
				.append("threadName:").append(threadName).append(": ");
		return selfInfoBuilder.toString();
	}


	protected String internalInfo() {
		return new StringBuilder()
				.append(selfInfo())
				.append(extraInfo == null ? "" : extraInfo.internalInfo()).toString();
	}

	public final String fullInfo() {
		return internalInfo();
	}

	public E getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(E extraInfo) {
		this.extraInfo = extraInfo;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public long getInfoId() {
		return infoId;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String id() {
		return this.taskId + "@" + infoId;
	}
}
