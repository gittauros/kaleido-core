package com.tauros.kaleido.core.exception;

/**
 * Created by tauros on 2016/5/14.
 */
public class KaleidoEncodeException extends Exception {

	public KaleidoEncodeException(String message) {
		super(message);
	}

	public KaleidoEncodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public KaleidoEncodeException(Throwable cause) {
		super(cause);
	}
}
