package com.github.romualdrousseau.shuju.cv;

import com.github.romualdrousseau.shuju.math.Tensor2D;

public class Template {
    public Template(float[][] data) {
        this.data = data;
    }

    public Template(Tensor2D data) {
        this.data = data.getFloats();
    }

    public int getWidth() {
        return this.data[0].length;
    }

    public int getHeight() {
        return this.data.length;
    }

    public float get(int x, int y) {
        return this.data[y][x];
    }

    public float sobel(ISearchBitmap searchBitmap, int x, int y) {
        int w = (this.data[0].length - 1) / 2;
        int h = (this.data.length - 1) / 2;
        float acc = 0;
        for (int i = 0; i < this.data.length; i++) {
            for (int j = 0; j < this.data[0].length; j++) {
                acc += this.data[i][j] * Float.valueOf(searchBitmap.get(x - w + j, y - h + i));
            }
        }
        return acc;
    }

    private float[][] data;
}
