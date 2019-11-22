package com.github.romualdrousseau.shuju.cv;

import java.util.ArrayList;
import java.util.List;

public class SearchPoint {

    public SearchPoint(int x, int y) {
        this.x = x;
        this.y = y;
        this.sad = 0;
    }

    public SearchPoint(int x, int y, float sad) {
        this.x = x;
        this.y = y;
        this.sad = sad;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getSAD() {
        return this.sad;
    }

    public void setSAD(int sad) {
        this.sad = sad;
    }

    public boolean equals(SearchPoint o) {
        return this.x == o.x && this.y == o.y;
    }

    public static int GetArea(SearchPoint[] s) {
        return (s[1].getX() - s[0].getX()) * (s[1].getY() - s[0].getY());
    }

    public static boolean IsOverlap(SearchPoint[] s1, SearchPoint[] s2) {
        return !(s2[1].getX() < s1[0].getX() || s2[0].getX() > s1[1].getX() || s2[1].getY() < s1[0].getY()
                || s2[0].getY() > s1[1].getY());
    }

    public static boolean IsInside(SearchPoint[] points, int x, int y) {
        return points[0].getX() <= x && x <= points[1].getX() && points[0].getY() <= y && y <= points[1].getY();
    }

    public static List<SearchPoint[]> RemoveOverlaps(List<SearchPoint[]> tablesWithOverlaps) {
        ArrayList<SearchPoint[]> result = new ArrayList<SearchPoint[]>();

        for (SearchPoint[] table1 : tablesWithOverlaps) {
            if (table1[1].getX() < table1[0].getX()) {
                continue;
            }

            boolean isNotOverlaped = true;
            for (SearchPoint[] table2 : tablesWithOverlaps) {
                if (table1 != table2 && IsOverlap(table1, table2)) {
                    if (GetArea(table2) > GetArea(table1)) {
                        clipping(table2, table1);
                        isNotOverlaped &= !IsOverlap(table1, table2);
                    }
                }
            }
            if(isNotOverlaped) {
                result.add(table1);
            }
        }

        return result;
    }

    public static List<SearchPoint[]> MergeInX(List<SearchPoint[]> shapes) {
        ArrayList<SearchPoint[]> result = new ArrayList<SearchPoint[]>();

        for (SearchPoint[] shape1 : shapes)
            if (shape1[0] != null && shape1[1] != null) {
                for (SearchPoint[] shape2 : shapes)
                    if (shape1 != shape2 && shape2[0] != null && shape2[1] != null) {
                        if (shape1[0].getY() == shape2[0].getY() && shape1[1].getY() == shape2[1].getY()) {
                            shape1[0].setX(Math.min(shape1[0].getX(), shape2[0].getX()));
                            shape1[1].setX(Math.max(shape1[1].getX(), shape2[1].getX()));
                            shape2[0] = null;
                            shape2[1] = null;
                        }
                    }
                result.add(shape1);
            }
        return result;
    }

    public static List<SearchPoint[]> MergeInY(List<SearchPoint[]> shapes) {
        ArrayList<SearchPoint[]> result = new ArrayList<SearchPoint[]>();

        for (SearchPoint[] shape1 : shapes)
            if (shape1[0] != null && shape1[1] != null) {
                for (SearchPoint[] shape2 : shapes)
                    if (shape1 != shape2 && shape2[0] != null && shape2[1] != null) {
                        if (shape1[0].getX() == shape2[0].getX() && shape1[1].getX() == shape2[1].getX()) {
                            shape1[0].setY(Math.min(shape1[0].getY(), shape2[0].getY()));
                            shape1[1].setY(Math.max(shape1[1].getY(), shape2[1].getY()));
                            shape2[0] = null;
                            shape2[1] = null;
                        }
                    }
                result.add(shape1);
            }
        return result;
    }

    public static List<SearchPoint[]> TrimInX(List<SearchPoint[]> shapes, ISearchBitmap bitmap) {
        for (SearchPoint[] shape : shapes) {
            for (int i = shape[0].getX(); i <= shape[1].getX(); i++) {
                if (SearchPoint.columnIsEmpty(shape, i, bitmap)) {
                    shape[0].setX(i + 1);
                } else {
                    break;
                }
            }

            for (int i = shape[1].getX(); i >= shape[0].getX(); i--) {
                if (SearchPoint.columnIsEmpty(shape, i, bitmap)) {
                    shape[1].setX(i - 1);
                } else {
                    break;
                }
            }
        }

        return shapes;
    }

    public static List<SearchPoint[]> TrimInY(List<SearchPoint[]> shapes, ISearchBitmap bitmap) {
        for (SearchPoint[] shape : shapes) {
            for (int i = shape[0].getY(); i <= shape[1].getY(); i++) {
                if (rowIsEmpty(shape, i, bitmap)) {
                    shape[0].setY(i + 1);
                } else {
                    break;
                }
            }

            for (int i = shape[1].getY(); i >= shape[0].getY(); i--) {
                if (rowIsEmpty(shape, i, bitmap)) {
                    shape[1].setY(i - 1);
                } else {
                    break;
                }
            }
        }

        return shapes;
    }

    private static void clipping(SearchPoint[] s1, SearchPoint[] s2) {
        if (s2[0].getX() < s1[0].getX()) {
            s2[1].setX(s1[0].getX() - 1);
        }
        if (s2[1].getX() > s1[1].getX()) {
            s2[0].setX(s1[1].getX() + 1);
        }
        if (s2[0].getY() < s1[0].getY()) {
            s2[1].setY(s1[0].getY() - 1);
        }
        if (s2[1].getY() > s1[1].getY()) {
            s2[0].setY(s1[1].getY() + 1);
        }
    }

    private static boolean columnIsEmpty(SearchPoint[] table, int colIndex, ISearchBitmap bitmap) {
        boolean isEmpty = true;
        for (int i = table[0].getY(); i <= table[1].getY(); i++) {
            if (bitmap.get(colIndex, i) > 0) {
                isEmpty = false;
                break;
            }
        }
        return isEmpty;
    }

    private static boolean rowIsEmpty(SearchPoint[] table, int rowIndex, ISearchBitmap bitmap) {
        boolean isEmpty = true;
        for (int i = table[0].getX(); i <= table[1].getX(); i++) {
            if (bitmap.get(i, rowIndex) > 0) {
                isEmpty = false;
                break;
            }
        }
        return isEmpty;
    }

    private int x;
    private int y;
    private float sad;
}
