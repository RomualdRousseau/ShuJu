package com.github.romualdrousseau.shuju.preprocessing.comparer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.romualdrousseau.shuju.preprocessing.Text;

public class RegexComparer implements Text.IComparer {

    private Map<String, String> patterns;
    private Map<String, Pattern> compiledPatterns;

    public RegexComparer(final Map<String, String> patterns) {
        this.patterns = patterns;
        this.compiledPatterns = patterns.keySet().stream().collect(Collectors.toMap(r -> r, r -> this.compileRegex(r)));
    }

    @Override
    public Boolean apply(final String a, final List<String> b) {
        return this.patterns.entrySet().stream()
                .filter(x -> a.equals(x.getValue()))
                .map(e -> this.compiledPatterns.get(e.getKey()))
                .anyMatch(p -> b.stream().anyMatch(v -> v != null && p.matcher(v).find()));
    }

    @Override
    public String anonymize(final String v) {
        return (v == null) ? null : this.patterns.entrySet().stream()
            .reduce(Map.entry("", v), (a, e) -> Map.entry("", this.compiledPatterns.get(e.getKey()).matcher(a.getValue()).replaceAll(e.getValue()))).getValue();
    }

    @Override
    public Optional<String> find(final String v) {
        return (v == null) ? null : this.patterns.entrySet().stream().map(e -> this.compiledPatterns.get(e.getKey()).matcher(v)).filter(m -> m.find()).map(m -> m.group()).findFirst();
    }

    private Pattern compileRegex(String r) {
        return Pattern.compile(r, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}