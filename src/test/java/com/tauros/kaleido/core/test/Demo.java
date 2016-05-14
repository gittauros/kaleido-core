package com.tauros.kaleido.core.test;

import com.tauros.kaleido.core.cache.KaleidoCache;
import com.tauros.kaleido.core.cache.SizeUnit;
import org.apache.http.util.Asserts;

import java.io.Serializable;

import static com.tauros.kaleido.core.util.KaleidoCodec.*;

/**
 * Created by tauros on 2016/4/9.
 */
public class Demo implements Serializable {
	private static final long serialVersionUID = -2777120453873339539L;

	static KaleidoCache<String, byte[]> cache = new KaleidoCache<>(SizeUnit.MEGABYTES, 100, (obj) -> obj.length);

	public static void main(String[] args) throws Exception {
		String str = "http://exhentai.org/g/876856/8ecfc6e3e3/";
		String str2 = "http://exhentai.org/g/702219/30151902b0/";

		int TIMES = 1000000;

		String encode = "\n";
		long time = System.currentTimeMillis();
		for (int i = 0; i < TIMES; i++) {
			encode = Base64.URL.encode((i & 1) == 1 ? str : str2);
		}
		System.out.println(System.currentTimeMillis() - time);
		System.out.println(encode);


		String decode = "\n";
		time = System.currentTimeMillis();
		for (int i = 0; i < TIMES; i++) {
			decode = Base64.URL.decode(encode);
		}
		System.out.println(System.currentTimeMillis() - time);
		System.out.println(decode);
	}
}
