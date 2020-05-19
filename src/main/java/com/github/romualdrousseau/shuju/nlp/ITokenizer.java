package com.github.romualdrousseau.shuju.nlp;

import com.github.romualdrousseau.shuju.math.Tensor1D;

public interface ITokenizer {
    void add(String s);

    String[] tokenize(String s);

    Tensor1D word2vec(String s, Tensor1D outVector);
}
