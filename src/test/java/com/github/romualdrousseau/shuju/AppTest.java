package com.github.romualdrousseau.shuju;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;
import java.net.URISyntaxException;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import com.github.romualdrousseau.shuju.columns.NumericColumn;
import com.github.romualdrousseau.shuju.columns.StringColumn;
import com.github.romualdrousseau.shuju.math.Blas;
import com.github.romualdrousseau.shuju.math.Linalg;
//import com.github.romualdrousseau.shuju.math.Marray;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;
import com.github.romualdrousseau.shuju.math.Tensor1D;
import com.github.romualdrousseau.shuju.transforms.*;
import com.github.romualdrousseau.shuju.ml.knn.*;
import com.github.romualdrousseau.shuju.ml.nn.Helper;
import com.github.romualdrousseau.shuju.ml.slr.*;
import com.github.romualdrousseau.shuju.nlp.StringList;
import com.github.romualdrousseau.shuju.util.*;

public class AppTest {

    // @Test public void testMarray() {
    //     Marray M = new Marray(2, 4, 4).arrange();
    //     Marray N = new Marray(1, 1, 4).arrange();
    //     Marray V = new Marray(4).arrange();
    //     System.out.println(M);
    //     System.out.println();
    //     System.out.println(M.transpose(0, 2, 1));
    //     System.out.println();
    //     System.out.println(N);
    //     System.out.println();
    //     System.out.println(M.iadd(N).iadd(0.5f));
    //     System.out.println();
    //     System.out.println(V.reshape(1, 4).transpose());
    //     System.out.println();
    //     System.out.println(M.transpose());
    // }

    // @Test
    // public void testSpeed() {
    //     final int w = 1024;
    //     float[] a = new float[w * w];
    //     float[] b = new float[w * w];
    //     float[] c = new float[w * w];

    //     long start = System.currentTimeMillis();
    //     for(int k = 0; k < 10; k++) {
    //         for(int i = 0; i < w; i++) {
    //             for(int j = 0; j < w; j++) {
    //                 for(int l = 0; l < w; l++) {
    //                     c[i * w + j] = Math.fma(a[i * w + j], b[l * w + j], c[i * w + j]);
    //                 }
    //             }
    //         }
    //     }
    //     long end = System.currentTimeMillis();
    //     float time = (float) (end - start) / 10.0f;
    //     System.out.println("loop took " + time + "ms");

    //     start = System.currentTimeMillis();
    //     for(int k = 0; k < 10; k++) {
    //         for(int i = 0; i < w; i++) {
    //             for(int l = 0; l < w; l++) {
    //                 fmav(w, a, i * w, b, l * w, c, i * w);
    //             }
    //         }
    //     }
    //     end = System.currentTimeMillis();
    //     time = (float) (end - start) / 10.0f;
    //     System.out.println("loop took " + time + "ms");
    // }

    // private void fmav(final int n, final float[] a, final int oa, final float[] b, final int ob, final float c[], final int oc) {
    //     final float[] tmpA = new float[8];
    //     final float[] tmpB = new float[8];
    //     final float[] tmpC = new float[8];
    //     for(int j = 0; j < n; j+=8) {
    //         System.arraycopy(a, oa + j, tmpA, 0, 8);
    //         System.arraycopy(b, ob + j, tmpB, 0, 8);
    //         System.arraycopy(c, oc + j, tmpC, 0, 8);
    //         tmpC[0] = Math.fma(tmpA[0], tmpB[0], tmpC[0]);
    //         tmpC[1] = Math.fma(tmpA[1], tmpB[1], tmpC[1]);
    //         tmpC[2] = Math.fma(tmpA[2], tmpB[2], tmpC[2]);
    //         tmpC[3] = Math.fma(tmpA[3], tmpB[3], tmpC[3]);
    //         tmpC[4] = Math.fma(tmpA[4], tmpB[4], tmpC[4]);
    //         tmpC[5] = Math.fma(tmpA[5], tmpB[5], tmpC[5]);
    //         tmpC[6] = Math.fma(tmpA[6], tmpB[5], tmpC[6]);
    //         tmpC[7] = Math.fma(tmpA[7], tmpB[5], tmpC[7]);
    //         System.arraycopy(tmpC, 0, c, oc + j, 8);
    //     }
    // }

    @Test
    public void testBlasGemm() {
        final Tensor2D M = new Tensor2D(4, 4).arrange(0);
        final Tensor2D M_T = M.transpose();
        final Tensor2D T = new Tensor2D(new float[][] { { 30, 70, 110, 150 }, { 70, 174, 278, 382 }, { 110, 278, 446, 614 }, { 150, 382, 614, 846 } });
        final Tensor2D R = new Tensor2D(4, 4);

        Blas.fgemm(false, false, M.getFloats(), 1.0f, M_T.getFloats(), 0.0f, R.getFloats());
        assertTrue("M@transpose(M) = T", R.equals(T));

        Blas.fgemm(false, true, M.getFloats(), 1.0f, M.getFloats(), 0.0f, R.getFloats());
        assertTrue("M@M_T = T", R.equals(T));

        Blas.fgemm(false, false, M_T.getFloats(), 1.0f, M.getFloats(), 0.0f, T.getFloats());
        Blas.fgemm(true, false, M.getFloats(), 1.0f, M.getFloats(), 0.0f, R.getFloats());
        assertTrue("M_T@M = T", R.equals(T));

        Blas.fgemm(false, false, M_T.getFloats(), 1.0f, M_T.getFloats(), 0.0f, T.zero().getFloats());
        Blas.fgemm(true, true, M.getFloats(), 1.0f, M.getFloats(), 0.0f, R.zero().getFloats());
        assertTrue("M_T@M = T", R.equals(T));
    }

    @Test
    public void testBlasSpeed() {
        final Tensor2D A = new Tensor2D(1024, 1024).randomize();
        final Tensor2D B = new Tensor2D(1024, 1024).randomize();
        final Tensor2D C = new Tensor2D(1024, 1024).randomize();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            Blas.fgemm(false, false, A.getFloats(), 1.0f, B.getFloats(), 1.0f, C.getFloats());
        }
        long end = System.currentTimeMillis();
        int time = (int) ((end - start) / 10);
        assertTrue("fgemm(false, false) took " + time + "ms", time < 500);

        start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            Blas.fgemm(false, true, A.getFloats(), 1.0f, B.getFloats(), 1.0f, C.getFloats());
        }
        end = System.currentTimeMillis();
        time = (int) ((end - start) / 10);
        assertTrue("fgemm(false, true) took " + time + "ms", time < 500);

        start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            Blas.fgemm(true, false, A.getFloats(), 1.0f, B.getFloats(), 1.0f, C.getFloats());
        }
        end = System.currentTimeMillis();
        time = (int) ((end - start) / 10);
        assertTrue("fgemm(true, false) took " + time + "ms", time < 500);

        start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            Blas.fgemm(true, true, A.getFloats(), 1.0f, B.getFloats(), 1.0f, C.getFloats());
        }
        end = System.currentTimeMillis();
        time = (int) ((end - start) / 10);
        assertTrue("fgemm(true, true) took " + time + "ms", time < 500);
    }

    @Test
    public void testMatrixMinor() {
        final Tensor2D M1 = new Tensor2D(new float[][] { { 2, 3, 1 }, { 4, 5, 6 }, { 7, 8, 1 } });
        final Tensor2D M2 = new Tensor2D(new float[][] { { 5, 6 }, { 8, 1 } });
        assertTrue("M1.minor(0, 0) = M2", M1.minor(0, 0).equals(M2));
    }

    @Test
    public void testMatrixDet() {
        final Tensor2D M = new Tensor2D(new float[][] { { 2, 3, 1 }, { 4, 5, 6 }, { 7, 8, 1 } });
        assertEquals("Det(M)", 25, M.det(), 0);
    }

    @Test
    public void testMatrixInverse() {
        final Tensor2D M1 = new Tensor2D(new float[][] { { 2, 3, 1 }, { 4, 5, 6 }, { 7, 8, 1 } });
        final Tensor2D M2 = new Tensor2D(new float[][] { { -1.72f, 0.2f, 0.52f }, { 1.52f, -0.2f, -0.32f }, { -0.12f, 0.2f, -0.08f } });
        final Tensor2D M3 = M1.inv();
        assertTrue("M1- = M2", M3.equals(M2, 1e-2f));
        assertTrue("M1- = Cof(M1)* / Det(m1)", M1.cof().transpose().mul(1.0f / M1.det()).equals(M3, 1e-2f));
        assertTrue("M1- = Adj(M1) / Det(m1)", M1.adj().mul(1.0f / M1.det()).equals(M3, 1e-2f));
    }

    @Test
    public void testMatrixReshape() {
        final Tensor2D M = new Tensor2D(new float[][] { { 52, 30, 49, 28 }, { 30, 50, 8, 44 }, { 49, 8, 46, 16 }, { 28, 44, 16, 22 } });
        assertTrue("M.reshape(1, 16, 1)* = M.reshape(16, 1, 0)", M.reshape(1, 16).transpose().equals(M.reshape(16, 1, 'F')));
        assertTrue("M.reshape(2, 8, 1)* = M.reshape(8, 2, 0)", M.reshape(2, 8).transpose().equals(M.reshape(8, 2, 'F')));
    }

    @Test
    public void testHelperIm2Col() {
        final Tensor3D M = new Tensor3D(2, 4, 4).arrange(0);
        assertTrue("Col2im(Im2col(M, 2, 2) = M", Helper.Conv2Img(Helper.Img2Conv(M, 2, 2, 0), 4, 4, 2, 2, 0).equals(M));
    }

    @Test
    public void testHelperConv() {
        final Tensor2D M = new Tensor2D(new float[][] { { 0, 50, 0, 29 }, { 0, 80, 31, 2 }, { 33, 90, 0, 75 }, { 0, 9, 0, 95 } });
        final Tensor2D F = new Tensor2D(new float[][] { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } });
        final Tensor2D R = new Tensor2D(new float[][] { { 29, -192 }, { -35, -22 } });
        assertTrue("F @ im2col(M) = R", F.reshape(1, 1, 3 * 3).matmul(Helper.Img2Conv(M.reshape(1, 4, 4), 3, 1, 0)).reshape(2, 2).equals(R));
        assertTrue("im2col(M, 2) = im2col(pad(M, 2))", Helper.Img2Conv(M.reshape(1, 4, 4), 3, 1, 2).equals(Helper.Img2Conv(M.pad(2, 0.0f).reshape(1, 8, 8), 3, 1, 0)));
    }

    @Test
    public void testHelperPooling() {
        final Tensor3D M = new Tensor3D(new float[][][] { { { 0, 50, 0, 29 }, { 0, 80, 31, 2 }, { 33, 90, 0, 75 }, { 0, 9, 0, 95 } } });
        final Tensor2D R1 = new Tensor2D(new float[][] { { 80, 31 }, { 90, 95 } });
        final Tensor2D R2 = new Tensor2D(new float[][] { { 0, 0 }, { 0, 0 } });
        final Tensor2D R3 = new Tensor2D(new float[][] { { 32.5f, 15.5f }, { 33.0f, 42.5f } });
        assertTrue("PoolMax(M, 2) = R1", Helper.Img2Conv(M, 2, 2, 0).max(0).reshape(2, 2).equals(R1));
        assertTrue("PoolMin(M, 2) = R2", Helper.Img2Conv(M, 2, 2, 0).min(0).reshape(2, 2).equals(R2));
        assertTrue("PoolAvg(M, 2) = R3", Helper.Img2Conv(M, 2, 2, 0).avg(0).reshape(2, 2).equals(R3));
    }

    @Test
    public void testHelperExpanding() {
        final Tensor3D M = new Tensor2D(new float[][] { { 0, 50, 0, 29 }, { 0, 80, 31, 2 }, { 33, 90, 0, 75 }, { 0, 9, 0, 95 } }).reshape(1, 4, 4);
        final Tensor3D E = new Tensor2D(new float[][] { { 1, 1 }, { 1, 1 } }).reshape(1, 2, 2);
        final Tensor2D R1 = new Tensor2D(new float[][] { { 0, 0, 0, 0 }, { 0, 1, 1, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 1 } });
        final Tensor2D R2 = new Tensor2D(new float[][] { { 1, 0, 1, 0 }, { 1, 0, 0, 0 }, { 0, 0, 1, 0 }, { 1, 0, 1, 0 } });
        final Tensor2D R3 = new Tensor2D(new float[][] { { 8.125f, 8.125f, 3.875f, 3.875f }, { 8.125f, 8.125f, 3.875f, 3.875f }, { 8.25f, 8.25f, 10.625f, 10.625f }, { 8.25f, 8.25f, 10.625f, 10.625f } });
        assertTrue("ExpandMax(M, 2) = R1", Helper.expandMinMax(Helper.Img2Conv(M, 2, 2, 0).max(0).reshape(1, 2, 2), M, E).reshape(4, 4).equals(R1));
        assertTrue("ExpandMin(M, 2) = R2", Helper.expandMinMax(Helper.Img2Conv(M, 2, 2, 0).min(0).reshape(1, 2, 2), M, E).reshape(4, 4).equals(R2));
        assertTrue("ExpandAvg(M, 2) = R3", Helper.expandAvg(Helper.Img2Conv(M, 2, 2, 0).avg(0).reshape(1, 2, 2), 2).reshape(4, 4).equals(R3));
    }

    @Test
    public void testLinalgUpper() {
        final Tensor2D M = new Tensor2D(new float[][] { { 2, 3, 1 }, { 4, 5, 6 }, { 7, 8, 1 } });
        final Tensor2D I = new Tensor2D(3, 3).identity();
        final Tensor2D U = Linalg.GaussianElimination(M, false);
        assertTrue("U is upper", U.isUpper(0, 1e-2f));
        assertTrue("SolveUpperTriangular(U) = I", Linalg.SolveTriangular(U, false).equals(I, 1e-2f));
    }

    @Test
    public void testLinalgLower() {
        final Tensor2D M = new Tensor2D(new float[][] { { 2, 3, 1 }, { 4, 5, 6 }, { 7, 8, 1 } });
        final Tensor2D L = Linalg.GaussianElimination(M, true);
        assertTrue("L is lower", L.isLower(0, 1e-2f));
        assertTrue("SolveLowerTriangular(L) = I",
                Linalg.SolveTriangular(L, true).equals(new Tensor2D(3, 3).identity(), 1e-2f));
    }

    @Test
    public void testLinalgSolve() {
        final Tensor2D M1 = new Tensor2D(new float[][] { { 2, 3, 1 }, { 4, 5, 6 }, { 7, 8, 1 } });
        final Tensor2D M2 = new Tensor2D(new float[] { 3, 5, 6 }, false);
        final Tensor2D I = new Tensor2D(3, 3).identity();
        assertTrue("Solve(M1, I) = M1-", Linalg.Solve(M1, I).equals(M1.inv(), 1e-2f));
        assertTrue("M1@Solve(M1, M2) = M2", M1.matmul(Linalg.Solve(M1, M2)).equals(M2, 1e-2f));
    }

    @Test
    public void testLinalgLU() {
        final Tensor2D M = new Tensor2D(
                new float[][] { { 3, 8, 1, -4 }, { 7, 3, -1, 2 }, { -1, 1, 4, -1 }, { 2, -4, -1, 6 } });
        final Tensor2D I = new Tensor2D(4, 4).identity();
        final Tensor2D P = Linalg.Pivot(M);
        final Tensor2D[] tmp = Linalg.LU(P.matmul(M));
        final Tensor2D L = tmp[0];
        final Tensor2D U = tmp[1];
        assertTrue("P is permutation i.e. P*@P = I", P.transpose().matmul(P).equals(I));
        assertTrue("L is lower", L.isLower(0, 1e-2f));
        assertTrue("U is upper", U.isUpper(0, 1e-2f));
        assertTrue("P*@L@U = M", P.transpose().matmul(L.matmul(U)).equals(M, 1e-2f));
    }

    @Test
    public void testLinalgCholesky() {
        final Tensor2D M = new Tensor2D(
                new float[][] { { 6, 3, 4, 8 }, { 3, 6, 5, 1 }, { 4, 5, 10, 7 }, { 8, 1, 7, 25 } });
        final Tensor2D L = Linalg.Cholesky(M);
        final Tensor2D U = L.transpose();
        assertTrue("L is lower", L.isLower(0, 1e-2f));
        assertTrue("U is upper", U.isUpper(0, 1e-2f));
        assertTrue("L@U = M", L.matmul(U).equals(M, 1e-2f));
    }

    @Test
    public void testLinalgQR() {
        final Tensor2D M = new Tensor2D(
                new float[][] { { 12, -51, 4 }, { 6, 167, -68 }, { -4, 24, -41 }, { -5, 25, -42 } });
        final Tensor2D[] tmp = Linalg.QR(M);
        final Tensor2D Q = tmp[0];
        final Tensor2D R = tmp[1];
        assertTrue("R is upper", R.isUpper(0, 1e-2f));
        assertTrue("Q is orthogonal", Q.isOrthogonal(1e-2f));
        assertTrue("Q@R = M", Q.matmul(R).equals(M, 1e-2f));
    }

    @Test
    public void testLinalgHessenberg() {
        // Matrix M = new Matrix(new float[][] { { 3, -1, 2 }, { 2, 5, -5 }, { -2, -3, 7
        // } });
        final Tensor2D M = new Tensor2D(
                new float[][] { { 52, 30, 49, 28 }, { 30, 50, 8, 44 }, { 49, 8, 46, 16 }, { 28, 44, 16, 22 } });
        // Matrix M = new Matrix(new float[][] { { -3.05f, 1.62f, -4.94f, -5.17f }, {
        // -3.72f, 2.18f, -6.11f, -6.32f }, { 13.24f, -7.23f, 21.51f, 22.44f }, { 7.01f,
        // -3.43f, 11.23f, 11.86f } });
        final Tensor2D[] tmp = Linalg.Hessenberg(M);
        assertTrue("H is square", tmp[0].isSquared());
        assertTrue("H is hessenger upper right form", tmp[0].isUpper(1, 1e-2f));
        assertTrue("Q is orthogonal", tmp[1].isOrthogonal(1e-2f));
        assertTrue("M = Q@A@Q*", tmp[1].matmul(tmp[0]).matmul(tmp[1].transpose()).equals(M, 1e-2f));
    }

    @Test
    public void testLinalgEig() {
        // Matrix M = new Matrix(new float[][] { { 52, 30, 49, 28 }, { 30, 50, 8, 44 },
        // { 49, 8, 46, 16 }, { 28, 44, 16, 22 } });
        final Tensor2D M = new Tensor2D(
                new float[][] { { -3.05f, 1.62f, -4.94f, -5.17f }, { -3.72f, 2.18f, -6.11f, -6.32f },
                        { 13.24f, -7.23f, 21.51f, 22.44f }, { 7.01f, -3.43f, 11.23f, 11.86f } });
        final Tensor2D[] tmp = Linalg.Eig(M, 1e-6f);
        for (int i = 0; i < tmp[0].shape[0]; i++) {
            final float l = tmp[0].get(i, i);
            final Tensor1D v = new Tensor1D(tmp[1].transpose().getFloats(i));
            assertTrue("M@v[" + i + "] = lv[" + i + "]", M.matmul(v).isSimilar(v.copy().mul(l), 1e-2f));
        }
        assertTrue("M = Q@H@Q-", tmp[1].matmul(tmp[0]).matmul(tmp[1].inv()).equals(M, 1e-2f));
    }

    @Test
    public void testFuzzyString() {
        assertThat(FuzzyString.Jaccard("MATERIAL CODE", "MATERIAL CODE"), is(greaterThanOrEqualTo(0.8f)));
        assertThat(FuzzyString.Jaccard("MATERIAL CODE", "MATERIAL COCE"), is(greaterThanOrEqualTo(0.8f)));
        assertThat(FuzzyString.Jaccard("MATERIAL CODE", "MAT. CODE"), is(greaterThanOrEqualTo(0.6f)));
        assertThat(FuzzyString.Jaccard("MATERIAL CODE", "MATERIAL NAME"), is(lessThan(0.8f)));
        assertThat(FuzzyString.Jaccard("MATERIAL CODE", "MAT. NAME"), is(lessThan(0.8f)));

        assertThat(FuzzyString.JaroWinkler("MATERIAL CODE", "MATERIAL CODE"), is(greaterThanOrEqualTo(0.9f)));
        assertThat(FuzzyString.JaroWinkler("MATERIAL CODE", "MATERIAL COCE"), is(greaterThanOrEqualTo(0.9f)));
        assertThat(FuzzyString.JaroWinkler("MATERIAL CODE", "MAT. CODE"), is(greaterThanOrEqualTo(0.7f)));
        assertThat(FuzzyString.JaroWinkler("MATERIAL CODE", "MATERIAL NAME"), is(lessThan(0.9f)));
        assertThat(FuzzyString.JaroWinkler("MATERIAL CODE", "MAT. NAME"), is(lessThan(0.9f)));
    }

    @Test
    public void testSepalLengthDataSummary() {
        final DataSet fisherset = loadFisherSet();

        final DataSummary summary = new DataSummary(fisherset, DataRow.FEATURES, 0);
        final Tensor1D var = DataStatistics.var(summary);
        final Tensor1D stdev = var.copy().sqrt();

        assertEquals("Count", 150, summary.count);
        assertEquals("Min", 4.300, summary.min.get(0), 0.001);
        assertEquals("Max", 7.900, summary.max.get(0), 0.001);
        assertEquals("Sum", 876.500, summary.sum.get(0), 0.001);
        assertEquals("Avg", 5.843, summary.avg.get(0), 0.001);
        assertEquals("Var", 0.685, var.get(0), 0.001);
        assertEquals("Stdev", 0.828, stdev.get(0), 0.001);
    }

    @Test
    public void testSepalWidthDataSummary() {
        final DataSet fisherset = loadFisherSet();

        final DataSummary summary = new DataSummary(fisherset, DataRow.FEATURES, 1);
        final Tensor1D var = DataStatistics.var(summary);
        final Tensor1D stdev = var.copy().sqrt();

        assertEquals("Count", 150, summary.count);
        assertEquals("Min", 2.000, summary.min.get(0), 0.001);
        assertEquals("Max", 4.400, summary.max.get(0), 0.001);
        assertEquals("Sum", 458.600, summary.sum.get(0), 0.001);
        assertEquals("Avg", 3.057, summary.avg.get(0), 0.001);
        assertEquals("Var", 0.189, var.get(0), 0.001);
        assertEquals("Stdev", 0.436, stdev.get(0), 0.001);
    }

    @Test
    public void testPetalLengthDataSummary() {
        final DataSet fisherset = loadFisherSet();

        final DataSummary summary = new DataSummary(fisherset, DataRow.FEATURES, 2);
        final Tensor1D var = DataStatistics.var(summary);
        final Tensor1D stdev = var.copy().sqrt();

        assertEquals("Count", 150, summary.count);
        assertEquals("Min", 1.000, summary.min.get(0), 0.001);
        assertEquals("Max", 6.900, summary.max.get(0), 0.001);
        assertEquals("Sum", 563.700, summary.sum.get(0), 0.001);
        assertEquals("Avg", 3.758, summary.avg.get(0), 0.001);
        assertEquals("Var", 3.116, var.get(0), 0.001);
        assertEquals("Stdev", 1.765, stdev.get(0), 0.001);
    }

    @Test
    public void testPetalWidthDataSummary() {
        final DataSet fisherset = loadFisherSet();

        final DataSummary summary = new DataSummary(fisherset, DataRow.FEATURES, 3);
        final Tensor1D var = DataStatistics.var(summary);
        final Tensor1D stdev = var.copy().sqrt();

        assertEquals("Count", 150, summary.count);
        assertEquals("Min", 0.100, summary.min.get(0), 0.001);
        assertEquals("Max", 2.500, summary.max.get(0), 0.001);
        assertEquals("Sum", 180.000, summary.sum.get(0), 0.001);
        assertEquals("Avg", 1.200, summary.avg.get(0), 0.001);
        assertEquals("Var", 0.580, var.get(0), 0.001);
        assertEquals("Stdev", 0.761, stdev.get(0), 0.001);
    }

    @Test
    public void testLengthCovariance() {
        final DataSet fisherset = loadFisherSet();

        final Tensor1D cov = DataStatistics.cov(new DataSummary(fisherset, DataRow.FEATURES, 0),
                new DataSummary(fisherset, DataRow.FEATURES, 2));

        assertEquals("Cov", 1.274, cov.get(0), 0.001);
    }

    @Test
    public void testWidthCovariance() {
        final DataSet fisherset = loadFisherSet();

        final Tensor1D cov = DataStatistics.cov(new DataSummary(fisherset, DataRow.FEATURES, 1),
                new DataSummary(fisherset, DataRow.FEATURES, 3));

        assertEquals("Cov", -0.121, cov.get(0), 0.001);
    }

    @Test
    public void testLengthCorellation() {
        final DataSet fisherset = loadFisherSet();

        final Tensor1D corr = DataStatistics.corr(new DataSummary(fisherset, DataRow.FEATURES, 0),
                new DataSummary(fisherset, DataRow.FEATURES, 2));

        assertEquals("Corr", 0.872, corr.get(0), 0.001);
    }

    @Test
    public void testSLR() {
        final DataSet training = loadLiquidStat();
        assert training != null;

        final SLR slr = new SLR();

        slr.fit(training.featuresAsVectorArray(), training.labelsAsVectorArray());

        final DataRow test = new DataRow().addFeature(new Tensor1D(1, 13.0f)).setLabel(new Tensor1D(1, 1.157f));

        final Tensor1D result = slr.predict(test.featuresAsOneVector());

        assertEquals("Result", test.label().get(0), result.get(0), 0.001f);
    }

    @Test
    public void testKNN() {
        final DataSet fisherset = loadFisherSet();
        assert fisherset != null;

        // Scale all features
        fisherset.transform(new NumericScaler(new DataSummary(fisherset, DataRow.FEATURES, 0)), DataRow.FEATURES, 0)
                .transform(new NumericScaler(new DataSummary(fisherset, DataRow.FEATURES, 1)), DataRow.FEATURES, 1)
                .transform(new NumericScaler(new DataSummary(fisherset, DataRow.FEATURES, 2)), DataRow.FEATURES, 2)
                .transform(new NumericScaler(new DataSummary(fisherset, DataRow.FEATURES, 3)), DataRow.FEATURES, 3)
                .shuffle();

        final KNN knn = new KNN(6);
        final DataSet training = fisherset.subset(0, 110);

        knn.fit(training.featuresAsVectorArray(), training.labelsAsVectorArray());

        final DataSet test = fisherset.subset(110, 150);

        int total = 0;
        int success = 0;
        float error = 0.0f;

        for (final DataRow row : test.rows()) {
            final Tensor1D result = knn.predict(row.featuresAsOneVector());
            assert !result.isNull();

            final int bestLabel = result.argmax();

            if (bestLabel == row.label().argmax()) {
                success++;
            }

            error += 1.0f - result.get(bestLabel);
            total++;
        }

        final float correct = (float) success / (float) total;
        final float incorrect = 1.0f - correct;
        error /= (float) total;

        assertEquals("Correctly Classified Instances", 1.0, correct, 0.2);
        assertEquals("Incorrectly Classified Instances", 0.0, incorrect, 0.2);
        assertEquals("Mean error", 0.0001f, error, 0.001);
        assertEquals("Total Number of Instances", total, test.rows().size());
    }

    private DataSet loadFisherSet() {
        final DataSet fisherSet = new DataSet();
        final NumericColumn c1 = new NumericColumn();
        final NumericColumn c2 = new NumericColumn();
        final NumericColumn c3 = new NumericColumn();
        final NumericColumn c4 = new NumericColumn();
        final StringColumn c5 = new StringColumn(
                new StringList(new String[] { "I. setosa", "I. versicolor", "I. virginica" }));

        final List<String[]> rows = loadTable("/data/fisher's data.csv", "header");
        assert rows != null;

        for (final String[] cells : rows) {
            fisherSet.addRow(new DataRow().addFeature(c1.valueOf(Float.valueOf(cells[1])))
                    .addFeature(c2.valueOf(Float.valueOf(cells[2]))).addFeature(c3.valueOf(Float.valueOf(cells[3])))
                    .addFeature(c4.valueOf(Float.valueOf(cells[4]))).setLabel(c5.valueOf(cells[5])));
        }

        return fisherSet;
    }

    private DataSet loadLiquidStat() {
        final DataSet liquidSet = new DataSet();
        final NumericColumn c1 = new NumericColumn();
        final NumericColumn c2 = new NumericColumn();

        final List<String[]> rows = loadTable("/data/x62.csv", "header");
        assert rows != null;

        for (final String[] cells : rows) {
            liquidSet.addRow(new DataRow().addFeature(c1.valueOf(Float.valueOf(cells[1])))
                    .setLabel(c2.valueOf(Float.valueOf(cells[2]))));
        }

        return liquidSet;
    }

    private List<String[]> loadTable(final String resourceName, final String options) {
        try {
            final URL resourceUrl = getClass().getResource(resourceName);
            assert resourceUrl != null;

            final List<String> tmp = Files.readAllLines(Paths.get(resourceUrl.toURI()));

            if (options.equals("header")) {
                tmp.remove(0); // remove headers
            }

            final ArrayList<String[]> table = new ArrayList<String[]>();
            for (final String line : tmp) {
                final String[] tokens = line.split(",");
                table.add(tokens);
            }
            table.trimToSize();

            return table;
        } catch (final URISyntaxException x) {
            assert false : x.getMessage();
            return null;
        } catch (final IOException x) {
            assert false : x.getMessage();
            return null;
        }
    }
}
