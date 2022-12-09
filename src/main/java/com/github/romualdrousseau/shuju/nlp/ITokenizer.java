package com.github.romualdrousseau.shuju.nlp;

import java.util.List;

import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor1D;

public interface ITokenizer {
    List<String> values();

    void add(String s);

    List<String> tokenize(String s);

    Tensor1D word2vec(String s, Tensor1D outVector);

    Tensor1D embedding(String s);

    JSONObject toJSON();
}
