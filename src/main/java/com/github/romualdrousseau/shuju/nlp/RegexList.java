package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor1D;
import com.github.romualdrousseau.shuju.util.StringUtility;

public class RegexList implements BaseList {

    public RegexList(int vectorSize) {
        this.vectorSize = vectorSize;
    }

    public RegexList(int vectorSize, String[] types) {
        this.vectorSize = vectorSize;
        this.types.addAll(Arrays.asList(types));
    }

    public RegexList(int vectorSize, String[] types, HashMap<String, String> patterns) {
        this.vectorSize = vectorSize;
        this.types.addAll(Arrays.asList(types));
        this.patterns.putAll(patterns);

        for (String regex : this.patterns.keySet()) {
            Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            this.compiledPatterns.put(regex, p);
        }
    }

    public RegexList(JSONObject json) {
        this.vectorSize = json.getInt("maxVectorSize");

        JSONArray jsonTypes = json.getJSONArray("types");
        for (int i = 0; i < jsonTypes.size(); i++) {
            String s = jsonTypes.getString(i);
            this.types.add(s);
        }

        JSONArray jsonPatterns = json.getJSONArray("patterns");
        for (int i = 0; i < jsonPatterns.size(); i++) {
            JSONObject entity = jsonPatterns.getJSONObject(i);
            String regex = entity.getString("pattern");
            String type = entity.getString("type");
            this.patterns.put(regex, type);
        }

        for (String regex : this.patterns.keySet()) {
            Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            this.compiledPatterns.put(regex, p);
        }
    }

    public List<String> values() {
        return this.types;
    }

    public int size() {
        return this.types.size();
    }

    public String get(int i) {
        if (i >= this.types.size()) {
            return null;
        }
        return this.types.get(i);
    }

    public int ordinal(String w) {
        return this.types.indexOf(w);
    }

    public RegexList add(String w) {
        if (StringUtility.isEmpty(w)) {
            return this;
        }
        if (this.types.indexOf(w) >= 0) {
            return this;
        }
        this.types.add(w);
        return this;
    }

    public RegexList addPattern(String regex, String type) {
        assert this.types.indexOf(type) > 0;
        this.patterns.put(regex, type);
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        this.compiledPatterns.put(regex, p);
        return this;
    }

    public int getVectorSize() {
        return this.vectorSize;
    }

    public String anonymize(String w) {
        String result = "";

        if (StringUtility.isEmpty(w)) {
            return result;
        }

        result = w;
        int order = this.types.size();
        for (Entry<String, Pattern> pattern : this.compiledPatterns.entrySet()) {
            Matcher m = pattern.getValue().matcher(w);
            if (m.find()) {
                String type = this.patterns.get(pattern.getKey());
                if (this.types.indexOf(type) < order) {
                    result = m.replaceAll(type);
                }
            }
        }

        return result;
    }

    public String find(String w) {
        String result = null;

        if (StringUtility.isEmpty(w)) {
            return result;
        }

        for (Entry<String, Pattern> pattern : this.compiledPatterns.entrySet()) {
            Matcher m = pattern.getValue().matcher(w);
            if (m.find()) {
                result = m.group(0);
            }
        }

        return result;
    }

    public Tensor1D word2vec(String w) {
        Tensor1D result = new Tensor1D(this.vectorSize);

        if (StringUtility.isEmpty(w)) {
            return result;
        }

        for (Entry<String, Pattern> pattern : this.compiledPatterns.entrySet()) {
            Matcher m = pattern.getValue().matcher(w);
            if (m.find()) {
                result.set(this.ordinal(this.patterns.get(pattern.getKey())), 1.0f);
            }
        }

        return result;
    }

    public JSONObject toJSON() {
        JSONArray jsonTypes = JSON.newJSONArray();
        for (String t : this.types) {
            jsonTypes.append(t);
        }

        JSONArray jsonPatterns = JSON.newJSONArray();
        for (String p : this.patterns.keySet()) {
            String t = this.patterns.get(p);
            JSONObject entity = JSON.newJSONObject();
            entity.setString("pattern", p);
            entity.setString("type", t.toString());
            jsonPatterns.append(entity);
        }

        JSONObject json = JSON.newJSONObject();
        json.setInt("maxVectorSize", this.vectorSize);
        json.setJSONArray("types", jsonTypes);
        json.setJSONArray("patterns", jsonPatterns);
        return json;
    }

    @Override
    public String toString() {
        return this.types.toString();
    }

    private ArrayList<String> types = new ArrayList<String>();
    private HashMap<String, String> patterns = new HashMap<String, String>();
    private HashMap<String, Pattern> compiledPatterns = new HashMap<String, Pattern>();
    private int vectorSize = 0;
}
