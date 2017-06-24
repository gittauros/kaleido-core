package com.tauros.kaleido.core.download.impl;

import com.tauros.kaleido.core.download.Downloader;
import com.tauros.kaleido.core.download.DownloaderInfo;
import com.tauros.kaleido.core.download.DownloaderStatus;
import com.tauros.kaleido.core.exception.KaleidoUnsupportedException;
import com.tauros.kaleido.core.task.TaskInfo;
import com.tauros.kaleido.core.task.impl.AbstractTask;

/**
 * Created by tauros on 2016/4/9.
 */
public abstract class AbstractDownloader extends AbstractTask implements Downloader {

    private DownloaderStatus status;

    public AbstractDownloader() {
        super();
        this.status = DownloaderStatus.CREATE;
    }

    @Override
    public final DownloaderStatus status() {
        return this.status;
    }

    private void setStatus(DownloaderStatus status) {
        this.status = status;
        updateStatus();
    }

    @Override
    public final boolean process() {
        this.processed = true;
        setStatus(DownloaderStatus.PREPARING);
        boolean preResult = preDownload();
        if (!preResult) {
            setStatus(DownloaderStatus.PREPARE_FAILED);
            if (isRollbackSupported()) {
                rollback();
            }
            return false;
        }

        setStatus(DownloaderStatus.DOWNLOADING);
        boolean processResult = processDownload();
        if (!processResult) {
            setStatus(DownloaderStatus.DOWNLOAD_FAILED);
            if (isRollbackSupported()) {
                rollback();
            }
            return false;
        }

        setStatus(DownloaderStatus.DOWNLOADED);
        boolean finalizeResult = afterDownload();
        if (!finalizeResult) {
            setStatus(DownloaderStatus.FINALIZE_FAILED);
            if (isRollbackSupported()) {
                rollback();
            }
            return false;
        }

        setStatus(DownloaderStatus.FINISHED);
        return true;
    }


    @Override
    protected void setTaskInfo(TaskInfo<TaskInfo> taskInfo) {
        DownloaderInfo<DownloaderInfo> downloaderInfo = new DownloaderInfo<>();
        setDownloaderInfo(downloaderInfo);
        taskInfo.setExtraInfo(downloaderInfo);
    }

    protected abstract void setDownloaderInfo(DownloaderInfo<DownloaderInfo> downloaderInfo);

    @Override
    public boolean isSuccess() {
        return this.status == DownloaderStatus.FINISHED;
    }

    @Override
    public final boolean isCancelSupported() {
        return super.isCancelSupported();
    }

    @Override
    public final void cancel() throws KaleidoUnsupportedException {
        super.cancel();
    }
}
