package com.github.romualdrousseau.shuju.preprocessing.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.romualdrousseau.shuju.preprocessing.Text;
import com.github.romualdrousseau.shuju.util.StringUtils;

public class ShingleTokenizer implements Text.ITokenizer{
    private static final Pattern CAMEL_PATTERN = Pattern.compile("(?<!(^|[A-Z/]))(?=[A-Z/])|(?<!^)(?=[A-Z/][a-z/])");

    private final Map<String, Set<String>> variants;

    public ShingleTokenizer(Set<String> lexicon) {
        this.variants = Text.get_lexicon(lexicon);
    }

    @Override
    public List<String> tokenize(String w) {
        String s = StringUtils.normalizeWhiteSpaces(w);

        // Split using a lexicon of known words if any

        for (Entry<String, Set<String>> lexem : variants.entrySet()) {
            for (String variant : lexem.getValue()) {
                if (s.toLowerCase().contains(variant)) {
                    s = s.replaceAll("(?i)" + variant, " " + lexem.getKey() + " ");
                    break;
                }
            }
        }

        // Clean by space and underscore

        s = s.replaceAll("[\\s_]+", " ").trim();

        // Split by space and then by Camel notation words

        ArrayList<String> result = new ArrayList<String>();
        for (String ss : s.split(" ")) {
            for (String sss : CAMEL_PATTERN.split(ss)) {
                if (sss.length() > 0 && (sss.length() > 1 || !Character.isAlphabetic(sss.charAt(0)))) {
                    result.add(sss.toLowerCase());
                }
            }
        }

        return result;
    }
}
