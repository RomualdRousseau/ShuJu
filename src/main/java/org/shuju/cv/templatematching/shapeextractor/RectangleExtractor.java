package org.shuju.cv.templatematching.shapeextractor;

import java.util.List;
import java.util.ArrayList;

import org.shuju.cv.Template;
import org.shuju.cv.ISearchBitmap;
import org.shuju.cv.SearchPoint;
import org.shuju.cv.templatematching.IShapeExtractor;
import org.shuju.cv.templatematching.TemplateMatcher;

public class RectangleExtractor extends IShapeExtractor
{
	public List<SearchPoint[]> extractAll(ISearchBitmap searchBitmap) {
		ArrayList<SearchPoint[]> result = new ArrayList<SearchPoint[]>();
	
		List<SearchPoint> topLefts = cornerTopLeft.matchAll(searchBitmap, 0, 0, searchBitmap.getWidth(), searchBitmap.getHeight(), 0.8);   
		for(SearchPoint topLeft: topLefts) {
			SearchPoint[] shape = extractOneRectangle(searchBitmap, topLeft);
			if(shape != null) {
				result.add(shape);
			}
		}

		return result;
	}

	public SearchPoint[] extractBest(ISearchBitmap searchBitmap) {
		SearchPoint[] result = null;
		int maxAera = 0;
	
		List<SearchPoint> topLefts = cornerTopLeft.matchAll(searchBitmap, 0, 0, searchBitmap.getWidth(), searchBitmap.getHeight(), 0.8);   
		for(SearchPoint topLeft: topLefts) {
			SearchPoint[] shape = extractOneRectangle(searchBitmap, topLeft);
			if(shape != null) {
				int area = (shape[1].getX() - shape[0].getX()) * (shape[1].getY() - shape[0].getY());
				if(area > maxAera) {
					maxAera = area;
					result = shape;
				}
			}
		}

		return result;
	}

	private SearchPoint[] extractOneRectangle(ISearchBitmap searchBitmap, SearchPoint topLeft) {
		int w = searchBitmap.getWidth() - topLeft.getX() - 1;
		int h = searchBitmap.getHeight() - topLeft.getY();

		SearchPoint topRight = cornerTopRight.matchFirst(searchBitmap, topLeft.getX() + 1, topLeft.getY(), w, h, 0.8);
		if(topRight == null) {
			return null;
		}

		w = topRight.getX() - topLeft.getX();  

		SearchPoint bottomLeft = cornerBottomLeft.matchFirst(searchBitmap, topLeft.getX(), topLeft.getY() + 1, w, h, 0.8);
		if(bottomLeft == null) {
			return null;
		}

		h = bottomLeft.getY() - topLeft.getY();

		SearchPoint bottomRight = cornerBottomRight.matchFirst(searchBitmap, topLeft.getX() + 1, topLeft.getY() + 1, w, h, 0.8);
		if(bottomRight == null) {
			return null;
		}

		return new SearchPoint[] {topLeft, bottomRight};
	}
  
  private TemplateMatcher cornerTopLeft = new TemplateMatcher(new Template(new int[][] {
	{0, 0, 0},
	{0, 1, 1},
	{0, 1, 1}
  }));
  
  private TemplateMatcher cornerTopRight = new TemplateMatcher(new Template(new int[][] {
	{0, 0, 0},
	{1, 1, 0},
	{1, 1, 0}
  }));
  
  private TemplateMatcher cornerBottomLeft = new TemplateMatcher(new Template(new int[][] {
	{0, 1, 1},
	{0, 1, 1},
	{0, 0, 0}
  }));
  
  private TemplateMatcher cornerBottomRight = new TemplateMatcher(new Template(new int[][] {
	{1, 1, 0},
	{1, 1, 0},
	{0, 0, 0}
  }));
}
