import java.util.List;
import com.github.romualdrousseau.shuju.cv.templatematching.SearchPoint;
import com.github.romualdrousseau.shuju.cv.templatematching.ISearchBitmap;
import com.github.romualdrousseau.shuju.cv.templatematching.shapeextractor.RectangleExtractor;

PImage image;
List<SearchPoint[]> shapes;

void setup() {
  size(500, 500);
  image = loadImage("data/image.png");
  shapes = new RectangleExtractor().extractAll(new Bitmap(image));
}

void draw() {
  background(image);
  
  noFill();
  strokeWeight(4); 
  stroke(255, 0, 0);
  for(SearchPoint[] shape: shapes) {
    rect(shape[0].getX(), shape[0].getY(), shape[1].getX() - shape[0].getX(), shape[1].getY() - shape[0].getY());
  }
}