package com.github.romualdrousseau.shuju.cv;

public class Template {
    public Template(int[][] data) {
        this.data = data;
    }

    public int getWidth() {
        return this.data[0].length;
    }

    public int getHeight() {
        return this.data.length;
    }

    public int get(int x, int y) {
        return this.data[y][x];
    }

    public int sobel(ISearchBitmap searchBitmap, int x, int y) {
        int w = this.data[0].length / 2;
        int h = this.data.length / 2;
        int acc = 0;
        for (int i = 0; i < this.data.length; i++) {
            for (int j = 0; j < this.data[0].length; j++) {
                acc += this.data[i][j] * searchBitmap.get(x + j - w, y + i - h);
            }
        }
        return acc;
    }

    private int[][] data;
}
