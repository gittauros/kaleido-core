package com.tauros.kaleido.core.exception;

/**
 * Created by tauros on 2016/4/9.
 */
public class KaleidoUnsupportedException extends KaleidoException {

    public KaleidoUnsupportedException() {
    }

    public KaleidoUnsupportedException(String message) {
        super(message);
    }

    public KaleidoUnsupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
