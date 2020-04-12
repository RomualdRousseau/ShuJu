package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;

public class Helper {

    public static Tensor3D Im2Col(final Tensor3D m, final int size, final int stride, final int pad) {
        final int rows_m = m.shape[1];
        final int cols_m = m.shape[2];
        final int rows_r = 1 + pad * 2 + (rows_m - size) / stride;
        final int cols_r = 1 + pad * 2 + (cols_m - size) / stride;

        assert ((rows_r - pad * 2 - 1) * stride + size == rows_m);
        assert ((cols_r - pad * 2 - 1) * stride + size == cols_m);

        final Tensor3D result = new Tensor3D(m.shape[0], size * size, rows_r * cols_r);

        for (int k = 0; k < result.shape[0]; k++) {
            final float[][] m_k = m.data[k];
            final float[][] r_k = result.data[k];

            for (int i = 0; i < rows_r; i++) {
                final int off_m_ki = i * stride - pad;
                final int off_r_ki = i * cols_r;
                final int y0 = Math.max(-off_m_ki, 0);
                final int y1 = Math.min(rows_m - off_m_ki, size);

                for (int j = 0; j < cols_r; j++) {
                    final int off_m_j = j * stride - pad;
                    final int x0 = Math.max(-off_m_j, 0);
                    final int x1 = Math.min(cols_m - off_m_j, size);

                    for (int y = y0; y < y1; y++) {
                        final float[] m_kiy = m_k[off_m_ki + y];
                        final int off_r_y = y * size;

                        for (int x = x0; x < x1; x++) {
                            r_k[off_r_y + x][off_r_ki + j] = m_kiy[off_m_j + x];
                        }
                    }
                }
            }
        }

        return result;
    }

    public static Tensor3D Col2Im(final Tensor3D m, final int rows, final int cols, final int size, final int stride, final int pad) {
        final int rows_n = 1 + (rows + pad * 2 - size) / stride;
        final int cols_n = 1 + (cols + pad * 2 - size) / stride;

        assert ((rows_n - 1) * stride + size == rows + pad * 2);
        assert ((cols_n - 1) * stride + size == cols + pad * 2);

        final Tensor3D result = new Tensor3D(m.shape[0], rows, cols);

        for (int k = 0; k < result.shape[0]; k++) {
            final float[][] m_k = m.data[k];
            final float[][] r_k = result.data[k];

            for (int i = 0; i < rows_n; i++) {
                final int off_r_i = i * stride - pad;
                final int y0 = Math.max(-off_r_i, 0);
                final int y1 = Math.min(rows - off_r_i, size);

                for (int j = 0; j < cols_n; j++) {
                    final int off_m_ij = i * cols_n + j;
                    final int off_r_j = j * stride - pad;
                    final int x0 = Math.max(-off_r_j, 0);
                    final int x1 = Math.min(cols - off_r_j, size);

                    for (int y = y0; y < y1; y++) {
                        final float[] r_iy = r_k[off_r_i + y];
                        final int off_m_y = y * size;

                        for (int x = x0; x < x1; x++) {
                            r_iy[off_r_j + x] += m_k[off_m_y + x][off_m_ij];
                        }
                    }
                }
            }
        }

        return result;
    }

    public static Tensor3D expandMinMax(final Tensor3D a, final Tensor3D b, final Tensor3D c) {
        final int size = b.shape[1] / a.shape[1];

        assert (a.shape[1] * size == b.shape[1]);
        assert (a.shape[2] * size == b.shape[2]);
        assert (a.shape[1] == c.shape[1]);
        assert (a.shape[2] == c.shape[2]);

        final Tensor3D result = new Tensor3D(b.shape[0], b.shape[1], b.shape[1]);

        for (int k = 0; k < result.shape[0]; k++) {
            final float[][] a_k = a.data[k];
            final float[][] b_k = b.data[k];
            final float[][] c_k = c.data[k];
            final float[][] r_k = result.data[k];

            for (int i = 0; i < result.shape[1]; i++) {
                final int ii = i / size;
                final float[] a_ki = a_k[ii];
                final float[] b_ki = b_k[i];
                final float[] c_ki = c_k[ii];
                final float[] r_ki = r_k[i];

                for (int j = 0; j < result.shape[2]; j++) {
                    final int jj = j / size;
                    if (b_ki[j] == a_ki[jj]) {
                        r_ki[j] = c_ki[jj];
                    }
                }
            }
        }
        return result;
    }

    public static Tensor3D expandAvg(final Tensor3D m, final int size) {
        final int N = size * size;

        final Tensor3D result = new Tensor3D(m.shape[0], m.shape[1] * size, m.shape[2] * size);

        for (int k = 0; k < result.shape[0]; k++) {
            final float[][] m_k = m.data[k];
            final float[][] r_k = result.data[k];

            for (int i = 0; i < result.shape[1]; i++) {
                final float[] m_ki = m_k[i / size];
                final float[] r_ki = r_k[i];

                for (int j = 0; j < result.shape[2]; j++) {
                    r_ki[j] = m_ki[j / size] / N;
                }
            }
        }

        return result;
    }

    public static boolean checkNaN(final Tensor2D m) {
        for (int i = 0; i < m.shape[0]; i++) {
            final float[] m_i = m.data[i];
            for (int j = 0; j < m.shape[1]; j++) {
                if (Float.isNaN(m_i[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkNaN(final Tensor3D m) {
        for (int i = 0; i < m.shape[0]; i++) {
            final float[][] m_i = m.data[i];
            for (int j = 0; j < m.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < m.shape[2]; k++) {
                    if (Float.isNaN(m_ij[k])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
