package com.github.romualdrousseau.shuju.preprocessing.comparer;

import java.util.List;

import com.github.romualdrousseau.shuju.preprocessing.Text;

public class DefaultComparer implements Text.IComparer {

    @Override
    public Boolean apply(final String a, final List<String> b) {
        return b.contains(a);
    }

    @Override
    public String anonymize(final String v) {
        return v;
    }
}
