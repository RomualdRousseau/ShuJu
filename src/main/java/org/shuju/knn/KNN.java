package org.shuju.knn;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.shuju.IClassifier;
import org.shuju.IFeature;
import org.shuju.DataRow;
import org.shuju.DataSet;
import org.shuju.Result;

public class KNN extends IClassifier
{	
	public KNN(int k, double p, double e) {
		this.k = k;
		this.p = p;
		this.e = e;
	}

	public IClassifier train(DataSet trainingSet) {
		this.trainingSet = trainingSet;
		return this;
	}

	public Result predict(DataRow features) {
		if(features == null) {
			return new Result(features, null, this.e);
		}

		TreeMapWithDuplicates<Double, IFeature> nn = new TreeMapWithDuplicates<Double, IFeature>();

		//Compute the distance between the data and the trainingSet, eliminates the trainingSet beyond distance p
		for(DataRow trainingRow: this.trainingSet.rows()) {
			double kmean = 0.0;
			for(int i = 0; i < trainingRow.features().size(); i++) {
				double loss = trainingRow.features().get(i).lossFunc(features.features().get(i), this.e);
				kmean += loss * loss;
			}
			if(kmean < this.p * this.p) {
				nn.put(kmean, trainingRow.getLabel());
			}
		}
		
		// Count poll for each kth classifications and set the max poll as the classification result
		HashMap<IFeature, Double> polls  = new HashMap<IFeature, Double>();
		double pollMax = 0.0;
		Result result = null;
		for(Entry<Double, IFeature> entry: nn.entrySet(this.k)) {
			Double kmean = entry.getKey();
			IFeature label = entry.getValue();

			//System.out.println(data + ": " + distance + " -> " + label);

			Double pollCount = polls.get(label);
			if(pollCount == null) {
				pollCount = weightedFunc(kmean);
			}
			else {
				pollCount += weightedFunc(kmean);
			}
	
			if(pollMax < pollCount) {
				double confidence = (result == null) ? kmean : Math.min(kmean, result.getConfidence());
				result = new Result(features, label, confidence);
				pollMax = pollCount;
			}

			polls.put(label, pollCount);
		}
		
		if(result == null) {
			result = new Result(features, null, this.e);
		}

		return result;
	}

	private static double weightedFunc(double x) {
		return 1 - 1 / ( 1 + Math.exp(-15 * (x - 0.2)));
	}

	private int k;
	private double p;
	private double e;
	private DataSet trainingSet;
}
