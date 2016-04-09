package com.tauros.kaleido.core.test;

import com.tauros.kaleido.core.download.DownloaderInfo;
import com.tauros.kaleido.core.task.TaskInfo;

import java.io.Serializable;

/**
 * Created by tauros on 2016/4/9.
 */
public class Demo implements Serializable {
	private static final long serialVersionUID = -2777120453873339539L;


	static class FinalInfo extends DownloaderInfo {
		@Override
		protected String selfInfo() {
			return "c";
		}
	}

	public static void main(String[] args) {
		TaskInfo<TaskInfo> taskInfo = new TaskInfo<>();
		DownloaderInfo<DownloaderInfo> downloaderInfo = new DownloaderInfo<>();
		FinalInfo finalInfo = new FinalInfo();
		taskInfo.setExtraInfo(downloaderInfo);
		downloaderInfo.setExtraInfo(finalInfo);
		System.out.println(taskInfo.fullInfo());
	}
}
