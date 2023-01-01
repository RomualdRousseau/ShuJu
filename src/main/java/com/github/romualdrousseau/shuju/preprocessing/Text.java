package com.github.romualdrousseau.shuju.preprocessing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.util.CollectionUtils;

public class Text {

    public interface ITokenizer {
        List<String> tokenize(String w);
    }

    public interface IHasher extends Function<String, Integer> {
    }

    public static Map<String, Set<String>> get_lexicon(Set<String> lexicon) {
        return lexicon.stream().map(w -> Arrays.asList(w.split(",")))
                .collect(Collectors.toMap(w -> w.get(0),
                        w -> w.stream().sorted((a, b) -> b.length() - a.length()).collect(Collectors.toSet())));
    }

    public static List<String> all_words(final List<String> documents, final List<String> filters, final ITokenizer tokenizer) {
        return documents.stream().flatMap(d -> Text.to_words(d, filters, tokenizer).stream()).distinct().sorted().toList();
    }

    public static List<String> to_words(final String text, final List<String> filters, final ITokenizer tokenizer) {
        return tokenizer.tokenize(filters.stream().reduce(text, (a, x) -> a.replaceAll("(?i)" + x, " ")));
    }

    public static List<Integer> one_hot(final String text, final List<String> filters, final ITokenizer tokenizer,
            final IHasher hasher) {
        return Text.to_words(text, filters, tokenizer).stream().map(hasher).toList();
    }

    public static List<Integer> pad_sequence(final List<Integer> sequence, final int maxLen, final int value) {
        return Stream.concat(sequence.stream(), IntStream.range(sequence.size(), maxLen).boxed().map(x -> value))
                .toList();
    }

    public static List<Integer> to_categorical(final List<String> labels, final List<String> classes) {
        return classes.stream().map(c -> labels.contains(c) ? 1 : 0).toList();
    }

    public static List<Integer> mutate_sequence(final List<Integer> sequence) {
        final List<Integer> shuffle = CollectionUtils.mutableRange(0,
                sequence.size());
        Collections.shuffle(shuffle);
        return shuffle.stream().map(x -> Math.random() < 0.1 ? 0 : sequence.get(x)).filter(x -> x != 0).toList();
    }

    public static JSONArray json_sequence(final List<Integer> sequence) {
        JSONArray result = JSON.newJSONArray();
        sequence.forEach(x -> result.append(x));
        return result;
    }
}
