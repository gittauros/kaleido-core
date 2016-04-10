package com.tauros.kaleido.core.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by tauros on 2016/4/10.
 */
public final class SystemUtils {

	/**
	 * 获取用户文件夹路径
	 *
	 * @return
	 */
	public static String getUserHomePath() {
		String userHomePath = System.getProperty("user.home");
		userHomePath = userHomePath.replaceAll("\\\\", "/");
		return userHomePath;
	}

	/**
	 * 获取保存路径文件的文件名
	 *
	 * @return
	 */
	public static String getSavePathFileName() {
		return getUserHomePath() + "/kaleido_save_path.info";
	}

	/**
	 * 获取默认保存路径
	 *
	 * @return
	 */
	public static String getDefaultSavePath() {
		return getUserHomePath() + "/myBenz";
	}

	private static File savePathFile;

	private static AtomicBoolean savePathCacheInitialized = new AtomicBoolean(false);
	private static String        cachedSavePath           = "";

	private static void initSavePath() {
		savePathFile = new File(getSavePathFileName());
		if (!savePathFile.exists()) {
			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(savePathFile);
				fileWriter.write(getDefaultSavePath());
				fileWriter.flush();
			} catch (IOException ioe) {
				ConsoleLog.e("写入保存路径文件异常", ioe);
			} finally {
				IOUtils.closeQuietly(fileWriter);
			}
		} else {
			FileReader fileReader = null;
			BufferedReader bufferedReader = null;
			try {
				fileReader = new FileReader(savePathFile);
				bufferedReader = new BufferedReader(fileReader);
				String savePath = bufferedReader.readLine();
				cachedSavePath = savePath;
			} catch (IOException ioe) {
				ConsoleLog.e("读取保存路径文件异常", ioe);
			} finally {
				IOUtils.closeQuietly(fileReader);
				IOUtils.closeQuietly(bufferedReader);
			}
		}
	}

	/**
	 * 获取下载文件保存路径
	 *
	 * @return
	 */
	public static String getSavePath() {
		if (!savePathCacheInitialized.get()) {
			initSavePath();
			savePathCacheInitialized.set(true);
		}
		if (StringUtils.isBlank(cachedSavePath)) {
			return getDefaultSavePath();
		}
		return cachedSavePath;
	}

	/**
	 * 设置下载文件保存路径
	 *
	 * @param savePath
	 */
	public synchronized static boolean setSavePath(String savePath) {
		File testFile = new File(savePath);
		if (testFile.exists() && !testFile.isDirectory()) {
			ConsoleLog.e("savePath 不是 文件路径");
			return false;
		}
		cachedSavePath = savePath;
		if (savePathFile == null || !savePathFile.exists()) {
			return false;
		}
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(savePathFile);
			fileWriter.write(savePath);
			fileWriter.flush();
			return true;
		} catch (IOException ioe) {
			ConsoleLog.e("写入保存路径文件异常", ioe);
			return false;
		} finally {
			IOUtils.closeQuietly(fileWriter);
		}
	}
}
