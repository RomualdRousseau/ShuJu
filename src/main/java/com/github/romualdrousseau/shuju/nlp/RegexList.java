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
    private ArrayList<String> types = new ArrayList<String>();
    private HashMap<String, String> patterns = new HashMap<String, String>();
    private int vectorSize = 0;

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
    }

    public RegexList(JSONObject json) {
        this.vectorSize = json.getInt("maxVectorSize");

        JSONArray jsonTypes = json.getJSONArray("types");
        for(int i = 0; i < jsonTypes.size(); i++) {
            String s = jsonTypes.getString(i);
            this.types.add(s);
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
        return this.types;
    }

    public int size() {
        return this.types.size();
    }

    public String get(int i) {
        return this.types.get(i);
    }

    public int ordinal(String w) {
        return this.types.indexOf(w);
    }

    public RegexList add(String w) {
        if(StringUtility.isEmpty(w)) {
            return this;
        }
        if(this.types.indexOf(w) >= 0) {
            return this;
        }
        this.types.add(w);
        return this;
    }

    public RegexList addPattern(String pattern, String regex) {
        assert this.types.indexOf(regex) > 0;
        this.patterns.put(pattern, regex);
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

        // int longestMatch = 0;
        // for (String pattern : this.patterns.keySet()) {
        //     Matcher m = Pattern.compile(pattern).matcher(w);
        //     while (m.find()) {
        //         String s = m.group(0);
        //         if(s.length() > longestMatch) {
        //             result = w.replace(s, this.patterns.get(pattern));
        //             longestMatch = s.length();
        //         }
        //     }
        // }

        result = w;
        for (String pattern : this.patterns.keySet()) {
            result = result.replaceAll(pattern, this.patterns.get(pattern));
        }

        return result;
    }

    public String find(String w) {
        String result = null;

        if (StringUtility.isEmpty(w)) {
            return result;
        }

        int longestMatch = 0;
        for (String pattern : this.patterns.keySet()) {
            Matcher m = Pattern.compile(pattern).matcher(w);
            while (m.find()) {
                String s = m.group(0);
                if(s.length() > longestMatch) {
                    result = s;
                    longestMatch = s.length();
                }
            }
        }

        return result;
    }

    public Vector word2vec(String w) {
        Vector result = new Vector(this.vectorSize);

        if (StringUtility.isEmpty(w)) {
            return result;
        }

        for (String pattern : this.patterns.keySet()) {
            if (Pattern.compile(pattern).matcher(w).find()) {
                String t = this.patterns.get(pattern);
                result.set(this.ordinal(t), 1.0f);
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
        return this.types.toString();
    }
}
