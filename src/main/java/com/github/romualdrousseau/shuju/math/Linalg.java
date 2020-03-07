package com.github.romualdrousseau.shuju.math;

public class Linalg {

    public static Matrix Pivot(final Matrix m) {
        assert (m.isSquared());
        final Matrix result = new Matrix(m.rows, m.rows).identity();
        for (int j = 0; j < m.rows; j++) {
            int row = j;
            float max = m.data[j][j];
            for (int i = j + 1; i < m.rows; i++) {
                if (Scalar.abs(m.data[i][j]) > max) {
                    max = m.data[i][j];
                    row = i;
                }
            }
            if (j != row) {
                for (int k = 0; k < m.cols; k++) {
                    final float tmp = result.data[j][k];
                    result.data[j][k] = result.data[row][k];
                    result.data[row][k] = tmp;
                }
            }
        }
        return result;
    }

    public static Matrix Sort(final Matrix m) {
        assert (m.isSquared());
        final Matrix result = new Matrix(m.rows, m.rows).identity();
        for (int j = 0; j < m.rows; j++) {
            int row = j;
            float max = m.data[j][j];
            for (int i = j + 1; i < m.rows; i++) {
                if (Scalar.abs(m.data[i][i]) > max) {
                    max = m.data[i][i];
                    row = i;
                }
            }
            if (j != row) {
                for (int k = 0; k < m.cols; k++) {
                    final float tmp = result.data[j][k];
                    result.data[j][k] = result.data[row][k];
                    result.data[row][k] = tmp;
                }
            }
        }
        return result;
    }

    public static Matrix GaussianElimination(final Matrix m, final boolean lower) {
        final Matrix q = m.copy();

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

    public static Matrix SolveTriangular(final Matrix m, final boolean lower) {
        final Matrix q = m.copy();

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

    public static Matrix Solve(final Matrix m, final Matrix y) {
        assert (m.isSquared());
        Matrix q = m.concat(y, 1);
        q = Linalg.GaussianElimination(q, false);
        q = Linalg.SolveTriangular(q, false);
        return q.copy(0, m.cols, y.rows, y.cols);
    }

    public static Vector Reflector(final Matrix m) {
        final Vector x = m.toVector(0, false);
        final float x_0 = x.data[0];
        final float u_0 = x_0 - x.norm() * Scalar.sign(x_0);
        final Vector u = x.copy().set(0, u_0);
        final Vector v = u.l2Norm();
        return v;
    }

    public static Matrix HouseHolder(final Matrix m, final int rows) {
        final Vector v = Linalg.Reflector(m);
        final Matrix result = new Matrix(rows, rows).identity();
        for (int i = 0; i < v.rows; i++) {
            for (int j = 0; j < v.rows; j++) {
                final float a = -2 * v.data[v.rows - 1 - i] * v.data[v.rows - 1 - j];
                result.data[rows - 1 - i][rows - 1 - j] += a;
            }
        }
        return result;
    }

    public static Matrix[] Hessenberg(final Matrix m) {
        final Matrix[] q = new Matrix[m.rows - 2];

        Matrix H = m;
        for (int k = 0; k <= m.rows - 3; k++) {
            q[k] = Linalg.HouseHolder(H.copy(k + 1, k), H.rows);
            H = q[k].matmul(H).matmul(q[k].transpose());
        }

        Matrix V = new Matrix(m.rows, m.cols).identity();
        for (int k = m.rows - 3; k >= 0; k--) {
            V = q[k].matmul(V);
        }

        return new Matrix[] { H, V };
    }

    public static Matrix[] LU(final Matrix m) {
        final int n = m.rows;
        final Matrix L = new Matrix(n, n).identity();
        final Matrix U = new Matrix(n, n);

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

        return new Matrix[] { L, U };
    }

    public static Matrix[] QR(final Matrix m) {
        Matrix tmp = Linalg.HouseHolder(m, m.rows);
        Matrix R = tmp.matmul(m);
        Matrix Q = tmp.transpose();

        for (int k = 1; k < m.rows - 1; k++) {
            tmp = Linalg.HouseHolder(R.minor(k - 1, k - 1), R.rows);
            R = tmp.matmul(R);
            Q = Q.matmul(tmp, false, true);
        }

        return new Matrix[] { Q, R };
    }

    public static Matrix Cholesky(final Matrix m) {
        assert (m.isSymetric(Scalar.EPSILON));

        final Matrix result = new Matrix(m.rows, m.cols, 0.0f);

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

    public static Matrix[] Eig(final Matrix m, final float e) {
        final Matrix[] h = Linalg.Hessenberg(m);
        Matrix H = h[0];
        Matrix Q = h[1];
        int its = 0;
        for (int p = H.rows - 1; p >= 1; p--) {
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
        Q = Q.matmul(Linalg.EigensValuesAndVectorsFromShur(H, e));
        return new Matrix[] { H, Q };
    }

    public static Matrix[] Svd(final Matrix m, final float e) {
        return Linalg.Eig(m.matmul(m.transpose()), e);
    }

    public static Matrix PCA(final Matrix m, final int n, final float e) {
        final Matrix cov = m.cov(0);
        final Matrix[] eig = Linalg.Eig(cov, e);
        final Matrix sort = Linalg.Sort(eig[0]);
        return eig[1].matmul(sort).copy(0, 0, eig[1].rowCount(), n);
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

    private static void QRStep(final int n0, final int n1, final int n, final Matrix A, final Matrix Q,
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

    private static Matrix EigensValuesAndVectorsFromShur(final Matrix H, final float e) {
        final Matrix Y = new Matrix(H.rows, H.cols);
        final int n = Y.rows - 1;
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

        for (int i = 0; i < H.rows; i++) {
            for (int j = 0; j < H.cols; j++) {
                if (j != i) {
                    H.data[i][j] = 0.0f;
                }
            }
        }

        return Y;
    }
}
