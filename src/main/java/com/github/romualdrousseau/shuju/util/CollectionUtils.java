package com.github.romualdrousseau.shuju.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.DataBuffers;
import org.tensorflow.types.TFloat32;

public class CollectionUtils {

    public static List<Integer> mutableRange(int a, int b) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = a; i < b; i++) {
            result.add(i);
        }
        return result;
    }

    public static <T> List<T> shuffle(List<T> l) {
        Collections.shuffle(l);
        return l;
    }

    public static TFloat32 ListOfIntegertoTFloat32(final List<Integer> l) {
        final float[] result = new float[l.size()];
        for (int i = 0, j = 0; i < l.size(); i++, j++) {
            result[j] = (float) l.get(i);
        }
        return TFloat32.tensorOf(Shape.of(1, l.size()), DataBuffers.of(result));
    }
}
