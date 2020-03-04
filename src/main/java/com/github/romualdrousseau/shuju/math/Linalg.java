package com.github.romualdrousseau.shuju.math;

public class Linalg {

    public static Matrix Pivot(Matrix m) {
        assert (m.isSquared());
        Matrix result = new Matrix(m.rows, m.rows).identity();
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
                    float tmp = result.data[j][k];
                    result.data[j][k] = result.data[row][k];
                    result.data[row][k] = tmp;
                }
            }
        }
        return result;
    }

    public static Matrix Sort(Matrix m) {
        assert (m.isSquared());
        Matrix result = new Matrix(m.rows, m.rows).identity();
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
                    float tmp = result.data[j][k];
                    result.data[j][k] = result.data[row][k];
                    result.data[row][k] = tmp;
                }
            }
        }
        return result;
    }

    public static Matrix GaussianElimination(Matrix m, boolean lower) {
        Matrix q = m.copy();

        if (lower) {
            for (int k = q.data.length - 1; k >= 0; k--) {
                float a = 1.0f / q.data[k][k];
                for (int j = 0; j < q.data[k].length; j++) {
                    q.data[k][j] *= a;
                }
                for (int i = k - 1; i >= 0; i--) {
                    float b = q.data[i][k];
                    for (int j = 0; j < q.data[i].length; j++) {
                        q.data[i][j] -= b * q.data[k][j];
                    }
                }
            }
        } else {
            for (int k = 0; k < q.data.length; k++) {
                float a = 1.0f / q.data[k][k];
                for (int j = 0; j < q.data[k].length; j++) {
                    q.data[k][j] *= a;
                }
                for (int i = k + 1; i < q.data.length; i++) {
                    float b = q.data[i][k];
                    for (int j = 0; j < q.data[i].length; j++) {
                        q.data[i][j] -= b * q.data[k][j];
                    }
                }
            }
        }

        return q;
    }

    public static Matrix SolveTriangular(Matrix m, boolean lower) {
        Matrix q = m.copy();

        if (lower) {
            for (int k = 1; k < q.data.length; k++) {
                for (int i = k - 1; i >= 0; i--) {
                    float a = q.data[k][i];
                    for (int j = 0; j < q.data[k].length; j++) {
                        q.data[k][j] -= a * q.data[i][j];
                    }
                }
            }
        } else {
            for (int k = q.data.length - 2; k >= 0; k--) {
                for (int i = k + 1; i < q.data.length; i++) {
                    float a = q.data[k][i];
                    for (int j = 0; j < q.data[k].length; j++) {
                        q.data[k][j] -= a * q.data[i][j];
                    }
                }
            }
        }

        return q;
    }

    public static Matrix Solve(Matrix m, Matrix y) {
        assert (m.isSquared());
        Matrix q = m.concat(y, 1);
        q = Linalg.GaussianElimination(q, false);
        q = Linalg.SolveTriangular(q, false);
        return q.copy(0, m.cols, y.rows, y.cols);
    }

    public static Vector Reflector(Matrix m) {
        Vector x = m.toVector(0, false);
        float x_0 = x.data[0];
        float u_0 = x_0 - x.norm() * Scalar.sign(x_0);
        Vector u = x.copy().set(0, u_0);
        Vector v = u.l2Norm();
        return v;
    }

    public static Matrix HouseHolder(Matrix m, int rows) {
        Vector v = Linalg.Reflector(m);
        Matrix result = new Matrix(rows, rows).identity();
        for (int i = 0; i < v.rows; i++) {
            for (int j = 0; j < v.rows; j++) {
                float a = -2 * v.data[v.rows - 1 - i] * v.data[v.rows - 1 - j];
                result.data[rows - 1 - i][rows - 1 - j] += a;
            }
        }
        return result;
    }

    public static Matrix[] Hessenberg(Matrix m) {
        Matrix[] q = new Matrix[m.rows - 2];

        Matrix values = m;
        for (int k = 0; k < m.rows - 2; k++) {
            q[k] = Linalg.HouseHolder(values.copy(k + 1, k), values.rows);
            values = q[k].matmul(values).matmul(q[k].transpose());
        }

        Matrix vectors = m.copy().identity();
        for (int k = m.rows - 3; k >= 0; k--) {
            vectors = q[k].matmul(vectors);
        }

        return new Matrix[] { values, vectors };
    }

    public static Matrix[] LU(Matrix m) {
        int n = m.rows;
        Matrix L = new Matrix(n, n).identity();
        Matrix U = new Matrix(n, n);

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

    public static Matrix[] QR(Matrix m) {
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

    public static Matrix Cholesky(Matrix m) {
        assert (m.isSymetric(Scalar.EPSILON));

        Matrix result = new Matrix(m.rows, m.cols, 0.0f);

        float[][] r_data = result.data;
        for (int i = 0; i < r_data.length; i++) {
            for (int k = 0; k <= i; k++) {
                float m_ik = m.get(i, k);

                float sum = 0.0f;
                for (int j = 0; j < k; j++) {
                    sum += r_data[i][j] * r_data[k][j];
                }

                float a = m_ik - sum;

                if (i == k) {
                    r_data[i][k] = Scalar.sqrt(a);
                } else {
                    r_data[i][k] = 1.0f / r_data[k][k] * a;
                }
            }
        }

        return result;
    }

    public static Matrix[] Eig(Matrix m, float e) {
        Matrix[] h = Linalg.Hessenberg(m);
        Matrix values = h[0];
        Matrix vectors = h[1];
        int its = 0;
        for(int p = values.rows - 1; p >= 1; p-- ) {
            do {
                float shift = WilkinsonShift(
                        values.data[p - 1][p - 1],
                        values.data[p - 1][p],
                        values.data[p][p - 1],
                        values.data[p][p]);

                for(int k = 0; k < p; k++) {
                    QRStep(k, k + 1, p + 1, values, vectors, shift);
                }

                its++;
                if(++its > 1000000L) {
                    throw new RuntimeException("too much iteration");
                }
            } while (Scalar.abs(values.data[p][p - 1]) >= e);
        }
        return new Matrix[] { values, vectors };
    }

    public static Matrix PCA(Matrix m, int n, float e) {
        Matrix cov = m.cov(0);
        Matrix[] eig = Linalg.Eig(cov, e);
        Matrix sort = Linalg.Sort(eig[0]);
        return eig[1].matmul(sort).copy(0, 0, cov.rowCount(), n);
    }

    private static float WilkinsonShift(float a, float b, float c, float d) {
        float s = (a - d) * 0.5f;
        return d + s - Scalar.sign(s) * Scalar.sqrt(s * s + 4 * b * c) * 0.5f;
    }

    private static void QRStep(int n0, int n1, int n, Matrix A, Matrix Q, float shift) {
        float c = A.data[n0][n0] - shift;
        float s = A.data[n0][n0 + 1];
        float v = Scalar.sqrt(c * c + s * s);
        if (v == 0.0f) {
            v = 1.0f;
            c = 1.0f;
            s = 0.0f;
        } else {
            c /= v;
            s /= v;
        }

        for (int j = Math.max(n0 - 1, 0); j < n; j++) {
            float a = A.data[n0][j];
            float b = A.data[n1][j];
            A.data[n0][j] = c * a + s * b;
            A.data[n1][j] = c * b - s * a;
        }

        for (int j = 0; j < Math.min(n1 + 2, n); j++) {
            float a = A.data[j][n0];
            float b = A.data[j][n1];
            A.data[j][n0] = c * a + s * b;
            A.data[j][n1] = c * b - s * a;
        }

        for (int j = 0; j < n; j++) {
            float a = Q.data[j][n0];
            float b = Q.data[j][n1];
            Q.data[j][n0] = c * a + s * b;
            Q.data[j][n1] = c * b - s* a;
        }
    }
}
