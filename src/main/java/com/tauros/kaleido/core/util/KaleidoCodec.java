package com.tauros.kaleido.core.util;

import com.tauros.kaleido.core.exception.KaleidoDecodeException;
import com.tauros.kaleido.core.exception.KaleidoEncodeException;
import org.apache.commons.io.IOUtils;
import org.apache.http.util.Asserts;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

/**
 * kaleido 编码/解码工具
 * 基于UTF-8编码
 * <p>
 * Created by tauros on 2016/5/14.
 */
public class KaleidoCodec {

	private static final int LOW_8  = 0x000000FF;
	private static final int HIGH_8 = 0x0000FF00;

	private static char[] base64_url_characters = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
	                                                         'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
	                                                         '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};

	private static char[] base64_normal_characters = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
	                                                            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
	                                                            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

	/**
	 * Base64编码/解码
	 */
	public enum Base64 implements BinaryEncoder, BinaryDecoder {

		URL(base64_url_characters),
		NORMAL(base64_normal_characters);

		private static final int E_FIRST_HEAD = 0xFC;
		private static final int E_FIRST_END  = 0x3;
		private static final int E_MID_HEAD   = 0xF0;
		private static final int E_MID_END    = 0x0F;
		private static final int E_LAST_HEAD  = 0xC0;
		private static final int E_LAST_END   = 0x3F;

		private static final int D_FIRST_SIX           = 0x3F;
		private static final int D_SECOND_PRE_FOUR     = 0xF0;
		private static final int D_SECOND_PRE_FOUR_TWO = 0x3;
		private static final int D_SECOND_SUF_FOUR     = 0x0F;
		private static final int D_THIRD_PRE_SIX       = 0xFC;
		private static final int D_THIRD_PRE_SIX_FOUR  = 0xF;
		private static final int D_THIRD_SUF_TWO       = 0x3;
		private static final int D_FOURTH_SUF_SIX      = 0x3F;

		private char[] characters;
		private byte[] characterIndex;

		private Base64(char[] characters) {
			this.characters = characters;
			char max = Character.MIN_VALUE;
			for (char character : this.characters) {
				if (character > max) {
					max = character;
				}
			}
			this.characterIndex = new byte[max + 1];
			Arrays.fill(this.characterIndex, Byte.MIN_VALUE);
			for (byte i = 0; i < this.characters.length; i++) {
				this.characterIndex[this.characters[i]] = i;
			}
		}

		public byte[] translate2ByteArray(byte[] source) {
			Asserts.notNull(source, "source is null");
			byte[] res = new byte[source.length];
			for (int i = 0; i < source.length; i++) {
				byte index = source[i];
				if (index == -1) {
					res[i] = '=';
				} else {
					res[i] = (byte) this.characters[index];
				}
			}
			return res;
		}

		public char[] translate2CharArray(byte[] source) {
			Asserts.notNull(source, "source is null");
			char[] res = new char[source.length];
			for (int i = 0; i < source.length; i++) {
				byte index = source[i];
				if (index == -1) {
					res[i] = '=';
				} else {
					res[i] = this.characters[index];
				}
			}
			return res;
		}

		public byte[] inverseTranslate(char[] source) {
			Asserts.notNull(source, "source is null");
			byte[] res = new byte[source.length];
			for (int i = 0; i < source.length; i++) {
				char character = source[i];
				if (character == '=') {
					res[i] = -1;
				} else {
					res[i] = this.characterIndex[character];
				}
			}
			return res;
		}

		public byte[] inverseTranslate(byte[] source) {
			Asserts.notNull(source, "source is null");
			byte[] res = new byte[source.length];
			for (int i = 0; i < source.length; i++) {
				byte character = source[i];
				if (character == '=') {
					res[i] = -1;
				} else {
					res[i] = this.characterIndex[character];
				}
			}
			return res;
		}

		public char[] encodeAndTranslate(byte[] source) throws KaleidoEncodeException {
			Asserts.notNull(source, "source is null");
			byte[] target = encode(source);
			return translate2CharArray(target);
		}

		public String encodeAndTranslateToString(byte[] source) throws KaleidoEncodeException {
			Asserts.notNull(source, "source is null");
			byte[] target = encode(source);
			return new String(translate2CharArray(target));
		}

		public byte[] decodeWithInverseTranslate(byte[] source) throws KaleidoDecodeException {
			Asserts.notNull(source, "source is null");
			byte[] target = inverseTranslate(source);
			return decode(target);
		}

		public byte[] decodeStringWithInverseTranslate(String source) throws KaleidoDecodeException {
			Asserts.notNull(source, "source is null");
			byte[] byteSource = source.getBytes();
			byte[] target     = inverseTranslate(byteSource);
			return decode(target);
		}

		@Override
		public byte[] decode(byte[] source) throws KaleidoDecodeException {
			Asserts.notNull(source, "source is null");

			byte[] target = new byte[(source.length + 3) / 4 * 3];
			int    pos    = 0;
			byte   sourceByte;
			for (int i = 0; i < source.length; ) {
				sourceByte = source[i++];
				if (sourceByte == -1) {
					break;
				}

				if (i >= source.length) {
					break;
				}
				byte first = (byte) ((sourceByte & D_FIRST_SIX) << 2);
				sourceByte = source[i++];
				if (sourceByte == -1) {
					break;
				}
				first = (byte) (first | (sourceByte & D_SECOND_PRE_FOUR) >>> 4 & D_SECOND_PRE_FOUR_TWO);
				target[pos++] = first;

				if (i >= source.length) {
					break;
				}
				byte mid = (byte) ((sourceByte & D_SECOND_SUF_FOUR) << 4);
				sourceByte = source[i++];
				if (sourceByte == -1) {
					break;
				}
				mid = (byte) (mid | (sourceByte & D_THIRD_PRE_SIX) >>> 2 & D_THIRD_PRE_SIX_FOUR);
				target[pos++] = mid;

				if (i >= source.length) {
					break;
				}
				byte last = (byte) ((sourceByte & D_THIRD_SUF_TWO) << 6);
				sourceByte = source[i++];
				if (sourceByte == -1) {
					break;
				}
				last = (byte) (last | sourceByte & D_FOURTH_SUF_SIX);
				target[pos++] = last;
			}

			return Arrays.copyOf(target, pos);
		}

		@Override
		public byte[] encode(byte[] source) throws KaleidoEncodeException {
			Asserts.notNull(source, "source is null");

			byte[] target = new byte[(source.length + 2) / 3 * 4];
			Arrays.fill(target, (byte) -1);
			int pos = 0;
			for (int i = 0; i < source.length; ) {
				byte first = source[i++];
				byte index = (byte) ((first & E_FIRST_HEAD) >>> 2);
				target[pos++] = index;

				index = (byte) ((first & E_FIRST_END) << 4);

				byte mid;
				if (i < source.length) {
					mid = source[i++];
					index = (byte) (index | ((mid & E_MID_HEAD) >>> 4));
					target[pos++] = index;
				} else {
					target[pos] = index;
					break;
				}

				index = (byte) ((mid & E_MID_END) << 2);

				byte last;
				if (i < source.length) {
					last = source[i++];
					index = (byte) (index | ((last & E_LAST_HEAD) >>> 6));
					target[pos++] = index;
					index = (byte) (last & E_LAST_END);
					target[pos++] = index;
				} else {
					target[pos] = index;
					break;
				}
			}

			return target;
		}

		@Override
		public Object decode(Object source) throws KaleidoDecodeException {
			if (source instanceof byte[]) {
				return decode((byte[]) source);
			} else if (source instanceof String) {
				return decode((String) source);
			} else if (source instanceof char[]) {
				return decode((char[]) source);
			}
			throw new KaleidoDecodeException("source is not a byte[], a char[] or a String");
		}

		@Override
		public Object encode(Object source) throws KaleidoEncodeException {
			if (source instanceof byte[]) {
				return encode((byte[]) source);
			} else if (source instanceof String) {
				return encode((String) source);
			} else if (source instanceof char[]) {
				return encode((char[]) source);
			}
			throw new KaleidoEncodeException("source is not a byte[], a char[] or a String");
		}

		public String encode(String source) throws KaleidoEncodeException {
			Asserts.notNull(source, "source is null");
			byte[] byteSource = string2ByteArray(source);
			byte[] byteTarget = encode(byteSource);
			char[] charTarget = translate2CharArray(byteTarget);
			return new String(charTarget);
		}

		public String decode(String source) throws KaleidoDecodeException {
			Asserts.notNull(source, "source is null");
			char[] charSource = source.toCharArray();
			byte[] byteSource = inverseTranslate(charSource);
			byte[] byteTarget = decode(byteSource);
			return byteArray2String(byteTarget);
		}

		public char[] encode(char[] source) throws KaleidoEncodeException {
			Asserts.notNull(source, "source is null");
			byte[] byteSource = charArray2ByteArray(source);
			byte[] byteTarget = encode(byteSource);
			return translate2CharArray(byteTarget);
		}

		public char[] decode(char[] source) throws KaleidoDecodeException {
			Asserts.notNull(source, "source is null");
			byte[] byteSource = inverseTranslate(source);
			byte[] byteTarget = decode(byteSource);
			return byteArray2CharArray(byteTarget);
		}

		public char[] encode(char[] source, int pos, int length) throws KaleidoEncodeException {
			Asserts.check(source != null && source.length >= pos + length, "source is null or out of bound");
			char[] newSource = Arrays.copyOf(source, length);
			System.arraycopy(source, pos, newSource, 0, length);
			char[] target    = encode(newSource);
			char[] newTarget = Arrays.copyOf(source, source.length - length + target.length);
			System.arraycopy(newTarget, pos + length, newTarget, pos + target.length, source.length - length - pos);
			System.arraycopy(target, 0, newTarget, pos, target.length);
			return newTarget;
		}

		public char[] decode(char[] source, int pos, int length) throws KaleidoDecodeException {
			Asserts.check(source != null && source.length >= pos + length, "source is null or out of bound");
			char[] newSource = Arrays.copyOf(source, length);
			System.arraycopy(source, pos, newSource, 0, length);
			char[] target    = decode(newSource);
			char[] newTarget = Arrays.copyOf(target, source.length - length + target.length);
			System.arraycopy(source, 0, newTarget, 0, pos);
			System.arraycopy(source, pos + length, newTarget, pos + target.length, source.length - pos - length);
			System.arraycopy(target, 0, newTarget, pos, target.length);
			return newTarget;
		}

		public byte[] encode(byte[] source, int pos, int length) throws KaleidoEncodeException {
			Asserts.check(source != null && source.length >= pos + length, "source is null or out of bound");
			byte[] newSource = Arrays.copyOf(source, length);
			System.arraycopy(source, pos, newSource, 0, length);
			byte[] target    = encode(newSource);
			byte[] newTarget = Arrays.copyOf(source, source.length - length + target.length);
			System.arraycopy(newTarget, pos + length, newTarget, pos + target.length, source.length - length - pos);
			System.arraycopy(target, 0, newTarget, pos, target.length);
			return newTarget;
		}

		public byte[] decode(byte[] source, int pos, int length) throws KaleidoDecodeException {
			Asserts.check(source != null && source.length >= pos + length, "source is null or out of bound");
			byte[] newSource = Arrays.copyOf(source, length);
			System.arraycopy(source, pos, newSource, 0, length);
			byte[] target    = decode(newSource);
			byte[] newTarget = Arrays.copyOf(target, source.length - length + target.length);
			System.arraycopy(source, 0, newTarget, 0, pos);
			System.arraycopy(source, pos + length, newTarget, pos + target.length, source.length - pos - length);
			System.arraycopy(target, 0, newTarget, pos, target.length);
			return newTarget;
		}

		public StringReader encode(Reader reader) throws KaleidoEncodeException, IOException {
			CharArrayWriter caw = null;
			try {
				caw = new CharArrayWriter();

				char[] buffer = new char[150];
				int    count;
				while ((count = reader.read(buffer)) != -1) {
					char[] encoded = encode(buffer, 0, count);
					caw.write(encoded, 0, encoded.length - (buffer.length - count));
				}

				return new StringReader(caw.toString());
			} finally {
				IOUtils.closeQuietly(caw);
			}
		}

		public StringReader decode(Reader reader) throws KaleidoDecodeException, IOException {
			CharArrayWriter caw = null;
			try {
				caw = new CharArrayWriter();

				char[] buffer = new char[200];
				int    count;
				while ((count = reader.read(buffer)) != -1) {
					caw.write(buffer, 0, count);
				}

				return new StringReader(decode(caw.toString()));
			} finally {
				IOUtils.closeQuietly(caw);
			}
		}
	}

	public static byte[] charArray2ByteArray(char[] chars) {
		Asserts.notNull(chars, "char array is null");
		byte[] res = new byte[chars.length << 1];
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			res[i << 1] = getCharHighByte(c);
			res[i << 1 | 1] = getCharLowByte(c);
		}
		return res;
	}

	public static byte[] string2ByteArray(String str) {
		Asserts.notNull(str, "str is null");
		char[] chars = str.toCharArray();
		return charArray2ByteArray(chars);
	}

	public static char[] byteArray2CharArray(byte[] bytes) {
		Asserts.notNull(bytes, "byte array is null");
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
		return chars;
	}

	public static String byteArray2String(byte[] bytes) {
		Asserts.notNull(bytes, "byte array is null");
		char[] chars = byteArray2CharArray(bytes);
		return new String(chars);
	}

	private static byte getCharHighByte(char c) {
		return (byte) ((c & HIGH_8) >>> 8);
	}

	private static byte getCharLowByte(char c) {
		return (byte) (c & LOW_8);
	}

	private static int byte2IntUnsigned(byte b) {
		return (int) b & LOW_8;
	}


	private interface Encoder {

		/**
		 * @param source 待编码对象
		 * @return 编码后对象
		 * @throws KaleidoEncodeException
		 */
		Object encode(Object source) throws KaleidoEncodeException;
	}

	private interface BinaryEncoder extends Encoder {

		/**
		 * @param source 待编码byteArray
		 * @return 编码后byteArray
		 * @throws KaleidoEncodeException
		 */
		byte[] encode(byte[] source) throws KaleidoEncodeException;
	}

	private interface Decoder {

		/**
		 * @param source 待解码对象
		 * @return 解码后对象
		 * @throws KaleidoDecodeException
		 */
		Object decode(Object source) throws KaleidoDecodeException;
	}

	private interface BinaryDecoder extends Decoder {

		/**
		 * @param source 待解码byteArray
		 * @return 解码后byteArray
		 * @throws KaleidoDecodeException
		 */
		byte[] decode(byte[] source) throws KaleidoDecodeException;
	}
}