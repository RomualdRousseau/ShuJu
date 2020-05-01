package com.github.romualdrousseau.shuju;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Tensor;

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
        assertThat(M, notEqualTo(1.0f, 0.0f));
    }

    @Test
    public void testOne() {
        Tensor M = new Tensor(4).ones();
        assertThat(M, equalTo(1.0f, 0.0f));
        assertThat(M, notEqualTo(0.0f, 0.0f));
    }

    @Test
    public void testArange() {
        Tensor M1 = new Tensor(4).arange(1, 1);
        Tensor M2 = new Tensor(4).fill(1.0f, 2.0f, 3.0f, 4.0f);
        assertThat(M1, equalTo(M2, 0.0f));
        assertThat(M1, notEqualTo(0.0f, 0.0f));
        assertThat(M1, notEqualTo(1.0f, 0.0f));
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
        Tensor M2 = new Tensor(4, 4).fill(new float[][] { { 1.0f, 2.0f, 3.0f, 4.0f }, { 5.0f, 7.0f, 8.0f, 8.0f },
                { 9.0f, 11.0f, 12.0f, 12.0f }, { 13.0f, 14.0f, 15.0f, 16.0f } });
        assertEquals(M2.view(1, 2, 1, 2), M1.view(1, 2, 1, 2).iadd(1.0f));
        assertEquals(M2, M1);
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
    public void testNorm() {
        Tensor M = new Tensor(1, 4).arange(1, 1).repeat(4);
        assertThat(M.norm(1), equalTo(5.477f, 0.001f));
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
        assertTrue("M1.M2 = [ 30.0f ]", M1.dot(M2, 0).equals(30.0f, 0.0f));
    }

    @Test
    public void testOuter() {
        Tensor M1 = new Tensor(4, 1).arange(1, 1);
        Tensor M2 = new Tensor(4).arange(1, 1);
        Tensor M3 = new Tensor(4, 4).fill(new float[][] { { 1.0f, 2.0f, 3.0f, 4.0f }, { 2.0f, 4.0f, 6.0f, 8.0f },
                { 3.0f, 6.0f, 9.0f, 12.0f }, { 4.0f, 8.0f, 12.0f, 16.0f } });
        assertTrue("M1xM2 = " + M3, M1.outer(M2).equals(M3));
    }

    @Test
    public void testMag() {
        Tensor M1 = new Tensor(4).arange(1, 1);
        Tensor M2 = new Tensor(4).arange(2, 2);
        assertTrue("M1.distance(M2) = [ 5.477f ]", M1.mag(M2, 0).equals(5.477f, 0.001f));
    }

    @Test
    public void testVar() {
        Tensor M1 = new Tensor(4).arange(1, 1);
        assertTrue("M1.var() = [ 1.667f ]", M1.var(0).equals(1.667f, 0.001f));
    }

    @Test
    public void testCov() {
        Tensor M1 = new Tensor(4).arange(1, 1);
        Tensor M2 = new Tensor(4).arange(2, 2);
        assertTrue("M1.cov(M1) = [ 1.667f ]", M1.cov(M1, 0).equals(1.667f, 0.001f));
        assertTrue("M1.cov(M2) = [ 3.333f ]", M1.cov(M2, 0).equals(3.333f, 0.001f));
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

    private static Matcher<Tensor> notEqualTo(final Tensor expectedTensor, final float epsilon) {
        return new BaseMatcher<Tensor>() {
            @Override
            public boolean matches(Object item) {
                return !((Tensor) item).equals(expectedTensor, epsilon);
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

    private static Matcher<Tensor> notEqualTo(final float expectedValue, final float epsilon) {
        return new BaseMatcher<Tensor>() {
            @Override
            public boolean matches(Object item) {
                return !((Tensor) item).equals(expectedValue, epsilon);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("[" + String.format("%1$10.3f", expectedValue).toString() + " ... ]");
            }
        };
    }
}
