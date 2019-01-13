package com.github.romualdrousseau.shuju;

import java.util.ArrayList;

public class DataRow
{
	public ArrayList<IFeature<?>> features() {
		return this.features;
	}

	public DataRow addFeature(IFeature<?> feature) {
		this.features.add(feature);
		return this;
	}

	public IFeature<?> getLabel() {
		return this.label;
	}

	public DataRow setLabel(IFeature<?> label) {
		this.label = label;
		return this;
	}

	public String toString() {
		String featuresString = "";
		boolean firstPass = true;
		for(IFeature<?> feature: this.features) {
			if(firstPass) {
				featuresString = feature.toString();
				firstPass = false;
			}
			else {
				featuresString += ", " + feature.toString();
			}
		}

		String labelString = "";
		if(this.label == null) {
			labelString = "<undefined>";
		}
		else {
			labelString = this.label.toString();
		}

		return String.format("[%s :- %s]", featuresString, labelString);
	}

	private ArrayList<IFeature<?>> features = new ArrayList<IFeature<?>>();
	private IFeature<?> label = null;
}
