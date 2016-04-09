package com.tauros.kaleido.core.download;

import com.tauros.kaleido.core.task.TaskInfo;

/**
 * Created by tauros on 2016/4/9.
 */
public class DownloaderInfo<E extends DownloaderInfo> extends TaskInfo {

	private String url;
	private String fileName;
	private String downloadPercentage;
	private String downloaderMessage;
	private E      extraInfo;

	@Override
	protected String selfInfo() {
		StringBuilder selfInfoBuilder = new StringBuilder("Downloader:\n");
		selfInfoBuilder.append("process:").append(downloadPercentage).append(", ")
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

	public String getDownloadPercentage() {
		return downloadPercentage;
	}

	public void setDownloadPercentage(String downloadPercentage) {
		this.downloadPercentage = downloadPercentage;
	}

	public String getDownloaderMessage() {
		return downloaderMessage;
	}

	public void setDownloaderMessage(String downloaderMessage) {
		this.downloaderMessage = downloaderMessage;
	}
}
