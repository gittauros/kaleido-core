package com.tauros.kaleido.core.service.impl;

import com.tauros.kaleido.core.constant.CacheTypeConstant;
import com.tauros.kaleido.core.constant.DownloadConstant;
import com.tauros.kaleido.core.constant.ExHentaiConstant;
import com.tauros.kaleido.core.download.UrlDownloaderDispatcher;
import com.tauros.kaleido.core.model.bean.ExHentaiListParamBean;
import com.tauros.kaleido.core.model.bo.ExHentaiGalleryBO;
import com.tauros.kaleido.core.model.bo.ExHentaiListBO;
import com.tauros.kaleido.core.model.bo.ExHentaiPhotoBO;
import com.tauros.kaleido.core.service.CacheService;
import com.tauros.kaleido.core.service.ExHentaiService;
import com.tauros.kaleido.core.spider.impl.ExHentaiJsoupCookieDocumentSpider;
import com.tauros.kaleido.core.util.ConsoleLog;
import com.tauros.kaleido.core.util.HttpUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by tauros on 2016/4/9.
 */
public class ExHentaiServiceImpl implements ExHentaiService, ExHentaiConstant, DownloadConstant {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private CacheService                      cacheService;
	@Resource
	private ExHentaiJsoupCookieDocumentSpider exHentaiJsoupCookieDocumentSpider;
	@Resource
	private UrlDownloaderDispatcher           urlDownloaderDispatcher;

	private Map<String, String> getRequestProperty() {
		Map<String, String> requestProperty = new HashMap<>();
		requestProperty.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		requestProperty.put("Accept-Encoding", "gzip, deflate, sdch");
		requestProperty.put("Accept-Language", "zh-CN,zh;q=0.8");
		requestProperty.put("Connection", "keep-alive");
		requestProperty.put("Host", "exhentai.org");
		requestProperty.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
		requestProperty.put("Cookie", exHentaiJsoupCookieDocumentSpider.getCookieString());

		return requestProperty;
	}

	@Override
	public Map<String, Object> searchListPage(ExHentaiListParamBean paramBean) {
		Map<String, Object> model = new HashMap<>();
		String url = EXHENTAI_LIST_URL + '?';
		if (paramBean.getPage() > 1) {
			url += EXHENTAI_LIST_PAGE + "=" + (paramBean.getPage() - 1) + "&";
		}
		url += EXHENTAI_LIST_FDOUJINSHI + "=" + paramBean.getfDoujinshi() + "&";
		url += EXHENTAI_LIST_FMANGA + "=" + paramBean.getfManga() + "&";
		url += EXHENTAI_LIST_FARTISTCG + "=" + paramBean.getfArtistcg() + "&";
		url += EXHENTAI_LIST_FGAMECG + "=" + paramBean.getfGamecg() + "&";
		url += EXHENTAI_LIST_FWESTERN + "=" + paramBean.getfWestern() + "&";
		url += EXHENTAI_LIST_FNONH + "=" + paramBean.getfNonh() + "&";
		url += EXHENTAI_LIST_FIMAGESET + "=" + paramBean.getfImageset() + "&";
		url += EXHENTAI_LIST_FCOSPLAY + "=" + paramBean.getfCosplay() + "&";
		url += EXHENTAI_LIST_FASIANPORN + "=" + paramBean.getfAsianporn() + "&";
		url += EXHENTAI_LIST_FMISC + "=" + paramBean.getfMisc() + "&";
		url += EXHENTAI_LIST_FSEARCH + "=" + paramBean.getfSearch() + "&";
		url += EXHENTAI_LIST_FAPPLY + "=" + paramBean.getfApply() + "&";
		url += EXHENTAI_LIST_ADVSEARCH + "=" + paramBean.getAdvsearch() + "&";
		url += EXHENTAI_LIST_FSNAME + "=" + paramBean.getfSname() + "&";
		url += EXHENTAI_LIST_FSTAGS + "=" + paramBean.getfStags() + "&";
		url += EXHENTAI_LIST_FSRDD + "=" + paramBean.getfSrdd();

		Document document = exHentaiJsoupCookieDocumentSpider.captureDocument(url, "exhentai.org", null);

		List<ExHentaiListBO> listBOs = new ArrayList<>();
		Elements table = document.select(".itg");
		if (table != null) {
			Elements trs = table.select("tr");
			for (Element tr : trs) {
				Elements tds = tr.select("td");
				if (tds.size() == 0) {
					continue;
				}
				Element tagCell = tds.get(0);
				Element timeCell = tds.get(1);
				Element detailCell = tds.get(2);

				String tagImg = tagCell.select("img").attr("src");
				String time = timeCell.html();
				String titleAndCoverImg = detailCell.select(".it2").first().html();
				String bzUrl = detailCell.select(".it5 a").first().attr("href");

				String[] titleAndCoverImgInfo = titleAndCoverImg.split("~");
				String coverImg;
				String title;
				if (titleAndCoverImgInfo.length > 3) {
					coverImg = "http://" + titleAndCoverImgInfo[1] + "/" + titleAndCoverImgInfo[2];
					title = titleAndCoverImgInfo[3];
				} else {
					Element img = detailCell.select(".it2").select("img").first();
					coverImg = img.attr("src");
					title = img.attr("alt");
				}

				ExHentaiListBO listBO = new ExHentaiListBO();
				listBO.setTagImg(tagImg);
				listBO.setPublishTime(time);
				listBO.setCoverImg(coverImg);
				listBO.setTitle(title);
				listBO.setBzUrl(bzUrl);
				listBO.setGalleryUrl(bzUrl);

				listBOs.add(listBO);
			}
		}

		//最大页数获取
		Elements pageTable = document.select(".ptb");
		int maxPage = 1;
		if (pageTable != null) {
			Elements tds = pageTable.select("td");
			if (tds.size() > 2) {
				Element maxPageTd = tds.get(tds.size() - 2);
				String maxPageStr = maxPageTd.select("a").first().html();
				maxPage = NumberUtils.toInt(maxPageStr, 1);
			}
		}

		model.put(LIST_BO_KEY, listBOs);
		model.put(MAX_PAGE_KEY, maxPage);

		return model;
	}

	@Override
	public Map<String, Object> galleryPage(String url, boolean large, int page) {
		Map<String, Object> model = new HashMap<>();
		url += "?p=" + (page - 1);
		url += "&inline_set=ts_" + (large ? "l" : "m");

		String cacheKey = url;

		Document document;
		String html = cacheService.getStringData(CacheTypeConstant.HTML, cacheKey);
		if (html != null) {
			document = Jsoup.parse(html);
		} else {
			document = exHentaiJsoupCookieDocumentSpider.captureDocument(url, "exhentai.org", null);
			cacheService.putStringData(CacheTypeConstant.HTML, cacheKey, document.toString());
		}

		List<ExHentaiGalleryBO> galleryBOs = new ArrayList<>();
		Elements photos;
		if (large) {
			photos = document.select(".gdtl");
		} else {
			photos = document.select(".gdtm");
		}
		for (Element photo : photos) {
			Element img = photo.select("img").first();

			String title = img.attr("title");
			String previewUrl = photo.select("a").first().attr("href");

			String largeImg = null;
			String smallImg = null;
			String smallImgPlaceHolder = null;
			int smallImgXOffset = 0;
			int smallImgYOffset = 0;
			int smallImgWidth = 0;
			int smallImgHeight = 0;
			if (large) {
				largeImg = img.attr("src");
			} else {
				smallImgPlaceHolder = img.attr("src");

				//小图背景及偏移量
				Element div = photo.select("div").get(1);
				String divStyle = div.attr("style");
				String[] keyValues = divStyle.split(";");
				for (String keyValueStr : keyValues) {
					String key = keyValueStr.split(":")[0];
					if (!key.contains("background")) {
						continue;
					}
					String value = keyValueStr.substring(keyValueStr.indexOf(":") + 1);
					int index = value.indexOf(" ");
					int i = 1;
					int smallImgStart = 0;
					int smallImgEnd = 0;
					int xOffsetEnd = 0;
					int yOffsetEnd = 0;
					while (index != -1) {
						switch (i) {
							case 1:
								smallImgStart = index;
								break;
							case 2:
								smallImgEnd = index;
								smallImg = value.substring(smallImgStart, smallImgEnd);
								smallImg = smallImg.replaceAll("url", "");
								smallImg = smallImg.replaceAll("[()]", "");
								smallImg = smallImg.trim();
								break;
							case 3:
								xOffsetEnd = index;
								String xOffsetStr = value.substring(smallImgEnd, xOffsetEnd);
								smallImgXOffset = NumberUtils.toInt(xOffsetStr.replaceAll("px", "").trim());
								break;
							case 4:
								yOffsetEnd = index;
								String yOffsetStr = value.substring(xOffsetEnd, yOffsetEnd);
								smallImgYOffset = NumberUtils.toInt(yOffsetStr.replaceAll("px", "").trim());
								break;
							default:
								break;
						}

						index = value.indexOf(" ", index + 1);
						i++;
					}
				}

				//小图宽高
				String imgStyle = img.attr("style");
				keyValues = imgStyle.split(";");
				for (int i = 0; i < keyValues.length; i++) {
					String keyValueStr = keyValues[i];
					String key = keyValueStr.split(":")[0];
					String value = keyValueStr.substring(keyValueStr.indexOf(":") + 1);
					int valueInt = NumberUtils.toInt(value.replaceAll("px", "").trim());
					if (key.contains("width")) {
						smallImgWidth = valueInt;
					} else if (key.contains("height")) {
						smallImgHeight = valueInt;
					}
				}
			}

			ExHentaiGalleryBO galleryBO = new ExHentaiGalleryBO();
			galleryBO.setLargeImg(largeImg);
			galleryBO.setSmallImgXOffset(smallImgXOffset);
			galleryBO.setSmallImgYOffset(smallImgYOffset);
			galleryBO.setSmallImg(smallImg);
			galleryBO.setSmallImgWidth(smallImgWidth);
			galleryBO.setSmallImgHeight(smallImgHeight);
			galleryBO.setSmallImgPlaceHolder(smallImgPlaceHolder);
			galleryBO.setTitle(title);
			galleryBO.setPreviewUrl(previewUrl);
			galleryBO.setPhotoUrl(previewUrl);
			galleryBOs.add(galleryBO);
		}

		//最大页数获取
		Elements pageTable = document.select(".ptt");
		Elements pageTds = pageTable.select("td");
		Element pageTd = pageTds.get(pageTds.size() - 2);
		Element pageA = pageTd.select("a").first();
		int maxPage = NumberUtils.toInt(pageA.html(), 1);

		model.put(GALLERY_BO_KEY, galleryBOs);
		model.put(MAX_PAGE_KEY, maxPage);

		return model;
	}

	@Override
	public Map<String, Object> photoPage(String url) {
		Map<String, Object> model = new HashMap<>();

		Document document;
		String html = cacheService.getStringData(CacheTypeConstant.HTML, url);
		if (html != null) {
			document = Jsoup.parse(html);
		} else {
			document = exHentaiJsoupCookieDocumentSpider.captureDocument(url, "exhentai.org", null);
			cacheService.putStringData(CacheTypeConstant.HTML, url, document.toString());
		}

		Element img = document.select("#img").first();
		String photoImg = img.attr("src");

		int imgWidth = 0;
		int imgHeight = 0;
		String imgStyle = img.attr("style");
		String[] keyValues = imgStyle.split(";");
		for (int i = 0; i < keyValues.length; i++) {
			String keyValueStr = keyValues[i];
			String[] keyValue = keyValueStr.split(":");
			String key = keyValue[0];
			String value = keyValueStr.substring(keyValueStr.indexOf(":") + 1);
			int valueInt = NumberUtils.toInt(value.replaceAll("px", "").trim());
			if (key.contains("width")) {
				imgWidth = valueInt;
			} else if (key.contains("height")) {
				imgHeight = valueInt;
			}
		}

		Element pageDiv = document.select(".sn").first();
		Element curPageSpan = pageDiv.select("span").first();
		Element lastPageSpan = pageDiv.select("span").last();
		Elements pages = pageDiv.select("a");

		String firstPageUrl = pages.get(0).attr("href");
		String prevPageUrl = pages.get(1).attr("href");
		String nextPageUrl = pages.get(2).attr("href");
		String lastPageUrl = pages.get(3).attr("href");
		int curPage = NumberUtils.toInt(curPageSpan.html(), 1);
		int lastPage = NumberUtils.toInt(lastPageSpan.html(), 1);

		ExHentaiPhotoBO photoBO = new ExHentaiPhotoBO();
		photoBO.setPhotoImg(photoImg);
		photoBO.setCurPage(curPage);
		photoBO.setLastPage(lastPage);
		photoBO.setFirstPageUrl(firstPageUrl);
		photoBO.setPrevPageUrl(prevPageUrl);
		photoBO.setNextPageUrl(nextPageUrl);
		photoBO.setLastPageUrl(lastPageUrl);
		photoBO.setImgWidth(imgWidth);
		photoBO.setImgHeight(imgHeight);

		model.put(PHOTO_BO_KEY, photoBO);

		return model;
	}

	@Override
	public String download(String saveBasePath, String url, long sleep, boolean origin) {
		return ex_hentai_download(saveBasePath, url, sleep, origin);
	}

	@Override
	public byte[] image(String url) {
		byte[] data = cacheService.getByteArrayData(CacheTypeConstant.IMAGE, url);
		if (data != null && data.length > 0) {
			return data;
		}
		InputStream inputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			URLConnection connection = HttpUtil.openConnection(url);
			HttpUtil.setRequestProperty(connection, getRequestProperty());

			inputStream = connection.getInputStream();
			byteArrayOutputStream = new ByteArrayOutputStream();

			boolean chunked;
			long contentLength = 0;
			long processLength = 0;
			if("chunked".equals(connection.getHeaderField("Transfer-Encoding"))) {
				chunked = true;
			} else {
				chunked = false;
				contentLength = connection.getContentLengthLong();
			}

			int count;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((count = inputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, count);
				processLength += count;
			}

			data = byteArrayOutputStream.toByteArray();
			if (!chunked && processLength == contentLength) {
				cacheService.putByteArrayData(CacheTypeConstant.IMAGE, url, data);
			}
			return data;
		} catch (IOException ioe) {
			logger.warn("visit image fail url=" + url, ioe);
			return new byte[0];
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(byteArrayOutputStream);
		}
	}

	private String ex_hentai_download(String saveBasePath, String url, long sleep, boolean origin) {
		try {
			Document document = exHentaiJsoupCookieDocumentSpider.captureDocument(url, "exhentai.org", null);
			Element firstImgDiv = document.select(".gdtm").first();
			if (firstImgDiv == null) {
				firstImgDiv = document.select(".gdtl").first();
			}
			Element firstA = firstImgDiv.select("a").first();
			String href = firstA.attr("href");

			Element title = document.select("#gn").first();
			String dirName = title.html();
			dirName = dirName.replaceAll("[\\\\/:\"*?<>|]", " ");
			String filePath = saveBasePath + dirName;

			filePath = filePathFilter(filePath);

			int page = 1;
			while (true) {
				String nextPage = start_ex_hentai_download(filePath, href, origin, page);
				if (END_OF_GRAB_PAGE.equals(nextPage)) {
					break;
				} else {
					href = nextPage;
					page++;
				}
				if (sleep > 0) {
					Thread.sleep(sleep);
				}
			}

			return "下载成功！";
		} catch (Exception e) {
			logger.warn("ex_hentai_download exception", e);
			return "下载错误！";
		}
	}

	private String start_ex_hentai_download(String filePath, String url, boolean origin, int page) {
		try {
			Document document = exHentaiJsoupCookieDocumentSpider.captureDocument(url, "exhentai.org", null);

			Element img = document.select("#img").first();
			String src = img.attr("src");

			String fileName = "page_" + page + "_" + getFileName(src);

			String downloadSrc = src;
			if (origin) {
				Element originImgDiv = document.select("#i7").first();
				Elements originImgA = originImgDiv.select("a");
				if (originImgA != null && originImgA.size() > 0) {
					downloadSrc = originImgA.first().attr("href");
					logger.info("下载原图 - " + downloadSrc);
				}
			}

			//开始下载
			urlDownloaderDispatcher.dispatch(filePath, fileName, downloadSrc, getRequestProperty());

			Elements next = document.select("#next");
			if (next != null) {
				String href = next.first().attr("href");
				Elements spans = document.select(".sn div span");
				if (spans != null && spans.size() > 1) {
					int curPage = NumberUtils.toInt(spans.get(0).html(), 0);
					int maxPage = NumberUtils.toInt(spans.get(1).html(), 0);
					if (curPage < maxPage) {
						return href;
					}
				}
			}

			return END_OF_GRAB_PAGE;
		} catch (Exception e) {
			logger.warn("start_ex_hentai_download exception", e);
			return END_OF_GRAB_PAGE;
		}
	}

	private static String filePathFilter(String filePath) {
		filePath = filePath.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5!@#$%^&*()_+-=,.\\[\\]{};'\\\\]", " ");
		filePath = filePath.replaceAll("\\s+", " ");
		filePath = filePath.trim();
		return filePath;
	}

	private static String getFileName(String imgSrc) {
		Matcher matcher = FILE_NAME_PATTERN.matcher(imgSrc);
		if (matcher.find()) {
			return matcher.group();
		}
		return "";
	}
}
