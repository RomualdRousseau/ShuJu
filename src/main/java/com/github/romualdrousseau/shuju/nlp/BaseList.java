package com.github.romualdrousseau.shuju.nlp;

import java.util.List;

import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor1D;

public interface BaseList {
    List<String> values();

    int size();

    String get(int i);

    int ordinal(String w);

    BaseList add(String w);

    int getVectorSize();

    Tensor1D word2vec(String w);

    Tensor1D embedding(String w);

    JSONObject toJSON();
}
