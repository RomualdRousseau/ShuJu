package com.github.romualdrousseau.shuju.math;

import java.util.Arrays;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class Tensor1D extends AbstractTensor<float[]> {
    protected int rows;

    public static final Tensor1D Null = new Tensor1D(0);

    public Tensor1D(int rows) {
        super(new int[] { rows}, new float[rows]);
        this.rows = rows;
    }

    public Tensor1D(int rows, float v) {
        this(rows);
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = v;
        }
    }

    public Tensor1D(float[] v) {
        this(v.length);
        System.arraycopy(v, 0, this.data, 0, this.rows);
    }

    public Tensor1D(Float[] v) {
        this(v.length);
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = v[i];
        }
    }

    public Tensor1D(JSONObject json) {
        this(json.getInt("rows"));
        JSONArray jsonData = json.getJSONArray("data");
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = jsonData.getFloat(i);
        }
    }

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

    public Tensor1D set(int i, float v) {
        this.data[i] = v;
        return this;
    }

    public boolean equals(final Tensor1D v) {
        return this.rows == v.rows && Arrays.equals(this.data, v.data);
    }

    public boolean equals(final Tensor1D v, final float e) {
        boolean result = this.rows == v.rows;
        for (int i = 0; i < this.rows && result; i++) {
            float a = this.data[i];
            float b = v.data[i];
            result &= Math.abs(a - b) < e;
        }
        return result;
    }

    public boolean isSimilar(final Tensor1D v, final float e) {
        return 1.0f -  Scalar.abs(this.similarity(v)) < e;
    }

    public float similarity(final Tensor1D v) {
        if(this.sparsity() == 1.0f || v.sparsity() == 1.0f) {
            return 0.0f;
        } else {
            return this.scalar(v) / (this.norm() * v.norm());
        }
    }

    public float sparsity() {
        int count = 0;
        for (int i = 0; i < this.rows; i++) {
            count += (this.data[i] == 0.0f) ? 1 : 0;
        }
        return (float) count / (float) this.rows;
    }

    public Tensor1D chop(final float e) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = (Scalar.abs(this.data[i]) < e) ? 0.0f : this.data[i];
        }
        return this;
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

    public float cov(Tensor1D v) {
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

    public Tensor1D min(Tensor1D v) {
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

    public Tensor1D max(Tensor1D v) {
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

    public Tensor1D zero() {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = 0.0f;
        }
        return this;
    }

    public Tensor1D ones() {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = 1.0f;
        }
        return this;
    }

    public Tensor1D oneHot(int k) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = (i == k) ? 1.0f : 0.0f;
        }
        return this;
    }

    public <T extends Enum<T>> Tensor1D oneHot(T e) {
        if (e == null) {
            return this.zero();
        }

        return this.oneHot(e.ordinal());
    }

    public <T extends Enum<T>> Tensor1D oneHot(T[] s) {
        if (s == null) {
            return this.zero();
        }

        for (int i = 0; i < this.rows; i++) {
            this.data[i] = (i == s[i].ordinal()) ? 1.0f : 0.0f;
        }
        return this;
    }

    public Tensor1D mutate(float rate, float variance) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] += Scalar.randomGaussian() * variance;
        }
        return this;
    }

    public Tensor1D randomize() {
        return this.randomize(1.0f);
    }

    public Tensor1D randomize(float n) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Scalar.random(-n, n);
        }
        return this;
    }

    public Tensor1D constrain(float a, float b) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Scalar.constrain(this.data[i], a, b);
        }
        return this;
    }

    public Tensor1D if_lt_then(float p, float a, float b) {
        for (int j = 0; j < this.rows; j++) {
            this.data[j] = Scalar.if_lt_then(this.data[j], p, a, b);
        }
        return this;
    }

    public Tensor1D softmax() {
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

    public Tensor1D l2Norm() {
        float w = 1.0f / this.norm();
        for (int i = 0; i < this.rows; i++) {
            this.data[i] *= w;
        }
        return this;
    }

    public Tensor1D batchNorm(float a, float b) {
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

    public Tensor1D add(float k) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] += k;
        }
        return this;
    }

    public Tensor1D add(final Tensor1D v) {
        assert (this.rows == v.shape[0]);
        for (int i = 0; i < this.rows; i++) {
            this.data[i] += ((float[]) v.data)[i];
        }
        return this;
    }

    public Tensor1D sub(float k) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] -= k;
        }
        return this;
    }

    public Tensor1D sub(final Tensor1D v) {
        assert (this.rows == v.rows);
        for (int i = 0; i < this.rows; i++) {
            this.data[i] -= v.data[i];
        }
        return this;
    }

    public Tensor1D mul(float k) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] *= k;
        }
        return this;
    }

    public Tensor1D mul(final Tensor1D v) {
        assert (this.rows == v.rows);

        for (int i = 0; i < this.rows; i++) {
            this.data[i] *= v.data[i];
        }
        return this;
    }

    public Tensor1D div(float k) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] /= k;
        }
        return this;
    }

    public Tensor1D div(final Tensor1D v) {
        assert (this.rows == v.rows);

        for (int i = 0; i < this.rows; i++) {
            this.data[i] /= v.data[i];
        }
        return this;
    }

    public Tensor1D abs() {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Scalar.abs(this.data[i]);
        }
        return this;
    }

    public Tensor1D pow(float n) {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Scalar.pow(this.data[i], n);
        }
        return this;
    }

    public Tensor1D exp() {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Scalar.exp(this.data[i]);
        }
        return this;
    }

    public Tensor1D sqrt() {
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = Scalar.sqrt(this.data[i]);
        }
        return this;
    }

    public float scalar(Tensor1D v) {
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

    public float distance(Tensor1D v) {
        assert (this.rows == v.rows);
        float sum = 0;
        for (int i = 0; i < this.rows; i++) {
            float a = this.data[i] - v.data[i];
            sum += a * a;
        }
        return Scalar.sqrt(sum);
    }

    public Tensor1D map(float start1, float stop1, float start2, float stop2) {
        final float m = (stop2 - start2) / (stop1 - start1);
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = m * (this.data[i] - start1) + start2;
        }
        return this;
    }

    public Tensor1D map(TensorFunction<Tensor1D> fn) {
        assert (fn != null);
        for (int i = 0; i < this.rows; i++) {
            this.data[i] = fn.apply(this.data[i], new int[] { i }, this);
        }
        return this;
    }

    public Tensor1D map(TensorFunction<Tensor1D> fn, Tensor1D other) {
        assert (fn != null);

        for (int i = 0; i < this.rows; i++) {
            this.data[i] = fn.apply(this.data[i], new int[] { i }, other);
        }
        return this;
    }

    public Tensor1D copy() {
        Tensor1D result = new Tensor1D(this.rows);
        System.arraycopy(this.data, 0, result.data, 0, result.rows);
        return result;
    }

    public Tensor1D concat(Tensor1D v) {
        Tensor1D result = new Tensor1D(this.rows + v.rows);
        for (int i = 0; i < this.rows; i++) {
            result.data[i] = this.data[i];
        }
        for (int i = 0; i < v.rows; i++) {
            result.data[this.rows + i] = v.data[i];
        }
        return result;
    }

    public Tensor2D dot(Tensor1D v) {
        assert (this.rows == v.rows);
        Tensor2D result = new Tensor2D(this.rows, this.rows);
        for (int i = 0; i < result.rows; i++) {
            for (int j = 0; j < result.cols; j++) {
                result.data[i][j] = this.data[i] * v.data[j];
            }
        }
        return result;
    }

    public Tensor2D dot(Tensor2D m) {
        assert (this.rows == m.rows);
        Tensor2D result = new Tensor2D(this.rows, this.rows);
        for (int i = 0; i < result.rows; i++) {
            for (int j = 0; j < result.cols; j++) {
                float sum = 0.0f;
                for(int k = 0; k < m.rows; k++) {
                    sum += this.data[i] * m.data[k][j];
                }
                result.data[i][j] = sum;
            }
        }
        return result;
    }

    public AbstractTensor<?> matmul(final AbstractTensor<?> a) {
        if(a.shape.length == 1) {
            return this.dot((Tensor1D) a);
        } else if(a.shape.length == 2) {
            return this.dot((Tensor2D) a);
        } else {
            return null;
        }
    }

    public String toString() {
        if (this.rows == 0) {
            return "||";
        }

        StringBuilder result = new StringBuilder();
        result.append("| ");
        for (int i = 0; i < this.rows; i++) {
            result.append(String.format("%1$10.3f ", this.data[i]));
        }
        result.append(" |").append(System.lineSeparator());

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
