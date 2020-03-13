import java.io.DataInputStream;

final int MnistImageSize = 28;
final int MnistLabelSize = 10;

PImage readMnistImages(String fileName, int maxOfImages) {
  DataInputStream input = new DataInputStream(createInput(fileName));
  try {
    int magicNumber = input.readInt();
    if (magicNumber != 2051) {
      throw new IOException("wrong format");
    }

    final int numberOfImages = min(maxOfImages, input.readInt());
    final int ww = ceil(sqrt(numberOfImages));
    final int w = input.readInt();
    final int h = input.readInt();
    
    assert(w == h);
    assert(w == MnistImageSize);
 
    PImage result = createImage(w * ww, h * ww, RGB);

    result.loadPixels();
    for (int k = 0; k < numberOfImages; k++) {
      final int si = w * (k / ww);
      final int sj = h * (k % ww);
      for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
          int c = color(input.readByte());
          result.pixels[(si + i) * result.width + (sj + j)] = c;
        }
      }
    }
    result.updatePixels();

    return result;
  }
  catch (IOException e) {
    throw new RuntimeException(e);
  }
  finally {
    try {
      input.close();
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}

int[][] readMnistLabels(String fileName, int maxOfImages) {
  DataInputStream input = new DataInputStream(createInput(fileName));
  try {
    int magicNumber = input.readInt();
    if (magicNumber != 2049) {
      throw new IOException("wrong format");
    }

    final int numberOfImages = min(maxOfImages, input.readInt());
    final int ww = ceil(sqrt(numberOfImages));
    
    int[][] result = new int[ww][ww];

    for (int k = 0; k < numberOfImages; k++) {
      final int si = k / ww;
      final int sj = k % ww;
      result[sj][si] = input.readByte();
    }
    
    return result;
  }
  catch (IOException e) {
    throw new RuntimeException(e);
  }
  finally {
    try {
      input.close();
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}
