package com.tauros.kaleido.core.service.impl;

import com.tauros.kaleido.core.constant.CacheTypeConstant;
import com.tauros.kaleido.core.constant.DownloadConstant;
import com.tauros.kaleido.core.constant.ExHentaiConstant;
import com.tauros.kaleido.core.download.UrlDownloaderDispatcher;
import com.tauros.kaleido.core.model.bo.ExHentaiListBO;
import com.tauros.kaleido.core.model.bean.ExHentaiListParamBean;
import com.tauros.kaleido.core.service.CacheService;
import com.tauros.kaleido.core.service.ExHentaiService;
import com.tauros.kaleido.core.spider.impl.ExHentaiJsoupCookieDocumentSpider;
import com.tauros.kaleido.core.util.ConsoleLog;
import com.tauros.kaleido.core.util.HttpUtils;
import com.tauros.kaleido.core.util.ImageUrlConverter;
import com.tauros.kaleido.core.util.StackTraceUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.math.NumberUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
		try {
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
					try {
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
						listBO.setTagImg(ImageUrlConverter.convertExhentaiImageUrl(tagImg));
						listBO.setPublishTime(time);
						listBO.setCoverImg(ImageUrlConverter.convertExhentaiImageUrl(coverImg));
						listBO.setTitle(title);
						listBO.setBzUrl(bzUrl);

						listBOs.add(listBO);
					} catch (Exception e) {
						ConsoleLog.e(StackTraceUtil.getStackTrace(e));
					}
				}
			}

			Elements pageTable = document.select(".ptb");
			int maxPage = 1;
			if (pageTable != null) {
				Elements tds = pageTable.select("td");
				if (tds.size() > 2) {
					Element maxPageTd = tds.get(tds.size() - 2);
					String maxPageStr = maxPageTd.select("a").first().html();
					maxPage = NumberUtils.toInt(maxPageStr, 0);
				}
			}

			model.put(LIST_BO_KEY, listBOs);
			model.put(MAX_PAGE_KEY, maxPage);

		} catch (Exception e) {
			ConsoleLog.e(" - 文档抓取异常", e);
		}
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
			URLConnection connection = HttpUtils.openConnection(url);
			HttpUtils.setRequestProperty(connection, getRequestProperty());

			inputStream = connection.getInputStream();
			byteArrayOutputStream = new ByteArrayOutputStream();
			int count;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((count = inputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, count);
			}

			data = byteArrayOutputStream.toByteArray();
			cacheService.putByteArrayData(CacheTypeConstant.IMAGE, url, data);
			return data;
		} catch (IOException ioe) {
			ConsoleLog.e("访问图片失败 url=" + url, ioe);
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
			ConsoleLog.e(e);
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
					ConsoleLog.e("下载原图 - " + downloadSrc);
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
			ConsoleLog.e(e);
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
