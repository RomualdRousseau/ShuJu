package com.github.romualdrousseau.shuju;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor1D;
import com.github.romualdrousseau.shuju.math.deprecated.TensorFunction;

public class DataSet {

    public DataSet() {
        this.rows = new ArrayList<DataRow>();
    }

    public DataSet(final List<DataRow> rows) {
        this.rows = rows;
    }

    public DataSet(final JSONObject json) {
        final JSONArray jsonInputs = json.getJSONArray("inputs");
        final JSONArray jsonTargets = json.getJSONArray("targets");
        this.rows = IntStream.range(0, jsonInputs.size())
                .mapToObj(i -> new DataRow(jsonInputs.getJSONArray(i), jsonTargets.getJSONObject(i)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<DataRow> rows() {
        return this.rows;
    }

    public DataSet addRow(final DataRow row) {
        return this.addRow(row, false);
    }

    public DataSet addRow(final DataRow row, final boolean replaceIfExists) {
        final int i = this.indexOf(row);
        if (i == -1) {
            this.rows.add(row);
        } else if (replaceIfExists && !this.rows.get(i).hasSameLabel(row)) {
            this.rows.set(i, row);
        }
        return this;
    }

    public DataSet shuffle() {
        java.util.Collections.shuffle(this.rows);
        return this;
    }

    public DataSet subset(final int rowStart, final int rowEnd) {
        return new DataSet(this.rows.subList(rowStart, rowEnd));
    }

    public DataSet join(final DataSet dataset, final boolean replaceIfExists) {
        dataset.rows.forEach(row -> DataSet.this.addRow(row, replaceIfExists));
        return this;
    }

    public DataSet filter(final Predicate<DataRow> predicate) {
        List<DataRow> rows = this.rows.stream()
                .filter(predicate)
                .collect(Collectors.toCollection(ArrayList::new));
        return new DataSet(rows);
    }

    public DataSet transform(final ITransform transfomer, final int partIndex, final int colIndex) {
        if (partIndex == DataRow.LABELS) {
            IntStream.range(0, this.rows.size())
                    .forEach(i -> transfomer.apply(this.rows.get(i).label(), i, colIndex));
        } else if (partIndex == DataRow.FEATURES) {
            IntStream.range(0, this.rows.size())
                    .forEach(i -> transfomer.apply(this.rows.get(i).features().get(colIndex), i, colIndex));
        }
        return this;
    }

    public int indexOf(final DataRow row) {
        return IntStream.range(0, this.rows.size())
                .filter(i -> this.rows.get(i).hasSameFeatures(row))
                .findAny()
                .orElse(-1);
    }

    private boolean duplicates(final DataRow other) {
        return this.rows.stream().anyMatch(row -> (row != other) && row.equals(other));
    }

    public boolean conflicts(final DataRow other) {
        return this.rows.stream().anyMatch(row -> (row != other) && row.conflicts(other));
    }

    public DataSet removeDuplicates() {
        return this.filter(x -> !this.duplicates(x));
    }

    public DataSet purgeConflicts() {
        return this.filter(x -> !this.conflicts(x));
    }

    public Tensor1D[] featuresAsVectorArray() {
        return this.rows.stream().map(x -> x.featuresAsOneVector()).toArray(Tensor1D[]::new);
    }

    public Tensor1D[] labelsAsVectorArray() {
        return this.rows.stream().map(x -> x.label()).toArray(Tensor1D[]::new);
    }

    public static DataSet makeBlobs(final int rows, final int features, final int labels) {
        final DataSet result = new DataSet();
        final int p = rows / labels;
        for (int i = 0; i < labels; i++) {
            final Tensor1D c = new Tensor1D(features).randomize(10);
            for (int k = 0; k < p; k++) {
                final Tensor1D x = c.copy().map(new TensorFunction<Tensor1D>() {
                    public float apply(final float v, final int[] ij, final Tensor1D vector) {
                        return v + Scalar.randomGaussian();
                    }
                });
                final Tensor1D y = new Tensor1D(labels).oneHot(i);
                result.addRow(new DataRow().addFeature(x).setLabel(y));
            }
        }

        return result;
    }

    public static DataSet makeCircles(final int rows, final int features, final int labels) {
        final DataSet result = new DataSet();
        final int p = rows / labels;
        final Tensor1D c = new Tensor1D(features).zero();
        for (int i = 0; i < labels; i++) {
            final float l = 15.0f * i / (float) labels;
            for (int k = 0; k < p; k++) {
                final Tensor1D x = c.copy().randomize().l2Norm().mul(l).map(new TensorFunction<Tensor1D>() {
                    public float apply(final float v, final int[] ij, final Tensor1D vector) {
                        return v + Scalar.randomGaussian();
                    }
                });
                final Tensor1D y = new Tensor1D(labels).oneHot(i);
                result.addRow(new DataRow().addFeature(x).setLabel(y));
            }
        }

        return result;
    }

    public DataSet augment(final float p, final Function<DataRow, DataRow> func) {
        final Map<String, Long> countPerClass = this.rows.stream()
                .map(x -> x.label().toString())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        final long biggerClassCnt = countPerClass.values().stream().max(Long::compare).get();
        final int noiseCnt = Math.round(this.rows.size() * (1.0f / p - 1.0f));
        final List<DataRow> noise = this.rows.stream()
                .flatMap(x -> LongStream.range(0, biggerClassCnt / countPerClass.get(x.label().toString()) - 1)
                        .mapToObj(i -> func.apply(x)))
                .collect(Collectors.toCollection(ArrayList::new));
        return new DataSet(this.rows)
                .join(new DataSet(noise).shuffle().subset(0, noiseCnt), false)
                .purgeConflicts()
                .shuffle();
    }

    public String toString() {
        String result = "";
        for (final DataRow row : this.rows) {
            result += row.toString() + "\n";
        }
        return result;
    }

    public JSONObject toJSON() {
        final JSONArray jsonInputs = JSON.newJSONArray();
        final JSONArray jsonTargets = JSON.newJSONArray();
        for (int i = 0; i < this.rows.size(); i++) {
            final DataRow row = this.rows.get(i);

            final JSONArray jsonInput = JSON.newJSONArray();
            for (int j = 0; j < row.features().size(); j++) {
                jsonInput.append(row.features().get(j).toJSON());
            }
            jsonInputs.append(jsonInput);

            jsonTargets.append(row.label().toJSON());
        }
        final JSONObject json = JSON.newJSONObject();
        json.setJSONArray("inputs", jsonInputs);
        json.setJSONArray("targets", jsonTargets);
        return json;
    }

    private final List<DataRow> rows;
}
