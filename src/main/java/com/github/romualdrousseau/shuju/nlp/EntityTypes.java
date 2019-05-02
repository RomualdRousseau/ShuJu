package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Vector;

public class EntityTypes {
    private ArrayList<String> types = new ArrayList<String>();
    private HashMap<String, String> patterns = new HashMap<String, String>();

    public EntityTypes(JSONArray json) {
        for(int i = 0; i < json.size(); i++) {
            JSONObject entity = json.getJSONObject(i);
            String p = entity.getString("pattern");
            String t = entity.getString("type");
            this.patterns.put(p, t);
            if(this.types.indexOf(t) == -1) {
                this.types.add(t);
            }
        }
    }

    public void registerType(String type, String pattern) {
        this.patterns.put(pattern, type);
        if(this.types.indexOf(type) == -1) {
            this.types.add(type);
        }
    }

    public Vector word2vec(String w) {
        Vector result = new Vector(this.types.size());

        for (String pattern : this.patterns.keySet()) {
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(w);
            if (m.find()) {
                result.set(types.indexOf(this.patterns.get(pattern)), 1.0f);
            }
        }

        return result;
    }

    public JSONArray toJSON() {
        JSONArray json = JSON.getFactory().newJSONArray();
        for (String p : this.patterns.keySet()) {
            String t = this.patterns.get(p);
            JSONObject entity =  JSON.getFactory().newJSONObject();
            entity.setString("pattern", p);
            entity.setString("type", t.toString());
            json.append(entity);
        }
        return json;
    }
}
