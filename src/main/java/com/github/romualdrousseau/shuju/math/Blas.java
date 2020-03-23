package com.github.romualdrousseau.shuju.math;

import java.util.stream.IntStream;

public class Blas {

    private static int ncpu = 1;
    static {
        Blas.ncpu = Runtime.getRuntime().availableProcessors();
    }

    public static void fgemv(final boolean tranA, final float[][] a, final float alpha, final float[] b,
            final float beta, final float[] c) {
        if (alpha == 0.0f && beta == 1.0f) {
            return;
        }

        final int M = tranA ? a[0].length : a.length;
        final int K = b.length;
        final int K_ = tranA ? a.length : a[0].length;

        // Check dimensionalities

        assert (K == K_) : "Illegal Dimension";
        assert (c.length == M) : "Illegal Dimension";

        // bC = C

        if (beta == 0.0f) {
            for (int i = 0; i < M; i++) {
                c[i] = 0.0f;
            }
        } else {
            for (int i = 0; i < M; i++) {
                c[i] *= beta;
            }
        }

        if (alpha == 0.0f) {
            return;
        }

        if (tranA) {

            // aA*@B + bC = C

            for (int j = 0; j < K; j++) {
                final float[] a_j = a[j];
                final float b_j = alpha * b[j];
                for (int i = 0; i < M; i++) {
                    c[i] += a_j[i] * b_j;
                }
            }
        } else {

            // aA@B + bC = C

            for (int i = 0; i < M; i++) {
                final float[] a_i = a[i];
                for (int j = 0; j < K; j++) {
                    c[i] += alpha * a_i[j] * b[j];
                }
            }
        }
    }

    public static void fgemm(final boolean tranA, final boolean tranB, final float[][] a, final float alpha,
            final float[][] b, final float beta, final float[][] c) {
        if (alpha == 0.0f && beta == 1.0f) {
            return;
        }

        final int M = tranA ? a[0].length : a.length;
        final int K = tranA ? a.length : a[0].length;


        // bC = C

        if (beta == 0.0f) {
            for (int i = 0; i < M; i++) {
                final float[] c_i = c[i];
                for (int j = 0; j < K; j++) {
                    c_i[j] = 0.0f;
                }
            }
        } else if (beta != 1.0f) {
            for (int i = 0; i < M; i++) {
                final float[] c_i = c[i];
                for (int j = 0; j < K; j++) {
                    c_i[j] *= beta;
                }
            }
        }

        if (alpha == 0.0f) {
            return;
        }

        if (b == null) {
            assert (c.length == M && c[0].length == K) : "Illegal Dimension";

            if (tranA) {

                // aA* + bC = C

                for (int j = 0; j < K; j++) {
                    final float[] a_j = a[j];
                    for (int i = 0; i < M; i++) {
                        c[i][j] += alpha * a_j[i];
                    }
                }
            } else {

                // aA + bC = C

                for (int i = 0; i < M; i++) {
                    final float[] a_i = a[i];
                    final float[] c_i = c[i];
                    for (int j = 0; j < K; j++) {
                        c_i[j] += alpha * a_i[j];
                    }
                }
            }
        } else {
            final int N = tranB ? b.length : b[0].length;
            final int K_ = tranB ? b[0].length : b.length;

            // Check dimensionalities

            assert (K == K_) : "Illegal Dimension";
            assert (c.length == M && c[0].length == N) : "Illegal Dimension";

            final int count = Math.min(M / 128 + 1, Blas.ncpu);

            if (count <= 1) {

                // Single core calculation

                Blas.fgemm_kernel_cpu(tranA, tranB, M, N, K, a, 0, alpha, b, 0, beta, c, 0);
            } else {
                final int stride = M / count + 1;

                // Muti core calculation

                IntStream.rangeClosed(0, count).map(i -> i * stride).parallel().forEach(i -> {
                    final int remaining = Math.min(M - i, stride);
                    Blas.fgemm_kernel_cpu(tranA, tranB, remaining, N, K, a, i, alpha, b, 0, beta, c, i);
                });
            }
        }
    }

    private static void fgemm_kernel_cpu(final boolean tranA, final boolean tranB, int M, int N, int K,
            final float[][] a, int offA, final float alpha, final float[][] b, int offB, final float beta,
            final float[][] c, int offC) {

        if (tranA && tranB) {
            final float[] cT_i = new float[N];

            // aA*@B* = C, but faster to compute a(B@A)* = C

            for (int i = 0; i < M; i++) {

                // Transpose one column of C_i => C_i*

                for (int j = 0; j < N; j++) {
                    cT_i[j] = c[j][offC + i];
                }

                // Compute one line B_i@A => C_i*

                final float[] b_i = b[offA + i];
                for (int k = 0; k < K; k++) {
                    final float b_ik = alpha * b_i[k];
                    final float[] a_k = a[offB + k];
                    for (int j = 0; j < N; j++) {
                        cT_i[j] += a_k[j] * b_ik;
                    }
                }

                // Transpose back one row of C_i* => C_i

                for (int j = 0; j < N; j++) {
                    c[j][offC + i] = cT_i[j] + c[j][offC + i];
                }
            }
        } else if (tranA && !tranB) {
            final float[] aT_i = new float[K];

            // aA@B* = C

            for (int i = 0; i < M; i++) {

                // Transpose one column of A_i => A_i*

                for (int k = 0; k < K; k++) {
                    aT_i[k] = a[k][offA + i];
                }

                // Compute one line A_i*@B = C_i

                final float[] c_i = c[offC + i];
                for (int k = 0; k < K; k++) {
                    final float aT_ik = alpha * aT_i[k];
                    final float[] b_k = b[offB + k];
                    for (int j = 0; j < N; j++) {
                        c_i[j] += aT_ik * b_k[j];
                    }
                }
            }
        } else if (!tranA && tranB) {
            final float[] bT_k = new float[N];

            // aA*@B = C

            for (int k = 0; k < K; k++) {

                // Transpose one column of B_i => B_i*

                for (int j = 0; j < N; j++) {
                    bT_k[j] = b[j][offB + k];
                }

                // Compute one line A@B_i* = C_i

                for (int i = 0; i < M; i++) {
                    final float a_ik = alpha * a[offA + i][k];
                    final float[] c_i = c[offC + i];
                    for (int j = 0; j < N; j++) {
                        c_i[j] += a_ik * bT_k[j];
                    }
                }
            }
        } else {

            // aA@B = C

            for (int i = 0; i < M; i++) {

                // Compute one line A_i@B = C_i

                final float[] a_i = a[offA + i];
                final float[] c_i = c[offC + i];
                for (int k = 0; k < K; k++) {
                    final float a_ik = alpha * a_i[k];
                    final float[] b_k = b[offB + k];
                    for (int j = 0; j < N; j++) {
                        c_i[j] += a_ik * b_k[j];
                    }
                }
            }
        }
    }

}
