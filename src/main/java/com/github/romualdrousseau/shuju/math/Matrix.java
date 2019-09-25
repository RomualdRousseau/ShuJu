package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class Matrix {
    private int rows;
    private int cols;
    private float[][] data;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new float[this.rows][this.cols];
    }

    public Matrix(int rows, int cols, float v) {
        this.rows = rows;
        this.cols = cols;
        this.data = new float[this.rows][this.cols];
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = v;
            }
        }
    }

    public Matrix(float[] v) {
        this.rows = v.length;
        this.cols = 1;
        this.data = new float[this.rows][this.cols];
        for (int i = 0; i < this.rows; i++) {
            this.data[i][0] = v[i];
        }
    }

    public Matrix(float[][] v) {
        this.rows = v.length;
        this.cols = v[0].length;
        this.data = v;
        for (int i = 0; i < this.rows; i++) {
            System.arraycopy(v[i], 0, this.data[i], 0, this.cols);
        }
    }

    public Matrix(Float[] v) {
        this.rows = v.length;
        this.cols = 1;
        this.data = new float[this.rows][this.cols];
        for (int i = 0; i < this.rows; i++) {
            this.data[i][0] = v[i];
        }
    }

    public Matrix(Float[][] v) {
        this.rows = v.length;
        this.cols = v[0].length;
        this.data = new float[this.rows][this.cols];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.data[i][j] = v[i][j];
            }
        }
    }

    public Matrix(Vector v) {
        this.rows = v.rowCount();
        this.cols = 1;
        this.data = new float[this.rows][this.cols];
        for (int i = 0; i < this.rows; i++) {
            this.data[i][0] = v.get(i);
        }
    }

    public Matrix(Vector[] v) {
        this.rows = v.length;
        this.cols = v[0].rowCount();
        this.data = new float[this.rows][this.cols];
        for (int i = 0; i < this.rows; i++) {
            System.arraycopy(v[i].getFloats(), 0, this.data[i], 0, this.cols);
        }
    }

    public Matrix(JSONObject json) {
        this.rows = json.getInt("rows");
        this.cols = json.getInt("cols");
        this.data = new float[this.rows][this.cols];
        JSONArray jsonData = json.getJSONArray("data");
        for (int i = 0; i < this.rows; i++) {
            JSONArray jsonRow = jsonData.getJSONArray(i);
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

    public float get(int row, int col) {
        return this.data[row][col];
    }

    public Matrix set(int row, int col, float v) {
        this.data[row][col] = v;
        return this;
    }

    public Vector get(int row) {
        return new Vector(this.data[row]);
    }

    public Matrix set(int row, Vector v) {
        if (this.cols != v.rowCount()) {
            throw new IllegalArgumentException("column of A must match cardinality of B.");
        }
        this.data[row] = v.getFloats();
        return this;
    }

    public boolean equals(final Matrix m) {
        boolean result = this.rows == m.rows && this.cols == m.cols;
        for (int i = 0; i < this.rows && result; i++) {
            float[] a = this.data[i];
            float[] b = m.data[i];
            result &= Arrays.equals(a, b);
        }
        return result;
    }

    public float sparsity() {
        int count = 0;
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                count += (a[j] == 0.0) ? 1 : 0;
            }
        }
        return (float) count / (float) (this.rows * this.cols);
    }

    public int argmin(int col) {
        int result = 0;
        float minValue = this.data[0][col];
        for (int i = 1; i < this.rows; i++) {
            if (this.data[i][col] < minValue) {
                minValue = this.data[i][col];
                result = i;
            }
        }
        return result;
    }

    public int argmax(int col) {
        int result = 0;
        float maxValue = this.data[0][col];
        for (int i = 1; i < this.rows; i++) {
            if (this.data[i][col] > maxValue) {
                maxValue = this.data[i][col];
                result = i;
            }
        }
        return result;
    }

    public float avg(int col) {
        float sum = 0.0f;
        for (int i = 0; i < this.rows; i++) {
            sum += this.data[i][col];
        }
        return sum / (float) this.rows;
    }

    public Matrix avg() {
        Matrix result = new Matrix(this.cols, 1);
        for (int i = 0; i < this.cols; i++) {
            result.data[i][0] = this.avg(i);
        }
        return result;
    }

    public float var(int col) {
        float avg = this.avg(col);
        float var = 0.0f;
        for (int i = 0; i < this.rows; i++) {
            float tmp = this.data[i][col] - avg;
            var += tmp * tmp;
        }
        return var / (float) (this.rows - 1);
    }

    public Matrix var() {
        Matrix result = new Matrix(this.cols, 1);
        for (int i = 0; i < this.cols; i++) {
            result.data[i][0] = this.var(i);
        }
        return result;
    }

    public float cov(int col1, int col2) {
        float avg1 = this.avg(col1);
        float avg2 = this.avg(col2);
        float cov = 0.0f;
        for (int i = 0; i < this.rows; i++) {
            float tmp1 = this.data[i][col1] - avg1;
            float tmp2 = this.data[i][col2] - avg2;
            cov += tmp1 * tmp2;
        }
        return cov / (float) (this.rows - 1);
    }

    public float cov(Matrix m, int col) {
        assert (this.rows == m.rows);

        float avg1 = this.avg(col);
        float avg2 = m.avg(col);
        float cov = 0.0f;
        for (int i = 0; i < this.rows; i++) {
            float tmp1 = this.data[i][col] - avg1;
            float tmp2 = m.data[i][col] - avg2;
            cov += tmp1 * tmp2;
        }
        return cov / (float) (this.rows - 1);
    }

    public Matrix cov(Matrix m) {
        Matrix result = new Matrix(this.cols, 1);
        for (int i = 0; i < this.cols; i++) {
            result.data[i][0] = this.cov(m, i);
        }
        return result;
    }

    public float min(int col) {
        float minValue = this.data[0][col];
        for (int i = 1; i < this.rows; i++) {
            if (this.data[i][col] < minValue) {
                minValue = this.data[i][col];
            }
        }
        return minValue;
    }

    public Matrix min() {
        Matrix result = new Matrix(this.cols, 1);
        for (int i = 0; i < this.cols; i++) {
            result.data[i][0] = this.min(i);
        }
        return result;
    }

    public float max(int col) {
        float maxValue = this.data[0][col];
        for (int i = 1; i < this.rows; i++) {
            if (this.data[i][col] > maxValue) {
                maxValue = this.data[i][col];
            }
        }
        return maxValue;
    }

    public Matrix max() {
        Matrix result = new Matrix(this.cols, 1);
        for (int i = 0; i < this.cols; i++) {
            result.data[i][0] = this.max(i);
        }
        return result;
    }

    public float flatten(int col) {
        float sum = 0.0f;
        for (int i = 0; i < this.rows; i++) {
            sum += this.data[i][col];
        }
        return sum;
    }

    public Matrix flatten() {
        Matrix sum = new Matrix(1, this.cols);
        for (int i = 0; i < this.cols; i++) {
            sum.set(0, i, this.flatten(i));
        }
        return sum;
    }

    public Matrix zero() {
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = 0.0f;
            }
        }
        return this;
    }

    public Matrix ones() {
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = 1.0f;
            }
        }
        return this;
    }

    public Matrix identity() {
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = (i == j) ? 1.0f : 0.0f;
            }
        }
        return this;
    }

    public Matrix mutate(float rate, float variance) {
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
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

    public Matrix randomize(float n) {
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = Scalar.random(-n, n);
            }
        }
        return this;
    }

    public Matrix constrain(float a, float b) {
        for (int i = 0; i < this.rows; i++) {
            float[] c = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                c[j] = Scalar.constrain(c[j], a, b);
            }
        }
        return this;
    }

    public Matrix cond(float p, float a, float b) {
        for (int i = 0; i < this.rows; i++) {
            float[] c = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                c[j] = Scalar.cond(c[j], p, a, b);
            }
        }
        return this;
    }

    public Matrix l2Norm() {
        for (int j = 0; j < this.cols; j++) {
            float sum = 0.0f;
            for (int i = 0; i < this.rows; i++) {
                sum += this.data[i][j] * this.data[i][j];
            }
            float w = 1.0f / Scalar.sqrt(sum);

            for (int i = 0; i < this.rows; i++) {
                this.data[i][j] *= w;
            }
        }
        return this;
    }

    public Matrix batchNorm(float a, float b) {
        for (int j = 0; j < this.cols; j++) {
            float avg = 0.0f;
            for (int i = 0; i < this.rows; i++) {
                avg += this.data[i][j];
            }
            avg /= (float) this.rows;

            float var = 0.0f;
            for (int i = 0; i < this.rows; i++) {
                float x = (this.data[i][j] - avg);
                var += x * x;
            }
            var /= (float) this.rows;

            for (int i = 0; i < this.rows; i++) {
                float x = (this.data[i][j] - avg) / Scalar.sqrt(var + Scalar.EPSILON);
                this.data[i][j] = a * x + b;
            }
        }
        return this;
    }

    public Matrix add(final Matrix m) {
        assert (this.rows == m.rows && this.cols == m.cols);

        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            float[] b = m.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] += b[j];
            }
        }
        return this;
    }

    public Matrix sub(final Matrix m) {
        assert (this.rows == m.rows && this.cols == m.cols);

        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            float[] b = m.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] -= b[j];
            }
        }
        return this;
    }

    public Matrix mult(float n) {
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] *= n;
            }
        }
        return this;
    }

    public Matrix mult(final Matrix m) {
        assert (this.rows == m.rows && this.cols == m.cols);

        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            float[] b = m.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] *= b[j];
            }
        }
        return this;
    }

    public Matrix div(float n) {
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] /= n;
            }
        }
        return this;
    }

    public Matrix div(final Matrix m) {
        assert (this.rows == m.rows && this.cols == m.cols);

        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            float[] b = m.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] /= b[j];
            }
        }
        return this;
    }

    public Matrix abs() {
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = Scalar.abs(a[j]);
            }
        }
        return this;
    }

    public Matrix pow(float n) {
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = Scalar.pow(a[j], n);
            }
        }
        return this;
    }

    public Matrix sqrt() {
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = Scalar.sqrt(a[j]);
            }
        }
        return this;
    }

    public Matrix exp() {
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = Scalar.exp(a[j]);
            }
        }
        return this;
    }

    public Matrix log() {
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
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
            float[] c = this.data[i];
            for (int k = 0; k < m1.cols; k++) {
                float a = m1.data[i][k];
                float[] b = m2.data[k];
                for (int j = 0; j < this.cols; j++) {
                    c[j] = a * b[j] + c[j];
                }
            }
        }
        return this;
    }

    public Matrix fma(final Matrix m1, final Matrix m2, boolean transposeA, boolean transposeB) {
        final int colsA = transposeA ? m1.rows : m1.cols;
        final int rowsA = transposeA ? m1.cols : m1.rows;
        final int colsB = transposeB ? m2.rows : m2.cols;
        final int rowsB = transposeB ? m2.cols : m2.rows;

        assert (this.rows == rowsA && this.cols == colsB);
        assert (colsA == rowsB);

        if (transposeA && transposeB) {
            for (int i = 0; i < this.rows; i++) {
                float[] c = this.data[i];
                for (int k = 0; k < colsA; k++) {
                    float a = m1.data[k][i];
                    float[][] b = m2.data;
                    for (int j = 0; j < this.cols; j++) {
                        c[j] = a * b[j][k] + c[j];
                    }
                }
            }
        } else if (transposeA && !transposeB) {
            for (int i = 0; i < this.rows; i++) {
                float[] c = this.data[i];
                for (int k = 0; k < colsA; k++) {
                    float a = m1.data[k][i];
                    float[] b = m2.data[k];
                    for (int j = 0; j < this.cols; j++) {
                        c[j] = a * b[j] + c[j];
                    }
                }
            }
        } else if (!transposeA && transposeB) {
            for (int i = 0; i < this.rows; i++) {
                float[] c = this.data[i];
                float[] a = m1.data[i];
                for (int j = 0; j < this.cols; j++) {
                    float[] b = m2.data[j];
                    for (int k = 0; k < colsA; k++) {
                        c[j] = a[k] * b[k] + c[j];
                    }
                }
            }
        } else {
            for (int i = 0; i < this.rows; i++) {
                float[] c = this.data[i];
                for (int k = 0; k < colsA; k++) {
                    float a = m1.data[i][k];
                    float[] b = m2.data[k];
                    for (int j = 0; j < this.cols; j++) {
                        c[j] = a * b[j] + c[j];
                    }
                }
            }
        }
        return this;
    }

    public Matrix fma(final Matrix m, float n) {
        assert (this.rows == m.rows && this.cols == m.cols);

        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            float[] b = m.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = b[j] * n + a[j];
            }
        }
        return this;
    }

    public Matrix fma(final Matrix m, float n, boolean transpose) {
        final int cols = transpose ? m.rows : m.cols;
        final int rows = transpose ? m.cols : m.rows;

        assert (this.rows == rows && this.cols == cols);

        if (transpose) {
            for (int i = 0; i < this.rows; i++) {
                float[] a = this.data[i];
                float[] b = m.data[i];
                for (int j = 0; j < this.cols; j++) {
                    a[j] = b[i] * n + a[j];
                }
            }
        } else {
            for (int i = 0; i < this.rows; i++) {
                float[] a = this.data[i];
                float[] b = m.data[i];
                for (int j = 0; j < this.cols; j++) {
                    a[j] = b[j] * n + a[j];
                }
            }
        }
        return this;
    }

    public Matrix map(MatrixFunction<Float, Float> fn) {
        assert (fn != null);

        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = fn.apply(a[j], i, j, this);
            }
        }
        return this;
    }

    public Matrix map(MatrixFunction<Float, Float> fn, Matrix other) {
        assert (fn != null);

        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            for (int j = 0; j < this.cols; j++) {
                a[j] = fn.apply(a[j], i, j, other);
            }
        }
        return this;
    }

    public Matrix copy() {
        Matrix result = new Matrix(this.rows, this.cols);
        for (int i = 0; i < result.rows; i++) {
            System.arraycopy(this.data[i], 0, result.data[i], 0, this.cols);
        }
        return result;
    }

    public Matrix concat(Matrix m) {
        assert (this.rows == m.rows);

        Matrix result = new Matrix(this.rows, this.cols + m.cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                result.data[i][j] = this.data[i][j];
            }
            for (int j = 0; j < m.cols; j++) {
                result.data[i][this.cols + j] = m.data[i][j];
            }
        }
        return result;
    }

    public Matrix squarify(boolean diagonal) {
        Matrix result = new Matrix(this.rows, this.rows);
        if (this.rows == this.cols) {
            for (int i = 0; i < this.rows; i++) {
                float[] a = this.data[i];
                float[] b = result.data[i];
                for (int j = 0; j < this.cols; j++) {
                    b[j] = a[j];
                }
            }
        } else if (diagonal) {
            for (int i = 0; i < this.rows; i++) {
                result.data[i][i] = this.data[i][0];
            }
        } else {
            for (int i = 0; i < this.rows; i++) {
                float[] a = this.data[i];
                float[] b = result.data[i];
                for (int j = 0; j < this.rows; j++) {
                    b[j] = a[0];
                }
            }
        }
        return result;
    }

    public Matrix transpose() {
        Matrix result = new Matrix(this.cols, this.rows);
        for (int i = 0; i < result.rows; i++) {
            float[] b = result.data[i];
            for (int j = 0; j < result.cols; j++) {
                b[j] = this.data[j][i];
            }
        }
        return result;
    }

    public Matrix transform(final Matrix m) {
        assert (this.cols == m.rows);

        Matrix result = new Matrix(this.rows, m.cols, 0.0f);
        for (int i = 0; i < result.rows; i++) {
            float[] c = result.data[i];
            for (int k = 0; k < this.cols; k++) {
                float a = this.data[i][k];
                float[] b = m.data[k];
                for (int j = 0; j < result.cols; j++) {
                    c[j] = a * b[j] + c[j];
                }
            }
        }
        return result;
    }

    public Matrix transform(final Matrix m, boolean transposeA, boolean transposeB) {
        final int colsA = transposeA ? this.rows : this.cols;
        final int rowsA = transposeA ? this.cols : this.rows;
        final int colsB = transposeB ? m.rows : m.cols;
        final int rowsB = transposeB ? m.cols : m.rows;

        assert (colsA == rowsB);

        Matrix result = new Matrix(rowsA, colsB, 0.0f);
        if (transposeA && transposeB) {
            for (int i = 0; i < result.rows; i++) {
                float[] c = result.data[i];
                for (int k = 0; k < colsA; k++) {
                    float a = this.data[k][i];
                    float[][] b = m.data;
                    for (int j = 0; j < result.cols; j++) {
                        c[j] = a * b[j][k] + c[j];
                    }
                }
            }
        } else if (transposeA && !transposeB) {
            for (int i = 0; i < result.rows; i++) {
                float[] c = result.data[i];
                for (int k = 0; k < colsA; k++) {
                    float a = this.data[k][i];
                    float[] b = m.data[k];
                    for (int j = 0; j < result.cols; j++) {
                        c[j] = a * b[j] + c[j];
                    }
                }
            }
        } else if (!transposeA && transposeB) {
            for (int i = 0; i < result.rows; i++) {
                float[] c = result.data[i];
                float[] a = this.data[i];
                for (int j = 0; j < result.cols; j++) {
                    float[] b = m.data[j];
                    for (int k = 0; k < colsA; k++) {
                        c[j] = a[k] * b[k] + c[j];
                    }
                }
            }
        } else {
            for (int i = 0; i < result.rows; i++) {
                float[] c = result.data[i];
                for (int k = 0; k < colsA; k++) {
                    float a = this.data[i][k];
                    float[] b = m.data[k];
                    for (int j = 0; j < result.cols; j++) {
                        c[j] = a * b[j] + c[j];
                    }
                }
            }
        }

        return result;
    }

    public Vector toVector(int col) {
        Vector result = new Vector(this.rows);
        for (int i = 0; i < this.rows; i++) {
            result.set(i, this.data[i][col]);
        }
        return result;
    }

    public String toString() {
        if (this.rows == 0 || this.cols == 0) {
            return "||";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            result.append("| ");
            for (int j = 0; j < this.cols - 1; j++) {
                result.append(String.format("%.5f\t", a[j]));
            }
            result.append(String.format("%.5f", a[this.cols - 1]));
            result.append(" |").append(System.lineSeparator());
        }

        return result.toString();
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();

        if (this.rows == 0 || this.cols == 0) {
            return json;
        }

        JSONArray jsonData = JSON.newJSONArray();
        for (int i = 0; i < this.rows; i++) {
            float[] a = this.data[i];
            JSONArray jsonRow = JSON.newJSONArray();
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
