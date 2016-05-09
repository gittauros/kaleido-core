package com.tauros.kaleido.core.test;

import com.tauros.kaleido.core.cache.KaleidoCache;
import com.tauros.kaleido.core.cache.SizeUnit;
import org.apache.http.util.Asserts;

import java.io.Serializable;

/**
 * Created by tauros on 2016/4/9.
 */
public class Demo implements Serializable {
	private static final long serialVersionUID = -2777120453873339539L;

	static KaleidoCache<String, byte[]> cache = new KaleidoCache<>(SizeUnit.MEGABYTES, 100, (obj) -> obj.length);

	public static void main(String[] args) throws Exception {
//		cache.put("zhy", new byte[]{1});
//		System.out.println(cache.get("zhy"));
		String str = "http://exhentai.org/g/876856/8ecfc6e3e3/";
		String str2 = "http://exhentai.org/g/702219/30151902b0/";
		System.out.println(encodeASCII2String(str));
		System.out.println(encode2String(str));

		String encode = "\n";
		long time = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			encode = encode2String((i & 1) == 1 ? str : str2);
		}
		System.out.println(System.currentTimeMillis() - time);
		System.out.println(encode);
	}

	public static String printBinBytes(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			int cut = 0x80;
			while (cut > 0) {
				sb.append((bytes[i] & cut) == 0 ? 0 : 1);
				cut >>= 1;
			}
		}
		return sb.toString();
	}

	public static String printBinLowChars(char[] chars) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < chars.length; i++) {
			byte low = (byte) (chars[i] & CUT_LOW);
			int  cut = 0x80;
			while (cut > 0) {
				sb.append((low & cut) == 0 ? 0 : 1);
				cut >>= 1;
			}
		}
		return sb.toString();
	}

	public static String printBinChars(char[] chars) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < chars.length; i++) {
			byte[] bytes = char2ByteArray(chars[i]);
			byte   high  = bytes[0];
			int    cut   = 0x80;
			while (cut > 0) {
				sb.append((high & cut) == 0 ? 0 : 1);
				cut >>= 1;
			}
			byte low = bytes[1];
			cut = 0x80;
			while (cut > 0) {
				sb.append((low & cut) == 0 ? 0 : 1);
				cut >>= 1;
			}
		}
		return sb.toString();
	}

	public static final int CUT_LOW  = 0x000000FF;
	public static final int CUT_HIGH = 0x0000FF00;
	public static final int LOW_8    = 0x000000FF;

	public static final int FIRST_HEAD = 0xFC;
	public static final int FIRST_END  = 0x3;
	public static final int MID_HEAD   = 0xF0;
	public static final int MID_END    = 0x0F;
	public static final int LAST_HEAD  = 0xC0;
	public static final int LAST_END   = 0x3F;

	public static byte getCharHighByte(char c) {
		byte res = (byte) ((c & CUT_HIGH) >>> 8);
		return res;
	}

	public static byte getCharLowByte(char c) {
		byte res = (byte) (c & CUT_LOW);
		return res;
	}

	public static int byte2IntUnsigned(byte b) {
		int res = (int) b & LOW_8;
		return res;
	}

	public static byte[] string2ByteArray(String str) {
		Asserts.notNull(str, "str is null");
		char[] chars = str.toCharArray();
		byte[] res   = new byte[chars.length << 1];
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			res[i << 1] = getCharHighByte(c);
			res[i << 1 | 1] = getCharLowByte(c);
		}
		return res;
	}

	public static byte[] string2LowByteArray(String str) {
		Asserts.notNull(str, "str is null");
		char[] chars = str.toCharArray();
		byte[] res   = new byte[chars.length];
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			res[i] = getCharLowByte(c);
		}
		return res;
	}

	public static byte[] char2ByteArray(char c) {
		byte[] bytes = new byte[2];
		bytes[0] = getCharHighByte(c);
		bytes[1] = getCharLowByte(c);
		return bytes;
	}

	public static String byteArray2String(byte[] bytes) {
		Asserts.notNull(bytes, "byte array is null");
		Asserts.check((bytes.length & 1) == 0, "byte array length is odd");
		char[] chars = new char[bytes.length >> 1];
		int    temp  = 0;
		for (int i = 0; i < bytes.length; i++) {
			if ((i & 1) == 0) {
				temp = byte2IntUnsigned(bytes[i]);
			} else {
				temp = (temp << 8 | byte2IntUnsigned(bytes[i]));
				chars[i >> 1] = (char) temp;
			}
		}

		return new String(chars);
	}

	public static char[] characters = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
	                                             'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
	                                             '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '_'};

	public static String encodeASCII2String(String str) {
		Asserts.notNull(str, "str is null");
		byte[] bytes = string2LowByteArray(str);
		return encode2String(bytes);
	}

	public static String encode2String(String str) {
		Asserts.notNull(str, "str is null");
		byte[] bytes = string2ByteArray(str);
		return encode2String(bytes);
	}

	public static String encode2String(byte[] bytes) {
		byte[] indexes = encode(bytes);
		char[] chars   = transform(indexes);
		return new String(chars);
	}

	private static char[] transform(byte[] indexes) {
		Asserts.notNull(indexes, "bytes is null");
		char[] chars = new char[indexes.length];
		for (int i = 0; i < indexes.length; i++) {
			byte index = indexes[i];
			if (index == -1) {
				chars[i] = '=';
			} else {
				chars[i] = characters[index];
			}
		}
		return chars;
	}

	public static byte[] encode(byte[] bytes) {
		Asserts.notNull(bytes, "bytes is null");

		byte[] indexes = new byte[(bytes.length + 2) / 3 * 4];
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = -1;
		}
		int pos = 0;
		for (int i = 0; i < bytes.length; ) {
			byte first = bytes[i++];
			byte index = (byte) ((first & FIRST_HEAD) >>> 2);
			indexes[pos++] = index;

			index = (byte) ((first & FIRST_END) << 4);

			byte mid;
			if (i < bytes.length) {
				mid = bytes[i++];
				index = (byte) (index | ((mid & MID_HEAD) >>> 4));
				indexes[pos++] = index;
			} else {
				indexes[pos] = index;
				break;
			}

			index = (byte) ((mid & MID_END) << 2);

			byte last;
			if (i < bytes.length) {
				last = bytes[i++];
				index = (byte) (index | ((last & LAST_HEAD) >>> 6));
				indexes[pos++] = index;
				index = (byte) (last & LAST_END);
				indexes[pos++] = index;
			} else {
				indexes[pos] = index;
				break;
			}
		}

		return indexes;
	}
}
