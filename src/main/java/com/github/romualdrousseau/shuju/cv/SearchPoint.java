package com.github.romualdrousseau.shuju.cv;

import java.util.ArrayList;
import java.util.List;

public class SearchPoint {
    public SearchPoint(int x, int y, float sad) {
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

    public float getSAD() {
        return this.sad;
    }

    public boolean equals(SearchPoint o) {
        return this.x == o.x && this.y == o.y;
    }

    public static boolean IsInRange(SearchPoint[] points, int x, int y) {
        return points[0].getX() <= x && x <= points[1].getX() && points[0].getY() <= y && y <= points[1].getY();
    }

    public static List<SearchPoint[]> RemoveOverlaps(List<SearchPoint[]> tablesWithOverlaps) {
        ArrayList<SearchPoint[]> result = new ArrayList<SearchPoint[]>();

        ArrayList<SearchPoint[]> tmp = new ArrayList<SearchPoint[]>();
        for (SearchPoint[] table1 : tablesWithOverlaps) {
            for (SearchPoint[] table2 : tablesWithOverlaps) {
                if (table1 != table2 && overlap(table1, table2)) {
                    int a1 = area(table1);
                    int a2 = area(table2);
                    if (a2 > a1) {
                        clipping(table2, table1);
                    }
                }
            }
            tmp.add(table1);
        }

        SearchPoint[] prevTable = null;
        for (SearchPoint[] table : tmp) {
            if (prevTable == null) {
                prevTable = table;
            } else if (overlap(prevTable, table)) {
                if (area(table) > area(prevTable)) {
                    prevTable = table;
                }
            } else {
                result.add(prevTable);
                prevTable = table;
            }
        }
        result.add(prevTable);

        return result;
    }

    public static List<SearchPoint[]> MergeInX(List<SearchPoint[]> shapes) {
        ArrayList<SearchPoint[]> result = new ArrayList<SearchPoint[]>();

        for (SearchPoint[] shape1 : shapes)
            if (shape1[0] != null && shape1[1] != null) {
                for (SearchPoint[] shape2 : shapes)
                    if (shape1 != shape2 && shape2[0] != null && shape2[1] != null) {
                        if (shape1[0].getY() == shape2[0].getY() && shape1[1].getY() == shape2[1].getY()) {
                            shape1[0] = new SearchPoint(Math.min(shape1[0].getX(), shape2[0].getX()), shape1[0].getY(),
                                    shape1[0].getSAD());
                            shape1[1] = new SearchPoint(Math.max(shape1[1].getX(), shape2[1].getX()), shape1[1].getY(),
                                    shape1[1].getSAD());
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
                            shape1[0] = new SearchPoint(shape1[0].getX(), Math.min(shape1[0].getY(), shape2[0].getY()),
                                    shape1[0].getSAD());
                            shape1[1] = new SearchPoint(shape1[1].getX(), Math.max(shape1[1].getY(), shape2[1].getY()),
                                    shape1[1].getSAD());
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
                if (columnIsEmpty(shape, i, bitmap)) {
                    shape[0] = new SearchPoint(i + 1, shape[0].getY(), shape[0].getSAD());
                } else {
                    break;
                }
            }

            for (int i = shape[1].getX(); i >= shape[0].getX(); i--) {
                if (columnIsEmpty(shape, i, bitmap)) {
                    shape[1] = new SearchPoint(i - 1, shape[1].getY(), shape[1].getSAD());
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
                    shape[0] = new SearchPoint(shape[0].getX(), i + 1, shape[0].getSAD());
                } else {
                    break;
                }
            }

            for (int i = shape[1].getY(); i >= shape[0].getY(); i--) {
                if (rowIsEmpty(shape, i, bitmap)) {
                    shape[1] = new SearchPoint(shape[1].getX(), i - 1, shape[1].getSAD());
                } else {
                    break;
                }
            }
        }

        return shapes;
    }

    private static int area(SearchPoint[] s) {
        return (s[1].getX() - s[0].getX()) * (s[1].getY() - s[0].getY());
    }

    private static boolean overlap(SearchPoint[] s1, SearchPoint[] s2) {
        return !(s2[1].getX() < s1[0].getX() || s2[0].getX() > s1[1].getX() || s2[1].getY() < s1[0].getY()
                || s2[0].getY() > s1[1].getY());
    }

    private static void clipping(SearchPoint[] s1, SearchPoint[] s2) {
        if (s2[0].getX() < s1[0].getX()) {
            s2[1] = new SearchPoint(s1[0].getX() - 1, s2[1].getY(), 0);
        }
        if (s2[1].getX() > s1[1].getX()) {
            s2[0] = new SearchPoint(s1[1].getX() + 1, s2[0].getY(), 0);
        }
        if (s2[0].getY() < s1[0].getY()) {
            s2[1] = new SearchPoint(s2[1].getX(), s1[0].getY() - 1, 0);
        }
        if (s2[1].getY() > s1[1].getY()) {
            s2[0] = new SearchPoint(s2[0].getX(), s1[1].getY() + 1, 0);
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
