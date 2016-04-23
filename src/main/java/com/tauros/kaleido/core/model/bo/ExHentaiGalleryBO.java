package com.tauros.kaleido.core.model.bo;

/**
 * Created by tauros on 2016/4/23.
 */
public class ExHentaiGalleryBO {

	private String title;
	private int    smallImgXOffset;
	private int    smallImgYOffset;
	private String smallImg;
	private String smallImgPlaceHolder;
	private String largeImg;
	private String previewUrl;
	private String photoUrl;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSmallImgXOffset() {
		return smallImgXOffset;
	}

	public void setSmallImgXOffset(int smallImgXOffset) {
		this.smallImgXOffset = smallImgXOffset;
	}

	public int getSmallImgYOffset() {
		return smallImgYOffset;
	}

	public void setSmallImgYOffset(int smallImgYOffset) {
		this.smallImgYOffset = smallImgYOffset;
	}

	public String getSmallImg() {
		return smallImg;
	}

	public void setSmallImg(String smallImg) {
		this.smallImg = smallImg;
	}

	public String getSmallImgPlaceHolder() {
		return smallImgPlaceHolder;
	}

	public void setSmallImgPlaceHolder(String smallImgPlaceHolder) {
		this.smallImgPlaceHolder = smallImgPlaceHolder;
	}

	public String getLargeImg() {
		return largeImg;
	}

	public void setLargeImg(String largeImg) {
		this.largeImg = largeImg;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
}
