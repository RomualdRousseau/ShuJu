package com.github.romualdrousseau.shuju.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.TreeMap;

public class TreeMapWithDuplicates<K, V> {
    public List<V> get(K key) {
        return map.get(key);
    }

    public void put(K key, V value) {
        List<V> slot = map.get(key);
        if (slot == null) {
            slot = new ArrayList<V>();
        }

        slot.add(value);
        map.put(key, slot);
    }

    public List<Entry<K, V>> entrySet() {
        ArrayList<Entry<K, V>> result = new ArrayList<Entry<K, V>>();

        for (Entry<K, List<V>> entry : map.entrySet()) {
            for (V value : entry.getValue()) {
                result.add(new SimpleEntry<K, V>(entry.getKey(), value));
            }
        }

        return result;
    }

    public List<Entry<K, V>> entrySet(int k) {
        ArrayList<Entry<K, V>> result = new ArrayList<Entry<K, V>>();
        int n = 0;

        for (Entry<K, List<V>> entry : map.entrySet()) {
            for (V value : entry.getValue()) {
                if (n >= k) {
                    return result;
                }
                result.add(new SimpleEntry<K, V>(entry.getKey(), value));
                n++;
            }
        }

        return result;
    }

    private TreeMap<K, List<V>> map = new TreeMap<K, List<V>>();
}
