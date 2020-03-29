package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class Tensor2D extends AbstractTensor<float[][]> {

    public Tensor2D(final int rows, final int cols) {
        super(new int[] { rows, cols }, new float[rows][cols]);
    }

    public Tensor2D(final int rows, final int cols, final boolean rowvar) {
        this(rowvar ? rows : cols, rowvar ? cols : rows);
    }

    public Tensor2D(final float[] v) {
        this(v, true);
    }

    public Tensor2D(final float[] v, final boolean rowvar) {
        this(1, v.length, rowvar);
        if (rowvar) {
            System.arraycopy(v, 0, this.data[0], 0, this.shape[1]);
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                this.data[i][0] = v[i];
            }
        }
    }

    public Tensor2D(final float[][] v) {
        this(v, true);
    }

    public Tensor2D(final float[][] v, final boolean rowvar) {
        this(v.length, v[0].length, rowvar);
        if (rowvar) {
            for (int i = 0; i < this.shape[0]; i++) {
                System.arraycopy(v[i], 0, this.data[i], 0, this.shape[1]);
            }
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                for (int j = 0; j < this.shape[1]; j++) {
                    this.data[i][j] = v[j][i];
                }
            }
        }
    }

    public Tensor2D(final Float[][] v) {
        this(v, true);
    }

    public Tensor2D(final Float[][] v, final boolean rowvar) {
        this(v.length, v[0].length, rowvar);
        if (rowvar) {
            for (int i = 0; i < this.shape[0]; i++) {
                for (int j = 0; j < this.shape[1]; j++) {
                    this.data[i][j] = v[i][j];
                }
            }
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                for (int j = 0; j < this.shape[1]; j++) {
                    this.data[i][j] = v[j][i];
                }
            }
        }
    }

    public Tensor2D(final Tensor1D v) {
        this(v, true);
    }

    public Tensor2D(final Tensor1D v, final boolean rowvar) {
        this(1, v.shape[0], rowvar);
        if (rowvar) {
            for (int j = 0; j < this.shape[1]; j++) {
                this.data[0][j] = v.data[j];
            }
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                this.data[i][0] = v.data[i];
            }
        }
    }

    public Tensor2D(final Tensor1D[] v) {
        this(v, true);
    }

    public Tensor2D(final Tensor1D[] v, final boolean rowvar) {
        this(v.length, v[0].shape[0], rowvar);
        if (rowvar) {
            for (int i = 0; i < this.shape[0]; i++) {
                System.arraycopy(v[i].data, 0, this.data[i], 0, this.shape[1]);
            }
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                for (int j = 0; i < this.shape[1]; j++) {
                    this.data[i][j] = v[j].data[i];
                }
            }
        }
    }

    public Tensor2D(final Tensor1D v, final int stride) {
        this(v, stride, true);
    }

    public Tensor2D(final Tensor1D v, final int stride, final boolean rowvar) {
        this(v.shape[0] / stride, stride, rowvar);
        assert (stride > 0);
        int vi = 0;
        if (rowvar) {
            for (int i = 0; i < this.shape[0]; i++) {
                for (int j = 0; j < this.shape[1]; j++) {
                    this.data[i][j] = v.data[vi++];
                }
            }
        } else {
            for (int j = 0; j < this.shape[1]; j++) {
                for (int i = 0; i < this.shape[0]; i++) {
                    this.data[i][j] = v.data[vi++];
                }
            }
        }
    }

    public Tensor2D(final JSONObject json) {
        this(json.getInt("shape[0]"), json.getInt("shape[1]"));
        final JSONArray jsonData = json.getJSONArray("data");
        for (int i = 0; i < this.shape[0]; i++) {
            final JSONArray jsonRow = jsonData.getJSONArray(i);
            for (int j = 0; j < this.shape[1]; j++) {
                this.data[i][j] = jsonRow.getFloat(j);
            }
        }
    }

    public boolean isNull() {
        return this.shape[0] == 0 || this.shape[1] == 0;
    }

    public int[] getShape() {
        return this.shape;
    }

    public float[][] getFloats() {
        return this.data;
    }

    public float[] getFloats(final int row) {
        return this.data[row];
    }

    public float get(final int row, final int col) {
        return this.data[row][col];
    }

    public Tensor2D set(final int row, final int col, final float v) {
        this.data[row][col] = v;
        return this;
    }

    public boolean equals(final Tensor2D m) {
        boolean result = this.shape[0] == m.shape[0] && this.shape[1] == m.shape[1];
        for (int i = 0; i < this.shape[0] && result; i++) {
            final float[] a = this.data[i];
            final float[] b = m.data[i];
            result &= Arrays.equals(a, b);
        }
        return result;
    }

    public boolean equals(final Tensor2D m, final float e) {
        boolean result = this.shape[0] == m.shape[0] && this.shape[1] == m.shape[1];
        for (int i = 0; i < this.shape[0] && result; i++) {
            final float[] a = this.data[i];
            final float[] b = m.data[i];
            for (int j = 0; j < this.shape[1] && result; j++) {
                result &= Math.abs(a[j] - b[j]) < e;
            }
        }
        return result;
    }

    public boolean isSquared() {
        return this.shape[0] == this.shape[1];
    }

    public boolean isUpper(final int offset, final float e) {
        final int n = Math.min(this.shape[0], this.shape[1]);
        assert (offset >= 0 && offset < n);
        for (int i = 0; i < n - offset; i++) {
            for (int j = 0; j < n - offset; j++) {
                if (j < i && Math.abs(this.data[i + offset][j]) >= e) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isLower(final int offset, final float e) {
        final int n = Math.min(this.shape[0], this.shape[1]);
        assert (offset >= 0 && offset < n);
        for (int i = 0; i < n - offset; i++) {
            for (int j = 0; j < n - offset; j++) {
                if (j > i && Math.abs(this.data[i][j + offset]) >= e) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isDiagonal(final int offset, final float e) {
        final int n = Math.min(this.shape[0], this.shape[1]);
        if (offset >= 0) {
            assert (offset < n);
            for (int i = 0; i < n - offset; i++) {
                for (int j = 0; j < n - offset; j++) {
                    if (j != i && Math.abs(this.data[i + offset][j]) >= e) {
                        return false;
                    }
                }
            }
        } else {
            assert (offset > -n);
            for (int i = 0; i < n + offset; i++) {
                for (int j = 0; j < n + offset; j++) {
                    if (j != i && Math.abs(this.data[i][j - offset]) >= e) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isSymetric(final float e) {
        if (!this.isSquared()) {
            return false;
        }
        for (int i = 0; i < this.shape[1]; i++) {
            for (int j = 0; j < this.shape[1]; j++) {
                if (Math.abs(this.data[i][j] - this.data[j][i]) >= e) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isOrthogonal(final float e) {
        return this.isSquared() && this.transpose().equals(this.inv(), e);
    }

    public float det() {
        assert (this.isSquared());
        if (this.shape[0] == 2) {
            return this.data[0][0] * this.data[1][1] - this.data[0][1] * this.data[1][0];
        } else {
            float sum = 0.0f;
            float a = 1.0f;
            for (int j = 0; j < this.shape[1]; j++) {
                sum += a * this.data[0][j] * this.minor(0, j).det();
                a *= -1.0f;
            }
            return sum;
        }
    }

    public float sparsity() {
        int count = 0;
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                count += (a[j] == 0.0) ? 1 : 0;
            }
        }
        return (float) count / (float) (this.shape[0] * this.shape[1]);
    }

    public float norm(final int idx, final int axis) {
        if (axis == 0) {
            float sum = 0.0f;
            for (int i = 0; i < this.shape[0]; i++) {
                sum += this.data[i][idx] * this.data[i][idx];
            }
            return Scalar.sqrt(sum);
        } else {
            float sum = 0.0f;
            for (int i = 0; i < this.shape[1]; i++) {
                sum += this.data[idx][i] * this.data[idx][i];
            }
            return Scalar.sqrt(sum);
        }
    }

    public Tensor2D norm(final int axis) {
        if (axis == 0) {
            final Tensor2D result = new Tensor2D(1, this.shape[1]);
            for (int i = 0; i < this.shape[1]; i++) {
                result.data[0][i] = this.norm(i, 0);
            }
            return result;
        } else {
            final Tensor2D result = new Tensor2D(this.shape[0], 1);
            for (int i = 0; i < this.shape[0]; i++) {
                result.data[i][0] = this.norm(i, 1);
            }
            return result;
        }
    }

    public int argmin(final int idx, final int axis) {
        if (axis == 0) {
            int result = 0;
            float minValue = this.data[0][idx];
            for (int i = 1; i < this.shape[0]; i++) {
                if (this.data[i][idx] < minValue) {
                    minValue = this.data[i][idx];
                    result = i;
                }
            }
            return result;
        } else {
            int result = 0;
            float minValue = this.data[idx][0];
            for (int i = 1; i < this.shape[1]; i++) {
                if (this.data[idx][i] < minValue) {
                    minValue = this.data[idx][i];
                    result = i;
                }
            }
            return result;
        }
    }

    public Tensor2D argmin(final int axis) {
        if (axis == 0) {
            final Tensor2D result = new Tensor2D(1, this.shape[1]);
            for (int i = 0; i < this.shape[1]; i++) {
                result.data[0][i] = this.argmin(i, 0);
            }
            return result;
        } else {
            final Tensor2D result = new Tensor2D(this.shape[0], 1);
            for (int i = 0; i < this.shape[0]; i++) {
                result.data[i][0] = this.argmin(i, 1);
            }
            return result;
        }
    }

    public int argmax(final int idx, final int axis) {
        if (axis == 0) {
            int result = 0;
            float maxValue = this.data[0][idx];
            for (int i = 1; i < this.shape[0]; i++) {
                if (this.data[i][idx] > maxValue) {
                    maxValue = this.data[i][idx];
                    result = i;
                }
            }
            return result;
        } else {
            int result = 0;
            float maxValue = this.data[idx][0];
            for (int i = 1; i < this.shape[1]; i++) {
                if (this.data[idx][i] > maxValue) {
                    maxValue = this.data[idx][i];
                    result = i;
                }
            }
            return result;
        }
    }

    public Tensor2D argmax(final int axis) {
        if (axis == 0) {
            final Tensor2D result = new Tensor2D(1, this.shape[1]);
            for (int i = 0; i < this.shape[1]; i++) {
                result.data[0][i] = this.argmax(i, 0);
            }
            return result;
        } else {
            final Tensor2D result = new Tensor2D(this.shape[0], 1);
            for (int i = 0; i < this.shape[0]; i++) {
                result.data[i][0] = this.argmax(i, 1);
            }
            return result;
        }
    }

    public Tensor2D oneHot(final int k) {
        assert (this.shape[0] == 1 || this.shape[1] == 1) : "Illegal shape; must be a row or column vector";

        if (this.shape[0] == 1) {

            // Loop over the row

            for (int j = 0; j < this.shape[1]; j++) {
                this.data[0][j] = (j == k) ? 1.0f : 0.0f;
            }
        } else {

            // Loop over the column

            for (int i = 0; i < this.shape[0]; i++) {
                this.data[i][0] = (i == k) ? 1.0f : 0.0f;
            }
        }

        return this;
    }

    public <T extends Enum<T>> Tensor2D oneHot(final T e) {
        assert (this.shape[0] == 1 || this.shape[1] == 1) : "Illegal shape; must be a row or column vector";

        if (e == null) {
            return this.zero();
        } else {
            return this.oneHot(e.ordinal());
        }
    }

    public <T extends Enum<T>> Tensor2D oneHot(final T[] s) {
        assert (this.shape[0] == 1 || this.shape[1] == 1) : "Illegal shape; must be a row or column vector";

        if (s == null) {
            return this.zero();
        } else if (this.shape[0] == 1) {

            // Loop over the row

            for (int i = 0; i < this.shape[1]; i++) {
                this.data[0][i] = (i == s[i].ordinal()) ? 1.0f : 0.0f;
            }
        } else {

            // Loop over the column

            for (int i = 0; i < this.shape[0]; i++) {
                this.data[i][0] = (i == s[i].ordinal()) ? 1.0f : 0.0f;
            }
        }

        return this;
    }

    public float avg(final int i, final int axis) {
        assert (axis == 0 || axis == 1) : "Illegal axis";
        assert (axis == 1 || axis == 0 && i >= 0 && i < this.shape[1]) : "Column index out of range";
        assert (axis == 0 || axis == 1 && i >= 0 && i < this.shape[0]) : "Row index out of range";

        float sum = 0.0f;

        if (axis == 0) {
            // Sum over the column

            for (int j = 0; j < this.shape[axis]; j++) {
                sum += this.data[j][i];
            }
        } else {
            // Sum over the row

            for (int j = 0; j < this.shape[axis]; j++) {
                sum += this.data[i][j];
            }
        }

        return sum / (float) this.shape[axis];
    }

    public Tensor2D avg(final int axis) {
        assert (axis == 0 || axis == 1) : "Illegal axis";

        final Tensor2D result;

        if (axis == 0) {
            result = new Tensor2D(1, this.shape[1]);

            // Loop over the columns

            for (int i = 0; i < this.shape[1]; i++) {
                result.data[0][i] = this.avg(i, axis);
            }
        } else {
            result = new Tensor2D(this.shape[0], 1);

            // Loop over the rows

            for (int i = 0; i < this.shape[0]; i++) {
                result.data[i][0] = this.avg(i, axis);
            }

        }

        return result;
    }

    public float var(final int idx, final int axis) {
        if (axis == 0) {
            final float avg = this.avg(idx, 0);
            float var = 0.0f;
            for (int i = 0; i < this.shape[0]; i++) {
                final float tmp = this.data[i][idx] - avg;
                var += tmp * tmp;
            }
            return var / (float) (this.shape[0] - 1);
        } else {
            final float avg = this.avg(idx, 1);
            float var = 0.0f;
            for (int i = 0; i < this.shape[1]; i++) {
                final float tmp = this.data[idx][i] - avg;
                var += tmp * tmp;
            }
            return var / (float) (this.shape[1] - 1);
        }
    }

    public Tensor2D var(final int axis) {
        if (axis == 0) {
            final Tensor2D result = new Tensor2D(1, this.shape[1]);
            for (int i = 0; i < this.shape[1]; i++) {
                result.data[0][i] = this.var(i, 0);
            }
            return result;
        } else {
            final Tensor2D result = new Tensor2D(this.shape[0], 1);
            for (int i = 0; i < this.shape[0]; i++) {
                result.data[i][0] = this.var(i, 1);
            }
            return result;
        }
    }

    public float cov(final int idx1, final int idx2, final int axis) {
        if (axis == 0) {
            final float avg1 = this.avg(idx1, 0);
            final float avg2 = this.avg(idx2, 0);
            float cov = 0.0f;
            for (int i = 0; i < this.shape[0]; i++) {
                final float tmp1 = this.data[i][idx1] - avg1;
                final float tmp2 = this.data[i][idx2] - avg2;
                cov += tmp1 * tmp2;
            }
            return cov / (float) (this.shape[0] - 1);
        } else {
            final float avg1 = this.avg(idx1, 1);
            final float avg2 = this.avg(idx2, 1);
            float cov = 0.0f;
            for (int i = 0; i < this.shape[1]; i++) {
                final float tmp1 = this.data[idx1][i] - avg1;
                final float tmp2 = this.data[idx2][i] - avg2;
                cov += tmp1 * tmp2;
            }
            return cov / (float) (this.shape[1] - 1);
        }
    }

    public Tensor2D cov(final int axis) {
        if (axis == 0) {
            final Tensor2D result = new Tensor2D(this.shape[1], this.shape[1]);
            for (int i = 0; i < this.shape[1]; i++) {
                for (int j = 0; j < this.shape[1]; j++) {
                    result.data[i][j] = this.cov(i, j, 0);
                }
            }
            return result;
        } else {
            final Tensor2D result = new Tensor2D(this.shape[0], this.shape[0]);
            for (int i = 0; i < this.shape[0]; i++) {
                for (int j = 0; j < this.shape[0]; j++) {
                    result.data[i][j] = this.cov(i, j, 1);
                }
            }
            return result;
        }
    }

    public float cov(final Tensor2D m, final int idx, final int axis) {
        return this.cov(m, idx, idx, axis);
    }

    public float cov(final Tensor2D m, final int idx1, final int idx2, final int axis) {
        if (axis == 0) {
            assert (this.shape[0] == m.shape[0]);
            final float avg1 = this.avg(idx1, 0);
            final float avg2 = m.avg(idx2, 0);
            float cov = 0.0f;
            for (int i = 0; i < this.shape[0]; i++) {
                final float tmp1 = this.data[i][idx1] - avg1;
                final float tmp2 = m.data[i][idx2] - avg2;
                cov += tmp1 * tmp2;
            }
            return cov / (float) (this.shape[0] - 1);
        } else {
            assert (this.shape[1] == m.shape[1]);
            final float avg1 = this.avg(idx1, 1);
            final float avg2 = m.avg(idx2, 1);
            float cov = 0.0f;
            for (int i = 0; i < this.shape[1]; i++) {
                final float tmp1 = this.data[idx1][i] - avg1;
                final float tmp2 = m.data[idx2][i] - avg2;
                cov += tmp1 * tmp2;
            }
            return cov / (float) (this.shape[1] - 1);
        }
    }

    public Tensor2D cov(final Tensor2D m, final int axis, final boolean full) {
        if (full) {
            if (axis == 0) {
                final Tensor2D result = new Tensor2D(this.shape[1], this.shape[1]);
                for (int i = 0; i < this.shape[1]; i++) {
                    for (int j = 0; j < this.shape[1]; j++) {
                        result.data[i][j] = this.cov(m, i, j, 0);
                    }
                }
                return result;
            } else {
                final Tensor2D result = new Tensor2D(this.shape[0], this.shape[0]);
                for (int i = 0; i < this.shape[0]; i++) {
                    for (int j = 0; j < this.shape[0]; j++) {
                        result.data[i][j] = this.cov(m, i, j, 1);
                    }
                }
                return result;
            }
        } else {
            if (axis == 0) {
                final Tensor2D result = new Tensor2D(1, this.shape[1]);
                for (int i = 0; i < this.shape[1]; i++) {
                    result.data[0][i] = this.cov(m, i, 0);
                }
                return result;
            } else {
                final Tensor2D result = new Tensor2D(this.shape[0], 1);
                for (int i = 0; i < this.shape[0]; i++) {
                    result.data[i][0] = this.cov(m, i, 1);
                }
                return result;
            }
        }
    }

    public float min(final int idx, final int axis) {
        if (axis == 0) {
            float minValue = this.data[0][idx];
            for (int i = 1; i < this.shape[0]; i++) {
                if (this.data[i][idx] < minValue) {
                    minValue = this.data[i][idx];
                }
            }
            return minValue;
        } else {
            float minValue = this.data[idx][0];
            for (int i = 1; i < this.shape[1]; i++) {
                if (this.data[idx][i] < minValue) {
                    minValue = this.data[idx][i];
                }
            }
            return minValue;
        }
    }

    public Tensor2D min(final int axis) {
        if (axis == 0) {
            final Tensor2D result = new Tensor2D(1, this.shape[1]);
            for (int i = 0; i < this.shape[1]; i++) {
                result.data[0][i] = this.min(i, 0);
            }
            return result;
        } else {
            final Tensor2D result = new Tensor2D(this.shape[0], 1);
            for (int i = 0; i < this.shape[0]; i++) {
                result.data[i][0] = this.min(i, 1);
            }
            return result;
        }
    }

    public float max(final int idx, final int axis) {
        if (axis == 0) {
            float maxValue = this.data[0][idx];
            for (int i = 1; i < this.shape[0]; i++) {
                if (this.data[i][idx] > maxValue) {
                    maxValue = this.data[i][idx];
                }
            }
            return maxValue;
        } else {
            float maxValue = this.data[idx][0];
            for (int i = 1; i < this.shape[1]; i++) {
                if (this.data[idx][i] > maxValue) {
                    maxValue = this.data[idx][i];
                }
            }
            return maxValue;
        }
    }

    public Tensor2D max(final int axis) {
        if (axis == 0) {
            final Tensor2D result = new Tensor2D(1, this.shape[1]);
            for (int i = 0; i < this.shape[1]; i++) {
                result.data[0][i] = this.max(i, 0);
            }
            return result;
        } else {
            final Tensor2D result = new Tensor2D(this.shape[0], 1);
            for (int i = 0; i < this.shape[0]; i++) {
                result.data[i][0] = this.max(i, 1);
            }
            return result;
        }
    }

    public float flatten(final int idx, final int axis) {
        if (axis == 0) {
            float sum = 0.0f;
            for (int i = 0; i < this.shape[0]; i++) {
                sum += this.data[i][idx];
            }
            return sum;
        } else {
            float sum = 0.0f;
            for (int j = 0; j < this.shape[1]; j++) {
                sum += this.data[idx][j];
            }
            return sum;
        }
    }

    public Tensor2D flatten(final int axis) {
        if (axis == 0) {
            final Tensor2D result = new Tensor2D(1, this.shape[1]);
            for (int i = 0; i < this.shape[1]; i++) {
                result.data[0][i] = this.flatten(i, 0);
            }
            return result;
        } else {
            final Tensor2D result = new Tensor2D(this.shape[0], 1);
            for (int i = 0; i < this.shape[0]; i++) {
                result.data[i][0] = this.flatten(i, 1);
            }
            return result;
        }
    }

    public Tensor2D zero() {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = 0.0f;
            }
        }
        return this;
    }

    public Tensor2D ones() {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = 1.0f;
            }
        }
        return this;
    }

    public Tensor2D fill(final float v) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = v;
            }
        }
        return this;
    }

    public Tensor2D identity() {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = (i == j) ? 1.0f : 0.0f;
            }
        }
        return this;
    }

    public Tensor2D swap(final int idx1, final int idx2, final int axis) {
        if (idx1 == idx2) {
            return this;
        } else if (axis == 0) {
            assert (idx1 >= 0 && idx1 < this.shape[0]);
            assert (idx2 >= 0 && idx2 < this.shape[0]);
            for (int i = 0; i < this.shape[1]; i++) {
                final float tmp = this.data[idx1][i];
                this.data[idx1][i] = this.data[idx2][i];
                this.data[idx2][i] = tmp;
            }
        } else {
            assert (idx1 >= 0 && idx1 < this.shape[1]);
            assert (idx2 >= 0 && idx2 < this.shape[1]);
            for (int j = 0; j < this.shape[0]; j++) {
                final float tmp = this.data[j][idx1];
                this.data[j][idx1] = this.data[j][idx2];
                this.data[j][idx2] = tmp;
            }
        }
        return this;
    }

    public Tensor2D mutate(final float rate, final float variance) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                if (Scalar.random(1.0f) < rate) {
                    m_i[j] += Scalar.randomGaussian() * variance;
                }
            }
        }
        return this;
    }

    public Tensor2D randomize() {
        return this.randomize(1.0f);
    }

    public Tensor2D randomize(final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = Scalar.random(-n, n);
            }
        }
        return this;
    }

    public Tensor2D arrange(final int axis) {
        if (axis == 0) {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] = i * this.shape[1] + j + 1;
                }
            }
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] = j * this.shape[0] + i + 1;
                }
            }
        }
        return this;
    }

    public Tensor2D chop(final float e) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = (Scalar.abs(m_i[j]) < e) ? 0.0f : m_i[j];
            }
        }
        return this;
    }

    public Tensor2D constrain(final float a, final float b) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = Scalar.constrain(m_i[j], a, b);
            }
        }
        return this;
    }

    public Tensor2D if_lt_then(final float p, final float a, final float b) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = Scalar.if_lt_then(m_i[j], p, a, b);
            }
        }
        return this;
    }

    public Tensor2D l2Norm(final int axis) {
        if (axis == 0) {
            for (int j = 0; j < this.shape[1]; j++) {
                float sum = 0.0f;
                for (int i = 0; i < this.shape[0]; i++) {
                    sum += this.data[i][j] * this.data[i][j];
                }
                final float w = 1.0f / Scalar.sqrt(sum);
                for (int i = 0; i < this.shape[0]; i++) {
                    this.data[i][j] *= w;
                }
            }
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                float sum = 0.0f;
                for (int j = 0; j < this.shape[1]; j++) {
                    sum += this.data[i][j] * this.data[i][j];
                }
                final float w = 1.0f / Scalar.sqrt(sum);
                for (int j = 0; j < this.shape[1]; j++) {
                    this.data[i][j] *= w;
                }
            }
        }
        return this;
    }

    public Tensor2D batchNorm(final float gamma, final float delta, final int axis) {
        if (axis == 0) {
            final Tensor2D avg = this.avg(0);
            final Tensor2D var_inv = this.var(0).add(Scalar.EPSILON).sqrt().pow(-1.0f);
            for (int i = 0; i < this.shape[0]; i++) {
                for (int j = 0; j < this.shape[1]; j++) {
                    final float x = (this.data[i][j] - avg.data[0][j]) * var_inv.data[0][j];
                    this.data[i][j] = gamma * x + delta;
                }
            }
        } else {
            final Tensor2D avg = this.avg(1);
            final Tensor2D var_inv = this.var(1).add(Scalar.EPSILON).sqrt().pow(-1.0f);
            for (int i = 0; i < this.shape[0]; i++) {
                for (int j = 0; j < this.shape[1]; j++) {
                    final float x = (this.data[i][j] - avg.data[i][0]) * var_inv.data[i][0];
                    this.data[i][j] = gamma * x + delta;
                }
            }
        }
        return this;
    }

    public Tensor2D add(final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] += n;
            }
        }
        return this;
    }

    // public Tensor2D add(final Tensor1D v, final int axis) {
    // if (axis == 0) {
    // assert (this.shape[1] == v.shape[0]);
    // for (int i = 0; i < this.shape[0]; i++) {
    // final float[] m_i = this.data[i];
    // final float[] b = v.data;
    // for (int j = 0; j < this.shape[1]; j++) {
    // m_i[j] += b[j];
    // }
    // }
    // } else {
    // assert (this.shape[0] == v.shape[0]);
    // for (int j = 0; j < this.shape[1]; j++) {
    // final float[] b = v.data;
    // for (int i = 0; i < this.shape[0]; i++) {
    // this.data[i][j] += b[i];
    // }
    // }
    // }
    // return this;
    // }

    public Tensor2D add(final Tensor2D a) {
        assert (this.shape[0] == a.shape[0] || this.shape[1] == a.shape[1]);
        if (this.shape[0] == a.shape[0] && this.shape[1] == a.shape[1]) {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] += a_i[j];
                }
            }
        } else if (this.shape[0] == a.shape[0]) {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] += a_i[j % a.shape[1]];
                }
            }
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i % a.shape[0]];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] += a_i[j];
                }
            }
        }
        return this;
    }

    public Tensor2D sub(final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] -= n;
            }
        }
        return this;
    }

    // public Tensor2D sub(final Tensor1D v, final int axis) {
    // if (axis == 0) {
    // assert (this.shape[1] == v.shape[0]);
    // for (int i = 0; i < this.shape[0]; i++) {
    // final float[] m_i = this.data[i];
    // final float[] b = v.data;
    // for (int j = 0; j < this.shape[1]; j++) {
    // m_i[j] -= b[j];
    // }
    // }
    // } else {
    // assert (this.shape[0] == v.shape[0]);
    // for (int j = 0; j < this.shape[1]; j++) {
    // final float[] b = v.data;
    // for (int i = 0; i < this.shape[0]; i++) {
    // this.data[i][j] -= b[i];
    // }
    // }
    // }
    // return this;
    // }

    public Tensor2D sub(final Tensor2D a) {
        assert (this.shape[0] == a.shape[0] || this.shape[1] == a.shape[1]);
        if (this.shape[0] == a.shape[0] && this.shape[1] == a.shape[1]) {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] -= a_i[j];
                }
            }
        } else if (this.shape[0] == a.shape[0]) {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] -= a_i[j % a.shape[1]];
                }
            }
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i % a.shape[0]];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] -= a_i[j];
                }
            }
        }
        return this;
    }

    public Tensor2D mul(final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] *= n;
            }
        }
        return this;
    }

    // public Tensor2D mul(final Tensor1D v, final int axis) {
    // if (axis == 0) {
    // assert (this.shape[1] == v.shape[0]);
    // for (int i = 0; i < this.shape[0]; i++) {
    // final float[] m_i = this.data[i];
    // final float[] b = v.data;
    // for (int j = 0; j < this.shape[1]; j++) {
    // m_i[j] *= b[j];
    // }
    // }
    // } else {
    // assert (this.shape[0] == v.shape[0]);
    // for (int j = 0; j < this.shape[1]; j++) {
    // final float[] b = v.data;
    // for (int i = 0; i < this.shape[0]; i++) {
    // this.data[i][j] *= b[i];
    // }
    // }
    // }
    // return this;
    // }

    public Tensor2D mul(final Tensor2D a) {
        assert (this.shape[0] == a.shape[0] || this.shape[1] == a.shape[1]);
        if (this.shape[0] == a.shape[0] && this.shape[1] == a.shape[1]) {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] *= a_i[j];
                }
            }
        } else if (this.shape[0] == a.shape[0]) {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] *= a_i[j % a.shape[1]];
                }
            }
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i % a.shape[0]];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] *= a_i[j];
                }
            }
        }
        return this;
    }

    public Tensor2D div(final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] /= n;
            }
        }
        return this;
    }

    // public Tensor2D div(final Tensor1D v, final int axis) {
    // if (axis == 0) {
    // assert (this.shape[1] == v.shape[0]);
    // for (int i = 0; i < this.shape[0]; i++) {
    // final float[] m_i = this.data[i];
    // final float[] b = v.data;
    // for (int j = 0; j < this.shape[1]; j++) {
    // m_i[j] /= b[j];
    // }
    // }
    // } else {
    // assert (this.shape[0] == v.shape[0]);
    // for (int j = 0; j < this.shape[1]; j++) {
    // final float[] b = v.data;
    // for (int i = 0; i < this.shape[0]; i++) {
    // this.data[i][j] /= b[i];
    // }
    // }
    // }
    // return this;
    // }

    public Tensor2D div(final Tensor2D a) {
        assert (this.shape[0] == a.shape[0] || this.shape[1] == a.shape[1]);
        if (this.shape[0] == a.shape[0] && this.shape[1] == a.shape[1]) {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] /= a_i[j];
                }
            }
        } else if (this.shape[0] == a.shape[0]) {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] /= a_i[j % a.shape[1]];
                }
            }
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i % a.shape[0]];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] /= a_i[j];
                }
            }
        }
        return this;
    }

    public Tensor2D expAvg(final Tensor2D a, final float n) {
        assert (this.shape[0] == a.shape[0] || this.shape[1] == a.shape[1]);
        if (this.shape[0] == a.shape[0] && this.shape[1] == a.shape[1]) {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] = n * m_i[j] + (1.0f - n) * a_i[j];
                }
            }
        } else if (this.shape[0] == a.shape[0]) {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] = n * m_i[j] + (1.0f - n) * a_i[j % a.shape[1]];
                }
            }
        } else {
            for (int i = 0; i < this.shape[0]; i++) {
                final float[] m_i = this.data[i];
                final float[] a_i = a.data[i % a.shape[0]];
                for (int j = 0; j < this.shape[1]; j++) {
                    m_i[j] = n * m_i[j] + (1.0f - n) * a_i[j];
                }
            }
        }
        return this;
    }

    public Tensor2D abs() {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = Scalar.abs(m_i[j]);
            }
        }
        return this;
    }

    public Tensor2D pow(final float n) {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = Scalar.pow(m_i[j], n);
            }
        }
        return this;
    }

    public Tensor2D sqrt() {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = Scalar.sqrt(m_i[j]);
            }
        }
        return this;
    }

    public Tensor2D invsqrt() {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = 1.0f / Scalar.sqrt(m_i[j]);
            }
        }
        return this;
    }

    public Tensor2D exp() {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = Scalar.exp(m_i[j]);
            }
        }
        return this;
    }

    public Tensor2D log() {
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = Scalar.log(m_i[j]);
            }
        }
        return this;
    }

    public Tensor2D fma(final Tensor2D m1, final Tensor2D m2) {
        Blas.fgemm(false, false, m1.data, 1.0f, m2.data, 1.0f, this.data);
        return this;
    }

    public Tensor2D fma(final Tensor2D m1, final Tensor2D m2, final boolean transposeA, final boolean transposeB) {
        Blas.fgemm(transposeA, transposeB, m1.data, 1.0f, m2.data, 1.0f, this.data);
        return this;
    }

    public Tensor2D fma(final Tensor2D m, final float n) {
        Blas.fgemm(false, false, m.data, n, null, 1.0f, this.data);
        return this;
    }

    public Tensor2D fma(final Tensor2D m, final float n, final boolean transpose) {
        Blas.fgemm(false, false, m.data, n, null, 1.0f, this.data);
        return this;
    }

    public Tensor2D pad(final int padding, final float value) {
        assert (padding > 0);
        final Tensor2D result = new Tensor2D(this.shape[0] + 2 * padding, this.shape[1] + 2 * padding).fill(value);
        for (int i = 0; i < this.shape[0]; i++) {
            for (int j = 0; j < this.shape[1]; j++) {
                result.data[padding + i][padding + j] = this.data[i][j];
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
            newRows = this.shape[0] * this.shape[1] / newCols;
        }
        if (newCols < 0) {
            newCols = this.shape[0] * this.shape[1] / newRows;
        }
        assert (this.shape[0] * this.shape[1] == newRows * newCols);
        if (newRows == this.shape[0] || newCols == this.shape[1]) {
            return this.copy();
        }
        final Tensor2D result = new Tensor2D(newRows, newCols);
        if (format == 'C') {
            for (int k = 0; k < this.shape[0] * this.shape[1]; k++) {
                final int i1 = k / this.shape[1];
                final int j1 = k % this.shape[1];
                final int i2 = k / result.shape[1];
                final int j2 = k % result.shape[1];
                result.data[i2][j2] = this.data[i1][j1];
            }

        } else { // 'F'
            for (int k = 0; k < this.shape[0] * this.shape[1]; k++) {
                final int i1 = k % this.shape[0];
                final int j1 = k / this.shape[0];
                final int i2 = k % result.shape[0];
                final int j2 = k / result.shape[0];
                result.data[i2][j2] = this.data[i1][j1];
            }
        }
        return result;
    }

    public Tensor3D reshape(final int newDepths, final int newRows, final int newCols) {
        return this.reshape(newDepths, newRows, newCols, 'C');
    }

    public Tensor3D reshape(int newDepths, int newRows, int newCols, final char format) {
        assert (newDepths > 0 || newRows > 0 || newCols > 0);
        if (newDepths < 0) {
            newDepths = this.shape[0] * this.shape[1] / (newRows * newCols);
        }
        if (newRows < 0) {
            newRows = this.shape[0] * this.shape[1] / (newDepths * newCols);
        }
        if (newCols < 0) {
            newCols = this.shape[0] * this.shape[1] / (newDepths * newRows);
        }
        assert (this.shape[0] * this.shape[1] == newDepths * newRows * newCols);
        final Tensor3D result = new Tensor3D(newDepths, newRows, newCols);
        if (format == 'C') {
            final int r_stride1 = result.shape[2] * result.shape[1];
            final int r_stride2 = result.shape[2];
            for (int i = 0; i < this.shape[0]; i++) {
                for (int j = 0; j < this.shape[1]; j++) {
                    final int m_off1 = this.shape[1] * i + j;
                    final float[][] r_i = result.data[m_off1 / r_stride1];
                    final int m_off2 = m_off1 % r_stride1;
                    final float[] r_ij = r_i[m_off2 / r_stride2];
                    r_ij[m_off2 % r_stride2] = this.data[i][j];
                }
            }
        } else { // 'F'
        }
        return result;
    }

    public Tensor2D conv(final Tensor2D f) {
        final int orow = f.shape[0] - 1;
        final int ocol = f.shape[1] - 1;
        final Tensor2D result = new Tensor2D(this.shape[0] - orow, this.shape[1] - ocol);
        for (int i = 0; i < this.shape[0] - orow; i++) {
            for (int j = 0; j < this.shape[1] - ocol; j++) {
                float acc = 0.0f;
                for (int y = 0; y < f.shape[0]; y++) {
                    final float[] f_y = f.data[y];
                    final float[] m_i = this.data[i + y];
                    for (int x = 0; x < f.shape[1]; x++) {
                        acc += f_y[x] * m_i[j + x];
                    }
                }
                result.data[i][j] = acc;
            }
        }
        return result;
    }

    public Tensor2D map(float start1, float stop1, float start2, float stop2) {
        final float m = (stop2 - start2) / (stop1 - start1);
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = m * (m_i[j] - start1) + start2;
            }
        }
        return this;
    }

    public Tensor2D map(final TensorFunction<Tensor2D> fn) {
        assert (fn != null);
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = fn.apply(m_i[j], new int[] { i, j }, this);
            }
        }
        return this;
    }

    public Tensor2D map(final TensorFunction<Tensor2D> fn, final Tensor2D other) {
        assert (fn != null);
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] m_i = this.data[i];
            for (int j = 0; j < this.shape[1]; j++) {
                m_i[j] = fn.apply(m_i[j], new int[] { i, j }, other);
            }
        }
        return this;
    }

    public Tensor2D copy() {
        return this.slice(0, 0);
    }

    public Tensor2D slice(final int a, final int b) {
        return this.slice(a, b, this.shape[0] - a, this.shape[1] - b);
    }

    public Tensor2D slice(int a, int b, int h, int w) {
        if (a < 0) {
            a = 0;
        }
        if (b < 0) {
            b = 0;
        }
        if (h < 0) {
            h = this.shape[0] - a;
        }
        if (w < 0) {
            w = this.shape[1] - b;
        }
        assert ((a + h) >= 0 && (a + h) <= this.shape[0]);
        assert ((b + w) >= 0 && (b + w) <= this.shape[1]);
        final Tensor2D result = new Tensor2D(h, w);
        for (int i = 0; i < h; i++) {
            System.arraycopy(this.data[a + i], b, result.data[i], 0, w);
        }
        return result;
    }

    public Tensor2D diagonal(final int axis) {
        if (axis == 0) {
            final Tensor2D result = new Tensor2D(1, this.shape[1]);
            for (int i = 0; i < this.shape[1]; i++) {
                result.data[0][i] = this.data[i][i];
            }
            return result;
        } else {
            final Tensor2D result = new Tensor2D(this.shape[0], 1);
            for (int i = 0; i < this.shape[0]; i++) {
                result.data[i][0] = this.data[i][i];
            }
            return result;
        }
    }

    public Tensor2D replace(final int idx, final Tensor1D v, final int axis) {
        if (axis == 0) {
            assert (this.shape[1] == v.shape[0]);
            System.arraycopy(v.data, 0, this.data[idx], 0, this.shape[1]);
            return this;
        } else {
            assert (this.shape[0] == v.shape[0]);
            for (int i = 0; i < this.shape[0]; i++) {
                this.data[i][idx] = v.data[i];
            }
            return this;
        }
    }

    public Tensor2D replace(final Tensor2D m) {
        return this.replace(0, 0, m);
    }

    public Tensor2D replace(final int a, final int b, final Tensor2D m) {
        assert ((a + m.shape[0]) >= 0 && (a + m.shape[0]) <= this.shape[0]);
        assert ((b + m.shape[1]) >= 0 && (b + m.shape[1]) <= this.shape[1]);
        for (int i = 0; i < m.shape[0]; i++) {
            System.arraycopy(m.data[i], 0, this.data[a + i], b, m.shape[1]);
        }
        return this;
    }

    public Tensor2D concatenate(final Tensor1D v, final int axis) {
        if (axis == 0) {
            assert (this.shape[1] == v.shape[0]);
            final Tensor2D result = new Tensor2D(this.shape[0] + 1, this.shape[1]);
            for (int i = 0; i < this.shape[0]; i++) {
                System.arraycopy(this.data[i], 0, result.data[i], 0, this.shape[1]);
            }
            System.arraycopy(v.data, 0, result.data[this.shape[0]], 0, this.shape[1]);
            return result;
        } else {
            assert (this.shape[0] == v.shape[0]);
            final Tensor2D result = new Tensor2D(this.shape[0], this.shape[1] + 1);
            for (int i = 0; i < this.shape[0]; i++) {
                System.arraycopy(this.data[i], 0, result.data[i], 0, this.shape[1]);
                result.data[i][this.shape[1]] = v.data[i];
            }
            return result;
        }
    }

    public Tensor2D concatenate(final Tensor2D m, final int axis) {
        if (axis == 0) {
            assert (this.shape[1] == m.shape[1]);
            final Tensor2D result = new Tensor2D(this.shape[0] + m.shape[0], this.shape[1]);
            for (int i = 0; i < this.shape[0]; i++) {
                System.arraycopy(this.data[i], 0, result.data[i], 0, this.shape[1]);
            }
            for (int i = 0; i < m.shape[0]; i++) {
                System.arraycopy(m.data[i], 0, result.data[this.shape[0] + i], 0, this.shape[1]);
            }
            return result;
        } else {
            assert (this.shape[0] == m.shape[0]);
            final Tensor2D result = new Tensor2D(this.shape[0], this.shape[1] + m.shape[1]);
            for (int i = 0; i < this.shape[0]; i++) {
                System.arraycopy(this.data[i], 0, result.data[i], 0, this.shape[1]);
                System.arraycopy(m.data[i], 0, result.data[i], this.shape[1], m.shape[1]);
            }
            return result;
        }
    }

    public Tensor2D transpose() {
        final Tensor2D result = new Tensor2D(this.shape[1], this.shape[0]);
        for (int i = 0; i < result.shape[0]; i++) {
            final float[] r_i = result.data[i];
            for (int j = 0; j < result.shape[1]; j++) {
                r_i[j] = this.data[j][i];
            }
        }
        return result;
    }

    public Tensor1D matmul(final Tensor1D v) {
        final Tensor1D result = new Tensor1D(this.shape[0], 0.0f);
        Blas.fgemv(false, this.data, 1.0f, v.data, 1.0f, result.data);
        return result;
    }

    public Tensor2D matmul(final Tensor2D m) {
        final Tensor2D result = new Tensor2D(this.shape[0], m.shape[1]);
        Blas.fgemm(false, false, this.data, 1.0f, m.data, 0.0f, result.data);
        return result;
    }

    public Tensor2D matmul(final Tensor2D m, final boolean transposeA, final boolean transposeB) {
        final int rows = transposeA ? this.shape[1] : this.shape[0];
        final int cols = transposeB ? m.shape[0] : m.shape[1];
        final Tensor2D result = new Tensor2D(rows, cols);
        Blas.fgemm(transposeA, transposeB, this.data, 1.0f, m.data, 0.0f, result.data);
        return result;
    }

    public Tensor2D minor(final int a, final int b) {
        final Tensor2D result = new Tensor2D(this.shape[0] - 1, this.shape[1] - 1);
        for (int i = 0; i < result.shape[0]; i++) {
            final float[] r_i = result.data[i];
            if (i < a) {
                final float[] m_i = this.data[i];
                for (int j = 0; j < result.shape[1]; j++) {
                    if (j < b) {
                        r_i[j] = m_i[j];
                    } else {
                        r_i[j] = m_i[j + 1];
                    }
                }
            } else {
                final float[] m_i = this.data[i + 1];
                for (int j = 0; j < result.shape[1]; j++) {
                    if (j < b) {
                        r_i[j] = m_i[j];
                    } else {
                        r_i[j] = m_i[j + 1];
                    }
                }
            }
        }
        return result;
    }

    public Tensor2D cof() {
        final Tensor2D result = new Tensor2D(this.shape[0], this.shape[1]);
        float b = 1.0f;
        for (int i = 0; i < result.shape[0]; i++) {
            final float[] r_i = result.data[i];
            float a = b;
            for (int j = 0; j < result.shape[1]; j++) {
                r_i[j] = a * this.minor(i, j).det();
                a *= -1.0;
            }
            b *= -1.0;
        }
        return result;
    }

    public Tensor2D adj() {
        final Tensor2D result = new Tensor2D(this.shape[1], this.shape[0]);
        float b = 1.0f;
        for (int i = 0; i < result.shape[0]; i++) {
            final float[] r_i = result.data[i];
            float a = b;
            for (int j = 0; j < result.shape[1]; j++) {
                r_i[j] = a * this.minor(j, i).det();
                a *= -1.0;
            }
            b *= -1.0;
        }
        return result;
    }

    public Tensor2D inv() {
        final float d = this.det();
        assert (d != 0.0f);
        final Tensor2D result = new Tensor2D(this.shape[0], this.shape[1]);
        float b = 1.0f / d;
        for (int i = 0; i < result.shape[0]; i++) {
            final float[] r_i = result.data[i];
            float a = b;
            for (int j = 0; j < result.shape[1]; j++) {
                r_i[j] = a * this.minor(j, i).det();
                a *= -1.0f;
            }
            b *= -1.0f;
        }
        return result;
    }

    // public Tensor1D toVector(final int idx) {
    // return this.toVector(idx, true);
    // }

    // public Tensor1D toVector(final int idx, final boolean rowvar) {
    // if (rowvar) {
    // return this.get(idx);
    // } else {
    // final Tensor1D result = new Tensor1D(this.shape[0]);
    // for (int i = 0; i < this.shape[0]; i++) {
    // result.data[i] = this.data[i][idx];
    // }
    // return result;
    // }
    // }

    // public Tensor1D[] toVectorArray() {
    // return this.toVectorArray(true);
    // }

    // public Tensor1D[] toVectorArray(final boolean rowvar) {
    // if (rowvar) {
    // final Tensor1D[] result = new Tensor1D[this.shape[0]];
    // for (int i = 0; i < this.shape[0]; i++) {
    // result[i] = this.get(i);
    // }
    // return result;
    // } else {
    // final Tensor1D[] result = new Tensor1D[this.shape[1]];
    // for (int j = 0; j < this.shape[1]; j++) {
    // result[j] = new Tensor1D(this.shape[0]);
    // for (int i = 0; i < this.shape[0]; i++) {
    // result[j].data[i] = this.data[i][j];
    // }
    // }
    // return result;
    // }
    // }

    public String toString() {
        if (this.shape[0] == 0 || this.shape[1] == 0) {
            return "||";
        }

        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] a = this.data[i];
            result.append("| ");
            for (int j = 0; j < this.shape[1]; j++) {
                result.append(String.format("%1$10.3f ", a[j]));
            }
            result.append("|").append(System.lineSeparator());
        }

        return result.toString();
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();

        if (this.shape[0] == 0 || this.shape[1] == 0) {
            return json;
        }

        final JSONArray jsonData = JSON.newJSONArray();
        for (int i = 0; i < this.shape[0]; i++) {
            final float[] a = this.data[i];
            final JSONArray jsonRow = JSON.newJSONArray();
            for (int j = 0; j < this.shape[1]; j++) {
                jsonRow.append(a[j]);
            }
            jsonData.append(jsonRow);
        }

        json.setInt("shape[0]", this.shape[0]);
        json.setInt("shape[1]", this.shape[1]);
        json.setJSONArray("data", jsonData);
        return json;
    }
}
