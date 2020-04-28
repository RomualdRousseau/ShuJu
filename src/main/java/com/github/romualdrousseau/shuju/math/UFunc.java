package com.github.romualdrousseau.shuju.math;

public interface UFunc {

    MArray call(final MArray a, final float v, MArray out);

    MArray reduce(final MArray a, final float v, final int axis, MArray out);

    MArray accumulate(final MArray a, final float v, final int axis, MArray out);

    MArray inner(final MArray a, final MArray b, MArray out);
}
