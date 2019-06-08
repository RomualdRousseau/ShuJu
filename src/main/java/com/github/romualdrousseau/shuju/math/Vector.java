package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class Vector {
    private int rows;
    private float[] data;

    public Vector(int rows) {
        this.rows = rows;
        this.data = new float[this.rows];
    }

    public Vector(int rows, float v) {
        this.rows = rows;
        this.data = new float[this.rows];
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = v;
        }
    }

    public Vector(float[] v) {
        this.rows = v.length;
        this.data = new float[this.rows];
        System.arraycopy(v, 0, this.data, 0, this.rows);
    }

    public Vector(Float[] v) {
        this.rows = v.length;
        this.data = new float[this.rows];
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = v[i];
        }
    }

    public Vector(JSONObject json) {
        this.rows = json.getInt("rows");
        this.data = new float[this.rows];
        JSONArray jsonData = json.getJSONArray("data");
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = jsonData.getFloat(i);
        }
    }

    // @Deprecated
    // public Vector(JSONArray json) {
    //     this.rows = json.size();
    //     this.data = new float[this.rows];
    //     for (int i = 0; i < this.rows; i++) {
    //         this.data[i] = json.getFloat(i);
    //     }
    // }

    public boolean isNull() {
        return this.rows == 0;
    }

    public int rowCount() {
        return this.rows;
    }

    public float[] getFloats() {
        return this.data;
    }

    public float get(int i) {
        return this.data[i];
    }

    public Vector set(int i, float v) {
        this.data[i] = v;
        return this;
    }

    public boolean equals(final Vector v) {
        return this.rows == v.rows && Arrays.equals(this.data, v.data);
    }

    public float isSimilar(final Vector v) {
        return this.scalar(v) / (v.norm() * this.norm());
    }

    public float sparsity() {
        int count = 0;
        for (int i = 0; i < this.rows; i++) {
            count += (this.data[i] == 0.0) ? 1 : 0;
        }
        return (float) count / (float) this.rows;
    }

    public float avg() {
        float sum = 0.0f;
        for (int i = 0; i < this.rows; i++) {
            sum += this.data[i];
        }
        return sum / (float) this.rows;
    }

    public float var() {
        float avg = this.avg();
        float var = 0.0f;
        for (int i = 0; i < this.rows; i++) {
            float tmp = this.data[i] - avg;
            var += tmp * tmp;
        }
        return var / (float) (this.rows - 1);
    }

    public float cov(Vector v) {
        assert (this.rows == v.rows);

        float avg1 = this.avg();
        float avg2 = v.avg();
        float cov = 0.0f;
        for (int i = 0; i < this.rows; i++) {
            float tmp1 = this.data[i] - avg1;
            float tmp2 = v.data[i] - avg2;
            cov += tmp1 * tmp2;
        }
        return cov / (float) (this.rows - 1);
    }

    public Vector min(Vector v) {
        assert (this.rows == v.rows);

        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Math.min(this.data[i], v.data[i]);
        }
        return this;
    }

    public float min() {
        float minValue = this.data[0];
        for (int i = 1; i < this.rows; i++) {
            if (this.data[i] < minValue) {
                minValue = this.data[i];
            }
        }
        return minValue;
    }

    public Vector max(Vector v) {
        assert (this.rows == v.rows);

        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Math.max(this.data[i], v.data[i]);
        }
        return this;
    }

    public float max() {
        float maxValue = this.data[0];
        for (int i = 1; i < this.rows; i++) {
            if (this.data[i] > maxValue) {
                maxValue = this.data[i];
            }
        }
        return maxValue;
    }

    public int argmin() {
        int result = 0;
        float minValue = this.data[0];
        for (int i = 1; i < this.rows; i++) {
            if (this.data[i] < minValue) {
                minValue = this.data[i];
                result = i;
            }
        }
        return result;
    }

    public int argmax() {
        int result = 0;
        float maxValue = this.data[0];
        for (int i = 1; i < this.rows; i++) {
            if (this.data[i] > maxValue) {
                maxValue = this.data[i];
                result = i;
            }
        }
        return result;
    }

    public float flatten() {
        float sum = 0.0f;
        for (int i = 0; i < this.rows; i++) {
            sum += this.data[i];
        }
        return sum;
    }

    public Vector zero() {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = 0.0f;
        }
        return this;
    }

    public Vector ones() {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = 1.0f;
        }
        return this;
    }

    public Vector oneHot(int k) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = (i == k) ? 1.0f : 0.0f;
        }
        return this;
    }

    public <T extends Enum<T>> Vector oneHot(T e) {
        if (e == null) {
            return this.zero();
        }

        return this.oneHot(e.ordinal());
    }

    public <T extends Enum<T>> Vector oneHot(T[] s) {
        if (s == null) {
            return this.zero();
        }

        for (int i = 0; i < this.rows; i++) {
            this.data[i] = (i == s[i].ordinal()) ? 1.0f : 0.0f;
        }
        return this;
    }

    public Vector mutate(float rate, float variance) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] += Scalar.randomGaussian() * variance;
        }
        return this;
    }

    public Vector randomize() {
        return this.randomize(1.0f);
    }

    public Vector randomize(float n) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Scalar.random(-n, n);
        }
        return this;
    }

    public Vector constrain(float a, float b) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Scalar.constrain(this.data[i], a, b);
        }
        return this;
    }

    public Vector cond(float p, float a, float b) {
        for (int j = 0; j < this.rows; j++) {
            this.data[j] = Scalar.cond(this.data[j], p, a, b);
        }
        return this;
    }

    public Vector softmax() {
        final float c = -this.data[this.argmax()];

        float sum = 0.0f;
        for (int k = 0; k < this.rowCount(); k++) {
            sum += Scalar.exp(this.data[k] + c);
        }
        final float w = 1.0f / sum;

        for (int k = 0; k < this.rowCount(); k++) {
            this.data[k] = Scalar.exp(this.data[k] + c) * w;
        }

        return this;
    }

    public Vector l2Norm() {
        float w = 1.0f / this.norm();
        for (int i = 0; i < this.rows; i++) {
            this.data[i] *= w;
        }
        return this;
    }

    public Vector batchNorm(float a, float b) {
        float avg = 0.0f;
        for (int i = 0; i < this.rows; i++) {
            avg += this.data[i];
        }
        avg /= (float) this.rows;

        float var = 0.0f;
        for (int i = 0; i < this.rows; i++) {
            float x = (this.data[i] - avg);
            var += x * x;
        }
        var /= (float) this.rows;

        for (int i = 0; i < this.rows; i++) {
            float x = (this.data[i] - avg) / Scalar.sqrt(var + Scalar.EPSILON);
            this.data[i] = a * x + b;
        }
        return this;
    }

    public Vector add(float k) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] += k;
        }
        return this;
    }

    public Vector add(final Vector v) {
        assert (this.rows == v.rows);

        for (int i = 0; i < this.rows; i++) {
            this.data[i] += v.data[i];
        }
        return this;
    }

    public Vector sub(float k) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] -= k;
        }
        return this;
    }

    public Vector sub(final Vector v) {
        assert (this.rows == v.rows);

        for (int i = 0; i < this.rows; i++) {
            this.data[i] -= v.data[i];
        }
        return this;
    }

    public Vector mult(float k) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] *= k;
        }
        return this;
    }

    public Vector mult(final Vector v) {
        assert (this.rows == v.rows);

        for (int i = 0; i < this.rows; i++) {
            this.data[i] *= v.data[i];
        }
        return this;
    }

    public Vector div(float k) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] /= k;
        }
        return this;
    }

    public Vector div(final Vector v) {
        assert (this.rows == v.rows);

        for (int i = 0; i < this.rows; i++) {
            this.data[i] /= v.data[i];
        }
        return this;
    }

    public Vector abs() {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Scalar.abs(this.data[i]);
        }
        return this;
    }

    public Vector pow(float n) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Scalar.pow(this.data[i], n);
        }
        return this;
    }

    public Vector sqrt() {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Scalar.sqrt(this.data[i]);
        }
        return this;
    }

    public float scalar(Vector v) {
        assert (this.rows == v.rows);

        float sum = 0;
        for (int i = 0; i < this.rows; i++) {
            sum += this.data[i] * v.data[i];
        }
        return sum;
    }

    public float norm() {
        float sum = 0;
        for (int i = 0; i < this.rows; i++) {
            sum += this.data[i] * this.data[i];
        }
        return Scalar.sqrt(sum);
    }

    public float distance(Vector v) {
        assert (this.rows == v.rows);

        float sum = 0;
        for (int i = 0; i < this.rows; i++) {
            float a = this.data[i] - v.data[i];
            sum += a * a;
        }
        return Scalar.sqrt(sum);
    }

    public Vector map(VectorFunction<Float, Float> fn) {
        assert (fn != null);

        for (int i = 0; i < this.rows; i++) {
            fn.apply(this.data[i], i, this);
        }
        return this;
    }

    public Vector map(VectorFunction<Float, Float> fn, Vector other) {
        assert (fn != null);

        for (int i = 0; i < this.rows; i++) {
            fn.apply(this.data[i], i, other);
        }
        return this;
    }

    public Vector copy() {
        Vector result = new Vector(this.rows);
        System.arraycopy(this.data, 0, result.data, 0, result.rows);
        return result;
    }

    public Vector concat(Vector v) {
        Vector result = new Vector(this.rows + v.rows);
        for (int i = 0; i < this.rows; i++) {
            result.data[i] = this.data[i];
        }
        for (int i = 0; i < v.rows; i++) {
            result.data[this.rows + i] = v.data[i];
        }
        return result;
    }

    public String toString() {
        if (this.rows == 0) {
            return "||";
        }

        StringBuilder result = new StringBuilder();
        result.append("| ");
        for (int i = 0; i < this.rows; i++) {
            result.append(String.format("%.5f\t", this.data[i]));
        }
        result.append(" |").append(System.lineSeparator());
        ;

        return result.toString();
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();

        if (this.rows == 0) {
            return json;
        }

        JSONArray jsonData = JSON.newJSONArray();
        for (int i = 0; i < this.rows; i++) {
            jsonData.append(this.data[i]);
        }

        json.setInt("rows", this.rows);
        json.setJSONArray("data", jsonData);
        return json;
    }
}
