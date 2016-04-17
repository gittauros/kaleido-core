package com.tauros.kaleido.core.service;

import com.tauros.kaleido.core.constant.CacheTypeConstant;

/**
 * Created by tauros on 2016/4/17.
 */
public interface CacheService {

	byte[] getByteArrayData(CacheTypeConstant type, String key);

	void putByteArrayData(CacheTypeConstant type, String key, byte[] data);
}
