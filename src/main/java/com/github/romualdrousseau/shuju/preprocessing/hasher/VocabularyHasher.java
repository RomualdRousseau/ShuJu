package com.github.romualdrousseau.shuju.preprocessing.hasher;

import java.util.Collections;
import java.util.List;

import com.github.romualdrousseau.shuju.preprocessing.Text;

public class VocabularyHasher implements Text.IHasher {

    private final List<String> vocabulary;

    public VocabularyHasher(final List<String> vocabulary) {
        this.vocabulary = vocabulary;
    }

    @Override
    public Integer apply(final String w) {
        return Collections.binarySearch(this.vocabulary, w) + 1;
    }
}
