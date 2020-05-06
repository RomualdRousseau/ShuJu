package com.github.romualdrousseau.shuju;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.is;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Tensor;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

public class TensorTest {

    @Test
    public void testNull() {
        Tensor M1 = Tensor.Null;
        Tensor M2 = new Tensor(0);
        Tensor M3 = new Tensor(1).zeros();
        assertTrue(M1 + " was expected Null", M1.isNull());
        assertTrue(M2 + " was expected Null", M2.isNull());
        assertFalse(M3 + " should not Null", M3.isNull());
    }

    @Test
    public void testZero() {
        Tensor M = new Tensor(4).zeros();
        assertThat(M, equalTo(0.0f, 0.0f));
        assertThat(M, not(equalTo(1.0f, 0.0f)));
    }

    @Test
    public void testOne() {
        Tensor M = new Tensor(4).ones();
        assertThat(M, equalTo(1.0f, 0.0f));
        assertThat(M, not(equalTo(0.0f, 0.0f)));
    }

    @Test
    public void testEqual() {
        Tensor M1 = new Tensor(4).arange(1, 1);
        Tensor M2 = new Tensor(4).arange(1, 1);
        Tensor M3 = new Tensor(4).ones();
        Tensor M4 = new Tensor(4, 4).arange(1, 1);
        assertThat(M1, equalTo(M1, 0.0f));
        assertThat(M1, equalTo(M2, 0.0f));
        assertThat(M2, equalTo(M1, 0.0f));
        assertThat(M1, not(equalTo(M3, 0.0f)));
        assertThat(M3, not(equalTo(M1, 0.0f)));
        assertThat(M1, not(equalTo(M4, 0.0f)));
        assertThat(M4, not(equalTo(M1, 0.0f)));
    }

    @Test
    public void testArange() {
        Tensor M1 = new Tensor(4).arange(1, 1);
        Tensor M2 = new Tensor(4).fill(1.0f, 2.0f, 3.0f, 4.0f);
        assertThat(M1, equalTo(M2, 0.0f));
        assertThat(M1, is(not(equalTo(0.0f, 0.0f))));
        assertThat(M1, is(not(equalTo(1.0f, 0.0f))));
    }

    @Test
    public void testTranspose() {
        Tensor M1 = new Tensor(4, 4).arange(1, 1);
        Tensor M2 = new Tensor(4, 4).fill(new float[][] {
            { 1.0f, 5.0f, 9.0f, 13.0f },
            { 2.0f, 6.0f, 10.0f, 14.0f },
            { 3.0f, 7.0f, 11.0f, 15.0f },
            { 4.0f, 8.0f, 12.0f, 16.0f } });
        assertThat(M1.T(), equalTo(M2, 0.0f));
        assertThat(M1.T().T(), equalTo(M1, 0.0f));
    }

    @Test
    public void testView() {
        Tensor M1 = new Tensor(16).arange(1, 1).reshape(4, 4);
        Tensor M2 = new Tensor(4, 4).fill(new float[][] {
            { 1.0f, 2.0f, 3.0f, 4.0f },
            { 5.0f, 7.0f, 8.0f, 8.0f },
            { 9.0f, 11.0f, 12.0f, 12.0f },
            { 13.0f, 14.0f, 15.0f, 16.0f } });
        assertThat(M1.view(1, 2, 1, 2).iadd(1.0f), equalTo(M2.view(1, 2, 1, 2), 0.0f));
        assertThat(M1, equalTo(M2, 0.0f));
    }

    @Test
    public void testAdd() {
        Tensor M1 = new Tensor(1).ones();
        Tensor M2 = new Tensor(4).ones();
        Tensor M2a = new Tensor(4, 1).ones();
        Tensor M3 = new Tensor(4, 4).ones();
        Tensor M4 = new Tensor(2, 4, 4).ones();

        Tensor M6 = new Tensor(1).arange(1, 1);
        Tensor M7 = new Tensor(4).arange(1, 1);
        Tensor M8 = new Tensor(4, 4).arange(1, 1);
        Tensor M9 = new Tensor(2, 4, 4).arange(1, 1);

        Tensor M10 = new Tensor(1).arange(2, 1);
        Tensor M11 = new Tensor(4).arange(2, 1);
        Tensor M12 = new Tensor(4, 4).arange(2, 1);
        Tensor M13 = new Tensor(2, 4, 4).arange(2, 1);

        assertEquals(M10, M6.add(M1));
        assertEquals(M10, M1.add(M6));
        assertEquals(M11, M7.add(M1));
        assertEquals(M11, M1.add(M7));
        assertEquals(M12, M8.add(M1));
        assertEquals(M12, M1.add(M8));
        assertEquals(M13, M9.add(M1));
        assertEquals(M13, M1.add(M9));

        assertEquals(M11, M7.add(M2));
        assertEquals(M11, M2.add(M7));
        assertEquals(M12, M8.add(M2));
        assertEquals(M12, M2.add(M8));
        assertEquals(M13, M9.add(M2));
        assertEquals(M13, M2.add(M9));

        assertEquals(M12, M8.add(M2a));
        assertEquals(M12, M2a.add(M8));
        assertEquals(M13, M9.add(M2a));
        assertEquals(M13, M2a.add(M9));

        assertEquals(M12, M8.add(M3));
        assertEquals(M12, M3.add(M8));
        assertEquals(M13, M9.add(M3));
        assertEquals(M13, M3.add(M9));

        assertEquals(M13, M9.add(M4));
        assertEquals(M13, M4.add(M9));
    }

    @Test
    public void testSum() {
        Tensor M1 = new Tensor(2, 2, 4).arange(1, 1);
        Tensor M2 = new Tensor(2, 4).fill(10, 12, 14, 16, 18, 20, 22, 24);
        Tensor M3 = new Tensor(2, 4).fill(6, 8, 10, 12, 22, 24, 26, 28);
        Tensor M4 = new Tensor(2, 2).fill(10, 26, 42, 58);
        assertThat(M1.sum(-1), equalTo(136.0f, 0.0f));
        assertThat(M1.sum(0), equalTo(M2, 0.0f));
        assertThat(M1.sum(1), equalTo(M3, 0.0f));
        assertThat(M1.sum(2), equalTo(M4, 0.0f));
    }

    @Test
    public void testMin() {
        Tensor M1 = new Tensor(4, 4).arange(1, 1);
        Tensor M2 = new Tensor(4).fill(1, 2, 3, 4);
        Tensor M3 = new Tensor(4).fill(1, 5, 9, 13);
        assertThat(M1.min(-1), equalTo(1.0f, 0.0f));
        assertThat(M1.min(0), equalTo(M2, 0.0f));
        assertThat(M1.min(1), equalTo(M3, 0.0f));
    }

    @Test
    public void testMax() {
        Tensor M1 = new Tensor(4, 4).arange(1, 1);
        Tensor M2 = new Tensor(4).fill(13, 14, 15, 16);
        Tensor M3 = new Tensor(4).fill(4, 8, 12, 16);
        assertThat(M1.max(-1), equalTo(16.0f, 0.0f));
        assertThat(M1.max(0), equalTo(M2, 0.0f));
        assertThat(M1.max(1), equalTo(M3, 0.0f));
    }

    @Test
    public void testArgMin() {
        Tensor M1 = new Tensor(4, 4).arange(1, 1);
        Tensor M2 = new Tensor(4).fill(0, 1, 2, 3);
        Tensor M3 = new Tensor(4).fill(0, 4, 8, 12);
        assertThat(M1.argmin(-1), equalTo(0.0f, 0.0f));
        assertThat(M1.argmin(0), equalTo(M2, 0.0f));
        assertThat(M1.argmin(1), equalTo(M3, 0.0f));
    }

    @Test
    public void testArgMax() {
        Tensor M1 = new Tensor(4, 4).arange(1, 1);
        Tensor M2 = new Tensor(4).fill(12, 13, 14, 15);
        Tensor M3 = new Tensor(4).fill(3, 7, 11, 15);
        assertThat(M1.argmax(-1), equalTo(15.0f, 0.0f));
        assertThat(M1.argmax(0), equalTo(M2, 0.0f));
        assertThat(M1.argmax(1), equalTo(M3, 0.0f));
    }


    @Test
    public void testAvg() {
        Tensor M = new Tensor(1, 4).arange(1, 1).repeat(4);
        assertThat(M.avg(1), equalTo(2.5f, Scalar.EPSILON));
    }

    @Test
    public void testDot() {
        Tensor M1 = new Tensor(4).arange(1, 1);
        Tensor M2 = new Tensor(4).arange(1, 1);
        assertThat(M1.dot(M2, 0), equalTo(30.0f, 0.0f));
    }

    @Test
    public void testOuter() {
        Tensor M1 = new Tensor(4, 1).arange(1, 1);
        Tensor M2 = new Tensor(4).arange(1, 1);
        Tensor M3 = new Tensor(4, 4).fill(new float[][] {
            { 1.0f, 2.0f, 3.0f, 4.0f },
            { 2.0f, 4.0f, 6.0f, 8.0f },
            { 3.0f, 6.0f, 9.0f, 12.0f },
            { 4.0f, 8.0f, 12.0f, 16.0f } });
        assertThat(M1.outer(M2), equalTo(M3, 0.0f));
    }

    @Test
    public void testNorm() {
        Tensor M = new Tensor(1, 4).arange(1, 1).repeat(4);
        assertThat(M.norm(1), equalTo(5.477f, 0.001f));
    }

    @Test
    public void testMag() {
        Tensor M1 = new Tensor(4).arange(1, 1);
        Tensor M2 = new Tensor(4).arange(2, 2);
        assertThat(M1.mag(M2, 0), equalTo(5.477f, 0.001f));
    }

    @Test
    public void testVar() {
        Tensor M1 = new Tensor(4).arange(1, 1);
        assertThat(M1.var(0), equalTo(1.667f, 0.001f));
    }

    @Test
    public void testCov() {
        Tensor M1 = new Tensor(4).arange(1, 1);
        Tensor M2 = new Tensor(4).arange(2, 2);
        assertThat(M1.cov(M1, 0), equalTo(1.667f, 0.001f));
        assertThat(M1.cov(M2, 0), equalTo(3.333f, 0.001f));

        Tensor X = new Tensor(3, 3).fill(0, 3, 4, 1, 2, 4, 3, 4, 5);
        System.out.println(X);
        System.out.println(X.cov(X, 0));
        System.out.println(X.cov2(0));
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

    private static Matcher<Tensor> equalTo(final Tensor expectedTensor, final float epsilon) {
        return new BaseMatcher<Tensor>() {
            @Override
            public boolean matches(Object item) {
                return ((Tensor) item).equals(expectedTensor, epsilon);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expectedTensor.toString());
            }
        };
    }

    private static Matcher<Tensor> equalTo(final float expectedValue, final float epsilon) {
        return new BaseMatcher<Tensor>() {
            @Override
            public boolean matches(Object item) {
                return ((Tensor) item).equals(expectedValue, epsilon);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("[" + String.format("%1$10.3f", expectedValue).toString() + " ... ]");
            }
        };
    }
}
