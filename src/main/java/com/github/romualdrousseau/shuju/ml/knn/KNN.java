package com.github.romualdrousseau.shuju.ml.knn;

import com.github.romualdrousseau.shuju.IClassifier;
import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.DataRow;
import com.github.romualdrousseau.shuju.DataSet;
import com.github.romualdrousseau.shuju.Result;
import com.github.romualdrousseau.shuju.util.TreeMapWithDuplicates;
import com.github.romualdrousseau.shuju.util.Election;
import com.github.romualdrousseau.shuju.util.Winner;

public class KNN implements IClassifier
{
	public KNN(int k) {
		this.k = k;
		this.p = 1.0;
	}

	public KNN(int k, double p) {
		this.k = k;
		this.p = p;
	}

	public DataSet getTrainingSet() {
		return this.trainingSet;
	}

	public IClassifier train(DataSet trainingSet) {
		this.trainingSet = trainingSet;
		return this;
	}

	public Result predict(DataRow features) {
		if(this.trainingSet == null || features == null) {
			return new Result(features, null, 0.0);
		}

		TreeMapWithDuplicates<Double, IFeature<?>> nn = new TreeMapWithDuplicates<Double, IFeature<?>>();

		//Compute the distance between the data and the trainingSet, eliminates the trainingSet beyond distance p
		for(DataRow trainingRow: this.trainingSet.rows()) {
			double kmean = 0.0;
			for(int i = 0; i < trainingRow.features().size(); i++) {
				kmean += trainingRow.features().get(i).costFunc(features.features().get(i));
			}
			if(kmean < this.p * this.p) {
				nn.put(kmean, trainingRow.getLabel());
			}
		}

		Winner<IFeature<?>> winner = new Election<IFeature<?>>().voteWithRank(nn.entrySet(this.k));
		if(winner != null) {
			return new Result(features, winner.getCandidate(), winner.getProbability());
		}
		else {
			return new Result(features, null, 0.0);
		}
	}

	private int k;
	private double p;
	private DataSet trainingSet;
}
