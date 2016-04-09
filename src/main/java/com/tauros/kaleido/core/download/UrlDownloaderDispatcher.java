package com.tauros.kaleido.core.download;

import com.tauros.kaleido.core.download.build.UrlDownloaderBuilder;
import com.tauros.kaleido.core.download.impl.UrlDownloader;
import com.tauros.kaleido.core.task.TaskDispatcher;
import com.tauros.kaleido.core.task.TaskManager;
import com.tauros.kaleido.core.task.impl.SimpleTaskStatusListener;

import java.util.Map;

/**
 * Created by tauros on 2016/4/9.
 */
public final class UrlDownloaderDispatcher implements TaskDispatcher<UrlDownloader> {

	/**
	 * 分发下载任务
	 *
	 * @param filePath
	 * @param fileName
	 * @param url
	 */
	public void dispatch(String filePath, String fileName, String url) {
		UrlDownloader urlDownloader = new UrlDownloaderBuilder()
				.setFilePath(filePath)
				.setFileName(fileName)
				.setUrl(url)
				.setTaskStatusListener(new SimpleTaskStatusListener())
				.build();
		TaskManager.putTask(urlDownloader);
	}

	/**
	 * 分发下载任务
	 *
	 * @param filePath
	 * @param fileName
	 * @param url
	 */
	public void dispatch(String filePath, String fileName, String url, Map<String, String> requestProperty) {
		UrlDownloader urlDownloader = new UrlDownloaderBuilder()
				.setFilePath(filePath)
				.setFileName(fileName)
				.setUrl(url)
				.setTaskStatusListener(new SimpleTaskStatusListener())
				.setRequestProperty(requestProperty)
				.build();
		TaskManager.putTask(urlDownloader);
	}
}
