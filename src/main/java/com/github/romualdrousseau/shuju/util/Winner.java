package com.github.romualdrousseau.shuju.util;

import com.github.romualdrousseau.shuju.StatisticClass;

public class Winner<T> extends StatisticClass
{
	public Winner(T candidate, double probability) {
		this.candidate = candidate;
		this.probability = probability;
	}

	public T getCandidate() {
		return this.candidate;
	}

	public double getProbability() {
		return this.probability;
	}

	public String toString() {
		return String.format("[%s, %.1f]", this.candidate.toString(), this.probability);
	}

	private T candidate;
	private double probability;
}