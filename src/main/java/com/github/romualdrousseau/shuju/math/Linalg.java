package com.github.romualdrousseau.shuju.math;

public class Linalg {

    public static Tensor2D BlockDiagonal(Tensor2D m, int repeat, boolean transpose) {
        final int split_r = m.shape[0] / repeat;
        if(transpose) {
            final Tensor2D result = new Tensor2D(m.shape[1] * repeat, m.shape[0]);
            for (int i = 0; i < m.shape[0]; i++) {
                final int off_r = (i / split_r) * m.shape[1];
                for (int j = 0; j < m.shape[1]; j++) {
                    result.data[off_r + j][i] = m.data[i][j];
                }
            }
            return result;
        } else {
            final Tensor2D result = new Tensor2D(m.shape[0], m.shape[1] * repeat);
            for (int i = 0; i < m.shape[0]; i++) {
                final int off_r = (i / split_r) * m.shape[1];
                for (int j = 0; j < m.shape[1]; j++) {
                    result.data[i][off_r + j] = m.data[i][j];
                }
            }
            return result;
        }
    }

    public static Tensor2D BlockColumn(Tensor2D m, int repeat, int axis) {
        if (axis == 0) {
            final int split_c = m.shape[1] / repeat;
            final Tensor2D result = new Tensor2D(m.shape[0] / repeat, m.shape[1]);
            for (int i = 0; i < result.shape[0]; i++) {
                for (int j = 0; j < result.shape[1]; j++) {
                    result.data[i][j] = m.data[(j / split_c) * result.shape[0] + i][j];
                }
            }
            return result;
        } else {
            final int split_r = m.shape[0] / repeat;
            final Tensor2D result = new Tensor2D(m.shape[0], m.shape[1] / repeat);
            for (int i = 0; i < result.shape[0]; i++) {
                final int off_m = (i / split_r) * result.shape[1];
                for (int j = 0; j < result.shape[1]; j++) {
                    result.data[i][j] = m.data[i][off_m + j];
                }
            }
            return result;
        }
    }

    public static Tensor2D Pivot(final Tensor2D m) {
        assert (m.isSquared());
        final Tensor2D result = new Tensor2D(m.shape[0], m.shape[0]).identity();
        for (int j = 0; j < m.shape[0]; j++) {
            int row = j;
            float max = m.data[j][j];
            for (int i = j + 1; i < m.shape[0]; i++) {
                if (Scalar.abs(m.data[i][j]) > max) {
                    max = m.data[i][j];
                    row = i;
                }
            }
            result.swap(j, row, 0);
        }
        return result;
    }

    public static Tensor2D Sort(final Tensor2D m) {
        assert (m.isSquared());
        final Tensor2D result = new Tensor2D(m.shape[0], m.shape[0]).identity();
        for (int j = 0; j < m.shape[0]; j++) {
            int row = j;
            float max = m.data[j][j];
            for (int i = j + 1; i < m.shape[0]; i++) {
                if (Scalar.abs(m.data[i][i]) > max) {
                    max = m.data[i][i];
                    row = i;
                }
            }
            result.swap(j, row, 0);
        }
        return result;
    }

    public static Tensor2D GaussianElimination(final Tensor2D m, final boolean lower) {
        final Tensor2D q = m.copy();

        if (lower) {
            for (int k = q.data.length - 1; k >= 0; k--) {
                final float a = 1.0f / q.data[k][k];
                for (int j = 0; j < q.data[k].length; j++) {
                    q.data[k][j] *= a;
                }
                for (int i = k - 1; i >= 0; i--) {
                    final float b = q.data[i][k];
                    for (int j = 0; j < q.data[i].length; j++) {
                        q.data[i][j] -= b * q.data[k][j];
                    }
                }
            }
        } else {
            for (int k = 0; k < q.data.length; k++) {
                final float a = 1.0f / q.data[k][k];
                for (int j = 0; j < q.data[k].length; j++) {
                    q.data[k][j] *= a;
                }
                for (int i = k + 1; i < q.data.length; i++) {
                    final float b = q.data[i][k];
                    for (int j = 0; j < q.data[i].length; j++) {
                        q.data[i][j] -= b * q.data[k][j];
                    }
                }
            }
        }

        return q;
    }

    public static Tensor2D SolveTriangular(final Tensor2D m, final boolean lower) {
        final Tensor2D q = m.copy();

        if (lower) {
            for (int k = 1; k < q.data.length; k++) {
                for (int i = k - 1; i >= 0; i--) {
                    final float a = q.data[k][i];
                    for (int j = 0; j < q.data[k].length; j++) {
                        q.data[k][j] -= a * q.data[i][j];
                    }
                }
            }
        } else {
            for (int k = q.data.length - 2; k >= 0; k--) {
                for (int i = k + 1; i < q.data.length; i++) {
                    final float a = q.data[k][i];
                    for (int j = 0; j < q.data[k].length; j++) {
                        q.data[k][j] -= a * q.data[i][j];
                    }
                }
            }
        }

        return q;
    }

    public static Tensor2D Solve(final Tensor2D m, final Tensor2D y) {
        assert (m.isSquared());
        Tensor2D q = m.concatenate(y, 1);
        q = Linalg.GaussianElimination(q, false);
        q = Linalg.SolveTriangular(q, false);
        return q.slice(0, m.shape[1], y.shape[0], y.shape[1]);
    }

    public static Tensor2D Reflector(final Tensor2D m) {
        final Tensor2D x = m.slice(0, 0, -1, 1);
        final float x_0 = x.data[0][0];
        final float u_0 = x_0 - x.norm(0, 0) * Scalar.sign(x_0);
        final Tensor2D u = x.transpose().set(0, 0, u_0);
        final Tensor2D v = u.l2Norm(1);
        return v;
    }

    public static Tensor2D HouseHolder(final Tensor2D m, final int rows) {
        final Tensor2D v = Linalg.Reflector(m);
        final Tensor2D result = new Tensor2D(rows, rows).identity();
        for (int i = 0; i < v.shape[1]; i++) {
            for (int j = 0; j < v.shape[1]; j++) {
                final float a = -2 * v.data[0][v.shape[1] - 1 - i] * v.data[0][v.shape[1] - 1 - j];
                result.data[rows - 1 - i][rows - 1 - j] += a;
            }
        }
        return result;
    }

    public static Tensor2D[] Hessenberg(final Tensor2D m) {
        final Tensor2D[] q = new Tensor2D[m.shape[0] - 2];

        Tensor2D H = m;
        for (int k = 0; k <= m.shape[0] - 3; k++) {
            q[k] = Linalg.HouseHolder(H.slice(k + 1, k), H.shape[0]);
            H = (Tensor2D) q[k].matmul(H).matmul(q[k].transpose());
        }

        Tensor2D V = new Tensor2D(m.shape[0], m.shape[1]).identity();
        for (int k = m.shape[0] - 3; k >= 0; k--) {
            V = (Tensor2D) q[k].matmul(V);
        }

        return new Tensor2D[] { H, V };
    }

    public static Tensor2D[] LU(final Tensor2D m) {
        final int n = m.shape[0];
        final Tensor2D L = new Tensor2D(n, n).identity();
        final Tensor2D U = new Tensor2D(n, n);

        for (int j = 0; j < n; j++) {
            for (int i = 0; i <= j; i++) {
                float s1 = 0.0f;
                for (int k = 0; k < i; k++) {
                    s1 += U.data[k][j] * L.data[i][k];
                }
                U.data[i][j] = m.data[i][j] - s1;
            }

            for (int i = j; i < n; i++) {
                float s2 = 0.0f;
                for (int k = 0; k < j; k++) {
                    s2 += U.data[k][j] * L.data[i][k];
                }
                L.data[i][j] = (m.data[i][j] - s2) / U.data[j][j];
            }
        }

        return new Tensor2D[] { L, U };
    }

    public static Tensor2D[] QR(final Tensor2D m) {
        Tensor2D tmp = Linalg.HouseHolder(m, m.shape[0]);
        Tensor2D R = (Tensor2D) tmp.matmul(m);
        Tensor2D Q = tmp.transpose();

        for (int k = 1; k < m.shape[0] - 1; k++) {
            tmp = Linalg.HouseHolder(R.minor(k - 1, k - 1), R.shape[0]);
            R = (Tensor2D) tmp.matmul(R);
            Q = Q.matmul(tmp, false, true);
        }

        return new Tensor2D[] { Q, R };
    }

    public static Tensor2D Cholesky(final Tensor2D m) {
        assert (m.isSymetric(Scalar.EPSILON));

        final Tensor2D result = new Tensor2D(m.shape[0], m.shape[1]).zero();

        final float[][] r_data = result.data;
        for (int i = 0; i < r_data.length; i++) {
            for (int k = 0; k <= i; k++) {
                final float m_ik = m.get(i, k);

                float sum = 0.0f;
                for (int j = 0; j < k; j++) {
                    sum += r_data[i][j] * r_data[k][j];
                }

                final float a = m_ik - sum;

                if (i == k) {
                    r_data[i][k] = Scalar.sqrt(a);
                } else {
                    r_data[i][k] = 1.0f / r_data[k][k] * a;
                }
            }
        }

        return result;
    }

    public static Tensor2D[] Eig(final Tensor2D m, final float e) {
        final Tensor2D[] h = Linalg.Hessenberg(m);
        Tensor2D H = h[0];
        Tensor2D Q = h[1];
        int its = 0;
        for (int p = H.shape[0] - 1; p >= 1; p--) {
            do {
                float shift;
                if ((its % 11) == 10) {
                    shift = H.data[p][p];
                } else {
                    shift = Linalg.WilkinsonShift(
                            H.data[p - 1][p - 1], H.data[p - 1][p],
                            H.data[p][p - 1], H.data[p][p]);
                }

                for (int k = 0; k < p; k++) {
                    Linalg.QRStep(k, k + 1, p + 1, H, Q, shift);
                }

                its++;
                if (++its > 100000) {
                    throw new RuntimeException("too much iteration");
                }
            } while (Scalar.abs(H.data[p][p - 1]) >= e);
        }
        Q = (Tensor2D) Q.matmul(Linalg.EigensValuesAndVectorsFromShur(H, e));
        return new Tensor2D[] { H, Q };
    }

    public static Tensor2D[] Svd(final Tensor2D m, final float e) {
        return Linalg.Eig((Tensor2D) m.matmul(m.transpose()), e);
    }

    public static Tensor2D PCA(final Tensor2D m, final int n, final float e) {
        final Tensor2D cov = m.cov(0);
        final Tensor2D[] eig = Linalg.Eig(cov, e);
        final Tensor2D sort = Linalg.Sort(eig[0]);
        return ((Tensor2D) eig[1].matmul(sort)).slice(0, 0, eig[1].shape[0], n);
    }

    private static float WilkinsonShift(final float a, final float b, final float c, final float d) {
        final float s = (a - d) * 0.5f;
        final float ss = Scalar.abs(s) + Scalar.sqrt(s * s + Scalar.abs(b * c));
        if (ss == 0.0f) {
            return d;
        } else {
            return d - Scalar.sign(s) * b * c / ss;
        }
    }

    private static void QRStep(final int n0, final int n1, final int n, final Tensor2D A, final Tensor2D Q,
            final float shift) {
        float c = A.data[n0][n0] - shift;
        float s = A.data[n1][n0];
        final float v = Scalar.sqrt(c * c + s * s);
        if (v == 0.0f) {
            c = 1.0f;
            s = 0.0f;
        } else {
            c /= v;
            s /= v;
        }

        for (int j = Math.max(n1 - 2, 0); j < n; j++) {
            final float a = A.data[n0][j];
            final float b = A.data[n1][j];
            A.data[n0][j] = c * a + s * b;
            A.data[n1][j] = c * b - s * a;
        }

        for (int j = 0; j < Math.min(n1 + 2, n); j++) {
            final float a = A.data[j][n0];
            final float b = A.data[j][n1];
            A.data[j][n0] = c * a + s * b;
            A.data[j][n1] = c * b - s * a;
        }

        for (int j = 0; j < n; j++) {
            final float a = Q.data[j][n0];
            final float b = Q.data[j][n1];
            Q.data[j][n0] = c * a + s * b;
            Q.data[j][n1] = c * b - s * a;
        }
    }

    private static Tensor2D EigensValuesAndVectorsFromShur(final Tensor2D H, final float e) {
        final Tensor2D Y = new Tensor2D(H.shape[0], H.shape[1]);
        final int n = Y.shape[0] - 1;
        final float smallnum = (n / e) * Float.MIN_VALUE;
        final float bignum = (e / n) * Float.MAX_VALUE;

        for (int k = n; k >= 0; k--) {
            for (int i = 0; i <= k - 1; i++) {
                Y.data[i][k] = -H.data[i][k];
            }
            Y.data[k][k] = 1.0f;
            for (int i = k + 1; i <= n; i++) {
                Y.data[i][k] = 0.0f;
            }

            final float dmin = Scalar.max(e * Scalar.abs(H.data[k][k]), smallnum);

            for (int j = k - 1; j >= 0; j--) {
                float d = H.data[j][j] - H.data[k][k];
                if (Scalar.abs(d) < dmin) {
                    d = dmin;
                }
                if ((Scalar.abs(Y.data[j][k]) / bignum) >= Scalar.abs(d)) {
                    final float s = Scalar.abs(d) / Scalar.abs(Y.data[j][k]);
                    for (int i = 0; i <= k; i++) {
                        Y.data[i][k] *= s;
                    }
                }
                Y.data[j][k] /= d;
                for (int i = 0; i <= j - 1; i++) {
                    Y.data[i][k] -= Y.data[j][k] * H.data[i][j];
                }
            }

            for (int i = 0; i <= k; i++) {
                float sum = 0.0f;
                for (int j = 0; j <= k; j++) {
                    sum = Y.data[j][k] * Y.data[j][k];
                }
                final float norm = 1.0f / Scalar.sqrt(sum);
                Y.data[i][k] *= norm;
            }
        }

        for (int i = 0; i < H.shape[0]; i++) {
            for (int j = 0; j < H.shape[1]; j++) {
                if (j != i) {
                    H.data[i][j] = 0.0f;
                }
            }
        }

        return Y;
    }
}
