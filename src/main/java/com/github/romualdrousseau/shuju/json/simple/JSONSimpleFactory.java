package com.github.romualdrousseau.shuju.json.simple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONFactory;
import com.github.romualdrousseau.shuju.json.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONSimpleFactory implements JSONFactory {
    public JSONArray loadJSONArray(String filePath) {
        try (BufferedReader reader = createReader(filePath)) {
            return new JSONSimpleArray((org.json.simple.JSONArray) new JSONParser().parse(reader));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject loadJSONObject(String filePath) {
        try (BufferedReader reader = createReader(filePath)) {
            return new JSONSimpleObject((org.json.simple.JSONObject) new JSONParser().parse(reader));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject parseJSONObject(String data) {
        try (Reader reader = new StringReader(data)) {
            return new JSONSimpleObject((org.json.simple.JSONObject) new JSONParser().parse(reader));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray parseJSONArray(String data) {
        try (Reader reader = new StringReader(data)) {
            return new JSONSimpleArray((org.json.simple.JSONArray) new JSONParser().parse(reader));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray newJSONArray() {
        return new JSONSimpleArray(new org.json.simple.JSONArray());
    }

    public JSONObject newJSONObject() {
        return new JSONSimpleObject(new org.json.simple.JSONObject());
    }

    public void saveJSONArray(JSONArray a, String filePath) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            writer.write(((JSONSimpleArray) a).ja.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveJSONObject(JSONObject o, String filePath) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            writer.write(((JSONSimpleObject) o).jo.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedReader createReader(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(filePath)), StandardCharsets.UTF_8));

        // consume the Unicode BOM (byte order marker) if present
        reader.mark(1);
        int c = reader.read();
        // if not the BOM, back up to the beginning again
        if (c != '\uFEFF') {
            reader.reset();
        }

        return reader;
    }
}
