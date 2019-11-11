package com.github.romualdrousseau.shuju.cv;

public interface ISearchBitmap {
    int getWidth();

    int getHeight();

    int get(int x, int y);

    void set(int x, int y, int v);

    ISearchBitmap clone();
}
