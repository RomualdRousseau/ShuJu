package com.github.romualdrousseau.shuju.math;

public interface UFunc {

    MArray call(MArray a, MArray out);

    MArray reduce(MArray a, int axis, MArray out);

    MArray accumulate(MArray a, int axis, MArray out);

    MArray inner(MArray a, MArray b, MArray out);
}
