package com.tauros.kaleido.core.model.bo;

/**
 * Created by tauros on 2016/4/23.
 */
public class ExHentaiPhotoBO {

	private String photoImg;
	private int curPage;
	private int lastPage;
	private String firstPageUrl;
	private String prevPageUrl;
	private String nextPageUrl;
	private String lastPageUrl;

	public String getPhotoImg() {
		return photoImg;
	}

	public void setPhotoImg(String photoImg) {
		this.photoImg = photoImg;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	public String getFirstPageUrl() {
		return firstPageUrl;
	}

	public void setFirstPageUrl(String firstPageUrl) {
		this.firstPageUrl = firstPageUrl;
	}

	public String getPrevPageUrl() {
		return prevPageUrl;
	}

	public void setPrevPageUrl(String prevPageUrl) {
		this.prevPageUrl = prevPageUrl;
	}

	public String getNextPageUrl() {
		return nextPageUrl;
	}

	public void setNextPageUrl(String nextPageUrl) {
		this.nextPageUrl = nextPageUrl;
	}

	public String getLastPageUrl() {
		return lastPageUrl;
	}

	public void setLastPageUrl(String lastPageUrl) {
		this.lastPageUrl = lastPageUrl;
	}
}
