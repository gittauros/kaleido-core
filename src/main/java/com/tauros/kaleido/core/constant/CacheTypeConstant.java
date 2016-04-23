package com.tauros.kaleido.core.constant;

/**
 * Created by tauros on 2016/4/17.
 */
public enum CacheTypeConstant {

	IMAGE("image"),
	HTML("html");

	private String type;

	CacheTypeConstant(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
