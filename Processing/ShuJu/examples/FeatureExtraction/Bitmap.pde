class Bitmap extends ISearchBitmap
{
  public Bitmap(PImage image) {
    this.image = image;
  }

  public int getWidth() {
    return this.image.width;
  }

  public int getHeight() {
    return this.image.height;
  }

  public int get(int x, int y) {
    if(x < 0 || x >= this.image.width || y < 0 || y >= this.image.height) {
      return 0;
    }
    else {
      return int(brightness(this.image.pixels[y * this.image.width + x])) > 0 ? 0 : 1;
    }
  }

  public void set(int x, int y, int c) {
      if(c > 0) {
          this.image.pixels[y * this.image.width + x] = 0xFF000000;
      }
      else {
          this.image.pixels[y * this.image.width + x] = 0xFFFFFFFF;
      }
  }

  private PImage image;
}
