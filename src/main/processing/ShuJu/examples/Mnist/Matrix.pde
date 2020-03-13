Matrix image2Matrix(PImage image, int x, int y, int w, int h) {
  Matrix result = new Matrix(h, w);
  image.loadPixels();
  for (int i = 0; i < h; i++) {
    for (int j = 0; j < w; j++) {
      int c = image.pixels[(y * h + i) * image.width + (x * w + j)] >> 16 & 0xFF;
      result.set(i, j, map(c, 0, 255, -0.5, 0.5));
    }
  }
  return result;
}

PImage matrix2Image(Matrix m) {
  PImage result = createImage(m.colCount(), m.rowCount(), RGB);
  result.loadPixels();
  for(int i = 0; i < m.rowCount(); i++) {
    for(int j = 0; j < m.colCount(); j++) {
      result.pixels[i * m.colCount() + j] = color(map(m.get(i, j), -0.5, 0.5, 0, 255)); 
    }
  }
  result.updatePixels();
  return result;
}
