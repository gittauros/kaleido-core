package com.tauros.kaleido.core.model.formbean;

/**
 * Created by tauros on 2016/4/9.
 */
public class ExHentaiListParamBean {

	private int    page;
	private int    fDoujinshi;
	private int    fManga;
	private int    fArtistcg;
	private int    fGamecg;
	private int    fWestern;
	private int    fNonh;
	private int    fImageset;
	private int    fCosplay;
	private int    fAsianporn;
	private int    fMisc;
	private int    advsearch;
	private int    fSrdd;
	private String fSearch;
	private String fApply;
	private String fSname;
	private String fStags;

	public ExHentaiListParamBean() {
		this.page = 1;
		this.fDoujinshi = 1;
		this.fManga = 1;
		this.fArtistcg = 1;
		this.fGamecg = 1;
		this.fWestern = 1;
		this.fNonh = 1;
		this.fImageset = 1;
		this.fCosplay = 1;
		this.fAsianporn = 1;
		this.fMisc = 1;
		this.advsearch = 1;
		this.fSrdd = 2;
		this.fApply = "Apply+Filter";
		this.fSname = "on";
		this.fStags = "on";
		this.fSearch = "";
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getfDoujinshi() {
		return fDoujinshi;
	}

	public void setfDoujinshi(int fDoujinshi) {
		this.fDoujinshi = fDoujinshi;
	}

	public int getfManga() {
		return fManga;
	}

	public void setfManga(int fManga) {
		this.fManga = fManga;
	}

	public int getfArtistcg() {
		return fArtistcg;
	}

	public void setfArtistcg(int fArtistcg) {
		this.fArtistcg = fArtistcg;
	}

	public int getfGamecg() {
		return fGamecg;
	}

	public void setfGamecg(int fGamecg) {
		this.fGamecg = fGamecg;
	}

	public int getfWestern() {
		return fWestern;
	}

	public void setfWestern(int fWestern) {
		this.fWestern = fWestern;
	}

	public int getfNonh() {
		return fNonh;
	}

	public void setfNonh(int fNonh) {
		this.fNonh = fNonh;
	}

	public int getfImageset() {
		return fImageset;
	}

	public void setfImageset(int fImageset) {
		this.fImageset = fImageset;
	}

	public int getfCosplay() {
		return fCosplay;
	}

	public void setfCosplay(int fCosplay) {
		this.fCosplay = fCosplay;
	}

	public int getfAsianporn() {
		return fAsianporn;
	}

	public void setfAsianporn(int fAsianporn) {
		this.fAsianporn = fAsianporn;
	}

	public int getfMisc() {
		return fMisc;
	}

	public void setfMisc(int fMisc) {
		this.fMisc = fMisc;
	}

	public int getAdvsearch() {
		return advsearch;
	}

	public void setAdvsearch(int advsearch) {
		this.advsearch = advsearch;
	}

	public int getfSrdd() {
		return fSrdd;
	}

	public void setfSrdd(int fSrdd) {
		this.fSrdd = fSrdd;
	}

	public String getfSearch() {
		return fSearch;
	}

	public void setfSearch(String fSearch) {
		this.fSearch = fSearch;
	}

	public String getfApply() {
		return fApply;
	}

	public void setfApply(String fApply) {
		this.fApply = fApply;
	}

	public String getfSname() {
		return fSname;
	}

	public void setfSname(String fSname) {
		this.fSname = fSname;
	}

	public String getfStags() {
		return fStags;
	}

	public void setfStags(String fStags) {
		this.fStags = fStags;
	}
}
