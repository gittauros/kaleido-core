package com.tauros.kaleido.core.download;

import com.tauros.kaleido.core.task.Task;

/**
 * Created by tauros on 2016/4/9.
 */
public interface Downloader extends Task {

    /**
     * 下载前置工作
     *
     * @return
     */
    boolean preDownload();

    /**
     * 执行下载
     *
     * @return
     */
    boolean processDownload();

    /**
     * 下载后置工作
     *
     * @return
     */
    boolean afterDownload();

    /**
     * 获取状态
     *
     * @return
     */
    DownloaderStatus status();


}
