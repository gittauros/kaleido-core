package com.tauros.kaleido.core.constant;

/**
 * Created by tauros on 2016/4/17.
 */
public enum CacheTypeConstant {

	IMAGE("image", 1000);

	private String type;
	private int cacheSize;

	CacheTypeConstant(String type, int cacheSize) {
		this.type = type;
		this.cacheSize = cacheSize;
	}

	public int getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
