package org.shuju;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class Result
{
	public Result(DataRow features, IFeature label, double confidence) {
		this.features = features;
		this.label = label;
		this.confidence = confidence;
	}

	public DataRow getFeatures() {
		return this.features;
	}

	public IFeature getLabel() {
		return this.label;
	}

	public double getConfidence() {
		return this.confidence;
	}

	public boolean isUndefined() {
		return this.label == null;
	}

	public String toString() {
		String featuresString = (this.features != null) ? "_" : this.features.toString();
		String labelString = isUndefined() ? "undefined" : this.label.toString();
		return featuresString + " :- " + labelString + ", " + this.confidence;
	}

	public void toFile(String filePath) throws IOException {
		PrintWriter writer = null;
		try
		{
	     	writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
			writer.println(this.toString());
		}
		finally {
			if(writer != null) {
				writer.close();	
			}
		}
	}

	private DataRow features;
	private IFeature label;
	private double confidence;
}
