package com.github.romualdrousseau.shuju.ml.knn;

import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.Vector;

public class KNN {
    public KNN(int k) {
        this.k = k;
    }

    public void fit(final Vector[] inputs, final Vector[] targets) {
        assert (inputs.length == targets.length);
        this.inputs = inputs;
        this.targets = targets;
    }

    public Vector predict(final Vector input) {
        if (this.inputs == null || this.targets == null || input == null) {
            return new Vector(0);
        }

        final int targetSize = this.targets[0].rowCount();

        Vector kmeans = new Vector(this.k, -1.0f);
        Matrix results = new Matrix(this.k, targetSize);

        for(int i = 0; i < this.inputs.length; i++) {
            float kmean = this.inputs[i].distance(input);
            int kmax = kmeans.argmax();
            float kvalue = kmeans.get(kmax);
            if(kvalue == -1.0f || kmean < kvalue) {
                kmeans.set(kmax, kmean);
                results.set(kmax, this.targets[i]);
            }
        }

        return results.flatten().get(0).l2Norm();
    }

    private int k;
    private Vector[] inputs;
    private Vector[] targets;
}
