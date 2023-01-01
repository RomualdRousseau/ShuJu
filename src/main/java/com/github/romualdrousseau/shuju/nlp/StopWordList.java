package com.github.romualdrousseau.shuju.nlp;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.util.StringUtils;

public class StopWordList  {
    private String[] stopwords;

    public StopWordList() {
        this.stopwords = new String[] {};
    }

    public StopWordList(String[] stopwords) {
        this.stopwords = stopwords;
    }

    public StopWordList(JSONArray json) {
        this.stopwords = new String[json.size()];
        for (int i = 0; i < json.size(); i++) {
            String p = json.getString(i);
            this.stopwords[i] = p;
        }
    }

    public String[] values() {
        return this.stopwords;
    }

    public int size() {
        return this.stopwords.length;
    }

    public String get(int i) {
        return this.stopwords[i];
    }

    public String removeStopWords(String s) {
        if (StringUtils.isBLank(s)) {
            return "";
        }

        for (int i = 0; i < this.stopwords.length; i++) {
            s = s.replaceAll("(?i)" + this.stopwords[i], " ");
        }

        return s;
    }

    public JSONArray toJSON() {
        JSONArray json = JSON.newJSONArray();
        for (int i = 0; i < this.stopwords.length; i++) {
            String stopword = this.stopwords[i];
            json.append(stopword);
        }
        return json;
    }

    @Override
    public String toString() {
        return this.stopwords.toString();
    }
}
