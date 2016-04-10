package com.tauros.kaleido.core.download;

import com.tauros.kaleido.core.constant.DownloadConstant;
import com.tauros.kaleido.core.task.TaskInfo;

import static java.lang.Math.abs;

/**
 * Created by tauros on 2016/4/9.
 */
public class DownloaderInfo<E extends DownloaderInfo> extends TaskInfo implements DownloadConstant {

	private String url;
	private String fileName;
	private long   processLength;
	private long   fileLength;
	private String downloaderMessage;
	private E      extraInfo;

	@Override
	protected String selfInfo() {
		StringBuilder selfInfoBuilder = new StringBuilder("Downloader:\n");
		selfInfoBuilder.append("process:").append(DEFAULT_FORMAT.format(abs(100.0 * processLength / fileLength))).append("%")
				.append("(").append(abs(processLength)).append("/").append(abs(fileLength)).append("), ")
				.append("url=").append(url).append(", ")
				.append("fileName=").append(fileName).append("\n")
				.append("message:").append(downloaderMessage);
		return selfInfoBuilder.toString();
	}

	@Override
	protected String internalInfo() {
		return new StringBuilder()
				.append(selfInfo())
				.append(extraInfo == null ? "" : extraInfo.internalInfo()).toString();
	}

	public E getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(E extraInfo) {
		this.extraInfo = extraInfo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDownloaderMessage() {
		return downloaderMessage;
	}

	public void setDownloaderMessage(String downloaderMessage) {
		this.downloaderMessage = downloaderMessage;
	}

	public long getProcessLength() {
		return processLength;
	}

	public void setProcessLength(long processLength) {
		this.processLength = processLength;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}
}
