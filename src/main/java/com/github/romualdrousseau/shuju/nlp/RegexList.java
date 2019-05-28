package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.util.StringUtility;

public class RegexList implements BaseList {
    private ArrayList<String> regexes = new ArrayList<String>();
    private HashMap<String, String> patterns = new HashMap<String, String>();
    private int vectorSize = 0;

    public RegexList(int vectorSize) {
        this.vectorSize = vectorSize;
    }

    public RegexList(int vectorSize, String[] regexes) {
        this.vectorSize = vectorSize;
        this.regexes.addAll(Arrays.asList(regexes));
    }

    public RegexList(int vectorSize, String[] regexes, HashMap<String, String> patterns) {
        this.vectorSize = vectorSize;
        this.regexes.addAll(Arrays.asList(regexes));
        this.patterns.putAll(patterns);
    }

    public RegexList(JSONObject json) {
        this.vectorSize = json.getInt("maxVectorSize");

        JSONArray jsonTypes = json.getJSONArray("types");
        for(int i = 0; i < jsonTypes.size(); i++) {
            String s = jsonTypes.getString(i);
            this.regexes.add(s);
        }

        JSONArray jsonPatterns = json.getJSONArray("patterns");
        for(int i = 0; i < jsonPatterns.size(); i++) {
            JSONObject entity = jsonPatterns.getJSONObject(i);
            String p = entity.getString("pattern");
            String t = entity.getString("type");
            this.patterns.put(p, t);
        }
    }

    public List<String> values() {
        return this.regexes;
    }

    public int size() {
        return this.regexes.size();
    }

    public String get(int i) {
        return this.regexes.get(i);
    }

    public int ordinal(String w) {
        return this.regexes.indexOf(w);
    }

    public RegexList add(String w) {
        if(StringUtility.isEmpty(w)) {
            return this;
        }
        if(this.regexes.indexOf(w) >= 0) {
            return this;
        }
        this.regexes.add(w);
        return this;
    }

    public RegexList addPattern(String pattern, String regex) {
        assert this.regexes.indexOf(regex) > 0;
        this.patterns.put(pattern, regex);
        return this;
    }

    public int getVectorSize() {
        return this.vectorSize;
    }

    public Vector word2vec(String w) {
        Vector result = new Vector(this.vectorSize);

        if(w == null) {
            return result;
        }

        for (String pattern : this.patterns.keySet()) {
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(w);
            if (m.matches()) {
                String t = this.patterns.get(pattern);
                result.set(this.ordinal(t), 1.0f);
            }
        }

        return result;
    }

    public JSONObject toJSON() {
        JSONArray jsonTypes = JSON.newJSONArray();
        for (String t : this.regexes) {
            jsonTypes.append(t);
        }

        JSONArray jsonPatterns = JSON.newJSONArray();
        for (String p : this.patterns.keySet()) {
            String t = this.patterns.get(p);
            JSONObject entity =  JSON.newJSONObject();
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
        return this.regexes.toString();
    }
}
