package com.tauros.kaleido.core.cache;

/**
 * Created by tauros on 2016/4/17.
 */
@FunctionalInterface
public interface MemoryCalculator<E> {

    long calculate(E obj);
}
