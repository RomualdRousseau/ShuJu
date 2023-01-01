package com.github.romualdrousseau.shuju.preprocessing.hasher;

import com.github.romualdrousseau.shuju.preprocessing.Text;

public class DefaultHasher implements Text.IHasher {

    @Override
    public Integer apply(final String w) {
        return w.hashCode();
    }
}
