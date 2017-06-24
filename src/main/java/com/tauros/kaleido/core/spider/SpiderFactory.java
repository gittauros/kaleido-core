package com.tauros.kaleido.core.spider;

import com.tauros.kaleido.core.constant.SpiderConstant;
import com.tauros.kaleido.core.exception.KaleidoException;
import com.tauros.kaleido.core.spider.impl.ExHentaiJsoupCookieDocumentSpider;
import com.tauros.kaleido.core.spider.impl.LofiEHentaiHttpDocumentSpider;

/**
 * Created by tauros on 2015/7/14.
 */
public final class SpiderFactory implements SpiderConstant {

    private SpiderFactory() {
        throw new KaleidoException("该类不应被实例化");
    }

    public static DocumentSpider getSpider() {
        return new LofiEHentaiHttpDocumentSpider();
    }

    public static DocumentSpider getSpider(String url) {
        if (LOFI_E_HENTAI.matcher(url).find()) {
            return new LofiEHentaiHttpDocumentSpider();
        }
        if (EX_HENTAI.matcher(url).find()) {
            return new ExHentaiJsoupCookieDocumentSpider();
        }
        return null;
    }
}
