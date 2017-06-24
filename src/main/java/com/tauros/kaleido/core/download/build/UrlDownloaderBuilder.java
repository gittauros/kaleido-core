package com.tauros.kaleido.core.download.build;

import com.tauros.kaleido.core.download.impl.UrlDownloader;
import com.tauros.kaleido.core.exception.KaleidoIllegalStateException;
import com.tauros.kaleido.core.task.TaskBuilder;
import com.tauros.kaleido.core.task.TaskStatusListener;

import java.util.Map;

/**
 * Created by tauros on 2016/4/9.
 */
public final class UrlDownloaderBuilder implements TaskBuilder<UrlDownloader> {

    private String              filePath;
    private String              fileName;
    private String              url;
    private int                 retryTimes;
    private TaskStatusListener  taskStatusListener;
    private Map<String, String> requestProperty;

    public UrlDownloaderBuilder setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public UrlDownloaderBuilder setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public UrlDownloaderBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public UrlDownloaderBuilder setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public UrlDownloaderBuilder setTaskStatusListener(TaskStatusListener taskStatusListener) {
        this.taskStatusListener = taskStatusListener;
        return this;
    }

    public UrlDownloaderBuilder setRequestProperty(Map<String, String> requestProperty) {
        this.requestProperty = requestProperty;
        return this;
    }

    @Override
    public UrlDownloader build() throws KaleidoIllegalStateException {
        if (fileName == null || filePath == null || url == null) {
            throw new KaleidoIllegalStateException("fileName or filePath or url is null");
        }
        UrlDownloader downloader = new UrlDownloader(filePath, fileName, url, retryTimes);
        if (requestProperty != null && !requestProperty.isEmpty()) {
            for (Map.Entry<String, String> entry : requestProperty.entrySet()) {
                downloader.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        if (taskStatusListener != null) {
            downloader.registerStatusListener(taskStatusListener);
        }
        downloader.ready();
        return downloader;
    }
}