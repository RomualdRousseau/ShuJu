package com.github.romualdrousseau.shuju.core;

import java.util.function.BiFunction;

public class UFunc0 extends UFunc<Float> {

    public UFunc0(final BiFunction<Float, Float, Float> func) {
        super(func, 1);
    }

    @Override
    protected void applyAccFunc(final int dim, final MArray a, int aoff, final int astr, final MArray b, int boff,
            final int bstr, float acc) {
        for (int i = 0; i < dim; i++) {
            acc = func.apply(acc, a.data[aoff]);
            b.data[boff] = acc;
            aoff += astr;
            boff += bstr;
        }
    }

    @Override
    protected void applyFunc(final int dim, final MArray a, int aoff, final int astr, final float b, final MArray c,
            int coff, final int cstr) {
        for (int i = 0; i < dim; i++) {
            c.data[coff] = func.apply(b, a.data[aoff]);
            aoff += astr;
            coff += cstr;
        }
    }

    @Override
    protected void applyFunc(final int dim, final MArray a, int aoff, final int astr, final MArray b, int boff,
            final int bstr, final MArray c, int coff, final int cstr) {
        for (int i = 0; i < dim; i++) {
            c.data[coff] = func.apply(b.data[boff], a.data[aoff]);
            aoff += astr;
            boff += bstr;
            coff += cstr;
        }
    }
}
