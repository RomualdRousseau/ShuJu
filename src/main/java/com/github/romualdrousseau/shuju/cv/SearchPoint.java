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

    public static boolean isInRange(SearchPoint[] points, int x, int y) {
        return points[0].getX() <= x && x <= points[1].getX() && points[0].getY() <= y && y <= points[1].getY();
    }

    private int x;
    private int y;
    private double sad;
}
