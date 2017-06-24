package com.tauros.kaleido.core.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by tauros on 2016/4/10.
 */
public final class SystemUtil {

    private static Logger logger = LoggerFactory.getLogger(SystemUtil.class);
    private static File savePathFile;
    private static boolean savePathCacheInitialized = false;
    private static String  cachedSavePath           = "";

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

    private synchronized static void initSavePath() {
        if (savePathFile != null) {
            return;
        }
        savePathFile = new File(getSavePathFileName());
        if (!savePathFile.exists()) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(savePathFile);
                fileWriter.write(getDefaultSavePath());
                fileWriter.flush();
            } catch (IOException ioe) {
                logger.error("initSavePath in write exception", ioe);
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
                logger.error("initSavePath in read exception", ioe);
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
        if (!savePathCacheInitialized) {
            initSavePath();
            savePathCacheInitialized = true;
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
            logger.warn(String.format("setSavePath exception:[%s is not directory]", savePath));
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
            logger.error("setSavePath write fail exception", ioe);
            return false;
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
    }
}
