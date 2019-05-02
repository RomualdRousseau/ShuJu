package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.Vector;

public class StringColumn implements IColumn<String> {
    private String[] dictionary;

    public StringColumn(String[] dictionary) {
        this.dictionary = dictionary;
    }

    public Vector valueOf(String v) {
        Vector result = new Vector(this.dictionary.length);

        int found = -1;
        for(int i = 0; i < this.dictionary.length; i++) {
            if(v.equals(this.dictionary[i])) {
                found = i;
                break;
            }
        }

        if(found >= 0) {
            result.oneHot(found);
        }

        return result;
    }
}
