package com.github.romualdrousseau.shuju.json;

import java.util.Optional;

public interface JSONArray {

    int size();

    <T> Optional<T> get(int i);

    <T> JSONArray set(int i, T o);

    <T> JSONArray append(T o);

    JSONArray remove(int i);
}
