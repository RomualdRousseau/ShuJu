package org.shuju.cv.templatematching;

import java.util.List;
import java.util.ArrayList;

import org.shuju.cv.SearchPoint;
import org.shuju.cv.ISearchBitmap;
import org.shuju.cv.Template;

public class TemplateMatcher
{
  public TemplateMatcher(Template template) {
    this.template = template;
  }
  
  public List<SearchPoint> matchAll(ISearchBitmap searchBitmap, int x, int y, int w, int h, double threshold) {
    ArrayList<SearchPoint> result = new ArrayList<SearchPoint>();
  
    for(int i = y; i < y + h; i++) {
      for(int j = x; j < x + w; j++) {   
        double sad = SAD(searchBitmap, j, i); 
        double score = normalize(sad);
        if(score > threshold) {
            result.add(new SearchPoint(j, i, sad));
        }
      }
    }
    
    return result;
  }

  public SearchPoint matchFirst(ISearchBitmap searchBitmap, int x, int y, int w, int h, double threshold) {
    for(int i = y; i < y + h; i++) {
      for(int j = x; j < x + w; j++) {   
        double sad = SAD(searchBitmap, j, i); 
        double score = normalize(sad);
        if(score > threshold) {
            return new SearchPoint(j, i, sad);
        }
      }
    }
    
    return null;
  }

  public SearchPoint matchBest(ISearchBitmap searchBitmap, int x, int y, int w, int h) {
    SearchPoint result = null;
    double maxScore = 0.0;
    
    for(int i = y; i < y + h; i++) {
      for(int j = x; j < x + w; j++) {
          double sad = SAD(searchBitmap, j, i);
          double score = normalize(sad);
          if(score > maxScore) {
            maxScore = score;
            result = new SearchPoint(j, i, sad);
          }
      }
    }
    
    return result;
  }

  private double normalize(double v) {
    return 1.0 - v / (this.template.getWidth() * this.template.getHeight());
  }

  private double SAD(ISearchBitmap searchBitmap, int x, int y) {
    int hw = this.template.getWidth() / 2;
    int hh = this.template.getHeight() / 2;
    double acc = 0.0;

    for(int i = 0; i < this.template.getHeight(); i++) {
      for(int j = 0; j < this.template.getWidth(); j++) {
        int searchPixel = searchBitmap.get(x + j - hw, y + i - hh);
        int templatePixel = this.template.get(j, i);
        acc += Math.abs(searchPixel - templatePixel);
      }
    }

    return acc;
  }
  
  private Template template;
}