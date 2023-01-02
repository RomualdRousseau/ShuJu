package com.github.romualdrousseau.shuju.preprocessing;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.preprocessing.hasher.DefaultHasher;
import com.github.romualdrousseau.shuju.preprocessing.tokenizer.DefaultTokenizer;
import com.github.romualdrousseau.shuju.preprocessing.comparer.DefaultComparer;
import com.github.romualdrousseau.shuju.util.CollectionUtils;

public class Text {

    public interface ITokenizer extends Function<String, List<String>> {
    }

    public interface IHasher extends Function<String, Integer> {
    }

    public interface IComparer extends BiFunction<String, List<String>, Boolean> {
        String anonymize(String v);
    }

    public static ITokenizer DefaultTokenizer = new DefaultTokenizer();

    public static IHasher DefaultHasher = new DefaultHasher();

    public static IComparer DefaultComparer = new DefaultComparer();

    public static List<String> DefaultFilters = Arrays.asList("[\\\\!\"#$%&()*+,-./:;<=>?@\\[\\]^_`{|}~\\t\\n]");

    public static Comparator<String> ComparatorByLength = (a, b) -> b.length() - a.length();

    public static Map<String, Set<String>> get_lexicon(Set<String> lexicon) {
        return lexicon.stream()
                .map(w -> Arrays.asList(w.split(",")))
                .collect(Collectors.toMap(
                        w -> w.get(0),
                        w -> w.stream().sorted(Text.ComparatorByLength).collect(Collectors.toSet())));
    }

    public static List<String> all_words(final List<String> documents) {
        return Text.all_words(documents, Text.DefaultFilters);
    }

    public static List<String> all_words(final List<String> documents, final List<String> filters) {
        return Text.all_words(documents, filters, Text.DefaultTokenizer);
    }

    public static List<String> all_words(final List<String> documents, final List<String> filters, final ITokenizer tokenizer) {
        return documents.stream()
                .flatMap(d -> Text.to_words(d, filters, tokenizer).stream())
                .distinct().sorted().toList();
    }

    public static List<String> to_words(final String text) {
        return Text.to_words(text, Text.DefaultFilters);
    }

    public static List<String> to_words(final String text, final List<String> filters) {
        return Text.to_words(text, filters, Text.DefaultTokenizer);
    }

    public static List<String> to_words(final String text, final List<String> filters, final ITokenizer tokenizer) {
        return tokenizer.apply(filters.stream().reduce(text, (a, x) -> a.replaceAll("(?i)" + x, " ")));
    }

    public static List<Integer> to_categorical(final List<String> labels, final List<String> classes) {
        return Text.to_categorical(labels, classes, Text.DefaultComparer);
    }

    public static List<Integer> to_categorical(final List<String> labels, final List<String> classes,
            final IComparer comparer) {
        return classes.stream().map(c -> comparer.apply(c, labels) ? 1 : 0).toList();
    }

    public static List<String> anonymize(final List<String> labels, final IComparer comparer) {
        return labels.stream().map(l -> comparer.anonymize(l)).toList();
    }

    public static List<Integer> one_hot(final String text, final List<String> filters) {
        return Text.one_hot(text, filters, Text.DefaultTokenizer, Text.DefaultHasher);
    }

    public static List<Integer> one_hot(final String text, final List<String> filters, final ITokenizer tokenizer) {
        return Text.one_hot(text, filters, tokenizer, Text.DefaultHasher);
    }

    public static List<Integer> one_hot(final String text, final List<String> filters, final ITokenizer tokenizer, IHasher hasher) {
        return Text.to_words(text, filters, tokenizer).stream().map(hasher).toList();
    }

    public static List<Integer> pad_sequence(final List<Integer> sequence, final int maxLen) {
        return Text.pad_sequence(sequence, maxLen, 0);
    }

    public static List<Integer> pad_sequence(final List<Integer> sequence, final int maxLen, final int value) {
        final IntStream padding = IntStream.range(sequence.size(), maxLen).map(x -> value);
        return Stream.concat(sequence.stream(), padding.boxed()).toList();
    }

    public static List<Integer> mutate_sequence(final List<Integer> sequence) {
        return Text.mutate_sequence(sequence, 0.1f);
    }

    public static List<Integer> mutate_sequence(final List<Integer> sequence, final float p) {
        final List<Integer> shuffler = CollectionUtils.shuffle(CollectionUtils.mutableRange(0, sequence.size()));
        final Function<Integer, Integer> mutator = x -> Math.random() < p ? 0 : sequence.get(x);
        return shuffler.stream().map(mutator).filter(x -> x != 0).toList();
    }

    public static JSONArray json_sequence(final List<Integer> sequence) {
        JSONArray result = JSON.newJSONArray();
        sequence.forEach(x -> result.append(x));
        return result;
    }
}
