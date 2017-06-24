package com.tauros.kaleido.core.spider.impl;

import com.tauros.kaleido.core.constant.SpiderConstant;
import com.tauros.kaleido.core.util.ConsoleLog;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tauros on 2015/12/27.
 */
public class ExHentaiJsoupCookieDocumentSpider extends AbstractJsoupCookieDocumentSpider implements SpiderConstant {

    private enum CookieMode {
        HARDCODE, AUTO_LOGIN
    }

    private CookieMode cookieMode = CookieMode.HARDCODE;
    //    private String hardcodeCookie = "__utma=185428086.917164177.1448776649.1448776649.1448776649.1; __utmz=185428086.1448776649.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); event=1448776679; ipb_member_id=1599448; ipb_pass_hash=455815f3429e5b1382d41e9b7d3b50d4; igneous=4861c41adf496a2c27443eb9376c131c29add7523c20d436a5404eb5e3986f2843162c1b3dc1ec0fdc954e1cee57cde90e4d113d4b272eb38df2b4fccdff7ddd; s=b79a5cc244d60f4bb035dfffe9aae4217c34f547c8625f35d34369c96243bec30c00a0e88b64dc3d9c9eba3eb48f2fbc8fe47b0fe24bb05dbbf01d3f3ddd3587; uconfig=tl_m-uh_y-rc_0-cats_0-xns_0-ts_m-tr_2-prn_y-dm_l-ar_0-rx_0-ry_0-ms_n-mt_n-cs_a-to_a-pn_0-sc_0-sa_y-oi_n-qb_n-tf_n-hp_-hk_-xl_; lv=1451198567-1451200954";
    private static String hardcodeCookie;
    private static File   cookieFile;

    static {
        Logger staticLogger = LoggerFactory.getLogger(ExHentaiJsoupCookieDocumentSpider.class);
        try {
            cookieFile = new File(ExHentaiJsoupCookieDocumentSpider.class.getClassLoader().getResource("config/exhentai.cookie").getPath());
            if (cookieFile.exists() && !cookieFile.isDirectory()) {
                FileReader fr = null;
                BufferedReader br = null;
                try {
                    fr = new FileReader(cookieFile);
                    br = new BufferedReader(fr);
                    hardcodeCookie = br.readLine();
                } catch (IOException ioe) {
                    staticLogger.error("read cookie file exception", ioe);
                } finally {
                    IOUtils.closeQuietly(fr);
                    IOUtils.closeQuietly(br);
                }
            } else {
                staticLogger.error("cookie file did not exist");
            }
        } catch (Exception e) {
            staticLogger.error("initialize exhentai-cookie exception", e);
        }
    }

    public ExHentaiJsoupCookieDocumentSpider setHardcodeCookie(String cookie) {
        if (cookie != null) {
            hardcodeCookie = cookie;
        }
        return this;
    }

    public ExHentaiJsoupCookieDocumentSpider setCookieMode(CookieMode cookieMode) {
        this.cookieMode = cookieMode;
        return this;
    }

    @Override
    protected boolean setCookie(Map<String, String> cookieMap) {
        if (cookieMap != null && !cookieMap.isEmpty()) {
            memoryCookieMap.putAll(cookieMap);
            hardcodeCookie = getCookieString();
            if (cookieFile != null && cookieFile.exists() && !cookieFile.isDirectory()) {
                FileWriter fw = null;
                BufferedWriter bw = null;
                try {
                    fw = new FileWriter(cookieFile);
                    bw = new BufferedWriter(fw);
                    bw.write(hardcodeCookie);
                    bw.flush();
                } catch (IOException ioe) {
                    ConsoleLog.e(" - cookie文件写入异常", ioe);
                } finally {
                    IOUtils.closeQuietly(fw);
                    IOUtils.closeQuietly(bw);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String getCookieString() {
        StringBuffer sb = new StringBuffer("");
        for (Map.Entry<String, String> kv : memoryCookieMap.entrySet()) {
            sb.append(kv.getKey()).append("=").append(kv.getValue()).append("; ");
        }
        return sb.toString();
    }

    @Override
    protected Connection makeConnection(String url, String cookie, String host, String referer) {
        if (!StringUtils.isBlank(cookie)) {
            if (cookiePattern.matcher(cookie).find()) {
                memoryCookieMap.clear();
                String[] tempSplitStrs = cookie.split(";");
                for (String kv : tempSplitStrs) {
                    try {
                        int index = kv.indexOf("=");
                        if (index != -1) {
                            String key = kv.substring(0, index).trim();
                            String value = kv.substring(index + 1, kv.length()).trim();
                            memoryCookieMap.put(key, value);
                        }
                    } catch (Exception e) {
                        logger.error(String.format("cookie convert exception kv:[%s]", kv), e);
                    }
                }
            }
        }
        if (!memoryCookieMap.isEmpty()) {
            cookie = getCookieString();
        }
        return super.makeConnection(url, cookie, host, referer);
    }

    public ExHentaiJsoupCookieDocumentSpider login(String userName, String password) {
        post(null);
        memoryPostData.put("UserName", userName);
        memoryPostData.put("PassWord", password);
        setCookieMode(CookieMode.AUTO_LOGIN);
        return this;
    }

    public Document captureDocument(String url, String host, String referer) {
        if (memoryCookieMap.isEmpty()) {
            if (cookieMode == CookieMode.HARDCODE) {
                if (hardcodeCookie == null) {
                    throw new RuntimeException("hardcodeCookie=null");
                }
                clearPostData();
                get();
                hardcodeCookieCaptureDocument("http://exhentai.org/", "exhentai.org", null);
            } else if (cookieMode == CookieMode.AUTO_LOGIN) {
                Map<String, String> map = new HashMap<>();
                map.put("returntype", "8");
                map.put("CookieDate", "1");
                map.put("b", "d");
                map.put("bt", "pone");
                map.put("ipb_login_submit", "Login!");
                post(map);
                captureDocument("https://forums.e-hentai.org/index.php?act=Login&CODE=01", REQUEST_LOGIN_COOKIE, "forums.e-hentai.org", "http://e-hentai.org");
                clearPostData();
                get();
                captureDocument("http://e-hentai.org/", null, "e-hentai.org", null);
            } else {
                throw new RuntimeException("cookieMode is unknown mode");
            }
            clearPostData();
            get();
        }
        return captureDocument(url, null, host, referer);
    }

    public Document hardcodeCookieCaptureDocument(String url, String host, String referer) {
        return captureDocument(url, hardcodeCookie, host, referer);
    }
}
