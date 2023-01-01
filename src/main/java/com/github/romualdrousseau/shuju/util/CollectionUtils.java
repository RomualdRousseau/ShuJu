package com.github.romualdrousseau.shuju.util;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {
    
    public static List<Integer> mutableRange(int a, int b) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = a; i < b; i++) {
            result.add(i);
        }
        return result;
    }
}
