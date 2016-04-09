package com.tauros.kaleido.core.constant;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Created by tauros on 2016/4/9.
 */
public interface DownloadConstant {

	int           BUFFER_SIZE        = 51200;
	int           LOOP_BUFFER_SIZE   = 4096;
	int           MAX_RETRY_TIMES    = 5;
	DecimalFormat DEFAULT_FORMAT     = new DecimalFormat("00");
	Pattern       FILE_NAME_PATTERN    = Pattern.compile("(?<=/)[^/]*?$");
}
