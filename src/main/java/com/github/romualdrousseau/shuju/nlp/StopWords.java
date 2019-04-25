package com.github.romualdrousseau.shuju.nlp;

import com.github.romualdrousseau.shuju.json.JSONFactory;
import com.github.romualdrousseau.shuju.json.JSONArray;

public class StopWords {
    private String[] stopwords;

    public String removeStopWords(String s) {
        for (int i = 0; i < this.stopwords.length; i++) {
            s = s.replaceAll(this.stopwords[i], "");
        }
        return s;
    }

    public void fromJSON(JSONArray json) {
        this.stopwords = new String[json.size()];
        for (int i = 0; i < json.size(); i++) {
            String p = json.getString(i);
            this.stopwords[i] = p;
        }
    }

    public JSONArray toJSON(JSONFactory jsonFactory) {
        JSONArray json = jsonFactory.newJSONArray();
        for (int i = 0; i < this.stopwords.length; i++) {
            String stopword = this.stopwords[i];
            json.setString(i, stopword);
        }
        return json;
    }
}
