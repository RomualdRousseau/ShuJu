package com.github.romualdrousseau.shuju.math;

public interface UFunc {

    MArray call(final MArray a, final float v, final float w, MArray out);

    MArray reduce(final MArray a, final float v, final float w, final int axis, MArray out);

    MArray accumulate(final MArray a, final float v, final float w, final int axis, MArray out);

    MArray inner(final MArray a, final MArray b, final float v, final float w, MArray out);
}
