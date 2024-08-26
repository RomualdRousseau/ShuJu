package com.github.romualdrousseau.shuju.yaml;

import java.util.Optional;

public interface YAMLArray {

    int size();

    <T> Optional<T> get(int i);

    <T> YAMLArray set(int i, T o);

    <T> YAMLArray append(T o);

    String toString(final boolean pretty);

    YAMLArray remove(int i);
}
