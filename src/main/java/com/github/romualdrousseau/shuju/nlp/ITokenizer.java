package com.github.romualdrousseau.shuju.nlp;

import com.github.romualdrousseau.shuju.math.Vector;

public interface ITokenizer {
    String[] tokenize(String s);

    Vector word2vec(String s, Vector outVector);
}
