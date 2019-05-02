package com.github.romualdrousseau.shuju.nlp;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;

public class StopWords {
    private String[] stopwords;

    public StopWords(JSONArray json) {
        this.stopwords = new String[json.size()];
        for (int i = 0; i < json.size(); i++) {
            String p = json.getString(i);
            this.stopwords[i] = p;
        }
    }

    public String removeStopWords(String s) {
        for (int i = 0; i < this.stopwords.length; i++) {
            s = s.replaceAll(this.stopwords[i], "");
        }
        return s;
    }

    public JSONArray toJSON() {
        JSONArray json = JSON.getFactory().newJSONArray();
        for (int i = 0; i < this.stopwords.length; i++) {
            String stopword = this.stopwords[i];
            json.setString(i, stopword);
        }
        return json;
    }
}
