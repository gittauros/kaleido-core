package com.tauros.kaleido.core.exception;

/**
 * Created by tauros on 2016/5/14.
 */
public class KaleidoDecodeException extends Exception {

    public KaleidoDecodeException(String message) {
        super(message);
    }

    public KaleidoDecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public KaleidoDecodeException(Throwable cause) {
        super(cause);
    }
}
