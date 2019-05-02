package com.github.romualdrousseau.shuju.json;

public class JSON {
    private static JSONFactory Factory;

    public static void setFacory(JSONFactory factory) {
        JSON.Factory = factory;
    }

    public static JSONFactory getFactory() {
        return JSON.Factory;
    }
}
