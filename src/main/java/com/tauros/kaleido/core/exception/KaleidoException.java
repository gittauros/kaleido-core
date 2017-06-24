package com.tauros.kaleido.core.exception;

/**
 * Created by tauros on 2016/4/9.
 */
public class KaleidoException extends RuntimeException {

    public KaleidoException() {
    }

    public KaleidoException(String message) {
        super(message);
    }

    public KaleidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
