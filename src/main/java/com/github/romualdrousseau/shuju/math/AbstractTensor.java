package com.github.romualdrousseau.shuju.math;

public abstract class AbstractTensor<A> {

    public int[] shape;
    public A data;

    public AbstractTensor(int[] shape, A data) {
        this.shape = shape;
        this.data = data;
    }

    protected static float[] memAlloc(int[] shape) {
        return new float[memSize(shape)];
    }

    protected static int memSize(int[] shape) {
        int sum = 1;
        for(int dim : shape) {
            sum *= dim;
        }
        return sum;
    }

    protected static int loc2off(int[] loc, int[] shape) {
        int off = 0;
        for(int i = 0; i < loc.length; i++) {
            int space = 1;
            for(int j = i + 1; j < shape.length; j++) {
                space *= shape[i];
            }
            off += space * loc[i];
        }
        return off;
    }

    protected static int[] off2loc(int off, int[] shape) {
        int[] loc = new int[shape.length];
        for(int i = 0; i < loc.length; i++) {
            int space = 1;
            for(int j = i + 1; j < shape.length; j++) {
                space *= shape[i];
            }
            loc[i] = off / space;
            off = off % space;
        }
        return loc;
    }
}
