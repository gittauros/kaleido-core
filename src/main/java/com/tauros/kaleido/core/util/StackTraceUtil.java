package com.tauros.kaleido.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by tauros on 2016/4/9.
 */
public final class StackTraceUtil {

    /**
     * 取出exception中的信息
     *
     * @param exception
     * @return
     */
    public static String getStackTrace(Throwable exception) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            return sw.toString();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
}
