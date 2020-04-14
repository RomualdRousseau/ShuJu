package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class Tensor3D extends AbstractTensor<float[][][]> {

    public Tensor3D(final int... shape) {
        super(new int[] { shape[0], shape[1], shape[2] }, new float[shape[0]][shape[1]][shape[2]]);
    }

    public Tensor3D(final float[][][] data) {
        super(new int[] { data.length, data[0].length, data[0][0].length }, data);
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

    public boolean equals(final Tensor3D m) {
        boolean result = this.shape[0] == m.shape[0] && this.shape[1] == m.shape[1] && this.shape[2] == m.shape[2];
        for (int i = 0; i < this.shape[0]; i++) {
            for (int j = 0; j < this.shape[1] && result; j++) {
                final float[] a = this.data[i][j];
                final float[] b = m.data[i][j];
                result &= Arrays.equals(a, b);
            }
        }
        return result;
    }

    public boolean equals(final Tensor3D m, final float e) {
        boolean result = this.shape[0] == m.shape[0] && this.shape[1] == m.shape[1] && this.shape[2] == m.shape[2];
        for (int i = 0; i < this.shape[0]; i++) {
            for (int j = 0; j < this.shape[1] && result; j++) {
                final float[] a = this.data[i][j];
                final float[] b = m.data[i][j];
                for (int k = 0; k < this.shape[2] && result; k++) {
                    result &= Math.abs(a[k] - b[k]) < e;
                }
            }
        }
        return result;
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
        final Tensor2D result = new Tensor2D(newRows, newCols);
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

    public Tensor3D reshape(final int newDepth, final int newRows, final int newCols) {
        return this.reshape(newDepth, newRows, newCols, 'C');
    }

    public Tensor3D reshape(int newDepth, int newRows, int newCols, final char format) {
        assert (newDepth > 0 || newRows > 0 || newCols > 0);

        if (newDepth < 0) {
            newDepth = this.shape[0] * this.shape[1] * this.shape[2] / (newRows * newCols);
        }
        if (newRows < 0) {
            newRows = this.shape[0] * this.shape[1] * this.shape[2] / (newDepth * newCols);
        }
        if (newCols < 0) {
            newCols = this.shape[0] * this.shape[1] * this.shape[2] / (newDepth * newCols);
        }

        assert (this.shape[0] * this.shape[1] * this.shape[2] == newDepth * newRows * newCols);

        final Tensor3D result = new Tensor3D(newDepth, newRows, newCols);

        if (format == 'C') {
            int a = 0;
            for (int i = 0; i < this.shape[0]; i++) {
                final float[][] m_i = this.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    final float[] m_ij = m_i[j];
                    for (int k = 0; k < this.shape[2]; k++) {
                        final int ii = a / (newCols * newRows);
                        final int jj = a % (newCols * newRows);
                        result.data[ii][jj / newCols][jj % newCols] = m_ij[k];
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
                        final int ii = a / (newCols * newRows);
                        final int jj = a % (newCols * newRows);
                        result.data[ii][jj / newCols][jj % newCols] = m_i[j][k];
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

    public Tensor3D ones() {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] = 1.0f;
                }
            }
        }
        return this;
    }

    public Tensor3D arrange(final int axis) {
        if (axis == 0) {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[][] m_i = this.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    final float[] m_ij = m_i[j];
                    for (int k = 0; k < this.shape[2]; k++) {
                        m_ij[k] = i * this.shape[1] * this.shape[2] + j * this.shape[2] + k + 1;
                    }
                }
            }
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[][] m_i = this.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    final float[] m_ij = m_i[j];
                    for (int k = 0; k < this.shape[2]; k++) {
                        m_ij[k] = i * this.shape[1] * this.shape[2] + k * this.shape[1] + j + 1;
                    }
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

    public Tensor3D flatten(int axis) {
		if (axis == 0) {
            final Tensor3D result = new Tensor3D(1, this.shape[1], this.shape[2]);
            for (int k = 0; k < this.shape[1]; k++) {
                for (int i = 0; i < this.shape[2]; i++) {
                    result.data[0][k][i] += this.data[0][k][i];
                }
            }
            return result;
        }
        else if (axis == 1) {
            final Tensor3D result = new Tensor3D(this.shape[0], 1, this.shape[2]);
            for (int k = 0; k < this.shape[0]; k++) {
                for (int i = 0; i < this.shape[2]; i++) {
                    result.data[k][0][i] += this.data[k][0][i];
                }
            }
            return result;
        } else {
            final Tensor3D result = new Tensor3D(this.shape[0], this.shape[1], 1);
            for (int k = 0; k < this.shape[0]; k++) {
                for (int i = 0; i < this.shape[1]; i++) {
                    result.data[k][i][0] += this.data[k][i][0];
                }
            }
            return result;
        }
	}

    public float max(final int k, final int idx, final int axis) {
        if (axis == 0) {
            float maxValue = this.data[0][k][idx];
            for (int i = 1; i < this.shape[0]; i++) {
                if (this.data[i][k][idx] > maxValue) {
                    maxValue = this.data[i][k][idx];
                }
            }
            return maxValue;
        } else if (axis == 1) {
            float maxValue = this.data[k][0][idx];
            for (int i = 1; i < this.shape[1]; i++) {
                if (this.data[k][i][idx] > maxValue) {
                    maxValue = this.data[k][i][idx];
                }
            }
            return maxValue;
        } else {
            float maxValue = this.data[k][idx][0];
            for (int i = 1; i < this.shape[2]; i++) {
                if (this.data[k][idx][i] > maxValue) {
                    maxValue = this.data[k][idx][i];
                }
            }
            return maxValue;
        }
    }

    public Tensor3D max(int axis) {
        if (axis == 0) {
            final Tensor3D result = new Tensor3D(1, this.shape[1], this.shape[2]);
            for (int k = 0; k < this.shape[1]; k++) {
                for (int i = 0; i < this.shape[2]; i++) {
                    result.data[0][k][i] = this.max(k, i, 0);
                }
            }
            return result;
        }
        else if (axis == 1) {
            final Tensor3D result = new Tensor3D(this.shape[0], 1, this.shape[2]);
            for (int k = 0; k < this.shape[0]; k++) {
                for (int i = 0; i < this.shape[2]; i++) {
                    result.data[k][0][i] = this.max(k, i, 1);
                }
            }
            return result;
        } else {
            final Tensor3D result = new Tensor3D(this.shape[0], this.shape[1], 1);
            for (int k = 0; k < this.shape[0]; k++) {
                for (int i = 0; i < this.shape[1]; i++) {
                    result.data[k][i][0] = this.max(k, i, 2);
                }
            }
            return result;
        }
    }

    public float min(final int k, final int idx, final int axis) {
        if (axis == 0) {
            float maxValue = this.data[0][k][idx];
            for (int i = 1; i < this.shape[0]; i++) {
                if (this.data[i][k][idx] < maxValue) {
                    maxValue = this.data[i][k][idx];
                }
            }
            return maxValue;
        } else if (axis == 1) {
            float maxValue = this.data[k][0][idx];
            for (int i = 1; i < this.shape[1]; i++) {
                if (this.data[k][i][idx] < maxValue) {
                    maxValue = this.data[k][i][idx];
                }
            }
            return maxValue;
        } else {
            float maxValue = this.data[k][idx][0];
            for (int i = 1; i < this.shape[2]; i++) {
                if (this.data[k][idx][i] < maxValue) {
                    maxValue = this.data[k][idx][i];
                }
            }
            return maxValue;
        }
    }

    public Tensor3D min(int axis) {
        if (axis == 0) {
            final Tensor3D result = new Tensor3D(1, this.shape[1], this.shape[2]);
            for (int k = 0; k < this.shape[1]; k++) {
                for (int i = 0; i < this.shape[2]; i++) {
                    result.data[0][k][i] = this.min(k, i, 0);
                }
            }
            return result;
        }
        else if (axis == 1) {
            final Tensor3D result = new Tensor3D(this.shape[0], 1, this.shape[2]);
            for (int k = 0; k < this.shape[0]; k++) {
                for (int i = 0; i < this.shape[2]; i++) {
                    result.data[k][0][i] = this.min(k, i, 1);
                }
            }
            return result;
        } else {
            final Tensor3D result = new Tensor3D(this.shape[0], this.shape[1], 1);
            for (int k = 0; k < this.shape[0]; k++) {
                for (int i = 0; i < this.shape[1]; i++) {
                    result.data[k][i][0] = this.min(k, i, 2);
                }
            }
            return result;
        }
    }

    public float avg(final int k, final int idx, final int axis) {
        if (axis == 0) {
            float sum = 0.0f;
            for (int i = 0; i < this.shape[0]; i++) {
                sum += this.data[i][k][idx];
            }
            return sum / (float) this.shape[0];
        } else if (axis == 1) {
            float sum = 0.0f;
            for (int i = 0; i < this.shape[1]; i++) {
                sum += this.data[k][i][idx];
            }
            return sum / (float) this.shape[1];
        } else {
            float sum = 0.0f;
            for (int i = 0; i < this.shape[2]; i++) {
                sum += this.data[k][idx][i];
            }
            return sum / (float) this.shape[2];
        }
    }

    public Tensor3D avg(int axis) {
        if (axis == 0) {
            final Tensor3D result = new Tensor3D(1, this.shape[1], this.shape[2]);
            for (int k = 0; k < this.shape[1]; k++) {
                for (int i = 0; i < this.shape[2]; i++) {
                    result.data[k][0][i] = this.avg(k, i, 0);
                }
            }
            return result;
        } else if (axis == 1) {
            final Tensor3D result = new Tensor3D(this.shape[0], 1, this.shape[2]);
            for (int k = 0; k < this.shape[0]; k++) {
                for (int i = 0; i < this.shape[2]; i++) {
                    result.data[k][0][i] = this.avg(k, i, 1);
                }
            }
            return result;
        } else {
            final Tensor3D result = new Tensor3D(this.shape[0], this.shape[1], 1);
            for (int k = 0; k < this.shape[0]; k++) {
                for (int i = 0; i < this.shape[1]; i++) {
                    result.data[k][i][0] = this.avg(k, i, 2);
                }
            }
            return result;
        }
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

    public Tensor3D expAvg(Tensor3D a, float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[][] a_i = a.data[i];
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] a_ij = a_i[j];
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] = n * m_ij[k] +  (1.0f - n) * a_ij[k];
                }
            }
        }
        return this;
    }

    public Tensor3D if_lt_then(final float p, final float a, final float b) {
		for (int i = 0; i < this.shape[0]; i++) {
            final float[][] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                final float[] m_ij = m_i[j];
                for (int k = 0; k < this.shape[2]; k++) {
                    m_ij[k] = Scalar.if_lt_then( m_ij[k], p, a, b);
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
        for (int i = 0; i < this.shape[0]; i++) {
            Blas.fgemm(transposeA, transposeB, this.data[i], 1.0f, a.data[i], 0.0f, result.data[i]);
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
