package com.github.romualdrousseau.shuju;

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

        assertThat(M6.add(M1), equalTo(M10, 0.0f));
        assertThat(M1.add(M6), equalTo(M10, 0.0f));
        assertThat(M7.add(M1), equalTo(M11, 0.0f));
        assertThat(M1.add(M7), equalTo(M11, 0.0f));
        assertThat(M8.add(M1), equalTo(M12, 0.0f));
        assertThat(M1.add(M8), equalTo(M12, 0.0f));
        assertThat(M9.add(M1), equalTo(M13, 0.0f));
        assertThat(M1.add(M9), equalTo(M13, 0.0f));

        assertThat(M7.add(M2), equalTo(M11, 0.0f));
        assertThat(M2.add(M7), equalTo(M11, 0.0f));
        assertThat(M8.add(M2), equalTo(M12, 0.0f));
        assertThat(M2.add(M8), equalTo(M12, 0.0f));
        assertThat(M9.add(M2), equalTo(M13, 0.0f));
        assertThat(M2.add(M9), equalTo(M13, 0.0f));

        assertThat(M8.add(M2a), equalTo(M12, 0.0f));
        assertThat(M2a.add(M8), equalTo(M12, 0.0f));
        assertThat(M9.add(M2a), equalTo(M13, 0.0f));
        assertThat(M2a.add(M9), equalTo(M13, 0.0f));

        assertThat(M8.add(M3), equalTo(M12, 0.0f));
        assertThat(M3.add(M8), equalTo(M12, 0.0f));
        assertThat(M9.add(M3), equalTo(M13, 0.0f));
        assertThat(M3.add(M9), equalTo(M13, 0.0f));

        assertThat(M9.add(M4), equalTo(M13, 0.0f));
        assertThat(M4.add(M9), equalTo(M13, 0.0f));
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
        assertThat(M1.var(0, 1), equalTo(1.667f, 0.001f));
    }

    @Test
    public void testCov() {
        Tensor M1 = new Tensor(4).arange(1, 1);
        Tensor M2 = new Tensor(4).arange(2, 2);
        Tensor M3 = new Tensor(2, 3).fill(-2.1f, -1, 4.3f, 3, 1.1f,  0.12f);
        Tensor M4 = new Tensor(3, 3).fill(13.005f, 5.355f, -10.659f, 5.355f, 2.205f, -4.389f, -10.659f, -4.389f, 8.736f);
        Tensor M5 = new Tensor(2, 2).fill(11.71f, -4.286f, -4.286f, 2.144f);
        assertThat(M1.cov(M1, 0, 1), equalTo(1.667f, 0.001f));
        assertThat(M1.cov(M2, 0, 1), equalTo(3.333f, 0.001f));
        assertThat(M3.cov2(M3, 0, 1), equalTo(M4, 0.001f));
        assertThat(M3.cov2(M3, 1, 1), equalTo(M5, 0.001f));
    }

    @Test
    public void testMatMul() {
        Tensor M1 = new Tensor(2, 3).fill(1, 2, 3, 4, 5, 6).repeat(2).reshape(2, 2, 3);
        Tensor M2 = new Tensor(3, 3).fill(1, 1, 1, 0, 1, 0, 1, 1, 1);
        Tensor M3 = new Tensor(2, 2, 3).fill(4, 6, 4, 10, 15, 10, 4, 6, 4, 10, 15, 10);
        assertThat(M1.matmul(new Tensor(3, 3).eye(0)), equalTo(M1, 0.0f));
        assertThat(M1.matmul(M2), equalTo(M3, 0.0f));
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
}
