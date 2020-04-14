package com.github.romualdrousseau.shuju.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GeneticPool {

    public GeneticPool() {
        this.pool = new ArrayList<Individual>();
    }

    public void newPool() {
        this.pool.clear();
    }

    public void addOne(Individual one) {
        this.pool.add(one);
    }

    public void sample() {
        this.sample((int) Math.floor(Math.random() * this.pool.size()));
    }

    public void sample(int sampleCount) {
        sampleCount = Math.max(1, sampleCount);

        Collections.sort(this.pool, Collections.reverseOrder(new Comparator<Individual>() {
            public int compare(Individual a, Individual b) {
                float d = a.getFitness() - b.getFitness();
                return (d < 0) ? -1 : ((d > 0) ? 1 : 0);
            }
        }));

        if (this.pool.size() > sampleCount) {
            for (int i = this.pool.size() - 1; i >= sampleCount; i--) {
                this.pool.remove(i);
            }
        }
    }

    public void normalize() {
        float sum = 0;
        for (int i = 0; i < this.pool.size(); i++) {
            Individual individual = this.pool.get(i);
            sum += individual.getFitness();
        }

        for (int i = 0; i < this.pool.size(); i++) {
            Individual individual = this.pool.get(i);
            individual.setFitness(individual.getFitness() / sum);
        }
    }

    public Individual selectParent() {
        float r = (float) Math.random();

        int bestIndex = 0;
        while (r > 0) {
            r -= pool.get(bestIndex).getFitness();
            bestIndex++;
        }
        bestIndex--;

        return pool.get(bestIndex);
    }

    public Individual spawn() {
        return this.selectParent().clone().mutate();
    }

    private ArrayList<Individual> pool;
}
