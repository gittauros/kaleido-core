package com.tauros.kaleido.core.constant;

import java.util.regex.Pattern;

/**
 * Created by tauros on 2016/4/9.
 */
public interface SpiderConstant {

    Pattern LOFI_E_HENTAI = Pattern.compile("^http://lofi\\.e-hentai\\.org/[gs]/[^/]*?/[^/]*?/?$");
    Pattern EX_HENTAI     = Pattern.compile("^http://([^.]+\\.)?exhentai\\.org/[gs]/[^/]*?/[^/]*?/?$");

    String LOFIE_ACCEPT          = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    String LOFIE_ACCEPT_ENCODING = "gzip, deflate, sdch";
    String LOFIE_ACCEPT_LANGUAGE = "zh-CN,zh;q=0.8";

    String E_ACCEPT          = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    String E_ACCEPT_ENCODING = "gzip, deflate";
    String E_ACCEPT_LANGUAGE = "zh-CN,zh;q=0.8";

    String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36";

    String REQUEST_LOGIN_COOKIE = "ipb_coppa=0; __utmt=1; __utma=185428086.1588729409.1451198823.1451198823.1451198823.1; __utmb=185428086.1.10.1451198823; __utmc=185428086; __utmz=185428086.1451198823.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)";

}
