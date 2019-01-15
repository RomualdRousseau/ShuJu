package com.github.romualdrousseau.shuju.cv.templatematching;

import java.util.List;

import com.github.romualdrousseau.shuju.cv.ISearchBitmap;
import com.github.romualdrousseau.shuju.cv.SearchPoint;

public abstract class IShapeExtractor {
    public abstract List<SearchPoint[]> extractAll(ISearchBitmap bitmap);

    public abstract SearchPoint[] extractBest(ISearchBitmap bitmap);
}
