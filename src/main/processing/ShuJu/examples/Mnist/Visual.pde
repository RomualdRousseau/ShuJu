void drawBadges() {
   final int w = width / testPerRow;
  final int h = height / testPerRow;
  
  image(testImages, 0, 0, width, height);
  for (int i = 0; i < testCount; i++) {
    final int imgx = i % testPerRow;
    final int imgy = i / testPerRow;

    if (testMap[i] == testLabels[imgx][imgy]) {
      fill(0, 255, 0, 128);
      noStroke();
      ellipse((imgx + 0.5) * w, (imgy + 0.5) * h, w, h);
    } else {
      fill(255, 0, 0, 128);
      noStroke();
      ellipse((imgx + 0.5) * w, (imgy + 0.5) * h, w, h);
    }
  }
}

void drawZoom(int x, int y) {
  x = x * testPerRow / width;
  y = y * testPerRow / height;
 
  PImage digit = testImages.get(x * 28, y * 28, 28, 28);
  digit.mask(digit);
  
  int glassx = mouseX;
  int glassy = mouseY;
  if(glassx < width / 2) {
    glassx += 32;
  } else {
    glassx -= 32;
  }
  if(glassy < height / 2) {
    glassy += 32;
  } else {
    glassy -= 32;
  }
  
  final int i = y * testPerRow + x;
  if (testMap[i] == testLabels[x][y]) {
      fill(0, 255, 0, 128);
      noStroke();
      ellipse(glassx, glassy, 64, 64);
      fill(255);
      stroke(255);
      image(digit, glassx - 14, glassy - 14, 28, 28);
      text(testMap[i], glassx - 20, glassy + 20);
    } else {
      fill(255, 0, 0, 128);
      noStroke();
      ellipse(glassx, glassy, 64, 64);
      fill(255);
      stroke(255);
      image(digit, glassx - 14, glassy - 14, 28, 28);
      text(testMap[i], glassx - 20, glassy + 20);
    }
}
