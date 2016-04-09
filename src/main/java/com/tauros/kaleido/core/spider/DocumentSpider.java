package com.tauros.kaleido.core.spider;

import org.jsoup.nodes.Document;

/**
 * Created by tauros on 2015/7/14.
 */
public interface DocumentSpider {

	int TIME_LIMIT  = 10000;
	int RETRY_TIMES = 3;

	Document captureDocument(String url, String cookie, String host, String referer);
}
