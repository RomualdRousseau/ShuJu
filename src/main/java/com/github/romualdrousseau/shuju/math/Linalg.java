package com.github.romualdrousseau.shuju.math;

public class Linalg {

    public static Matrix Solve(Matrix m, Matrix y) {
        Matrix q = m.concat(y);
        float[][] data = q.getFloats();

        for (int k = 0; k < data.length; k++) {
            float a = 1.0f / data[k][k];
            for (int j = 0; j < data[k].length; j++) {
                data[k][j] *= a;
            }
            for (int i = k + 1; i < data.length; i++) {
                float b = data[i][k];
                for (int j = 0; j < data[i].length; j++) {
                    data[i][j] -= b * data[k][j];
                }
            }
        }

        for (int k = data.length - 2; k >= 0; k--) {
            for (int i = k + 1; i < data.length; i++) {
                float a = data[k][i];
                for (int j = 0; j < data[k].length; j++) {
                    data[k][j] -= a * data[i][j];
                }
            }
        }

        Matrix result = new Matrix(y.rowCount(), y.colCount());
        for (int i = 0; i < result.rowCount(); i++) {
            for (int j = 0; j < result.colCount(); j++) {
                result.set(i, j, q.get(i, j + m.colCount()));
            }
        }
        return result;
    }

    public static Matrix HouseHolder(Matrix m, int rows, int cols) {
        float[][] data = m.getFloats();

        float[] x = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            x[i] = data[i][0];
        }

        float n = 0;
        for (int i = 0; i < data.length; i++) {
            n += x[i] * x[i];
        }
        n = (float) Math.sqrt(n);

        float[] u = new float[x.length];
        for (int i = 0; i < x.length; i++) {
            u[i] = x[i];
        }
        u[0] -= n * (x[0] >= 0 ? 1 : -1);

        float nn = 0;
        for (int i = 0; i < u.length; i++) {
            nn += u[i] * u[i];
        }
        nn = (float) Math.sqrt(nn);

        float[] v = new float[u.length];
        for (int i = 0; i < u.length; i++) {
            v[i] = u[i] / nn;
        }

        float[][] p = new float[data.length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                if (i == j) {
                    p[i][j] = 1 - 2 * v[i] * v[j];
                } else {
                    p[i][j] = -2 * v[i] * v[j];
                }
            }
        }

        Matrix result = new Matrix(rows, cols).identity();
        for (int i = 0; i < p.length; i++) {
            for (int j = 0; j < p[i].length; j++) {
                result.set(rows - 1 - i, cols - 1 - j, p[p.length - 1 - i][p[i].length - 1 - j]);
            }
        }
        return result;
    }

    public static Matrix[] QR(Matrix m) {
        Matrix tmp = Linalg.HouseHolder(m, m.rowCount(), m.rowCount());
        Matrix R = tmp.transform(m);
        Matrix Q = tmp.transpose();

        for (int k = 1; k < m.rowCount() - 1; k++) {
            tmp = Linalg.HouseHolder(R.minor(k - 1, k - 1), R.rowCount(), R.rowCount());
            R = tmp.transform(R);
            Q = Q.transform(tmp, false, true);
        }

        return new Matrix[] { R, Q };
    }
}
