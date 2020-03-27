package com.github.romualdrousseau.shuju.math;

import java.util.stream.IntStream;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class Tensor3D extends AbstractTensor<float[][][]> {

    public Tensor3D(final int... shape) {
        super(new int[] { shape[0], shape[1], shape[2] }, new float[shape[0]][shape[1]][shape[2]]);
    }

    public Tensor3D(final JSONObject json) {
        this(json.getInt("shape0"), json.getInt("shape1"), json.getInt("shape2"));
        final JSONArray jsonData = json.getJSONArray("data");
        for (int i = 0; i < this.shape[0]; i++) {
            final JSONArray json_s0 = jsonData.getJSONArray(i);
            for (int j = 0; j < this.shape[1]; j++) {
                final JSONArray json_s1 = json_s0.getJSONArray(j);
                for (int k = 0; k < this.shape[2]; k++) {
                    this.data[i][j][k] = json_s1.getFloat(j);
                }
            }
        }
    }

    public float get(final int i, final int j, final int k) {
        return this.data[i][j][k];
    }

    public Tensor3D copy() {
        final Tensor3D result = new Tensor3D(this.shape[0], this.shape[1], this.shape[2]);
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            final float[][] r_i = result.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                System.arraycopy(m_i[j], 0, r_i[j], 0, this.shape[2]);
            }
        }
        return result;
    }

    public Tensor2D reshape(final int newRows, final int newCols) {
        return this.reshape(newRows, newCols, 'C');
    }

    public Tensor2D reshape(int newRows, int newCols, final char format) {
        assert (newRows > 0 || newCols > 0);
        if (newRows < 0) {
            newRows = this.shape[0] * this.shape[1] * this.shape[2] / newCols;
        }
        if (newCols < 0) {
            newCols = this.shape[0] * this.shape[1] * this.shape[2] / newRows;
        }
        assert (this.shape[0] * this.shape[1] * this.shape[2] == newRows * newCols);
        final Tensor2D result =  new Tensor2D(newRows, newCols);
        if (format == 'C') {
            int a = 0;
            for (int i = 0; i < this.shape[0]; i++) {
                final float[][] m_i = this.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    final float[] m_ij = m_i[j];
                    for (int k = 0; k < this.shape[2]; k++) {
                        result.data[a / newCols][a % newCols] = m_ij[k];
                        a++;
                    }
                }
            }
        } else { // 'F'
            int a = 0;
            for (int i = 0; i < this.shape[0]; i++) {
                final float[][] m_i = this.data[i];
                for (int k = 0; k < this.shape[2]; k++) {
                    for (int j = 0; j < this.shape[1]; j++) {
                        result.data[a / newCols][a % newCols] = m_i[j][k];
                        a++;
                    }
                }
            }
        }
        return result;
    }

    public Tensor3D zero() {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] = 0.0f;
                }
            }
        }
        return this;
    }

    public Tensor3D randomize() {
        return this.randomize(1);
    }

    public Tensor3D randomize(final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] = Scalar.random(-n, n);
                }
            }
        }
        return this;
    }

    public Tensor3D abs() {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] = Scalar.abs(m_ij[k]);
                }
            }
        }
        return this;
    }

    public Tensor3D pow(final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] = Scalar.pow(m_ij[k], n);
                }
            }
        }
        return this;
    }

    public Tensor3D add(final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] += n;
                }
            }
        }
        return this;
    }

    public Tensor3D add(final Tensor1D a, final int axis) {
        if (axis == 0) {
            assert (this.shape[2] == a.rows);
            for (int i = 0; i < this.shape[0]; i++) {
                final float[][] m_i = this.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    final float[] m_ij = m_i[j];
                    for (int k = 0; k < this.shape[2]; k++) {
                        m_ij[k] += a.data[k];
                    }
                }
            }
        } else {
            assert (this.shape[1] == a.rows);
            for (int i = 0; i < this.shape[0]; i++) {
                final float[][] m_i = this.data[i];
                for (int k = 0; k < this.shape[2]; k++) {
                    for (int j = 0; j < this.shape[1]; j++) {
                        m_i[j][k] += a.data[k];
                    }
                }
            }
        }
        return this;
    }

    public Tensor3D add(final Tensor2D a) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] a_j = a.data[j];
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] += a_j[k];
                }
            }
        }
        return this;
    }

    public Tensor3D add(final Tensor3D a) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] a_i = a.data[i];
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] a_ij = a_i[j];
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] += a_ij[k];
                }
            }
        }
        return this;
    }

    public Tensor3D sub(final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] -= n;
                }
            }
        }
        return this;
    }

    public Tensor3D sub(final Tensor2D a) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] a_j = a.data[j];
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] -= a_j[k];
                }
            }
        }
        return this;
    }

    public Tensor3D sub(final Tensor3D a) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] a_i = a.data[i];
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] a_ij = a_i[j];
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] -= a_ij[k];
                }
            }
        }
        return this;
    }

    public Tensor3D mul(final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] *= n;
                }
            }
        }
        return this;
    }

    public Tensor3D mul(final Tensor2D a) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] a_j = a.data[j];
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] += a_j[k];
                }
            }
        }
        return this;
    }

    public Tensor3D mul(final Tensor3D a) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] a_i = a.data[i];
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] a_ij = a_i[j];
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] *= a_ij[k];
                }
            }
        }
        return this;
    }

    public Tensor3D div(final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] /= n;
                }
            }
        }
        return this;
    }

    public Tensor3D div(final Tensor2D a) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] a_j = a.data[j];
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] /= a_j[k];
                }
            }
        }
        return this;
    }

    public Tensor3D div(final Tensor3D a) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] a_i = a.data[i];
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] a_ij = a_i[j];
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] /= a_ij[k];
                }
            }
        }
        return this;
    }

    public Tensor3D map(final TensorFunction<Tensor3D> fn) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] = fn.apply(m_ij[k], new int[] { i, j, k }, this);
                }
            }
        }
        return this;
    }

    public Tensor3D map(final TensorFunction<Tensor3D> fn, final Tensor3D v) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] = fn.apply(m_ij[k], new int[] { i, j, k }, v);
                }
            }
        }
        return this;
    }

    public Tensor3D fma(final Tensor3D m, final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            Blas.fgemm(false, false, m.data[i], n, null, 1.0f, this.data[i]);
        }
        return this;
    }

    public Tensor3D matmul(final Tensor3D a) {
        return this.matmul(a, false, false);
    }

    public Tensor3D matmul(final Tensor3D a, boolean transposeA, boolean transposeB) {
        final int rows = transposeA ? this.shape[2] : this.shape[1];
        final int cols = transposeB ? a.shape[1] : a.shape[2];
        final Tensor3D result = new Tensor3D(this.shape[0], rows, cols);

        final int surface = this.shape[0] * this.shape[1] * this.shape[2];
        final int count = Math.min(surface / (64 * 64) + 1, Runtime.getRuntime().availableProcessors());

        if ((this.shape[0] / count) <= 1) {

            // Single core calculation

            for (int i = 0; i < this.shape[0]; i++) {
                Blas.fgemm(transposeA, transposeB, this.data[i], 1.0f, a.data[i], 0.0f, result.data[i]);
            }
        } else {
            final int stride = this.shape[0] / count + 1;

            // Muti core calculation

            IntStream.rangeClosed(0, count).map(i -> i * stride).parallel().forEach(i -> {
                final int remaining = Math.min(this.shape[0] - i, stride);
                for (int j = 0; j < remaining; j++) {
                    Blas.fgemm(transposeA, transposeB, this.data[i + j], 1.0f, a.data[i + j], 0.0f, result.data[i + j]);
                }
            });
        }

        return result;
    }

    public String toString() {
        if (this.shape[0] == 0 || this.shape[1] == 0 || this.shape[2] == 0) {
            return "||";
        }

        final StringBuilder result = new StringBuilder();
        for (int k = 0; k < this.shape[0]; k++) {
            for (int i = 0; i < this.shape[1]; i++) {
                final float[] a = this.data[k][i];
                result.append("| ");
                for (int j = 0; j < this.shape[2]; j++) {
                    result.append(String.format("%1$10.3f ", a[j]));
                }
                result.append("|").append(System.lineSeparator());
            }
            result.append(System.lineSeparator());
        }

        return result.toString();
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();

        if (this.shape[0] == 0 || this.shape[1] == 0 || this.shape[2] == 0) {
            return json;
        }

        final JSONArray jsonData = JSON.newJSONArray();
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            final JSONArray json_s0 = JSON.newJSONArray();
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                final JSONArray json_s1 = JSON.newJSONArray();
                for (int k = 0; k < this.shape[2]; k++) {
                    json_s1.append(m_ij[k]);
                }
                json_s0.append(json_s1);
            }
            jsonData.append(json_s0);
        }

        json.setInt("shape0", this.shape[0]);
        json.setInt("shape1", this.shape[1]);
        json.setInt("shape2", this.shape[2]);
        json.setJSONArray("data", jsonData);
        return json;
    }
}
