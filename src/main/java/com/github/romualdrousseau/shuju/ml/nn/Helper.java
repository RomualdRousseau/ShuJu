package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;

public class Helper {

    public static Tensor3D Img2Conv(Tensor3D m, int size, int stride) {
        assert (stride > 0);
        assert (size > 0);
        final int rows_m = m.shape[1];
        final int cols_m = m.shape[2];
        final int rows_r = 1 + (rows_m - size) / stride;
        final int cols_r = 1 + (cols_m - size) / stride;
        assert ((rows_r - 1) * stride + size == rows_m);
        assert ((cols_r - 1) * stride + size == cols_m);
        final Tensor3D result = new Tensor3D(m.shape[0], size * size, rows_r * cols_r);
        for (int k = 0; k < m.shape[0]; k++) {
            final float[][] m_k = m.data[k];
            final float[][] r_k = result.data[k];
            for (int i = 0; i < rows_r; i++) {
                final int off_m_ki = i * stride;
                final int off_r_ki = i * cols_r;
                for (int j = 0; j < cols_r; j++) {
                    final int off_m_j = j * stride;
                    for (int y = 0; y < size; y++) {
                        final float[] m_kiy = m_k[off_m_ki + y];
                        final int off_r_y = y * size;
                        for (int x = 0; x < size; x++) {
                            r_k[off_r_y + x][off_r_ki + j] = m_kiy[off_m_j + x];
                        }
                    }
                }
            }
        }
        return result;
    }

    public static Tensor3D Conv2Img(Tensor3D m, int rows, int cols, int size, int stride) {
        assert (stride > 0);
        assert (size > 0);
        final int rows_n = 1 + (rows - size) / stride;
        final int cols_n = 1 + (cols - size) / stride;
        assert ((rows_n - 1) * stride + size == rows);
        assert ((cols_n - 1) * stride + size == cols);
        final Tensor3D result = new Tensor3D(m.shape[0], rows, cols);
        for (int k = 0; k < m.shape[0]; k++) {
            final float[][] m_k = m.data[k];
            final float[][] r_k = result.data[k];
            for (int i = 0; i < rows_n; i++) {
                final int off_r_ki = i * stride;
                for (int j = 0; j < cols_n; j++) {
                    final int off_m_ij = i * cols_n + j;
                    final int off_r_j = j * stride;
                    for (int y = 0; y < size; y++) {
                        final float[] r_kiy = r_k[off_r_ki + y];
                        final int off_m_ky = y * size;
                        for (int x = 0; x < size; x++) {
                            r_kiy[off_r_j + x] += m_k[off_m_ky + x][off_m_ij];
                        }
                    }
                }
            }
        }
        return result;
    }

    public static Tensor2D Img2Conv(Tensor2D m, int size, int stride) {
        assert (stride > 0);
        assert (size > 0);
        final int rows_m = m.shape[0];
        final int cols_m = m.shape[1];
        final int rows_r = 1 + (rows_m - size) / stride;
        final int cols_r = 1 + (cols_m - size) / stride;
        assert ((rows_r - 1) * stride + size == rows_m);
        assert ((cols_r - 1) * stride + size == cols_m);
        final Tensor2D result = new Tensor2D(size * size, rows_r * cols_r);
        for (int i = 0; i < rows_r; i++) {
            final int off_m_ki = i * stride;
            final int off_r_ki = i * cols_r;
            for (int j = 0; j < cols_r; j++) {
                final int off_m_j = j * stride;
                for (int y = 0; y < size; y++) {
                    final float[] data_m_kiy = m.data[off_m_ki + y];
                    final int off_r_y = y * size;
                    for (int x = 0; x < size; x++) {
                        result.data[off_r_y + x][off_r_ki + j] = data_m_kiy[off_m_j + x];
                    }
                }
            }
        }
        return result;
    }

    public static Tensor2D Conv2Img(Tensor2D m, int rows, int cols, int size, int stride) {
        assert (stride > 0);
        assert (size > 0);
        final int rows_n = 1 + (rows - size) / stride;
        final int cols_n = 1 + (cols - size) / stride;
        assert ((rows_n - 1) * stride + size == rows);
        assert ((cols_n - 1) * stride + size == cols);
        final Tensor2D result = new Tensor2D(rows, cols);
        for (int i = 0; i < rows_n; i++) {
            final int off_r_i = i * stride;
            for (int j = 0; j < cols_n; j++) {
                final int off_m_ij = i * cols_n + j;
                final int off_r_j = j * stride;
                for (int y = 0; y < size; y++) {
                    final int off_r_iy = off_r_i + y;
                    final int off_m_y = y * size;
                    for (int x = 0; x < size; x++) {
                        result.data[off_r_iy][off_r_j + x] += m.data[off_m_y + x][off_m_ij];
                    }
                }
            }
        }
        return result;
    }

    public static Tensor2D expand_minmax(Tensor2D a, Tensor2D b, Tensor2D c) {
        final int size = b.shape[0] / a.shape[0];
        assert (a.shape[0] * size == b.shape[0]);
        assert (a.shape[1] * size == b.shape[1]);
        assert (a.shape[0] == c.shape[0]);
        assert (a.shape[1] == c.shape[1]);
        Tensor2D result = new Tensor2D(1, b.shape[0] * b.shape[1]);
        for (int i = 0; i < b.shape[0]; i++) {
            final float[] aa = a.data[i / size];
            final float[] bb = b.data[i];
            final float[] cc = c.data[i / size];
            for (int j = 0; j < b.shape[1]; j++) {
                if (bb[j] == aa[j / size]) {
                    result.data[0][i * b.shape[1] + j] = cc[j / size];
                }
            }
        }
        return result;
    }

    public static Tensor2D expand_avg(Tensor2D m, int size) {
        assert (size > 0);
        final int N = size * size;
        Tensor2D result = new Tensor2D(1, m.shape[0] * m.shape[1] * N);
        for (int i = 0; i < m.shape[0] * size; i++) {
            final float[] mm = m.data[i / size];
            for (int j = 0; j < m.shape[1] * size; j++) {
                result.data[0][i * m.shape[1] * size + j] = mm[j / size] / N;
            }
        }
        return result;
    }

    public static boolean checkNaN(Tensor2D m) {
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
