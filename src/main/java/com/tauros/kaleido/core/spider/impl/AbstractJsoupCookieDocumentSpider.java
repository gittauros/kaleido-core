package com.tauros.kaleido.core.spider.impl;

import com.tauros.kaleido.core.constant.SpiderConstant;
import com.tauros.kaleido.core.spider.DocumentSpider;
import com.tauros.kaleido.core.util.ConsoleLog;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by tauros on 2015/12/27.
 */
public abstract class AbstractJsoupCookieDocumentSpider implements DocumentSpider, SpiderConstant {

	protected       ConcurrentHashMap<String, String> memoryCookieMap  = new ConcurrentHashMap<>();
	protected       ConcurrentHashMap<String, String> memoryPostData   = new ConcurrentHashMap<>();
	protected       Connection.Method                 connectionMethod = Connection.Method.GET;
	protected final Pattern                           cookiePattern    = Pattern.compile("^\\s*([^\\s;]*;\\s*)*([^\\s;]*;?)\\s*$");

	protected abstract boolean setCookie(Map<String, String> cookieMap);

	public abstract String getCookieString();

	public AbstractJsoupCookieDocumentSpider get() {
		connectionMethod = Connection.Method.GET;
		return this;
	}

	public AbstractJsoupCookieDocumentSpider post(Map<String, String> postData) {
		connectionMethod = Connection.Method.POST;
		if (postData != null) {
			memoryPostData.putAll(postData);
		}
		return this;
	}

	public AbstractJsoupCookieDocumentSpider clearPostData() {
		memoryPostData.clear();
		return this;
	}

	protected Document parseDocument(Connection.Response response) {
		try {
			Document document = response.parse();
			return document;
		} catch (Exception e) {
			ConsoleLog.e(" - 转换文档失败", e);
			return null;
		}
	}

	protected Connection.Response executeConnection(Connection connection) {
		for (int i = 1; i <= RETRY_TIMES; i++) {
			try {
				Connection.Response response = connection.execute();
				return response;
			} catch (Exception e) {
				if (i < RETRY_TIMES) {
					ConsoleLog.e(" - 重试第" + i + "次, url=" + connection.request().url().toString(), e);
				} else {
					ConsoleLog.e(" - 重试失败", e);
				}
			}
		}
		return null;
	}

	protected Connection makeConnection(String url, String cookie, String host, String referer) {
		Connection connection = Jsoup.connect(url)
				.timeout(TIME_LIMIT)
				.header("Accept", E_ACCEPT)
				.header("Accept-Encoding", E_ACCEPT_ENCODING)
				.header("Accept-Language", E_ACCEPT_LANGUAGE)
				.header("KaleidoCache-Control", "no-cache")
				.header("Connection", "keep-alive")
				.header("User-Agent", DEFAULT_USER_AGENT)
				.method(connectionMethod);

		if (connectionMethod == Connection.Method.POST && memoryPostData != null && !memoryPostData.isEmpty()) {
			connection.data(memoryPostData);
		}

		if (cookie != null && !"".equals(cookie)) {
			connection.header("Cookie", cookie);
		}
		if (host != null && !"".equals(host)) {
			connection.header("Host", host);
		}
		if (referer != null && !"".equals(referer)) {
			connection.header("Referer", referer);
		}
		return connection;
	}

	@Override
	public Document captureDocument(String url, String cookie, String host, String referer) {
		Connection connection = makeConnection(url, cookie, host, referer);
		Connection.Response response = executeConnection(connection);
		if (response != null) {
			setCookie(response.cookies());
		}
		Document document = parseDocument(response);
		return document;
	}
}
