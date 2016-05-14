package com.tauros.kaleido.core.test;

import com.tauros.kaleido.core.cache.KaleidoCache;
import com.tauros.kaleido.core.cache.SizeUnit;
import com.tauros.kaleido.core.util.KaleidoCodec;
import org.apache.commons.codec.binary.*;
import org.apache.http.util.Asserts;
import org.apache.http.util.LangUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static com.tauros.kaleido.core.util.KaleidoCodec.*;

/**
 * Created by tauros on 2016/4/9.
 */
public class Demo implements Serializable {
	private static final long serialVersionUID = -2777120453873339539L;

	static KaleidoCache<String, byte[]> cache = new KaleidoCache<>(SizeUnit.MEGABYTES, 100, (obj) -> obj.length);

	public static void main(String[] args) throws Exception {
		FileReader reader = new FileReader("C:/Users/Administrator/Desktop/read.txt");
		StringReader stringReader = KaleidoCodec.Base64.NORMAL.encode(reader);

		int c;
		File encodedFile = new File("C:/Users/Administrator/Desktop/encoded.txt");
		if (!encodedFile.exists()) {
			encodedFile.createNewFile();
		}
		FileWriter writer = new FileWriter(encodedFile);
		while ((c = stringReader.read()) != -1) {
			System.out.print((char) c);
			writer.write(c);
		}
		writer.flush();

		System.out.println("\n");

		File decodedFile = new File("C:/Users/Administrator/Desktop/decoded.txt");
		reader = new FileReader(encodedFile);
		stringReader = KaleidoCodec.Base64.NORMAL.decode(reader);
		writer = new FileWriter(decodedFile);
		while ((c = stringReader.read()) != -1) {
			System.out.print((char) c);
			writer.write(c);
		}
		writer.flush();
	}
}
