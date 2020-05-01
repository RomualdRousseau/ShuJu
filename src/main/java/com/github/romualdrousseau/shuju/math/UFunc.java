package com.github.romualdrousseau.shuju.math;

import java.util.function.BiFunction;

public abstract class UFunc<T> {

    public final BiFunction<T, T, T> func;

    public UFunc(BiFunction<T, T, T> func) {
        this.func = func;
    }

    public MArray inner(final MArray a, final float b, final float initital, final int axis, MArray out) {
        return MFuncs.Add.reduce(this.outer(a, b, null), initital, axis, out);
    }

    public MArray inner(final MArray a, final MArray b, final float initital, final int axis, MArray out) {
        return MFuncs.Add.reduce(this.outer(a, b, null), initital, axis, out);
    }

    public abstract MArray reduce(final MArray a, final float initital, final int axis, MArray out);

    public abstract MArray accumulate(final MArray a, final float initital, final int axis, MArray out);

    public abstract MArray outer(final MArray a, final float b,  MArray out);

    public abstract MArray outer(final MArray a, final MArray b, MArray out);


}
