package com.github.romualdrousseau.shuju.genetic;

public interface Individual {
    float getFitness();

    void setFitness(float f);

    Individual clone();

    Individual mutate();
}
