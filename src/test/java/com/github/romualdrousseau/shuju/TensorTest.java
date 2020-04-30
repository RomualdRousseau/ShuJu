package com.github.romualdrousseau.shuju;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import com.github.romualdrousseau.shuju.math.Tensor;

public class TensorTest {

    @Test
    public void testNull() {
        Tensor M1 = Tensor.Null;
        Tensor M2 = new Tensor(4).zeros();
        assertTrue("M1 is Null", M1.isNull());
        assertTrue("M2 is not Null", !M2.isNull());
    }

    @Test
    public void testZero() {
        Tensor M = new Tensor(4).zeros();
        assertTrue("M = [ 0.0f, 0.0f, 0.0f, 00f ]", M.equals(0.0f));
        assertTrue("M != [ 1.0f, 1.0f, 1.0f, 1.0f ]", !M.equals(1.0f));
    }

    @Test
    public void testOne() {
        Tensor M = new Tensor(4).ones();
        assertTrue("M = [ 1.0f, 1.0f, 1.0f, 1.0f ]", M.equals(1.0f));
        assertTrue("M != [ 0.0f, 0.0f, 0.0f, 0.0f ]", !M.equals(0.0f));
    }

    @Test
    public void testArrange() {
        Tensor M1 = new Tensor(4).create(1.0f, 2.0f, 3.0f, 4.0f);
        Tensor M2 = new Tensor(4).arrange(1, 1, 0);
        assertTrue("M1 = [ 1.0f, 2.0f, 3.0f, 4.0f ]", M1.equals(M2));
        assertTrue("M1 != [ 0.0f, 0.0f, 0.0f, 0.0f ]", !M1.equals(0.0f));
        assertTrue("M1 != [ 1.0f, 1.0f, 1.0f, 1.0f ]", !M1.equals(1.0f));
    }

    @Test
    public void testTranspose() {
        Tensor M1 = new Tensor(16).arrange(1, 1, 0).reshape(4, 4);
        Tensor M2 = new Tensor(4, 4).create(new float[][] {
            { 1.0f, 5.0f, 9.0f, 13.0f },
            { 2.0f, 6.0f, 10.0f, 14.0f },
            { 3.0f, 7.0f, 11.0f, 15.0f },
            { 4.0f, 8.0f, 12.0f, 16.0f }
        });
        assertTrue("M1.T = M2", M2.equals(M1.T()));
        assertTrue("M1.T.T = M1", M1.equals(M1.T().T()));
    }

    @Test
    public void testView() {
        Tensor M1 = new Tensor(16).arrange(1, 1, 0).reshape(4, 4);
        M1.get(1,2, 1,2).iadd(1.0f);
        Tensor M3 = new Tensor(4, 4).create(new float[][] {
            { 1.0f, 2.0f, 3.0f, 4.0f },
            { 5.0f, 7.0f, 8.0f, 8.0f },
            { 9.0f, 11.0f, 12.0f, 12.0f },
            { 13.0f, 14.0f, 15.0f, 16.0f }
        });
        assertTrue("M1[1:2, 1:2] + 1 = M3", M1.equals(M3));
    }

    @Test
    public void testAdd() {
        Tensor M1 = new Tensor(1).full(1.0f);
        Tensor M2 = new Tensor(4).arrange(1, 1, 0);
        Tensor M3 = new Tensor(4).create(2.0f, 3.0f, 4.0f, 5.0f);
        assertTrue("M1 + M2 = [ 2.0f, 3.0f, 4.0f, 5.0f ]", M1.add(M2).equals(M3));
        assertTrue("M2 + M1 = [ 2.0f, 3.0f, 4.0f, 5.0f ]", M2.add(M1).equals(M3));
    }

    @Test
    public void testNorm() {
        Tensor M = new Tensor(4, 4).arrange(1, 1, 1);
        assertTrue("M.norm(1) = [ 5.477f, 5.477f, 5.477f, 5.477f ]", M.norm(1).equals(5.477f, 0.001f));
    }

    @Test
    public void testAvg() {
        Tensor M = new Tensor(4, 4).arrange(1, 1, 1);
        assertTrue("M.avg(1) = [ 2.5f, 2.5f ,2.5f ,2.5f ]", M.avg(1).equals(2.5f));
    }

    @Test
    public void testDot() {
        Tensor M1 = new Tensor(4).arrange(1, 1, 0);
        Tensor M2 = new Tensor(4).arrange(1, 1, 0);
        assertTrue("M1.M2 = [ 30.0f ]", M1.dot(M2, -1).equals(30.0f));
    }

    @Test
    public void testOuter() {
        Tensor M1 = new Tensor(4).arrange(1, 1, 0).reshape(4, 1);
        Tensor M2 = new Tensor(4).arrange(1, 1, 0).reshape(1, 4);
        Tensor M3 = new Tensor(4, 4).create(new float[][] {
            { 1.0f, 2.0f, 3.0f, 4.0f },
            { 2.0f, 4.0f, 6.0f, 8.0f },
            { 3.0f, 6.0f, 9.0f, 12.0f },
            { 4.0f, 8.0f, 12.0f, 16.0f }
        });
        assertTrue("M1xM2 = " + M3, M1.outer(M2).equals(M3));
    }

    @Test
    public void testMag() {
        Tensor M1 = new Tensor(4).arrange(1, 1, 0);
        Tensor M2 = new Tensor(4).arrange(1, 1, 0).imul(2.0f);
        assertTrue("M1.distance(M2) = [ 5.477f ]", M1.mag(M2, -1).equals(5.477f, 0.001f));
    }

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
