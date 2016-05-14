package com.tauros.kaleido.core.test;

import com.tauros.kaleido.core.cache.KaleidoCache;
import com.tauros.kaleido.core.cache.SizeUnit;
import com.tauros.kaleido.core.util.KaleidoCodec;

import java.io.Serializable;

import static com.tauros.kaleido.core.util.KaleidoCodec.*;

/**
 * Created by tauros on 2016/4/9.
 */
public class Demo implements Serializable {
	private static final long serialVersionUID = -2777120453873339539L;

	static KaleidoCache<String, byte[]> cache = new KaleidoCache<>(SizeUnit.MEGABYTES, 100, (obj) -> obj.length);

	public static void main(String[] args) throws Exception {
		String str = "a";

		for (int i = 0;i < 2; i ++) {
			str = encode(str);
		}

		System.out.println(str);
	}

	public static String encode(String source) throws Exception {
		return Base64.URL.encode(source);
	}
}
