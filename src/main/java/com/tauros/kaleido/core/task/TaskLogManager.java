package com.tauros.kaleido.core.task;

import com.tauros.kaleido.core.util.ConsoleLog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by tauros on 2016/4/10.
 */
public enum  TaskLogManager {

	INSTANCE;

	private static long LOG_SLEEP = 1500;
	private ConcurrentHashMap<Long, TaskStatusListener> listenerPool;
	private Executor executor;

	TaskLogManager() {
		init();
	}

	private void init() {
		listenerPool = new ConcurrentHashMap<>();
		executor = Executors.newSingleThreadExecutor();
	}

	private synchronized void putListener(TaskStatusListener taskStatusListener) {
		listenerPool.put(taskStatusListener.getTaskId(), taskStatusListener);
		if (listenerPool.size() == 1) {
			executor.execute(new RunnableLog());
		}
	}

	private synchronized void removeListener(TaskStatusListener taskStatusListener){
		listenerPool.remove(taskStatusListener.getTaskId());
	}

	private class RunnableLog implements Runnable {
		@Override
		public void run() {
			while (!listenerPool.isEmpty()) {
				StringBuilder logBuilder = new StringBuilder("");
				int index = 0;
				for (TaskStatusListener taskStatusListener : listenerPool.values()) {
					index ++;
					TaskInfo taskInfo = taskStatusListener.fetchInfo();
					if (taskInfo != null) {
						logBuilder.append(taskInfo.fullInfo());
						if (index < listenerPool.size()) {
							logBuilder.append("\n");
						}
					}
				}
				ConsoleLog.e(logBuilder.toString());
				try {
					Thread.sleep(LOG_SLEEP);
				} catch (InterruptedException ie) {
					executor.execute(new RunnableLog());
					break;
				}
			}
		}
	}

	public static void registerListener(TaskStatusListener taskStatusListener) {
		INSTANCE.putListener(taskStatusListener);
	}

	public static void unRegisterListener(TaskStatusListener taskStatusListener) {
		INSTANCE.removeListener(taskStatusListener);
	}
}
