package com.github.romualdrousseau.shuju;

import java.util.List;
import java.util.ArrayList;

import com.github.romualdrousseau.shuju.json.JSONFactory;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Vector;

public class DataSet {
    public DataSet() {
        this.rows = new ArrayList<DataRow>();
    }

    public DataSet(List<DataRow> rows) {
        this.rows = rows;
    }

    public List<DataRow> rows() {
        return this.rows;
    }

    public DataSet addRow(DataRow row) {
        this.rows.add(row);
        return this;
    }

    public DataSet shuffle() {
        java.util.Collections.shuffle(this.rows);
        return this;
    }

    public DataSet subset(int rowStart, int rowEnd) {
        List<DataRow> temp = new ArrayList<DataRow>();
        for (int i = rowStart; i < rowEnd; i++) {
            temp.add(this.rows.get(i));
        }
        return new DataSet(temp);
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
        while (i < this.rows.size() && this.rows.get(i).features().equals(row.features())) i++;
        return (i == this.rows.size()) ? -1 : i;
    }

    public Vector[] featuresAsVectorArray() {
        Vector[] result = new Vector[this.rows.size()];
        for(int i = 0; i < this.rows.size(); i++) {
            result[i] = this.rows.get(i).featuresAsOneVector();
        }
        return result;
    }

    public Vector[] labelsAsVectorArray() {
        Vector[] result = new Vector[this.rows.size()];
        for(int i = 0; i < this.rows.size(); i++) {
            result[i] = this.rows.get(i).label();
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

    public JSONObject toJSON(JSONFactory jsonFactory) {
        JSONArray jsonInputs = jsonFactory.newJSONArray();
        JSONArray jsonTargets = jsonFactory.newJSONArray();
        for (int i = 0; i < this.rows.size(); i++) {
            DataRow row = this.rows.get(i);

            JSONArray jsonInput = jsonFactory.newJSONArray();
            for (int j = 0; j < row.features().size(); j++) {
                jsonInput.append(row.features().get(j).toJSON());
            }
            jsonInputs.append(jsonInput);

            jsonTargets.append(row.label().toJSON());
        }

        JSONObject json = jsonFactory.newJSONObject();
        json.setJSONArray("inputs", jsonInputs);
        json.setJSONArray("targets", jsonTargets);
        return json;
    }

    public void fromJSON(JSONObject json) {
        JSONArray jsonInputs = json.getJSONArray("inputs");
        JSONArray jsonTargets = json.getJSONArray("targets");

        ArrayList<DataRow> rows = new ArrayList<DataRow>();

        for (int i = 0; i < jsonInputs.size(); i++) {
            DataRow row = new DataRow();

            JSONArray jsonInput = jsonInputs.getJSONArray(i);
            for (int j = 0; j < jsonInput.size(); j++) {
                row.addFeature(new Vector(jsonInputs.getJSONObject(j)));
            }

            JSONObject jsonTarget = jsonTargets.getJSONObject(i);
            row.setLabel(new Vector(jsonTarget));

            rows.add(row);
        }

        this.rows = rows;
    }

    private List<DataRow> rows;
}
