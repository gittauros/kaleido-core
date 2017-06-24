package com.tauros.kaleido.core.exception;

/**
 * Created by tauros on 2016/4/9.
 */
public class KaleidoIllegalStateException extends KaleidoException {

    public KaleidoIllegalStateException() {
    }

    public KaleidoIllegalStateException(String message) {
        super(message);
    }

    public KaleidoIllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
