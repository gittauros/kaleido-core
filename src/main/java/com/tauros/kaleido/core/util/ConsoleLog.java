package com.tauros.kaleido.core.util;

/**
 * Created by tauros on 2015/2/18.
 */
public final class ConsoleLog {

	public static void e(String msg, Exception e) {
		System.out.println(msg + "\n" + StackTraceUtil.getStackTrace(e));
	}

	public static void e(Exception e) {
		System.out.println(StackTraceUtil.getStackTrace(e));
	}

	public static void e(String msg) {
		System.out.println(msg);
	}

	public static void e(Object obj) {
		System.out.println(obj);
	}
}
