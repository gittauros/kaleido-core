package com.tauros.kaleido.core.download;

/**
 * Created by tauros on 2016/4/9.
 */
public enum  DownloaderStatus {

	CREATE("create"),
	PREPARING("preparing"),
	DOWNLOADING("downloading"),
	PREPARE_FAILED("prepare_failed"),
	DOWNLOAD_FAILED("download_failed"),
	FINALIZE_FAILED("finalize_failed"),
	DOWNLOADED("downloaded"),
	FINISHED("finished");

	private String code;

	DownloaderStatus(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
