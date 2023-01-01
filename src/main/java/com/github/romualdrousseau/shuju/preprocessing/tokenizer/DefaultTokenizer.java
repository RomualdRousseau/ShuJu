package com.github.romualdrousseau.shuju.preprocessing.tokenizer;

import java.util.Arrays;
import java.util.List;

import com.github.romualdrousseau.shuju.preprocessing.Text;
import com.github.romualdrousseau.shuju.util.StringUtils;

public class DefaultTokenizer implements Text.ITokenizer {
    @Override
    public List<String> tokenize(String w) {
        String s = StringUtils.normalizeWhiteSpaces(w);
        return Arrays.asList(s.split(" "));
    }
}
