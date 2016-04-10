package com.tauros.kaleido.core.test;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

/**
 * Created by tauros on 2016/4/9.
 */
public class Demo implements Serializable {
	private static final long serialVersionUID = -2777120453873339539L;

	private static String trans(String str) {
		int index = str.indexOf("?");
		String pre = str.substring(0, index);
		String suf = str.substring(index, str.length());
		pre = pre.substring(pre.indexOf("$") + 1, pre.length());
		pre = pre.replaceAll("=2", ":");
		pre = pre.replaceAll("=1", ".");

		return pre + suf;
	}

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			String str = scanner.nextLine();
			System.out.println(trans(str));
		}
	}
}
