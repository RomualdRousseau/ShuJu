package com.github.romualdrousseau.shuju.math;

import java.util.function.BiFunction;

public class UFunc0i extends UFunc0 {

    public UFunc0i(BiFunction<Float, Float, Float> func) {
        super(func);
    }

    @Override
    protected void applyAccFunc(final int dim, final MArray a, int aoff, final int astr, final MArray b, int boff,
            final int bstr, float acc) {
        int iacc = aoff;
        for (int i = 0; i < dim; i++) {
            final float oacc = acc;
            acc = this.func.apply(acc, a.data[aoff]);
            if (acc != oacc) {
                iacc = aoff;
            }
            b.data[boff] = iacc;
            aoff += astr;
            boff += bstr;
        }
    }
}
