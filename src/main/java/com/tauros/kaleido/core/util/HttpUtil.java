package com.tauros.kaleido.core.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by tauros on 2016/4/10.
 */
public final class HttpUtil {

	private static final int CONNECTION_TIMEOUT = 6000;
	private static final int READ_TIMEOUT = 50000;

	public static URLConnection openConnection(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		//设置代理
//		Proxy proxy = null;
//		if (ProxySettings.isReady()) {
//			InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName(ProxySettings.getProxyIp()), ProxySettings.getPort());
//			proxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);
//		}

		URLConnection connection;
//		if (proxy == null) {
		connection = url.openConnection();
//		} else {
//			connection = url.openConnection(proxy);
//		}
		//设置超时时间
		connection.setConnectTimeout(CONNECTION_TIMEOUT);
		//设置读取超时时间
		connection.setReadTimeout(READ_TIMEOUT);
		return connection;
	}

	public static void setRequestProperty(URLConnection connection, Map<String, String> requestProperty) {
		if (connection == null || requestProperty == null) {
			return;
		}
		if (!requestProperty.isEmpty()) {
			for (Map.Entry<String, String> entry : requestProperty.entrySet()) {
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}
}
