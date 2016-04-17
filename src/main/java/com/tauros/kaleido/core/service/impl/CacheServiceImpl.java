package com.tauros.kaleido.core.service.impl;

import com.tauros.kaleido.core.constant.CacheTypeConstant;
import com.tauros.kaleido.core.exception.KaleidoIllegalStateException;
import com.tauros.kaleido.core.kaleidolib.KaleidoCache;
import com.tauros.kaleido.core.kaleidolib.MemoryCalculator;
import com.tauros.kaleido.core.kaleidolib.SizeUnit;
import com.tauros.kaleido.core.service.CacheService;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by tauros on 2016/4/17.
 */
public class CacheServiceImpl implements CacheService, InitializingBean {

	private KaleidoCache<String, byte[]> imageCache;

	@Override
	public byte[] getByteArrayData(CacheTypeConstant type, String key) {
		switch (type) {
			case IMAGE:
				return imageCache.get(key);
			default:
				throw new KaleidoIllegalStateException("no such cache type");
		}
	}

	@Override
	public void putByteArrayData(CacheTypeConstant type, String key, byte[] data) {
		switch (type) {
			case IMAGE:
				imageCache.put(key, data);
				break;
			default:
				throw new KaleidoIllegalStateException("no such cache type");
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.imageCache = new KaleidoCache<>(SizeUnit.GIGABYTES, 1, new MemoryCalculator<byte[]>() {
			@Override
			public long calculate(byte[] obj) {
				if (obj == null) {
					return 0;
				}
				return obj.length;
			}
		});
	}
}
