package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class Matrix {
    protected int rows;
    protected int cols;
    protected float[][] data;

    public Matrix(final int rows, final int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new float[this.rows][this.cols];
    }

    public Matrix(final int rows, final int cols, final float v) {
        this.rows = rows;
        this.cols = cols;
        this.data = new float[this.rows][this.cols];
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = v;
            }
        }
    }

    public Matrix(final float[] v) {
        this(v, true);
    }

    public Matrix(final float[] v, final boolean rowvar) {
        if (rowvar) {
            this.rows = 1;
            this.cols = v.length;
            this.data = new float[this.rows][this.cols];
            System.arraycopy(v, 0, this.data[0], 0, this.cols);
        } else {
            this.rows = v.length;
            this.cols = 1;
            this.data = new float[this.rows][this.cols];
            for (int i = 0; i < this.rows; i++) {
                this.data[i][0] = v[i];
            }
        }
    }

    public Matrix(final float[][] v) {
        this(v, true);
    }

    public Matrix(final float[][] v, final boolean rowvar) {
        if (rowvar) {
            this.rows = v.length;
            this.cols = v[0].length;
            this.data = new float[this.rows][this.cols];
            for (int i = 0; i < this.rows; i++) {
                System.arraycopy(v[i], 0, this.data[i], 0, this.cols);
            }
        } else {
            this.rows = v[0].length;
            this.cols = v.length;
            this.data = new float[this.rows][this.cols];
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < this.cols; j++) {
                    this.data[i][j] = v[j][i];
                }
            }
        }
    }

    public Matrix(final Float[] v) {
        this(v, true);
    }

    public Matrix(final Float[] v, final boolean rowvar) {
        if (rowvar) {
            this.rows = 1;
            this.cols = v.length;
            this.data = new float[this.rows][this.cols];
            for (int j = 0; j < this.cols; j++) {
                this.data[0][j] = v[j];
            }
        } else {
            this.rows = v.length;
            this.cols = 1;
            this.data = new float[this.rows][this.cols];
            for (int i = 0; i < this.rows; i++) {
                this.data[i][0] = v[i];
            }
        }
    }

    public Matrix(final Float[][] v) {
        this(v, true);
    }

    public Matrix(final Float[][] v, final boolean rowvar) {
        if (rowvar) {
            this.rows = v.length;
            this.cols = v[0].length;
            this.data = new float[this.rows][this.cols];
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < this.cols; j++) {
                    this.data[i][j] = v[i][j];
                }
            }
        } else {
            this.rows = v[0].length;
            this.cols = v.length;
            this.data = new float[this.rows][this.cols];
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < this.cols; j++) {
                    this.data[i][j] = v[j][i];
                }
            }
        }
    }

    public Matrix(final Vector v) {
        this(v, true);
    }

    public Matrix(final Vector v, final boolean rowvar) {
        if (rowvar) {
            this.rows = 1;
            this.cols = v.rows;
            this.data = new float[this.rows][this.cols];
            for (int j = 0; j < this.cols; j++) {
                this.data[0][j] = v.data[j];
            }
        } else {
            this.rows = v.rows;
            this.cols = 1;
            this.data = new float[this.rows][this.cols];
            for (int i = 0; i < this.rows; i++) {
                this.data[i][0] = v.data[i];
            }
        }
    }

    public Matrix(final Vector[] v) {
        this(v, true);
    }

    public Matrix(final Vector[] v, final boolean rowvar) {
        if (rowvar) {
            this.rows = v.length;
            this.cols = v[0].rows;
            this.data = new float[this.rows][this.cols];
            for (int i = 0; i < this.rows; i++) {
                System.arraycopy(v[i].data, 0, this.data[i], 0, this.cols);
            }
        } else {
            this.rows = v[0].rows;
            this.cols = v.length;
            this.data = new float[this.rows][this.cols];
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; i < this.cols; j++) {
                    this.data[i][j] = v[j].data[i];
                }
            }
        }
    }

    public Matrix(final Vector v, final int stride) {
        this(v, stride, true);
    }

    public Matrix(final Vector v, final int stride, final boolean rowvar) {
        assert (stride > 0);
        if (rowvar) {
            this.rows = v.rows / stride;
            this.cols = stride;
            this.data = new float[this.rows][this.cols];
            int vi = 0;
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < this.cols; j++) {
                    this.data[i][j] = v.data[vi++];
                }
            }
        } else {
            this.rows = stride;
            this.cols = v.rows / stride;
            this.data = new float[this.rows][this.cols];
            int vi = 0;
            for (int j = 0; j < this.cols; j++) {
                for (int i = 0; i < this.rows; i++) {
                    this.data[i][j] = v.data[vi++];
                }
            }
        }
    }

    public Matrix(final JSONObject json) {
        this.rows = json.getInt("rows");
        this.cols = json.getInt("cols");
        this.data = new float[this.rows][this.cols];
        final JSONArray jsonData = json.getJSONArray("data");
        for (int i = 0; i < this.rows; i++) {
            final JSONArray jsonRow = jsonData.getJSONArray(i);
            for (int j = 0; j < this.cols; j++) {
                this.data[i][j] = jsonRow.getFloat(j);
            }
        }
    }

    public boolean isNull() {
        return this.rows == 0 || this.cols == 0;
    }

    public int rowCount() {
        return this.rows;
    }

    public int colCount() {
        return this.cols;
    }

    public float[][] getFloats() {
        return this.data;
    }

    public float get(final int row, final int col) {
        return this.data[row][col];
    }

    public Matrix set(final int row, final int col, final float v) {
        this.data[row][col] = v;
        return this;
    }

    public Vector get(final int row) {
        return new Vector(this.data[row]);
    }

    public Matrix set(final int row, final Vector v) {
        assert (this.cols == v.rows);
        System.arraycopy(v.data, 0, this.data[row], 0, this.cols);
        return this;
    }

    public boolean equals(final Matrix m) {
        boolean result = this.rows == m.rows && this.cols == m.cols;
        for (int i = 0; i < this.rows && result; i++) {
            final float[] a = this.data[i];
            final float[] b = m.data[i];
            result &= Arrays.equals(a, b);
        }
        return result;
    }

    public boolean equals(final Matrix m, final float e) {
        boolean result = this.rows == m.rows && this.cols == m.cols;
        for (int i = 0; i < this.rows && result; i++) {
            final float[] a = this.data[i];
            final float[] b = m.data[i];
            for (int j = 0; j < this.cols && result; j++) {
                result &= Math.abs(a[j] - b[j]) < e;
            }
        }
        return result;
    }

    public boolean isSquared() {
        return this.rows == this.cols;
    }

    public boolean isUpper(final int offset, final float e) {
        int n = Math.min(this.rows, this.cols);
        assert(offset >= 0 && offset < n);
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
        int n = Math.min(this.rows, this.cols);
        assert(offset >= 0 && offset < n);
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
        int n = Math.min(this.rows, this.cols);
        if(offset >= 0) {
            assert(offset < n);
            for (int i = 0; i < n - offset; i++) {
                for (int j = 0; j < n - offset; j++) {
                    if (j != i && Math.abs(this.data[i + offset][j]) >= e) {
                        return false;
                    }
                }
            }
        } else {
            assert(offset > -n);
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
        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.cols; j++) {
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

    public Matrix reshape(final int newRows, final int axis) {
        return this.reshape(newRows, this.rows * this.cols / newRows, axis);
    }

    public Matrix reshape(final int newRows, final int newCols, final int axis) {
        assert (newRows > 0 && newCols > 0);
        assert (this.rows * this.cols == newRows * newCols);
        final Matrix result = new Matrix(newRows, newCols);
        if (axis == 0) {
            for (int k = 0; k < this.rows * this.cols; k++) {
                final int i1 = k % this.rows;
                final int j1 = k / this.rows;
                final int i2 = k % result.rows;
                final int j2 = k / result.rows;
                result.data[i2][j2] = this.data[i1][j1];
            }
        } else {
            for (int k = 0; k < this.rows * this.cols; k++) {
                final int i1 = k / this.cols;
                final int j1 = k % this.cols;
                final int i2 = k / result.cols;
                final int j2 = k % result.cols;
                result.data[i2][j2] = this.data[i1][j1];
            }
        }
        return result;
    }

    public float det() {
        assert (this.isSquared());
        if (this.rows == 2) {
            return this.data[0][0] * this.data[1][1] - this.data[0][1] * this.data[1][0];
        } else {
            float sum = 0.0f;
            float a = 1.0f;
            for (int j = 0; j < this.cols; j++) {
                sum += a * this.data[0][j] * this.minor(0, j).det();
                a *= -1.0f;
            }
            return sum;
        }
    }

    public float sparsity() {
        int count = 0;
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                count += (a[j] == 0.0) ? 1 : 0;
            }
        }
        return (float) count / (float) (this.rows * this.cols);
    }

    public int argmin(final int idx, final int axis) {
        if (axis == 0) {
            int result = 0;
            float minValue = this.data[0][idx];
            for (int i = 1; i < this.rows; i++) {
                if (this.data[i][idx] < minValue) {
                    minValue = this.data[i][idx];
                    result = i;
                }
            }
            return result;
        } else {
            int result = 0;
            float minValue = this.data[idx][0];
            for (int i = 1; i < this.cols; i++) {
                if (this.data[idx][i] < minValue) {
                    minValue = this.data[idx][i];
                    result = i;
                }
            }
            return result;
        }
    }

    public Matrix argmin(final int axis) {
        if (axis == 0) {
            final Matrix result = new Matrix(1, this.cols);
            for (int i = 0; i < this.cols; i++) {
                result.data[0][i] = this.argmin(i, 0);
            }
            return result;
        } else {
            final Matrix result = new Matrix(this.rows, 1);
            for (int i = 0; i < this.rows; i++) {
                result.data[i][0] = this.argmin(i, 1);
            }
            return result;
        }
    }

    public int argmax(final int idx, final int axis) {
        if (axis == 0) {
            int result = 0;
            float maxValue = this.data[0][idx];
            for (int i = 1; i < this.rows; i++) {
                if (this.data[i][idx] > maxValue) {
                    maxValue = this.data[i][idx];
                    result = i;
                }
            }
            return result;
        } else {
            int result = 0;
            float maxValue = this.data[idx][0];
            for (int i = 1; i < this.cols; i++) {
                if (this.data[idx][i] > maxValue) {
                    maxValue = this.data[idx][i];
                    result = i;
                }
            }
            return result;
        }
    }

    public Matrix argmax(final int axis) {
        if (axis == 0) {
            final Matrix result = new Matrix(1, this.cols);
            for (int i = 0; i < this.cols; i++) {
                result.data[0][i] = this.argmax(i, 0);
            }
            return result;
        } else {
            final Matrix result = new Matrix(this.rows, 1);
            for (int i = 0; i < this.rows; i++) {
                result.data[i][0] = this.argmax(i, 1);
            }
            return result;
        }
    }

    public float avg(final int idx, final int axis) {
        if (axis == 0) {
            float sum = 0.0f;
            for (int i = 0; i < this.rows; i++) {
                sum += this.data[i][idx];
            }
            return sum / (float) this.rows;
        } else {
            float sum = 0.0f;
            for (int i = 0; i < this.cols; i++) {
                sum += this.data[idx][i];
            }
            return sum / (float) this.cols;
        }
    }

    public Matrix avg(final int axis) {
        if (axis == 0) {
            final Matrix result = new Matrix(1, this.cols);
            for (int i = 0; i < this.cols; i++) {
                result.data[0][i] = this.avg(i, 0);
            }
            return result;
        } else {
            final Matrix result = new Matrix(this.rows, 1);
            for (int i = 0; i < this.rows; i++) {
                result.data[i][0] = this.avg(i, 1);
            }
            return result;
        }
    }

    public float var(final int idx, final int axis) {
        if (axis == 0) {
            final float avg = this.avg(idx, 0);
            float var = 0.0f;
            for (int i = 0; i < this.rows; i++) {
                final float tmp = this.data[i][idx] - avg;
                var += tmp * tmp;
            }
            return var / (float) (this.rows - 1);
        } else {
            final float avg = this.avg(idx, 1);
            float var = 0.0f;
            for (int i = 0; i < this.cols; i++) {
                final float tmp = this.data[idx][i] - avg;
                var += tmp * tmp;
            }
            return var / (float) (this.cols - 1);
        }
    }

    public Matrix var(final int axis) {
        if (axis == 0) {
            final Matrix result = new Matrix(1, this.cols);
            for (int i = 0; i < this.cols; i++) {
                result.data[0][i] = this.var(i, 0);
            }
            return result;
        } else {
            final Matrix result = new Matrix(this.rows, 1);
            for (int i = 0; i < this.rows; i++) {
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
            for (int i = 0; i < this.rows; i++) {
                final float tmp1 = this.data[i][idx1] - avg1;
                final float tmp2 = this.data[i][idx2] - avg2;
                cov += tmp1 * tmp2;
            }
            return cov / (float) (this.rows - 1);
        } else {
            final float avg1 = this.avg(idx1, 1);
            final float avg2 = this.avg(idx2, 1);
            float cov = 0.0f;
            for (int i = 0; i < this.cols; i++) {
                final float tmp1 = this.data[idx1][i] - avg1;
                final float tmp2 = this.data[idx2][i] - avg2;
                cov += tmp1 * tmp2;
            }
            return cov / (float) (this.cols - 1);
        }
    }

    public Matrix cov(final int axis) {
        if (axis == 0) {
            final Matrix result = new Matrix(this.cols, this.cols);
            for (int i = 0; i < this.cols; i++) {
                for (int j = 0; j < this.cols; j++) {
                    result.data[i][j] = this.cov(i, j, 0);
                }
            }
            return result;
        } else {
            final Matrix result = new Matrix(this.rows, this.rows);
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < this.rows; j++) {
                    result.data[i][j] = this.cov(i, j, 1);
                }
            }
            return result;
        }
    }

    public float cov(final Matrix m, final int idx, final int axis) {
        return this.cov(m, idx, idx, axis);
    }

    public float cov(final Matrix m, final int idx1, final int idx2, final int axis) {
        if (axis == 0) {
            assert (this.rows == m.rows);
            final float avg1 = this.avg(idx1, 0);
            final float avg2 = m.avg(idx2, 0);
            float cov = 0.0f;
            for (int i = 0; i < this.rows; i++) {
                final float tmp1 = this.data[i][idx1] - avg1;
                final float tmp2 = m.data[i][idx2] - avg2;
                cov += tmp1 * tmp2;
            }
            return cov / (float) (this.rows - 1);
        } else {
            assert (this.cols == m.cols);
            final float avg1 = this.avg(idx1, 1);
            final float avg2 = m.avg(idx2, 1);
            float cov = 0.0f;
            for (int i = 0; i < this.cols; i++) {
                final float tmp1 = this.data[idx1][i] - avg1;
                final float tmp2 = m.data[idx2][i] - avg2;
                cov += tmp1 * tmp2;
            }
            return cov / (float) (this.cols - 1);
        }
    }

    public Matrix cov(final Matrix m, final int axis, final boolean full) {
        if (full) {
            if (axis == 0) {
                final Matrix result = new Matrix(this.cols, this.cols);
                for (int i = 0; i < this.cols; i++) {
                    for (int j = 0; j < this.cols; j++) {
                        result.data[i][j] = this.cov(m, i, j, 0);
                    }
                }
                return result;
            } else {
                final Matrix result = new Matrix(this.rows, this.rows);
                for (int i = 0; i < this.rows; i++) {
                    for (int j = 0; j < this.rows; j++) {
                        result.data[i][j] = this.cov(m, i, j, 1);
                    }
                }
                return result;
            }
        } else {
            if (axis == 0) {
                final Matrix result = new Matrix(1, this.cols);
                for (int i = 0; i < this.cols; i++) {
                    result.data[0][i] = this.cov(m, i, 0);
                }
                return result;
            } else {
                final Matrix result = new Matrix(this.rows, 1);
                for (int i = 0; i < this.rows; i++) {
                    result.data[i][0] = this.cov(m, i, 1);
                }
                return result;
            }
        }
    }

    public float min(final int idx, final int axis) {
        if (axis == 0) {
            float minValue = this.data[0][idx];
            for (int i = 1; i < this.rows; i++) {
                if (this.data[i][idx] < minValue) {
                    minValue = this.data[i][idx];
                }
            }
            return minValue;
        } else {
            float minValue = this.data[idx][0];
            for (int i = 1; i < this.cols; i++) {
                if (this.data[idx][i] < minValue) {
                    minValue = this.data[idx][i];
                }
            }
            return minValue;
        }
    }

    public Matrix min(final int axis) {
        if (axis == 0) {
            final Matrix result = new Matrix(1, this.cols);
            for (int i = 0; i < this.cols; i++) {
                result.data[0][i] = this.min(i, 0);
            }
            return result;
        } else {
            final Matrix result = new Matrix(this.rows, 1);
            for (int i = 0; i < this.rows; i++) {
                result.data[i][0] = this.min(i, 1);
            }
            return result;
        }
    }

    public float max(final int idx, final int axis) {
        if (axis == 0) {
            float maxValue = this.data[0][idx];
            for (int i = 1; i < this.rows; i++) {
                if (this.data[i][idx] > maxValue) {
                    maxValue = this.data[i][idx];
                }
            }
            return maxValue;
        } else {
            float maxValue = this.data[idx][0];
            for (int i = 1; i < this.cols; i++) {
                if (this.data[idx][i] > maxValue) {
                    maxValue = this.data[idx][i];
                }
            }
            return maxValue;
        }
    }

    public Matrix max(final int axis) {
        if (axis == 0) {
            final Matrix result = new Matrix(1, this.cols);
            for (int i = 0; i < this.cols; i++) {
                result.data[0][i] = this.max(i, 0);
            }
            return result;
        } else {
            final Matrix result = new Matrix(this.rows, 1);
            for (int i = 0; i < this.rows; i++) {
                result.data[i][0] = this.max(i, 1);
            }
            return result;
        }
    }

    public float flatten(final int idx, final int axis) {
        if (axis == 0) {
            float sum = 0.0f;
            for (int i = 0; i < this.rows; i++) {
                sum += this.data[i][idx];
            }
            return sum;
        } else {
            float sum = 0.0f;
            for (int i = 0; i < this.cols; i++) {
                sum += this.data[idx][i];
            }
            return sum;
        }
    }

    public Matrix flatten(final int axis) {
        if (axis == 0) {
            final Matrix result = new Matrix(1, this.cols);
            for (int i = 0; i < this.cols; i++) {
                result.data[0][i] = this.flatten(i, 0);
            }
            return result;
        } else {
            final Matrix result = new Matrix(this.rows, 1);
            for (int i = 0; i < this.rows; i++) {
                result.data[i][0] = this.flatten(i, 1);
            }
            return result;
        }
    }

    public Matrix diagonal(final int axis) {
        if (axis == 0) {
            final Matrix result = new Matrix(1, this.cols);
            for (int i = 0; i < this.cols; i++) {
                result.data[0][i] = this.data[i][i];
            }
            return result;
        } else {
            final Matrix result = new Matrix(this.rows, 1);
            for (int i = 0; i < this.rows; i++) {
                result.data[i][0] = this.data[i][i];
            }
            return result;
        }
    }

    public Matrix zero() {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = 0.0f;
            }
        }
        return this;
    }

    public Matrix ones() {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = 1.0f;
            }
        }
        return this;
    }

    public Matrix identity() {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = (i == j) ? 1.0f : 0.0f;
            }
        }
        return this;
    }

    public Matrix mutate(final float rate, final float variance) {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                if (Scalar.random(1.0f) < rate) {
                    a[j] += Scalar.randomGaussian() * variance;
                }
            }
        }
        return this;
    }

    public Matrix randomize() {
        return this.randomize(1.0f);
    }

    public Matrix randomize(final float n) {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = Scalar.random(-n, n);
            }
        }
        return this;
    }

    public Matrix chop(final float e) {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = (Scalar.abs(a[j]) < e) ? 0.0f : a[j];
            }
        }
        return this;
    }

    public Matrix constrain(final float a, final float b) {
        for (int i = 0; i < this.rows; i++) {
            final float[] c = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                c[j] = Scalar.constrain(c[j], a, b);
            }
        }
        return this;
    }

    public Matrix if_lt_then(final float p, final float a, final float b) {
        for (int i = 0; i < this.rows; i++) {
            final float[] c = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                c[j] = Scalar.if_lt_then(c[j], p, a, b);
            }
        }
        return this;
    }

    public Matrix l2Norm(final int axis) {
        if (axis == 0) {
            for (int j = 0; j < this.cols; j++) {
                float sum = 0.0f;
                for (int i = 0; i < this.rows; i++) {
                    sum += this.data[i][j] * this.data[i][j];
                }
                final float w = 1.0f / Scalar.sqrt(sum);
                for (int i = 0; i < this.rows; i++) {
                    this.data[i][j] *= w;
                }
            }
        } else {
            for (int i = 0; i < this.rows; i++) {
                float sum = 0.0f;
                for (int j = 0; j < this.cols; j++) {
                    sum += this.data[i][j] * this.data[i][j];
                }
                final float w = 1.0f / Scalar.sqrt(sum);
                for (int j = 0; j < this.cols; j++) {
                    this.data[i][j] *= w;
                }
            }
        }
        return this;
    }

    public Matrix batchNorm(final float a, final float b, final int axis) {
        if (axis == 0) {
            for (int j = 0; j < this.cols; j++) {
                float avg = this.avg(j, 0);
                float var = this.var(j, 0);
                for (int i = 0; i < this.rows; i++) {
                    final float x = (this.data[i][j] - avg) / Scalar.sqrt(var + Scalar.EPSILON);
                    this.data[i][j] = a * x + b;
                }
            }
        } else {
            for (int i = 0; i < this.rows; i++) {
                float avg = this.avg(i, 1);
                float var = this.var(i, 1);
                for (int j = 0; j < this.cols; j++) {
                    final float x = (this.data[i][j] - avg) / Scalar.sqrt(var + Scalar.EPSILON);
                    this.data[i][j] = a * x + b;
                }
            }
        }
        return this;
    }

    public Matrix add(final float n) {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] += n;
            }
        }
        return this;
    }

    public Matrix add(final Vector v, final int axis) {
        if (axis == 0) {
            assert (this.cols == v.rows);
            for (int i = 0; i < this.rows; i++) {
                final float[] a = this.data[i];
                final float[] b = v.data;
                for (int j = 0; j < this.cols; j++) {
                    a[j] += b[j];
                }
            }
        } else {
            assert (this.rows == v.rows);
            for (int j = 0; j < this.cols; j++) {
                final float[] b = v.data;
                for (int i = 0; j < this.rows; i++) {
                    this.data[i][j] += b[i];
                }
            }
        }
        return this;
    }

    public Matrix add(final Matrix m) {
        assert (this.rows == m.rows && this.cols == m.cols);
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            final float[] b = m.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] += b[j];
            }
        }
        return this;
    }

    public Matrix sub(final float n) {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] -= n;
            }
        }
        return this;
    }

    public Matrix sub(final Vector v, final int axis) {
        if (axis == 0) {
            assert (this.cols == v.rows);
            for (int i = 0; i < this.rows; i++) {
                final float[] a = this.data[i];
                final float[] b = v.data;
                for (int j = 0; j < this.cols; j++) {
                    a[j] -= b[j];
                }
            }
        } else {
            assert (this.rows == v.rows);
            for (int j = 0; j < this.cols; j++) {
                final float[] b = v.data;
                for (int i = 0; j < this.rows; i++) {
                    this.data[i][j] -= b[i];
                }
            }
        }
        return this;
    }

    public Matrix sub(final Matrix m) {
        assert (this.rows == m.rows && this.cols == m.cols);
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            final float[] b = m.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] -= b[j];
            }
        }
        return this;
    }

    public Matrix mul(final float n) {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] *= n;
            }
        }
        return this;
    }

    public Matrix mul(final Vector v, final int axis) {
        if (axis == 0) {
            assert (this.cols == v.rows);
            for (int i = 0; i < this.rows; i++) {
                final float[] a = this.data[i];
                final float[] b = v.data;
                for (int j = 0; j < this.cols; j++) {
                    a[j] *= b[j];
                }
            }
        } else {
            assert (this.rows == v.rows);
            for (int j = 0; j < this.cols; j++) {
                final float[] b = v.data;
                for (int i = 0; j < this.rows; i++) {
                    this.data[i][j] *= b[i];
                }
            }
        }
        return this;
    }

    public Matrix mul(final Matrix m) {
        assert (this.rows == m.rows && this.cols == m.cols);
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            final float[] b = m.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] *= b[j];
            }
        }
        return this;
    }

    public Matrix div(final float n) {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] /= n;
            }
        }
        return this;
    }

    public Matrix div(final Vector v, final int axis) {
        if (axis == 0) {
            assert (this.cols == v.rows);
            for (int i = 0; i < this.rows; i++) {
                final float[] a = this.data[i];
                final float[] b = v.data;
                for (int j = 0; j < this.cols; j++) {
                    a[j] /= b[j];
                }
            }
        } else {
            assert (this.rows == v.rows);
            for (int j = 0; j < this.cols; j++) {
                final float[] b = v.data;
                for (int i = 0; j < this.rows; i++) {
                    this.data[i][j] /= b[i];
                }
            }
        }
        return this;
    }

    public Matrix div(final Matrix m) {
        assert (this.rows == m.rows && this.cols == m.cols);
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            final float[] b = m.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] /= b[j];
            }
        }
        return this;
    }

    public Matrix abs() {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = Scalar.abs(a[j]);
            }
        }
        return this;
    }

    public Matrix pow(final float n) {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = Scalar.pow(a[j], n);
            }
        }
        return this;
    }

    public Matrix sqrt() {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = Scalar.sqrt(a[j]);
            }
        }
        return this;
    }

    public Matrix exp() {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = Scalar.exp(a[j]);
            }
        }
        return this;
    }

    public Matrix log() {
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = Scalar.log(a[j]);
            }
        }
        return this;
    }

    public Matrix fma(final Matrix m1, final Matrix m2) {
        assert (this.rows == m1.rows && this.cols == m2.cols);
        assert (m1.cols == m2.rows);
        for (int i = 0; i < this.rows; i++) {
            final float[] c = this.data[i];
            for (int k = 0; k < m1.cols; k++) {
                final float a = m1.data[i][k];
                final float[] b = m2.data[k];
                for (int j = 0; j < this.cols; j++) {
                    c[j] = a * b[j] + c[j];
                }
            }
        }
        return this;
    }

    public Matrix fma(final Matrix m1, final Matrix m2, final boolean transposeA, final boolean transposeB) {
        final int colsA = transposeA ? m1.rows : m1.cols;
        final int rowsA = transposeA ? m1.cols : m1.rows;
        final int colsB = transposeB ? m2.rows : m2.cols;
        final int rowsB = transposeB ? m2.cols : m2.rows;

        assert (this.rows == rowsA && this.cols == colsB);
        assert (colsA == rowsB);

        if (transposeA && transposeB) {
            for (int i = 0; i < this.rows; i++) {
                final float[] c = this.data[i];
                for (int k = 0; k < colsA; k++) {
                    final float a = m1.data[k][i];
                    final float[][] b = m2.data;
                    for (int j = 0; j < this.cols; j++) {
                        c[j] = a * b[j][k] + c[j];
                    }
                }
            }
        } else if (transposeA && !transposeB) {
            for (int i = 0; i < this.rows; i++) {
                final float[] c = this.data[i];
                for (int k = 0; k < colsA; k++) {
                    final float a = m1.data[k][i];
                    final float[] b = m2.data[k];
                    for (int j = 0; j < this.cols; j++) {
                        c[j] = a * b[j] + c[j];
                    }
                }
            }
        } else if (!transposeA && transposeB) {
            for (int i = 0; i < this.rows; i++) {
                final float[] c = this.data[i];
                final float[] a = m1.data[i];
                for (int j = 0; j < this.cols; j++) {
                    final float[] b = m2.data[j];
                    for (int k = 0; k < colsA; k++) {
                        c[j] = a[k] * b[k] + c[j];
                    }
                }
            }
        } else {
            for (int i = 0; i < this.rows; i++) {
                final float[] c = this.data[i];
                for (int k = 0; k < colsA; k++) {
                    final float a = m1.data[i][k];
                    final float[] b = m2.data[k];
                    for (int j = 0; j < this.cols; j++) {
                        c[j] = a * b[j] + c[j];
                    }
                }
            }
        }
        return this;
    }

    public Matrix fma(final Matrix m, final float n) {
        assert (this.rows == m.rows && this.cols == m.cols);

        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            final float[] b = m.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = b[j] * n + a[j];
            }
        }
        return this;
    }

    public Matrix fma(final Matrix m, final float n, final boolean transpose) {
        final int cols = transpose ? m.rows : m.cols;
        final int rows = transpose ? m.cols : m.rows;

        assert (this.rows == rows && this.cols == cols);

        if (transpose) {
            for (int i = 0; i < this.rows; i++) {
                final float[] a = this.data[i];
                final float[] b = m.data[i];
                for (int j = 0; j < this.cols; j++) {
                    a[j] = b[i] * n + a[j];
                }
            }
        } else {
            for (int i = 0; i < this.rows; i++) {
                final float[] a = this.data[i];
                final float[] b = m.data[i];
                for (int j = 0; j < this.cols; j++) {
                    a[j] = b[j] * n + a[j];
                }
            }
        }
        return this;
    }

    public Matrix map(final MatrixFunction<Float, Float> fn) {
        assert (fn != null);
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = fn.apply(a[j], i, j, this);
            }
        }
        return this;
    }

    public Matrix map(final MatrixFunction<Float, Float> fn, final Matrix other) {
        assert (fn != null);
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = fn.apply(a[j], i, j, other);
            }
        }
        return this;
    }

    public Matrix copy() {
        return this.copy(0, 0);
    }

    public Matrix copy(final int a, final int b) {
        return this.copy(a, b, this.rows - a, this.cols - b);
    }

    public Matrix copy(final int a, final int b, final int h, final int w) {
        final Matrix result = new Matrix(h, w);
        for (int i = 0; i < h; i++) {
            System.arraycopy(this.data[a + i], b, result.data[i], 0, w);
        }
        return result;
    }

    public Matrix concat(final Vector v, final int axis) {
        if (axis == 0) {
            assert (this.cols == v.rows);
            final Matrix result = new Matrix(this.rows + 1, this.cols);
            for (int i = 0; i < this.rows; i++) {
                System.arraycopy(this.data[i], 0, result.data[i], 0, this.cols);
            }
            System.arraycopy(v.data, 0, result.data[this.rows], 0, this.cols);
            return result;
        } else {
            assert (this.rows == v.rows);
            final Matrix result = new Matrix(this.rows, this.cols + 1);
            for (int i = 0; i < this.rows; i++) {
                System.arraycopy(this.data[i], 0, result.data[i], 0, this.cols);
                result.data[i][this.cols] = v.data[i];
            }
            return result;
        }
    }

    public Matrix concat(final Matrix m, final int axis) {
        if (axis == 0) {
            assert (this.cols == m.cols);
            final Matrix result = new Matrix(this.rows + m.rows, this.cols);
            for (int i = 0; i < this.rows; i++) {
                System.arraycopy(this.data[i], 0, result.data[i], 0, this.cols);
            }
            for (int i = 0; i < m.rows; i++) {
                System.arraycopy(m.data[i], 0, result.data[this.rows + i], 0, this.cols);
            }
            return result;
        } else {
            assert (this.rows == m.rows);
            final Matrix result = new Matrix(this.rows, this.cols + m.cols);
            for (int i = 0; i < this.rows; i++) {
                System.arraycopy(this.data[i], 0, result.data[i], 0, this.cols);
                System.arraycopy(m.data[i], 0, result.data[i], this.cols, m.cols);
            }
            return result;
        }
    }

    public Matrix transpose() {
        final Matrix result = new Matrix(this.cols, this.rows);
        for (int i = 0; i < result.rows; i++) {
            final float[] b = result.data[i];
            for (int j = 0; j < result.cols; j++) {
                b[j] = this.data[j][i];
            }
        }
        return result;
    }

    public Vector matmul(Vector v) {
        assert (v.rows == this.cols);
        Vector result = new Vector(v.rows, 0.0f);
        for (int i = 0; i < result.rows; i++) {
            float c = 0.0f;
            for (int k = 0; k < this.cols; k++) {
                float a = this.data[i][k];
                float b = v.data[k];
                c += a * b;
            }
            result.data[i] = c;
        }
        return result;
    }

    public Matrix matmul(final Matrix m) {
        assert (this.cols == m.rows);
        final Matrix result = new Matrix(this.rows, m.cols, 0.0f);
        for (int i = 0; i < result.rows; i++) {
            final float[] c = result.data[i];
            for (int k = 0; k < this.cols; k++) {
                final float a = this.data[i][k];
                final float[] b = m.data[k];
                for (int j = 0; j < result.cols; j++) {
                    c[j] = a * b[j] + c[j];
                }
            }
        }
        return result;
    }

    public Matrix matmul(final Matrix m, final boolean transposeA, final boolean transposeB) {
        final int colsA = transposeA ? this.rows : this.cols;
        final int rowsA = transposeA ? this.cols : this.rows;
        final int colsB = transposeB ? m.rows : m.cols;
        final int rowsB = transposeB ? m.cols : m.rows;

        assert (colsA == rowsB);

        final Matrix result = new Matrix(rowsA, colsB, 0.0f);
        if (transposeA && transposeB) {
            for (int i = 0; i < result.rows; i++) {
                final float[] c = result.data[i];
                for (int k = 0; k < colsA; k++) {
                    final float a = this.data[k][i];
                    final float[][] b = m.data;
                    for (int j = 0; j < result.cols; j++) {
                        c[j] = a * b[j][k] + c[j];
                    }
                }
            }
        } else if (transposeA && !transposeB) {
            for (int i = 0; i < result.rows; i++) {
                final float[] c = result.data[i];
                for (int k = 0; k < colsA; k++) {
                    final float a = this.data[k][i];
                    final float[] b = m.data[k];
                    for (int j = 0; j < result.cols; j++) {
                        c[j] = a * b[j] + c[j];
                    }
                }
            }
        } else if (!transposeA && transposeB) {
            for (int i = 0; i < result.rows; i++) {
                final float[] c = result.data[i];
                final float[] a = this.data[i];
                for (int j = 0; j < result.cols; j++) {
                    final float[] b = m.data[j];
                    for (int k = 0; k < colsA; k++) {
                        c[j] = a[k] * b[k] + c[j];
                    }
                }
            }
        } else {
            for (int i = 0; i < result.rows; i++) {
                final float[] c = result.data[i];
                for (int k = 0; k < colsA; k++) {
                    final float a = this.data[i][k];
                    final float[] b = m.data[k];
                    for (int j = 0; j < result.cols; j++) {
                        c[j] = a * b[j] + c[j];
                    }
                }
            }
        }

        return result;
    }

    public Matrix minor(final int a, final int b) {
        final Matrix result = new Matrix(this.rows - 1, this.cols - 1);
        for (int i = 0; i < result.rows; i++) {
            for (int j = 0; j < result.cols; j++) {
                if (i < a) {
                    if (j < b) {
                        result.data[i][j] = this.data[i][j];
                    } else {
                        result.data[i][j] = this.data[i][j + 1];
                    }
                } else {
                    if (j < b) {
                        result.data[i][j] = this.data[i + 1][j];
                    } else {
                        result.data[i][j] = this.data[i + 1][j + 1];
                    }
                }
            }
        }
        return result;
    }

    public Matrix cof() {
        final Matrix result = new Matrix(this.rows, this.cols);
        float b = 1.0f;
        for (int i = 0; i < result.rows; i++) {
            float a = b;
            for (int j = 0; j < result.cols; j++) {
                result.data[i][j] = a * this.minor(i, j).det();
                a *= -1.0;
            }
            b *= -1.0;
        }
        return result;
    }

    public Matrix adj() {
        final Matrix result = new Matrix(this.cols, this.rows);
        float b = 1.0f;
        for (int i = 0; i < result.rows; i++) {
            float a = b;
            for (int j = 0; j < result.cols; j++) {
                result.data[i][j] = a * this.minor(j, i).det();
                a *= -1.0;
            }
            b *= -1.0;
        }
        return result;
    }

    public Matrix inv() {
        final float d = this.det();
        assert (d != 0.0f);

        final Matrix result = new Matrix(this.rows, this.cols);
        float b = 1.0f / d;
        for (int i = 0; i < result.rows; i++) {
            float a = b;
            for (int j = 0; j < result.cols; j++) {
                result.data[i][j] = a * this.minor(j, i).det();
                a *= -1.0f;
            }
            b *= -1.0f;
        }
        return result;
    }

    public Vector toVector(final int idx) {
        return this.toVector(idx, true);
    }

    public Vector toVector(final int idx, final boolean rowvar) {
        if (rowvar) {
            return this.get(idx);
        } else {
            final Vector result = new Vector(this.rows);
            for (int i = 0; i < this.rows; i++) {
                result.data[i] = this.data[i][idx];
            }
            return result;
        }
    }

    public Vector[] toVectorArray() {
        return this.toVectorArray(true);
    }

    public Vector[] toVectorArray(final boolean rowvar) {
        if (rowvar) {
            final Vector[] result = new Vector[this.rows];
            for (int i = 0; i < this.rows; i++) {
                result[i] = this.get(i);
            }
            return result;
        } else {
            final Vector[] result = new Vector[this.cols];
            for (int j = 0; j < this.cols; j++) {
                result[j] = new Vector(this.rows);
                for (int i = 0; i < this.rows; i++) {
                    result[j].data[i] = this.data[i][j];
                }
            }
            return result;
        }
    }

    public String toString() {
        if (this.rows == 0 || this.cols == 0) {
            return "||";
        }

        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            result.append("| ");
            for (int j = 0; j < this.cols; j++) {
                result.append(String.format("%.3f\t", a[j]));
            }
            result.append(" |").append(System.lineSeparator());
        }

        return result.toString();
    }

    public JSONObject toJSON() {
        final JSONObject json = JSON.newJSONObject();

        if (this.rows == 0 || this.cols == 0) {
            return json;
        }

        final JSONArray jsonData = JSON.newJSONArray();
        for (int i = 0; i < this.rows; i++) {
            final float[] a = this.data[i];
            final JSONArray jsonRow = JSON.newJSONArray();
            for (int j = 0; j < this.cols; j++) {
                jsonRow.append(a[j]);
            }
            jsonData.append(jsonRow);
        }

        json.setInt("rows", this.rows);
        json.setInt("cols", this.cols);
        json.setJSONArray("data", jsonData);
        return json;
    }
}
