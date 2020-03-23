package com.github.romualdrousseau.shuju.math;

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
                for (int k = 0; k < this.shape[2]; k++){
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

    public Tensor2D reshape(final int newRows, final int newCols, final char format) {
        return new Tensor2D(newRows, newCols);
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
                for (int k = 0; k < this.shape[2]; k++){
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

    public Tensor3D matmul(final Tensor2D a) {
        final Tensor3D result = new Tensor3D(this.shape[0], this.shape[1], a.shape[1]).zero();
        for (int i = 0; i < this.shape[0]; i++) {
            Blas.fgemm(false, false, this.data[i], 1.0f, a.data, 1.0f, result.data[i]);
        }
        return result;
	}

    public Tensor3D matmul(final Tensor3D a) {
        final Tensor3D result = new Tensor3D(this.shape[0], this.shape[1], a.shape[2]).zero();
        for (int i = 0; i < this.shape[0]; i++) {
            Blas.fgemm(false, false, this.data[i], 1.0f, a.data[i], 1.0f, result.data[i]);
        }
        return result;
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
                for (int k = 0; k < this.shape[2]; k++){
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
