package com.tauros.kaleido.core.service;

import com.tauros.kaleido.core.model.bean.ExHentaiListParamBean;

import java.util.Map;

/**
 * Created by tauros on 2016/4/9.
 */
public interface ExHentaiService {

	/**
	 * 抓取ex-hentai搜索页
	 *
	 * @return
	 */
	Map<String, Object> searchListPage(ExHentaiListParamBean paramBean);

	/**
	 * 抓取ex-hentai相册页
	 *
	 * @param url
	 * @param large
	 * @param page
	 * @return
	 */
	Map<String, Object> galleryPage(String url, boolean large, int page);

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
