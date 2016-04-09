package com.tauros.kaleido.core.task;

import java.util.Comparator;
import java.util.concurrent.*;

/**
 * Created by tauros on 2016/4/9.
 */
public enum TaskManager {

	INSTANCE;

	private Executor                    executor;
	private PriorityBlockingQueue<Task> taskPriorityBlockingQueue;

	TaskManager() {
		init();
	}

	private void init() {
		executor = new ThreadPoolExecutor(5, 15, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
		taskPriorityBlockingQueue = new PriorityBlockingQueue<>(50, new Comparator<Task>() {
			@Override
			public int compare(Task o1, Task o2) {
				if (o1.isReady() && o2.isReady()) {
					return 0;
				} else if (o1.isReady() && !o2.isReady()) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	/**
	 * 管理器添加任务
	 *
	 * @param task
	 */
	public static void putTask(Task task) {
		INSTANCE.addTask(task);
	}

	private void addTask(Task task) {
		if (task == null) {
			return;
		}
		taskPriorityBlockingQueue.add(task);
		executor.execute(new RunnableTask());
	}

	private class RunnableTask implements Runnable {
		@Override
		public void run() {
			if (taskPriorityBlockingQueue == null || taskPriorityBlockingQueue.isEmpty()) {
				return;
			}
			Task task = taskPriorityBlockingQueue.poll();
			if (task == null) {
				return;
			}
			if (!task.isReady()) {
				addTask(task);
				return;
			}
			if (!task.isProcessed()) {
				task.process();
			}
			if (!task.isSuccess()) {
				if (task.isRetryAble()) {
					Task retryTask = task.obtain();
					if (retryTask != null) {
						addTask(retryTask);
					}
				} else if (task.isRollbackSupported()) {
					task.rollback();
				}
			}
			task.unRegisterStatusListener();
		}
	}
}
