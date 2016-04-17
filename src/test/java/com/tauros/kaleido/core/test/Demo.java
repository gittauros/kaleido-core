package com.tauros.kaleido.core.test;

import com.tauros.kaleido.core.kaleidolib.KaleidoCache;
import com.tauros.kaleido.core.kaleidolib.SizeUnit;

import java.io.Serializable;

/**
 * Created by tauros on 2016/4/9.
 */
public class Demo implements Serializable {
	private static final long serialVersionUID = -2777120453873339539L;

	static KaleidoCache<String, byte[]> cache =new KaleidoCache<String, byte[]>(SizeUnit.MEGABYTES, 100, (obj)->obj.length);

	public static void main(String[] args) throws Exception {
		cache.put("zhy", new byte[]{1});
		System.out.println(cache.get("zhy"));
	}
}
