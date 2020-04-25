Tensor2D image2Tensor2D(PImage image, int x, int y, int w, int h) {
  Tensor2D result = new Tensor2D(h, w);
  image.loadPixels();
  for (int i = 0; i < h; i++) {
    for (int j = 0; j < w; j++) {
      float c = red(image.pixels[(y * h + i) * image.width + (x * w + j)]);
      result.set(i, j, map(c, 0, 255, 0, 1));
    }
  }
  return result;
}

PImage Tensor2D2Image(Tensor2D m) {
  PImage result = createImage(m.shape[1], m.shape[0], RGB);
  result.loadPixels();
  for(int i = 0; i < m.shape[0]; i++) {
    for(int j = 0; j < m.shape[1]; j++) {
      result.pixels[i * m.shape[1] + j] = color(map(m.get(i, j), 0, 1, 0, 255));
    }
  }
  result.updatePixels();
  return result;
}
