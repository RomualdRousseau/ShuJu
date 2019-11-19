package com.github.romualdrousseau.shuju.cv.templatematching.shapeextractor;

import java.util.ArrayList;
import java.util.List;

import com.github.romualdrousseau.shuju.cv.ISearchBitmap;
import com.github.romualdrousseau.shuju.cv.SearchPoint;
import com.github.romualdrousseau.shuju.cv.Template;
import com.github.romualdrousseau.shuju.cv.templatematching.IShapeExtractor;
import com.github.romualdrousseau.shuju.cv.templatematching.TemplateMatcher;

public class RectangleExtractor extends IShapeExtractor {

    @Override
    public List<SearchPoint[]> extractAll(ISearchBitmap searchBitmap) {
        ArrayList<SearchPoint[]> result = new ArrayList<SearchPoint[]>();

        ArrayList<List<SearchPoint>> allCorners = new ArrayList<List<SearchPoint>>();
        allCorners.add(
                cornerTopLeft.matchAll(searchBitmap, 0, 0, searchBitmap.getWidth(), searchBitmap.getHeight(), 0.8));
        allCorners.add(
                cornerTopRight.matchAll(searchBitmap, 0, 0, searchBitmap.getWidth(), searchBitmap.getHeight(), 0.8));
        allCorners.add(
                cornerBottomRight.matchAll(searchBitmap, 0, 0, searchBitmap.getWidth(), searchBitmap.getHeight(), 0.8));
        allCorners.add(
                cornerBottomLeft.matchAll(searchBitmap, 0, 0, searchBitmap.getWidth(), searchBitmap.getHeight(), 0.8));

        // Simple version of Hought transformation with 4 pre-defined rotations
        for (int phi = 0; phi < allCorners.size(); phi++) {
            for (SearchPoint corner : allCorners.get(phi)) {
                SearchPoint[] a = houghTransform(phi, corner, allCorners);
                if (count(a) < (a.length - 1)) {
                    continue;
                }

                int[][] bbox = minmax(a);
                if (searchBitmap.get(bbox[0][0], bbox[0][1]) > 0 && searchBitmap.get(bbox[1][0], bbox[0][1]) > 0
                        && searchBitmap.get(bbox[1][0], bbox[1][1]) > 0
                        && searchBitmap.get(bbox[0][0], bbox[1][1]) > 0) {
                    SearchPoint[] newSP = new SearchPoint[] {
                            new SearchPoint(bbox[0][0], bbox[0][1], searchBitmap.get(bbox[0][0], bbox[0][1])),
                            new SearchPoint(bbox[1][0], bbox[1][1], searchBitmap.get(bbox[1][0], bbox[1][1])) };

                    boolean foundDuplicate = false;
                    for (SearchPoint[] sp : result) {
                        if (newSP[0].equals(sp[0]) && newSP[1].equals(sp[1])) {
                            foundDuplicate = true;
                        }
                    }
                    if (!foundDuplicate) {
                        result.add(newSP);
                    }
                }
            }
        }

        if(result.size() > 1) {
            return SearchPoint.RemoveOverlaps(result);
        } else {
            return result;
        }
    }

    @Override
    public SearchPoint[] extractBest(ISearchBitmap searchBitmap) {
        SearchPoint[] result = null;
        int maxArea = 0;

        ArrayList<List<SearchPoint>> allCorners = new ArrayList<List<SearchPoint>>();
        allCorners.add(
                cornerTopLeft.matchAll(searchBitmap, 0, 0, searchBitmap.getWidth(), searchBitmap.getHeight(), 0.8));
        allCorners.add(
                cornerTopRight.matchAll(searchBitmap, 0, 0, searchBitmap.getWidth(), searchBitmap.getHeight(), 0.8));
        allCorners.add(
                cornerBottomRight.matchAll(searchBitmap, 0, 0, searchBitmap.getWidth(), searchBitmap.getHeight(), 0.8));
        allCorners.add(
                cornerBottomLeft.matchAll(searchBitmap, 0, 0, searchBitmap.getWidth(), searchBitmap.getHeight(), 0.8));

        // Simple version of Hought transformation with 4 pre-defined rotations
        for (int i = 0; i < allCorners.size(); i++) {
            for (SearchPoint corner : allCorners.get(i)) {
                SearchPoint[] a = houghTransform(i, corner, allCorners);
                if (count(a) < (a.length - 1)) {
                    continue;
                }

                int[][] bbox = minmax(a);
                if (searchBitmap.get(bbox[0][0], bbox[0][1]) > 0 && searchBitmap.get(bbox[1][0], bbox[0][1]) > 0
                        && searchBitmap.get(bbox[1][0], bbox[1][1]) > 0
                        && searchBitmap.get(bbox[0][0], bbox[1][1]) > 0) {
                    int area = (bbox[1][0] - bbox[0][0]) * (bbox[1][1] - bbox[0][1]);
                    if (area > maxArea) {
                        maxArea = area;
                        result = new SearchPoint[] {
                                new SearchPoint(bbox[0][0], bbox[0][1], searchBitmap.get(bbox[0][0], bbox[0][1])),
                                new SearchPoint(bbox[1][0], bbox[1][1], searchBitmap.get(bbox[1][0], bbox[1][1])) };
                    }
                }
            }
        }

        return result;
    }

    private SearchPoint[] houghTransform(int phi, SearchPoint locus, List<List<SearchPoint>> points) {
        SearchPoint[] a = { locus, null, null, null };

        for (int j = 0; j < points.size(); j++)
            if (j != phi) {
                for (SearchPoint point : points.get(j)) {
                    int[] g = gradient(locus, point);
                    for (int k = 1; k < a.length; k++) {
                        if (g[0] == R[phi][k][0] && g[1] == R[phi][k][1]) {
                            if (a[k] == null || distance(point, locus) < distance(a[k], locus)) {
                                a[k] = point;
                            }
                        }
                    }
                }
            }

        return a;
    }

    private double distance(SearchPoint p1, SearchPoint p2) {
        int vx = p1.getX() - p2.getX();
        int vy = p1.getY() - p2.getY();
        return Math.sqrt(vx * vx + vy * vy);
    }

    private int[] gradient(SearchPoint p1, SearchPoint p2) {
        int vx = p2.getX() - p1.getX();
        vx = (vx == 0) ? 0 : vx / Math.abs(vx);
        int vy = p2.getY() - p1.getY();
        vy = (vy == 0) ? 0 : vy / Math.abs(vy);
        return new int[] { vx, vy };
    }

    private int count(SearchPoint[] points) {
        int count = 0;
        for (int k = 0; k < 4; k++) {
            if (points[k] != null) {
                count++;
            }
        }
        return count;
    }

    private int[][] minmax(SearchPoint[] points) {
        int minX = points[0].getX();
        int minY = points[0].getY();
        int maxX = points[0].getX();
        int maxY = points[0].getY();
        for (int k = 1; k < 4; k++) {
            if (points[k] != null) {
                minX = Math.min(minX, points[k].getX());
                minY = Math.min(minY, points[k].getY());
                maxX = Math.max(maxX, points[k].getX());
                maxY = Math.max(maxY, points[k].getY());
            }
        }
        return new int[][] { { minX, minY }, { maxX, maxY } };
    }

    private int R[][][] = { { { 0, 0 }, { 1, 0 }, { 1, 1 }, { 0, 1 } }, { { 0, 0 }, { -1, 0 }, { -1, 1 }, { 0, 1 } },
            { { 0, 0 }, { -1, 0 }, { -1, -1 }, { 0, -1 } }, { { 0, 0 }, { 1, 0 }, { 1, -1 }, { 0, -1 } } };

    private TemplateMatcher cornerTopLeft = new TemplateMatcher(
            new Template(new float[][] { { 0, 0, 0 }, { 0, 1, 1 }, { 0, 1, 1 } }));

    private TemplateMatcher cornerTopRight = new TemplateMatcher(
            new Template(new float[][] { { 0, 0, 0 }, { 1, 1, 0 }, { 1, 1, 0 } }));

    private TemplateMatcher cornerBottomLeft = new TemplateMatcher(
            new Template(new float[][] { { 0, 1, 1 }, { 0, 1, 1 }, { 0, 0, 0 } }));

    private TemplateMatcher cornerBottomRight = new TemplateMatcher(
            new Template(new float[][] { { 1, 1, 0 }, { 1, 1, 0 }, { 0, 0, 0 } }));
}
