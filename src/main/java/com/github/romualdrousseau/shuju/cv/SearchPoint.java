package com.github.romualdrousseau.shuju.cv;

public class SearchPoint {
    public SearchPoint(int x, int y, double sad) {
        this.x = x;
        this.y = y;
        this.sad = sad;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double getSAD() {
        return this.sad;
    }

    public boolean equals(SearchPoint o) {
        return this.x == o.x && this.y == o.y;
    }

    private int x;
    private int y;
    private double sad;
}
