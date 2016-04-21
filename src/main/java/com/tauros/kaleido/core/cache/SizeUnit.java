package com.tauros.kaleido.core.cache;

import com.tauros.kaleido.core.exception.KaleidoIllegalStateException;

/**
 * Created by tauros on 2016/4/17.
 */
public enum SizeUnit {
	BYTES,
	KILOBYTES,
	MEGABYTES,
	GIGABYTES;

	public static final int TIMES = 1024;

	public static long convertToBytes(SizeUnit unit, long memorySize) {
		if (unit == null || memorySize < 0) {
			throw new KaleidoIllegalStateException("unit is null or memory size is negative");
		}
		switch (unit) {
			case BYTES:
				return memorySize;
			case KILOBYTES:
				return convertToBytes(BYTES, memorySize * TIMES);
			case MEGABYTES:
				return convertToBytes(KILOBYTES, memorySize * TIMES);
			case GIGABYTES:
				return convertToBytes(MEGABYTES, memorySize * TIMES);
			default:
				throw new KaleidoIllegalStateException("no such size unit");
		}
	}
}
