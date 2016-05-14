package com.tauros.kaleido.core.test;

import com.tauros.kaleido.core.cache.KaleidoCache;
import com.tauros.kaleido.core.cache.SizeUnit;
import com.tauros.kaleido.core.util.KaleidoCodec;
import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;


/**
 * Created by tauros on 2016/4/9.
 */
public class Demo implements Serializable {
	private static final long serialVersionUID = -2777120453873339539L;

	static KaleidoCache<String, byte[]> cache = new KaleidoCache<>(SizeUnit.MEGABYTES, 100, (obj) -> obj.length);

	public static void main(String[] args) throws Exception {

		byte[] bytes = new byte[]{1, 0};
		byte[] en = KaleidoCodec.Base64.URL.encode(bytes);
		en = KaleidoCodec.Base64.URL.decode(en);
		System.out.println(en);

		en = Base64.encodeBase64(bytes);
		en = Base64.decodeBase64(en);
		System.out.println(en);
	}
}
