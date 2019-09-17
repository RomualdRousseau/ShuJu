package com.github.romualdrousseau.shuju.genetic;

interface Individual {
    float getFitness();

    void setFitness(float f);

    void mutate();
}
