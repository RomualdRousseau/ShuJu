package org.shuju.cv.templatematching;

import java.util.List;

import org.shuju.cv.SearchPoint;
import org.shuju.cv.ISearchBitmap;

public abstract class IShapeExtractor
{
  public abstract List<SearchPoint[]> extractAll(ISearchBitmap bitmap);

  public abstract SearchPoint[] extractBest(ISearchBitmap bitmap);
}