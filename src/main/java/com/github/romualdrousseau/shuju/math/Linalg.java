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
        do {
            float mu = WilkinsonShift(
                    values.data[values.rows - 2][values.cols - 2],
                    values.data[values.rows - 2][values.cols - 1],
                    values.data[values.rows - 1][values.cols - 2],
                    values.data[values.rows - 1][values.cols - 1]);
            Matrix shift = values.copy().identity().mul(mu);
            Matrix[] tmp = Linalg.QR(values.sub(shift));
            vectors = vectors.matmul(tmp[0]);
            values = tmp[1].matmul(tmp[0]).add(shift);
            its++;
            if(++its > 1000000L) {
                 throw new RuntimeException("too much iteration");
            }
        } while (Scalar.abs(values.data[values.rows - 1][values.cols - 2]) >= e);
        return new Matrix[] { values, vectors };
    }

    public static void QRHess(Matrix A, Matrix Q) {
        int n = A.rows;

        float norm = 0.0f;
        for(int x = 0; x < n; x++) {
            for(int y = 0; y < Scalar.min(x + 2, n); y++) {
                norm += A.data[y][x] * A.data[y][x];
            }
        }
        norm = Scalar.sqrt(norm) / (float) n;

        if (norm == 0.0f) {
            return;
        }

        int n0 = 0;
        int n1 = n;

        float eps = Scalar.EPSILON / (100 * n);
        int maxits = 100000;

        int its = 0;
        // int totalits = 0;

        while (true) {
            int k = n0;

            while (k + 1 < n1) {
                float s = Scalar.abs(A.data[k][k] + A.data[k + 1][k + 1]);
                if (s < eps * norm) {
                    s = norm;
                }
                if (Scalar.abs(A.data[k + 1][k]) < eps * s) {
                    break;
                }
                k += 1;
            }

            if (k + 1 < n1) {
                A.data[k + 1][k] = 0.0f;
                n0 = k + 1;

                its = 0;

                if (n0 + 1 >= n1) {
                    n0 = 0;
                    n1 = k + 1;
                    if (n1 < 2) {
                        return;
                    }
                }
            } else {
                float shift = WilkinsonShift(A.data[n1 - 2][n1 - 2], A.data[n1 - 2][n1 - 1], A.data[n1 - 1][n1 - 2], A.data[n1 - 1][n1 - 1]);
                Linalg.QRStep(n0, n1, A, Q, shift);

                its++;
                // totalits++;

                if (its > maxits) {
                    throw new RuntimeException("qr: failed to converge after " + its + " steps");
                }
            }
        }
    }

    private static void QRStep(int n0, int n1, Matrix A, Matrix Q, float shift) {
        int n = A.rows;

        float c = A.data[n0][n0] - shift;
        float s = A.data[n0 + 1][n0];

        float v = Scalar.sqrt(c * c + s * s);

        if (v == 0.0f) {
            v = 1.0f;
            c = 1.0f;
            s = 0.0f;
        } else {
            c /= v;
            s /= v;
        }

        for (int k = n0; k < n; k++) {
            float x = A.data[n0][k];
            float y = A.data[n0 + 1][k];
            A.data[n0][k] = c * x + s * y;
            A.data[n0 + 1][k] = c * y - s * x;
        }

        for (int k = 0; k < Scalar.min(n1, n0 + 3); k++) {
            float x = A.data[k][n0];
            float y = A.data[k][n0 + 1];
            A.data[k][n0] = c * x + s * y;
            A.data[k][n0 + 1] = c * y - s * x;
        }

        for (int k = 0; k < n; k++) {
            float x = Q.data[k][n0];
            float y = Q.data[k][n0 + 1];
            Q.data[k][n0] = c * x + s * y;
            Q.data[k][n0 + 1] = c * y - s * x;
        }

        for (int j = n0; j < n1 - 2; j++) {

            c = A.data[j + 1][j] - shift;
            s = A.data[j + 2][j];

            v = Scalar.sqrt(c * c + s * s);

            if (v == 0.0f) {
                v = 1.0f;
                c = 1.0f;
                s = 0.0f;
            } else {
                c /= v;
                s /= v;
            }

            for (int k = j + 1; k < n; k++) {
                float x = A.data[j + 1][k];
                float y = A.data[j + 2][k];
                A.data[j + 1][k] = c * x + s * y;
                A.data[j + 2][k] = c * y - s * x;
            }

            for (int k = 0; k < Scalar.min(n1, j + 4); k++) {
                float x = A.data[k][j + 1];
                float y = A.data[k][j + 2];
                A.data[k][j + 1] = c * x + s * y;
                A.data[k][j + 2] = c * y - s * x;
            }

            for (int k = 0; k < n; k++) {
                float x = Q.data[k][j + 1];
                float y = Q.data[k][j + 2];
                Q.data[k][j + 1] = c * x + s * y;
                Q.data[k][j + 2] = c * y - s * x;
            }
        }
    }

    private static float WilkinsonShift(float a, float b, float c, float d) {
        float s = (a - d) * 0.5f;
        return d + s - Scalar.sign(s) * Scalar.sqrt(s * s + 4 * b * c) * 0.5f;
    }
}
