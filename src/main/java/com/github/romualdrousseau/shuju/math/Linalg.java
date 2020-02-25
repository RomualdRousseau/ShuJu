package com.github.romualdrousseau.shuju.math;

public class Linalg {

    public static Matrix Solve(Matrix m, Matrix y) {
        assert (m.isSquared());

        Matrix result = new Matrix(y.rows, y.cols);

        Matrix q = m.concat(y);

        float[][] q_data = q.data;
        for (int k = 0; k < q_data.length; k++) {
            float a = 1.0f / q_data[k][k];
            for (int j = 0; j < q_data[k].length; j++) {
                q_data[k][j] *= a;
            }
            for (int i = k + 1; i < q_data.length; i++) {
                float b = q_data[i][k];
                for (int j = 0; j < q_data[i].length; j++) {
                    q_data[i][j] -= b * q_data[k][j];
                }
            }
        }

        for (int k = q_data.length - 2; k >= 0; k--) {
            for (int i = k + 1; i < q_data.length; i++) {
                float a = q_data[k][i];
                for (int j = 0; j < q_data[k].length; j++) {
                    q_data[k][j] -= a * q_data[i][j];
                }
            }
        }

        int m_cols = m.cols;
        float[][] r_data = result.data;
        for (int i = 0; i < r_data.length; i++) {
            for (int j = 0; j < r_data[i].length; j++) {
                r_data[i][j] = q_data[i][m_cols + j];
            }
        }

        return result;
    }

    public static Matrix HouseHolder(Matrix m, int rows, int cols, int k) {
        Matrix result = new Matrix(rows, cols).identity();

        Vector x = m.toVector(0);
        for (int i = 0; i < k; i++) {
            x.data[i] = 0.0f;
        }

        float x_0 = x.data[k];
        float u_0 = x_0 - x.norm() * Scalar.sign(x_0);
        Vector u = x.copy().set(k, u_0);
        Vector v = u.l2Norm();

        float[][] data = result.data;
        for (int i = 0; i < v.rows; i++) {
            for (int j = 0; j < v.rows; j++) {
                float a = -2 * v.data[v.rows - 1 - i] * v.data[v.rows - 1 - j];
                data[rows - 1 - i][cols - 1 - j] += a;
            }
        }

        return result;
    }

    public static Matrix[] Hessenberg(Matrix m) {
        Matrix[] q = new Matrix[m.rows - 2];

        Matrix values = m;
        for (int k = 0; k < m.rows - 2; k++) {
            q[k] = Linalg.HouseHolder(values.copy(k, k), values.rows, values.rows, 1);
            values = q[k].transform(values).transform(q[k].transpose());
        }

        Matrix vectors = m.copy().identity();
        for (int k = m.rows - 3; k >= 0; k--) {
            vectors = q[k].transform(vectors);
        }

        return new Matrix[] { values, vectors };
    }

    public static Matrix[] QR(Matrix m) {
        Matrix tmp = Linalg.HouseHolder(m, m.rows, m.rows, 0);
        Matrix R = tmp.transform(m);
        Matrix Q = tmp.transpose();

        for (int k = 1; k < m.rows - 1; k++) {
            tmp = Linalg.HouseHolder(R.minor(k - 1, k - 1), R.rows, R.rows, 0);
            R = tmp.transform(R);
            Q = Q.transform(tmp, false, true);
        }

        return new Matrix[] { Q, R };
    }

    public static Matrix Cholesky(Matrix m) {
        assert (m.isSymetric());

        Matrix result = m.copy().zero();

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

    public static Matrix[] LU(Matrix m) {
        int n = m.rowCount();
        Matrix L = new Matrix(n, n).identity();
        Matrix U = new Matrix(n, n);

        float[][] M_data = m.data;
        float[][] L_data = L.data;
        float[][] U_data = U.data;
        for (int j = 0; j < n; j++) {
            for (int i = 0; i <= j; i++) {
                float s1 = 0.0f;
                for (int k = 0; k < i; k++) {
                    s1 += U_data[k][j] * L_data[i][k];
                }
                U_data[i][j] = M_data[i][j] - s1;
            }

            for (int i = j; i < n; i++) {
                float s2 = 0.0f;
                for (int k = 0; k < j; k++) {
                    s2 += U_data[k][j] * L_data[i][k];
                }
                L_data[i][j] = (M_data[i][j] - s2) / U_data[j][j];
            }
        }

        return new Matrix[] { L, U };
    }

    public static Matrix[] Eig(Matrix m, float e) {
        Matrix[] h = Linalg.Hessenberg(m);
        Matrix values = h[0];
        Matrix vectors = h[1];
        while (Scalar.abs(values.data[values.rows - 1][values.cols - 2]) > e) {
            float mu = WilkinsonShift(
                values.data[values.rows - 2][values.cols - 2],
                values.data[values.rows - 2][values.cols - 1],
                values.data[values.rows - 1][values.cols - 1]);
            // float mu = values.data[values.rows - 1][values.cols - 1];
            Matrix shift = values.copy().identity().mult(mu);
            Matrix[] tmp = Linalg.QR(values.sub(shift));
            vectors = vectors.transform(tmp[0]);
            values = tmp[1].transform(tmp[0]).add(shift);
        }
        return new Matrix[] { values, vectors };
    }

    private static float WilkinsonShift(float a, float b, float c) {
        float s = (a - c) * 0.5f;
        return c + s - Scalar.sign(s) * Scalar.sqrt(s * s + b * b);
    }
}
