package com.github.romualdrousseau.shuju.ml.knn;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor1D;

public class KNN {
    public KNN(int k) {
        this.k = k;
    }

    public void fit(final Tensor1D[] inputs, final Tensor1D[] targets) {
        assert (inputs.length == targets.length);
        this.inputs = inputs;
        this.targets = targets;
    }

    public Tensor1D predict(final Tensor1D input) {
        if (this.inputs == null || this.targets == null || input == null) {
            return new Tensor1D(0);
        }

        final int targetSize = this.targets[0].rowCount();

        Tensor1D kmeans = new Tensor1D(this.k, -1.0f);
        Tensor2D results = new Tensor2D(this.k, targetSize);

        for(int i = 0; i < this.inputs.length; i++) {
            float kmean = this.inputs[i].distance(input);
            int kmax = kmeans.argmax();
            float kvalue = kmeans.get(kmax);
            if(kvalue == -1.0f || kmean < kvalue) {
                kmeans.set(kmax, kmean);
                results.replace(kmax, this.targets[i], 0);
            }
        }

        return new Tensor1D(results.flatten(0).l2Norm(1).getFloats(0));
    }

    private int k;
    private Tensor1D[] inputs;
    private Tensor1D[] targets;
}
