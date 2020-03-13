package com.github.romualdrousseau.shuju.math;

public class Helper {

    public static boolean checkNaN(Matrix m) {
        for (int i = 0; i < m.rows; i++) {
            final float[] a = m.data[i];
            for (int j = 0; j < m.cols; j++) {
                if (Float.isNaN(a[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Matrix xw_plus_b(Matrix input, Matrix weights, Matrix bias) {
        return weights.matmul(input).add(bias);
    }

    public static Matrix xw_plus_b(Matrix input, Matrix weights, Vector bias) {
        return weights.matmul(input).add(bias, 1);
    }

    public static Matrix xw_plus_b(Matrix input, Matrix weights, float bias) {
        return weights.matmul(input).add(bias);
    }

    public static Matrix a_mul_b(Matrix a, Matrix b) {
        return (a.colCount() == b.colCount()) ? b.mul(a) : b.matmul(a);
    }

    public static Matrix rotate_180(Matrix m) {
        final Matrix mm = m.copy();
        for (int j = 0; j < mm.cols/ 2; j++) {
            mm.swap(j, mm.cols - j - 1, 1);
        }
        for (int i = 0; i < mm.rows / 2; i++) {
            mm.swap(i, mm.rows - i - 1, 0);
        }
        return mm;
    }

    public static Matrix expand_minmax(Matrix a, Matrix b, Matrix c) {
        final int size = b.rows / a.rows;
        assert(a.rows * size == b.rows);
        assert(a.cols * size == b.cols);
        assert(a.rows == c.rows);
        assert(a.cols == c.cols);
        Matrix result  = new Matrix(1, b.rows * b.cols);
        for(int i = 0; i < b.rows; i++) {
            final float[] aa = a.data[i / size];
            final float[] bb = b.data[i];
            final float[] cc = c.data[i / size];
            for(int j = 0; j < b.cols; j++) {
                if(bb[j] == aa[j / size]) {
                    result.data[0][i * b.cols + j] = cc[j / size];
                }
            }
        }
        return result;
    }

    public static Matrix expand_avg(Matrix m, int size) {
        assert (size > 0);
        final int N = size * size;
        Matrix result  = new Matrix(1, m.rows * m.cols * N);
        for(int i = 0; i < m.rows * size; i++) {
            final float[] mm = m.data[i / size];
            for(int j = 0; j < m.cols * size; j++) {
                result.data[0][i * m.cols * size + j] = mm[j / size] / N;
            }
        }
        return result;
    }

    public static Matrix block_diag(Matrix m, int repeat, boolean transpose) {
        final int split_r = m.rows / repeat;
        if(transpose) {
            final Matrix result = new Matrix(m.cols * repeat, m.rows);
            for (int i = 0; i < m.rows; i++) {
                final int off_r = (i / split_r) * m.cols;
                for (int j = 0; j < m.cols; j++) {
                    result.data[off_r + j][i] = m.data[i][j];
                }
            }
            return result;
        } else {
            final Matrix result = new Matrix(m.rows, m.cols * repeat);
            for (int i = 0; i < m.rows; i++) {
                final int off_r = (i / split_r) * m.cols;
                for (int j = 0; j < m.cols; j++) {
                    result.data[i][off_r + j] = m.data[i][j];
                }
            }
            return result;
        }
    }

    public static Matrix block_undiag(Matrix m, int repeat) {
        final int split_r = m.rows / repeat;
        final Matrix result = new Matrix(m.rows, m.cols / repeat);
        for (int i = 0; i < result.rows; i++) {
            final int off_m = (i / split_r) * result.cols;
            for (int j = 0; j < result.cols; j++) {
                result.data[i][j] = m.data[i][off_m + j];
            }
        }
        return result;
    }

    public static Matrix im2col(Matrix m, int repeat, int size, int stride, boolean transpose) {
        assert (repeat > 0);
        assert (stride > 0);
        assert (size > 0);
        final int rows_m = m.rows / repeat;
        final int cols_m = m.cols;
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

    public static Matrix col2im(Matrix m, int repeat, int mrows, int mcols, int size, int stride) {
        assert (stride > 0);
        assert (size > 0);
        final int rows_n = 1 + (mrows - size) / stride;
        final int cols_n = 1 + (mcols - size) / stride;
        assert ((rows_n - 1) * stride + size == mrows);
        assert ((cols_n - 1) * stride + size == mcols);
        final Matrix result = new Matrix(mrows * repeat, mcols);
        for(int k = 0; k < repeat; k++) {
            final int off_m_k = k * mrows;
            for (int i = 0; i < rows_n; i++) {
                final int off_r_ki = off_m_k + i * stride;
                for (int j = 0; j < cols_n; j++) {
                    final int off_m_ij = i * cols_n + j;
                    final int off_r_j = j * stride;
                    for (int y = 0; y < size; y++) {
                        final int off_r_kiy = off_r_ki + y;
                        final int off_m_ky = off_m_k + y * size;
                        for (int x = 0; x < size; x++) {
                            result.data[off_r_kiy][off_r_j + x] = m.data[off_m_ky + x][off_m_ij];
                        }
                    }
                }
            }
        }
        return result;
    }
}
