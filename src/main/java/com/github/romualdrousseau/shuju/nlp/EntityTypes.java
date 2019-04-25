package com.github.romualdrousseau.shuju.nlp;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.romualdrousseau.shuju.json.JSONFactory;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class EntityTypes<T extends Enum<T>> {
    private HashMap<String, T> entities = new HashMap<String, T>();

    public T[] find(String s, T[] result) {
        int i = 0;

        for (String pattern : this.entities.keySet()) {
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(s);
            if (m.find()) {
                result[i] = this.entities.get(pattern);
            }
            i++;
        }

        return result;
    }

    public void fromJSON(JSONArray json, Class<T> enumType) {
        this.entities.clear();
        for(int i = 0; i < json.size(); i++) {
            JSONObject entity = json.getJSONObject(i);
            String p = entity.getString("pattern");
            T t = Enum.valueOf(enumType, entity.getString("type"));
            this.entities.put(p, t);
        }
    }

    public JSONArray toJSON(JSONFactory jsonFactory) {
        JSONArray json = jsonFactory.newJSONArray();
        for (String p : this.entities.keySet()) {
            T t = this.entities.get(p);
            JSONObject entity = jsonFactory.newJSONObject();
            entity.setString("pattern", p);
            entity.setString("type", t.toString());
            json.append(entity);
        }
        return json;
    }
}
