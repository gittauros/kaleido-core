package com.tauros.kaleido.core.service;

import com.tauros.kaleido.core.model.formbean.ExHentaiListParamBean;

import java.io.IOException;
import java.util.Map;

/**
 * Created by tauros on 2016/4/9.
 */
public interface ExHentaiService {

	String MAX_PAGE_KEY = "maxPage";
	String LIST_BO_KEY  = "listBOs";

	/**
	 * 抓取ex-hentai搜索页
	 *
	 * @return
	 */
	Map<String, Object> searchListPage(String contextPath, ExHentaiListParamBean paramBean);

	/**
	 * 下载ex-hentai相册页图片
	 *
	 * @param saveBasePath
	 * @param url
	 * @param sleep
	 * @param origin
	 * @return
	 */
	String download(String saveBasePath, String url, long sleep, boolean origin);

	/**
	 * 访问ex-hentai图片
	 *
	 * @param url
	 * @return
	 */
	byte[] image(String url);
}
