package com.tauros.kaleido.core.spider.impl;

import com.tauros.kaleido.core.constant.SpiderConstant;
import com.tauros.kaleido.core.spider.DocumentSpider;
import com.tauros.kaleido.core.util.ConsoleLog;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by tauros on 2015/7/14.
 */
public class LofiEHentaiHttpDocumentSpider implements DocumentSpider, SpiderConstant {

	protected HttpHost getProxy() {
//		if (ProxySettings.isReady()) {
//			HttpHost proxy = new HttpHost(ProxySettings.getProxyIp(), ProxySettings.getPort());
//			return proxy;
//		}
		return null;
	}

	protected RequestConfig getConfig() {
		RequestConfig.Builder builder = RequestConfig.custom();
		HttpHost proxy = getProxy();
		if (proxy != null) {
			builder = builder.setProxy(proxy);
		}
		builder.setConnectTimeout(TIME_LIMIT);
		return builder.build();
	}

	protected HttpGet makeGet(String url, String cookie, String host, String referer) {
		HttpGet get = new HttpGet(url);
		get.setConfig(getConfig());
		get.addHeader("Accept", LOFIE_ACCEPT);
		get.addHeader("Accept-Encoding", LOFIE_ACCEPT_ENCODING);
		get.addHeader("Accept-Language", LOFIE_ACCEPT_LANGUAGE);
		get.addHeader("Cache-Control", "no-cache");
		get.addHeader("Connection", "keep-alive");
		get.addHeader("User-Agent", DEFAULT_USER_AGENT);
		if (cookie != null && !"".equals(cookie)) {
			get.addHeader("Cookie", cookie);
		}
		if (host != null && !"".equals(host)) {
			get.addHeader("Host", host);
		}
		if (referer != null && !"".equals(referer)) {
			get.addHeader("Referer", referer);
		}
		return get;
	}

	protected HttpResponse executeGet(HttpGet get) {
		HttpClientBuilder builder = HttpClientBuilder.create();
		HttpClient httpClient = builder.build();
		for (int i = 1; i <= RETRY_TIMES; i++) {
			try {
				HttpResponse response = httpClient.execute(get);
				return response;
			} catch (Exception e) {
				if (i < RETRY_TIMES) {
					ConsoleLog.e(" - 重试第" + i + "次, url=" + get.getURI());
				} else {
					ConsoleLog.e(" - 重试失败");
				}
			}
		}
		return null;
	}

	private String getBody(HttpResponse response) {
		if (response == null) {
			return null;
		}
		HttpEntity entity = response.getEntity();
		for (int i = 1; i <= RETRY_TIMES; i++) {
			InputStreamReader isReader = null;
			BufferedReader bufferReader = null;
			try {
				isReader = new InputStreamReader(entity.getContent());
				bufferReader = new BufferedReader(isReader);
				StringBuffer sb = new StringBuffer("");
				String buffer = "";
				while ((buffer = bufferReader.readLine()) != null) {
					sb = sb.append(buffer.trim());
				}

				return sb.toString();
			} catch (Exception e) {
				if (i < RETRY_TIMES) {
					ConsoleLog.e(" - 重试第" + i + "次, response=" + response, e);
				} else {
					ConsoleLog.e(" - 重试失败", e);
				}
			} finally {
				IOUtils.closeQuietly(isReader);
				IOUtils.closeQuietly(bufferReader);
			}
		}
		return null;
	}

	protected Document parseDocument(HttpResponse response) {
		String html = getBody(response);
		if (html == null) {
			return null;
		}
		Document document = Jsoup.parse(html);
		return document;
	}

	@Override
	public Document captureDocument(String url, String cookie, String host, String referer) {
		HttpGet get = makeGet(url, cookie, host, referer);
		HttpResponse response = executeGet(get);
		Document document = parseDocument(response);
		return document;
	}
}
