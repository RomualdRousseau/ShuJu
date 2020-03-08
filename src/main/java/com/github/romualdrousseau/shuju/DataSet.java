package com.github.romualdrousseau.shuju;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.math.VectorFunction;

public class DataSet {
    public DataSet() {
        this.rows = new ArrayList<DataRow>();
    }

    public DataSet(List<DataRow> rows) {
        this.rows = rows;
    }

    public DataSet(JSONObject json) {
        JSONArray jsonInputs = json.getJSONArray("inputs");
        JSONArray jsonTargets = json.getJSONArray("targets");

        ArrayList<DataRow> rows = new ArrayList<DataRow>();

        for (int i = 0; i < jsonInputs.size(); i++) {
            DataRow row = new DataRow();

            JSONArray jsonInput = jsonInputs.getJSONArray(i);
            for (int j = 0; j < jsonInput.size(); j++) {
                row.addFeature(new Vector(jsonInput.getJSONObject(j)));
            }

            row.setLabel(new Vector(jsonTargets.getJSONObject(i)));

            rows.add(row);
        }

        this.rows = rows;
    }

    public List<DataRow> rows() {
        return this.rows;
    }

    public DataSet addRow(DataRow row) {
        this.rows.add(row);
        return this;
    }

    public DataSet addRow(DataRow row, boolean replaceIfExists) {
        int i = this.indexOf(row);
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

    public DataSet purgeConflicts() {
        List<DataRow> temp = new ArrayList<DataRow>();
        for (DataRow row : this.rows) {
            if (!this.conflicts(row)) {
                temp.add(row);
            }
        }
        return new DataSet(temp);
    }

    public DataSet subset(int rowStart, int rowEnd) {
        return new DataSet(this.rows.subList(rowStart, rowEnd));
    }

    public DataSet join(DataSet dataset) {
        this.rows.addAll(dataset.rows);
        return this;
    }

    public DataSet filter(Predicate<DataRow> predicate) {
        DataSet result = new DataSet();

        for (DataRow row : this.rows) {
            if (predicate.test(row)) {
                result.addRow(row);
            }
        }

        return result;
    }

    public DataSet transform(ITransform transfomer, int partIndex, int colIndex) {
        int rowIndex = 0;
        if (partIndex == DataRow.LABELS) {
            for (DataRow row : this.rows) {
                transfomer.apply(row.label(), rowIndex++, colIndex);
            }
        } else if (partIndex == DataRow.FEATURES) {
            for (DataRow row : this.rows) {
                transfomer.apply(row.features().get(colIndex), rowIndex++, colIndex);
            }
        }
        return this;
    }

    public int indexOf(DataRow row) {
        int i = 0;
        while (i < this.rows.size() && !this.rows.get(i).hasSameFeatures(row)) {
            i++;
        }
        return (i == this.rows.size()) ? -1 : i;
    }

    public boolean conflicts(DataRow other) {
        boolean result = false;
        for (DataRow row : this.rows) {
            if (row != other) {
                result |= row.conflicts(other);
            }
        }
        return result;
    }

    public Vector[] featuresAsVectorArray() {
        Vector[] result = new Vector[this.rows.size()];
        for (int i = 0; i < this.rows.size(); i++) {
            result[i] = this.rows.get(i).featuresAsOneVector();
        }
        return result;
    }

    public Vector[] labelsAsVectorArray() {
        Vector[] result = new Vector[this.rows.size()];
        for (int i = 0; i < this.rows.size(); i++) {
            result[i] = this.rows.get(i).label();
        }
        return result;
    }

    public static DataSet makeBlobs(int rows, int features, int labels) {
        DataSet result = new DataSet();
        final int p = rows / labels;
        for (int i = 0; i < labels; i++) {
            final Vector c = new Vector(features).randomize(10);
            for (int k = 0; k < p; k++) {
                Vector x = c.copy().map(new VectorFunction<Float, Float>() {
                    public Float apply(Float v, int row, Vector vector) {
                        return v + Scalar.randomGaussian();
                    }
                });
                Vector y = new Vector(labels).oneHot(i);
                result.addRow(new DataRow().addFeature(x).setLabel(y));
            }
        }

        return result;
    }

    public static DataSet makeCircles(int rows, int features, int labels) {
        DataSet result = new DataSet();
        final int p = rows / labels;
        final Vector c = new Vector(features).zero();
        for (int i = 0; i < labels; i++) {
            final float l = 15.0f * i / (float) labels;
            for (int k = 0; k < p; k++) {
                Vector x = c.copy().randomize().l2Norm().mul(l).map(new VectorFunction<Float, Float>() {
                    public Float apply(Float v, int row, Vector vector) {
                        return v + Scalar.randomGaussian();
                    }
                });
                Vector y = new Vector(labels).oneHot(i);
                result.addRow(new DataRow().addFeature(x).setLabel(y));
            }
        }

        return result;
    }

    public String toString() {
        String result = "";
        for (DataRow row : this.rows) {
            result += row.toString() + "\n";
        }
        return result;
    }

    public JSONObject toJSON() {
        JSONArray jsonInputs = JSON.newJSONArray();
        JSONArray jsonTargets = JSON.newJSONArray();
        for (int i = 0; i < this.rows.size(); i++) {
            DataRow row = this.rows.get(i);

            JSONArray jsonInput = JSON.newJSONArray();
            for (int j = 0; j < row.features().size(); j++) {
                jsonInput.append(row.features().get(j).toJSON());
            }
            jsonInputs.append(jsonInput);

            jsonTargets.append(row.label().toJSON());
        }

        JSONObject json = JSON.newJSONObject();
        json.setJSONArray("inputs", jsonInputs);
        json.setJSONArray("targets", jsonTargets);
        return json;
    }

    private List<DataRow> rows;
}
