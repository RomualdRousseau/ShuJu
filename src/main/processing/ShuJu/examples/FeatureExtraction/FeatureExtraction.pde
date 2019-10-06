import com.github.romualdrousseau.shuju.cv.EdgeFilter;
import com.github.romualdrousseau.shuju.cv.SearchPoint;
import com.github.romualdrousseau.shuju.cv.ISearchBitmap;
import com.github.romualdrousseau.shuju.cv.templatematching.shapeextractor.RectangleExtractor;

import java.util.List;

PImage image;
List<SearchPoint[]> shapes;
SearchPoint[] bestShape;

void setup() {
  size(500, 500);
  image = loadImage("data/image.png");

  PImage temp = createImage(image.width, image.height, RGB);
  new EdgeFilter().apply(new Bitmap(image), new Bitmap(temp), 1.0);

  shapes = new RectangleExtractor().extractAll(new Bitmap(temp));
  bestShape = new RectangleExtractor().extractBest(new Bitmap(temp));
}

void draw() {
  background(image);

  noFill();
  strokeWeight(4);

  stroke(255, 0, 0);
  for(SearchPoint[] shape: shapes) {
    rect(shape[0].getX(), shape[0].getY(), shape[1].getX() - shape[0].getX(), shape[1].getY() - shape[0].getY());
  }

  stroke(0, 0, 255);
  rect(bestShape[0].getX(), bestShape[0].getY(), bestShape[1].getX() - bestShape[0].getX(), bestShape[1].getY() - bestShape[0].getY());
}
