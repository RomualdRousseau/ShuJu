package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Matrix;

public class Helper {

    public static Matrix xw_plus_b(Matrix input, Matrix weights, Matrix bias) {
        return weights.matmul(input).add(bias);
    }

    public static Matrix xw_plus_b(Matrix input, Matrix weights, float bias) {
        return weights.matmul(input).add(bias);
    }

    public static Matrix a_mul_b(Matrix a, Matrix b) {
        return (a.colCount() == b.colCount()) ? b.mul(a) : b.matmul(a);
    }

    public static Matrix[] a_mul_b(Matrix a, Matrix[] b) {
        Matrix[] result = new Matrix[b.length];
        for(int i = 0; i < result.length; i++) {
            result[i] = b[i].matmul(a);
        }
        return result;
    }

    public static Matrix[] a_mul_b(Matrix[] a, Matrix[] b) {
        Matrix[] result = new Matrix[b.length];
        for(int i = 0; i < result.length; i++) {
            result[i] = b[i].matmul(a[i]);
        }
        return result;
    }

    public static Matrix[] reshape(Matrix m, int dim1, int dim2, int dim3) {
        Matrix[] result = new Matrix[dim1];
        for(int i = 0; i < result.length; i++) {
            result[i] = m.reshape(-1, dim3).slice(i * dim2, 0, dim2, -1);
        }
		return result;
    }

    public static Matrix reshape(Matrix[] m, int dim1, int dim2) {
        Matrix result = new Matrix(dim1, dim2);
        final int step = dim1 / m.length;
        for(int i = 0; i < m.length; i++) {
            result.replace(i * step, 0, m[i]);
        }
        return result;
    }

    public static Matrix[] Img2Conv(Matrix[] m, int size, int stride, boolean transpose) {
        Matrix[] result = new Matrix[m.length];
        for(int i = 0; i < result.length; i++) {
            result[i] = Helper.Img2Conv(m[i], 1, size, stride, transpose);
        }
		return result;
    }

    public static Matrix[] Conv2Img(Matrix[] m, int rows, int cols, int size, int stride) {
		Matrix[] result = new Matrix[m.length];
        for(int i = 0; i < result.length; i++) {
            result[i] = Helper.Conv2Img(m[i], 1, rows, cols, size, stride);
        }
		return result;
    }

    public static String toString(Matrix[] m) {
        String result = "";
        for(int i = 0; i < m.length; i++) {
            result += m[i].toString() +"\n";
        }
        return result;
    }

    public static Matrix Img2Conv(Matrix m, int repeat, int size, int stride, boolean transpose) {
        assert (repeat > 0);
        assert (stride > 0);
        assert (size > 0);
        final int rows_m = m.shape[0] / repeat;
        final int cols_m = m.shape[1];
        final int rows_r = 1 + (rows_m - size) / stride;
        final int cols_r = 1 + (cols_m - size) / stride;
        assert ((rows_r - 1) * stride + size == rows_m);
        assert ((cols_r - 1) * stride + size == cols_m);
        if(transpose) {
            final Matrix result = new Matrix(rows_r * cols_r, repeat * size * size);
            for(int k = 0; k < repeat; k++) {
                for (int i = 0; i < rows_r; i++) {
                    final int off_m_ki = k * rows_m + i * stride;
                    final int off_r_ki = i * cols_r;
                    for (int j = 0; j < cols_r; j++) {
                        final int off_m_j = j * stride;
                        for (int y = 0; y < size; y++) {
                            final float[] data_m_kiy = m.data[off_m_ki + y];
                            final int off_r_y = (k * size + y) * size;
                            for (int x = 0; x < size; x++) {
                                result.data[off_r_ki + j][off_r_y + x] = data_m_kiy[off_m_j + x];
                            }
                        }
                    }
                }
            }
            return result;
        } else {
            final Matrix result = new Matrix(repeat * size * size, rows_r * cols_r);
            for(int k = 0; k < repeat; k++) {
                for (int i = 0; i < rows_r; i++) {
                    final int off_m_ki = k * rows_m + i * stride;
                    final int off_r_ki = i * cols_r;
                    for (int j = 0; j < cols_r; j++) {
                        final int off_m_j = j * stride;
                        for (int y = 0; y < size; y++) {
                            final float[] data_m_kiy = m.data[off_m_ki + y];
                            final int off_r_y = (k * size + y) * size;
                            for (int x = 0; x < size; x++) {
                                result.data[off_r_y + x][off_r_ki + j] = data_m_kiy[off_m_j + x];
                            }
                        }
                    }
                }
            }
            return result;
        }
    }

    public static Matrix Conv2Img(Matrix m, int repeat, int rows, int cols, int size, int stride) {
        assert (repeat > 0);
        assert (stride > 0);
        assert (size > 0);
        final int rows_n = 1 + (rows - size) / stride;
        final int cols_n = 1 + (cols - size) / stride;
        assert ((rows_n - 1) * stride + size == rows);
        assert ((cols_n - 1) * stride + size == cols);
        final Matrix result = new Matrix(rows * repeat, cols);
        for(int k = 0; k < repeat; k++) {
            final int off_m_k = k * size;
            final int off_r_k = k * rows;
            for (int i = 0; i < rows_n; i++) {
                final int off_r_ki = off_r_k + i * stride;
                for (int j = 0; j < cols_n; j++) {
                    final int off_m_ij = i * cols_n + j;
                    final int off_r_j = j * stride;
                    for (int y = 0; y < size; y++) {
                        final int off_r_kiy = off_r_ki + y;
                        final int off_m_ky = (off_m_k + y) * size;
                        for (int x = 0; x < size; x++) {
                            result.data[off_r_kiy][off_r_j + x] += m.data[off_m_ky + x][off_m_ij];
                        }
                    }
                }
            }
        }
        return result;
    }

    public static Matrix expand_minmax(Matrix a, Matrix b, Matrix c) {
        final int size = b.shape[0] / a.shape[0];
        assert(a.shape[0] * size == b.shape[0]);
        assert(a.shape[1] * size == b.shape[1]);
        assert(a.shape[0] == c.shape[0]);
        assert(a.shape[1] == c.shape[1]);
        Matrix result  = new Matrix(1, b.shape[0] * b.shape[1]);
        for(int i = 0; i < b.shape[0]; i++) {
            final float[] aa = a.data[i / size];
            final float[] bb = b.data[i];
            final float[] cc = c.data[i / size];
            for(int j = 0; j < b.shape[1]; j++) {
                if(bb[j] == aa[j / size]) {
                    result.data[0][i * b.shape[1] + j] = cc[j / size];
                }
            }
        }
        return result;
    }

    public static Matrix expand_avg(Matrix m, int size) {
        assert (size > 0);
        final int N = size * size;
        Matrix result  = new Matrix(1, m.shape[0] * m.shape[1] * N);
        for(int i = 0; i < m.shape[0] * size; i++) {
            final float[] mm = m.data[i / size];
            for(int j = 0; j < m.shape[1] * size; j++) {
                result.data[0][i * m.shape[1] * size + j] = mm[j / size] / N;
            }
        }
        return result;
    }

    public static boolean checkNaN(Matrix m) {
        for (int i = 0; i < m.shape[0]; i++) {
            final float[] a = m.data[i];
            for (int j = 0; j < m.shape[0]; j++) {
                if (Float.isNaN(a[j])) {
                    return true;
                }
            }
        }
        return false;
    }
}
