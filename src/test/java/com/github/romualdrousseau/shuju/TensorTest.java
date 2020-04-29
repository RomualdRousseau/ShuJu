package com.github.romualdrousseau.shuju;

import org.junit.Test;

import com.github.romualdrousseau.shuju.math.Tensor;

public class TensorTest {

    @Test
    public void testCreate() {
        Tensor M1 = new Tensor(2, 4, 4).zeros();
        System.out.println(M1);

        Tensor M2 = new Tensor(1, 1, 1).ones();
        System.out.println(M2);

        Tensor M3 = new Tensor(2, 4, 4).arrange(1.0f);
        System.out.println(M3);

        Tensor M4 = M3.add(M2);
        Tensor M5 = M2.add(M3);
        System.out.println(M4);
        System.out.println(M5);
        System.out.println(M5.equals(M4));

        System.out.println(new Tensor(2, 4, 4).arrange(1.0f));
        System.out.println(new Tensor(2, 4, 4).arrange(1.0f).norm(1));
        System.out.println(new Tensor(2, 4, 4).arrange(1.0f).avg(1));
        System.out.println(new Tensor(4).arrange(1.0f).norm(-1));
        System.out.println(new Tensor(4).arrange(1.0f).dot(new Tensor(4).arrange(1.0f), -1));
    }

    // @Test
    // public void testMarray() {
    // MArray M = new MArray(2, 4, 4).arrange();
    // MArray N = new MArray(1, 1, 4).arrange();
    // MArray V = new MArray(4).arrange();
    // System.out.println(M);
    // System.out.println();
    // System.out.println(M.transpose(0, 2, 1));
    // System.out.println();
    // System.out.println(N);
    // System.out.println();
    // System.out.println(M.iadd(N).iadd(0.5f));
    // System.out.println();
    // System.out.println(V.reshape(1, 4).transpose());
    // System.out.println();
    // System.out.println(M.transpose());
    // System.out.println();
    // System.out.println(new MArray(32).arrange().reshape(2, 4, 4));
    // System.out.println();
    // System.out.println(new MArray(32).arrange().reshape(2, 4, 4).max(0));
    // System.out.println();
    // System.out.println(new MArray(32).arrange().reshape(2, 4, 4).max(1));
    // System.out.println();
    // System.out.println(new MArray(32).arrange().reshape(2, 4, 4).max(2));
    // System.out.println();

    // MArray M1 = new MArray(32);
    // M1 = MArray.Ones.accumulate(M1, 0, M1).reshape(2, 4, 4);
    // MArray M2 = new MArray(4);
    // M2 = MArray.Ones.call(M2, M2).reshape(1, 1, 4);
    // System.out.println(M1);
    // System.out.println(M2);
    // System.out.println();

    // MArray M3 = MArray.MagSq.inner(M1, M2, null);
    // MArray M4 = MArray.Add.reduce(M3, 1, null);
    // MArray M5 = MArray.Sqrt.call(M4, null).reshape(1, 4, 2);
    // System.out.println(M3);
    // System.out.println(M4);
    // System.out.println(M5);
    // System.out.println();

    // MArray M6 = MArray.Mul.inner(M1, M5.transpose(), null);
    // MArray M7 = MArray.Add.reduce(M6, 1, null).reshape(4, 2);
    // System.out.println(M6);
    // System.out.println(M7);
    // System.out.println();

    // MArray M8 = MArray.Add.reduce(M1, 1, null);
    // MArray M9 = new UFunc0((x, y) -> x / (float) M8.shape[1]).call(M8, null);
    // System.out.println(M8);
    // System.out.println(M9);
    // System.out.println();

    // MArray M10 = MArray.Pow2.call(M2.reshape(4), null);
    // MArray M11 = MArray.Add.reduce(M10, 0, null);
    // MArray M12 = MArray.Div.inner(M10, M11, null);
    // System.out.println(M10);
    // System.out.println(M11);
    // System.out.println(M12);
    // }

    // @Test
    // public void testSpeed() {
    // final int w = 1024;
    // float[] a = new float[w * w];
    // float[] b = new float[w * w];
    // float[] c = new float[w * w];

    // long start = System.currentTimeMillis();
    // for(int k = 0; k < 10; k++) {
    // for(int i = 0; i < w; i++) {
    // for(int j = 0; j < w; j++) {
    // for(int l = 0; l < w; l++) {
    // c[i * w + j] = Math.fma(a[i * w + j], b[l * w + j], c[i * w + j]);
    // }
    // }
    // }
    // }
    // long end = System.currentTimeMillis();
    // float time = (float) (end - start) / 10.0f;
    // System.out.println("loop took " + time + "ms");

    // start = System.currentTimeMillis();
    // for(int k = 0; k < 10; k++) {
    // for(int i = 0; i < w; i++) {
    // for(int l = 0; l < w; l++) {
    // fmav(w, a, i * w, b, l * w, c, i * w);
    // }
    // }
    // }
    // end = System.currentTimeMillis();
    // time = (float) (end - start) / 10.0f;
    // System.out.println("loop took " + time + "ms");
    // }

    // private void fmav(final int n, final float[] a, final int oa, final float[]
    // b, final int ob, final float c[], final int oc) {
    // final float[] tmpA = new float[8];
    // final float[] tmpB = new float[8];
    // final float[] tmpC = new float[8];
    // for(int j = 0; j < n; j+=8) {
    // System.arraycopy(a, oa + j, tmpA, 0, 8);
    // System.arraycopy(b, ob + j, tmpB, 0, 8);
    // System.arraycopy(c, oc + j, tmpC, 0, 8);
    // tmpC[0] = Math.fma(tmpA[0], tmpB[0], tmpC[0]);
    // tmpC[1] = Math.fma(tmpA[1], tmpB[1], tmpC[1]);
    // tmpC[2] = Math.fma(tmpA[2], tmpB[2], tmpC[2]);
    // tmpC[3] = Math.fma(tmpA[3], tmpB[3], tmpC[3]);
    // tmpC[4] = Math.fma(tmpA[4], tmpB[4], tmpC[4]);
    // tmpC[5] = Math.fma(tmpA[5], tmpB[5], tmpC[5]);
    // tmpC[6] = Math.fma(tmpA[6], tmpB[5], tmpC[6]);
    // tmpC[7] = Math.fma(tmpA[7], tmpB[5], tmpC[7]);
    // System.arraycopy(tmpC, 0, c, oc + j, 8);
    // }
    // }
}
