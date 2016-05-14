package com.tauros.kaleido.core.download.impl;

import com.tauros.kaleido.core.constant.DownloadConstant;
import com.tauros.kaleido.core.download.DownloaderInfo;
import com.tauros.kaleido.core.download.build.UrlDownloaderBuilder;
import com.tauros.kaleido.core.exception.KaleidoException;
import com.tauros.kaleido.core.exception.KaleidoIllegalStateException;
import com.tauros.kaleido.core.task.impl.SimpleTaskStatusListener;
import com.tauros.kaleido.core.util.ConsoleLog;
import com.tauros.kaleido.core.util.HttpUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.abs;

/**
 * Created by tauros on 2016/4/9.
 */
public final class UrlDownloader extends AbstractDownloader implements DownloadConstant {

	static {
		System.setProperty("http.maxRedirects", "100");
	}

	private static ConcurrentHashMap<String, CopyOnWriteArrayList<String>> downloadedLog = new ConcurrentHashMap<String, CopyOnWriteArrayList<String>>();

	private final HashMap<String, String> requestProperty = new HashMap<>();

	private final String url;

	private long fileLength = -1;
	private long processLength;
	private boolean chunked;

	private final String filePath;
	private final String fileName;
	private       int    retryTimes;
	private boolean retryAble;

	private String downloaderMessage;

	public UrlDownloader(String filePath, String fileName, String url, int retryTimes) {
		super();
		this.filePath = filePath;
		this.fileName = fileName;
		this.retryTimes = retryTimes;
		this.url = url;
		this.retryAble = true;
	}

	public void ready() {
		this.ready = true;
	}

	private void updateStatus(String message) {
		downloaderMessage = message;
		super.updateStatus();
	}

	@Override
	protected void setDownloaderInfo(DownloaderInfo<DownloaderInfo> downloaderInfo) {
		downloaderInfo.setUrl(this.url);
		downloaderInfo.setFileName(this.filePath + "/" + this.fileName);
		downloaderInfo.setFileLength(this.fileLength);
		downloaderInfo.setProcessLength(this.processLength);
		downloaderInfo.setDownloaderMessage(this.downloaderMessage);
		downloaderInfo.setChunked(chunked);
	}

	/**
	 * 从文件读取已下载文件列表
	 *
	 * @return
	 */
	private CopyOnWriteArrayList<String> readDownloadedList() {
		CopyOnWriteArrayList<String> fileNameList = new CopyOnWriteArrayList<String>();
		try {
			File file = new File(filePath + "/downloaded.log");
			if (file.exists()) {
				List<String> tempList = FileUtils.readLines(file);
				fileNameList.addAll(tempList);
			}
		} catch (IOException e) {
			ConsoleLog.e(e);
			fileNameList = new CopyOnWriteArrayList<String>();
		}
		return fileNameList;
	}

	/**
	 * 获取已下载列表并放入缓存
	 *
	 * @return
	 */
	private CopyOnWriteArrayList<String> getDownloadedList() {
		CopyOnWriteArrayList<String> downloadedList = downloadedLog.get(filePath);
		if (downloadedList == null) {
			downloadedList = readDownloadedList();
			downloadedLog.put(filePath, downloadedList);
		}
		return downloadedList;
	}

	@Override
	public boolean preDownload() {
		updateStatus("准备下载工作");
		if (StringUtils.isBlank(filePath) || StringUtils.isBlank(fileName) || findDownloaded()) {
			ConsoleLog.e(url + " - 下载取消");
			this.retryAble = false;
			return false;
		}
		return true;
	}

	/**
	 * 查找文件是否已经被下载过
	 *
	 * @return
	 */
	private boolean findDownloaded() {
		CopyOnWriteArrayList<String> downloadedList = getDownloadedList();
		File findFile = new File(filePath + "/" + fileName);
		if (findFile.exists()) {
			for (String downloaded : downloadedList) {
				if (downloaded.equals(fileName)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean processDownload() {
		InputStream inputStream = null;
		BufferedInputStream bufferedInputStream = null;
		OutputStream outputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			File filePath = new File(this.filePath);
			if (!filePath.exists()) {
				boolean mkdirsResult = filePath.mkdirs();
				if (mkdirsResult) {
					updateStatus("文件夹创建成功");
				} else {
					updateStatus("文件夹创建失败");
					return false;
				}
			}

			URLConnection connection = HttpUtil.openConnection(this.url);
			HttpUtil.setRequestProperty(connection, requestProperty);
			inputStream = connection.getInputStream();
			updateStatus("成功获取网络输入流");
			bufferedInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE);
			outputStream = new FileOutputStream(this.filePath + "/" + this.fileName);
			bufferedOutputStream = new BufferedOutputStream(outputStream, BUFFER_SIZE);
			updateStatus("成功打开文件输出流");

			processLength = 0;

			if("chunked".equals(connection.getHeaderField("Transfer-Encoding"))) {
				this.chunked = true;
			} else {
				this.chunked = false;
				setFileLength(connection.getContentLengthLong());
				updateStatus("文件长度已确定");
			}

			byte[] buffer = new byte[LOOP_BUFFER_SIZE];
			while (true) {
				int len = bufferedInputStream.read(buffer);
				if (len == -1) {
					break;
				}
				bufferedOutputStream.write(buffer, 0, len);
				processLength += len;
				if (chunked) {
					setFileLength(processLength);
				}
				updateStatus("更新已下载文件长度");
			}
//			ConsoleLog.e(processLength + "/" + fileLength);
			if (processLength < fileLength) {
				updateStatus(String.format("processDownload fail processLength:{} fileLength:{}", processLength, fileLength));
				return false;
			} else if (processLength > fileLength) {
				throw new KaleidoIllegalStateException(String.format("processDownload > processLength {}/{}", processLength, fileLength));
			}
			bufferedOutputStream.flush();
			return true;
		} catch (IOException ioe) {
			ConsoleLog.e("processDownload exception url=" + url, ioe);
			updateStatus("下载失败");
			return false;
		} catch (KaleidoException ke) {
			ConsoleLog.e("processDownload exception url=" + url, ke);
			updateStatus("下载失败");
			return false;
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(bufferedInputStream);
			IOUtils.closeQuietly(outputStream);
			IOUtils.closeQuietly(bufferedOutputStream);
		}
	}

	private void setFileLength(long fileLength) {
		if (fileLength < 0) {
			throw new KaleidoIllegalStateException("file length incorrect fileLength=" + fileLength);
		}
		if (!this.chunked && this.fileLength != -1) {
			throw new KaleidoException("file length has been determined");
		}
		this.fileLength = fileLength;
	}

	@Override
	public boolean afterDownload() {
		writeDownloaded();
		updateStatus("下载完毕");
		return true;
	}

	/**
	 * 写入文件已经下载并放入缓存
	 */
	private void writeDownloaded() {
		FileOutputStream outputStream = null;
		File file = new File(filePath + "/downloaded.log");
		try {
			outputStream = new FileOutputStream(file, true);
			byte[] bytes = (fileName + "\n").getBytes();
			outputStream.write(bytes, 0, bytes.length);
			//写入缓存
			CopyOnWriteArrayList<String> downloadedList = getDownloadedList();
			downloadedList.add(fileName);
		} catch (IOException ioe) {
			ConsoleLog.e("写入文件下载成功出错", ioe);
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}

	@Override
	public UrlDownloader obtain() {
		if (this.retryTimes >= MAX_RETRY_TIMES) {
			updateStatus("重试已超过" + MAX_RETRY_TIMES + "次，下载失败");
			return null;
		}
		UrlDownloader downloader = new UrlDownloaderBuilder()
				.setFilePath(this.filePath)
				.setFileName(this.fileName)
				.setUrl(this.url)
				.setRetryTimes(this.retryTimes + 1)
				.setTaskStatusListener(new SimpleTaskStatusListener())
				.setRequestProperty(this.requestProperty)
				.build();
		updateStatus("重试 url=" + this.url + ", retryTimes=" + this.retryTimes + 1);
		return downloader;
	}

	@Override
	public boolean isRetryAble() {
		return this.retryAble & this.retryTimes < MAX_RETRY_TIMES;
	}

	public void clearRequestProperty() {
		requestProperty.clear();
	}

	public void addRequestProperty(String key, String val) {
		requestProperty.put(key, val);
	}
}
