import romuald.ann.*;

double[][] examples = new double[][] {{0, 1, 0}, {1, 1, 0}, {0, 0, 1}, {0, 1, 1}};
double[][] labels = new double[][] {{0, 0, 1, 0.25}, {0, 0, 0, 0.25}, {0, 0, 1, 0.15}, {0, 0, 1, 0.25}};

Network network = new Network();

void printData() {
  for(int i = 0; i < examples.length; i++) {
    String buffer = "";
    double[] v = network.evaluate(examples[i]);
    for(int j = 0; j < examples[i].length; j++) {
      buffer += String.format("%d, ", round((float) examples[i][j]));
    }
    for(int j = 0; j < labels[i].length; j++) {
      if(j < 3) {
        buffer += String.format("%d, ", round((float) labels[i][j]));
      }
      else {
        buffer += String.format("%.02f", labels[i][j]);
      }
    }
    text(buffer, 16, (i + 2) * 16);
  }
}

void printResults() {
  for(int i = 0; i < examples.length; i++) {
    String buffer = "";
    double[] v = network.evaluate(examples[i]);
    for(int j = 0; j < examples[i].length; j++) {
      buffer += String.format("%d, ", round((float) examples[i][j]));
    }
    for(int j = 0; j < v.length; j++) {
      if(j < 3) {
        buffer += String.format("%d, ", round((float) v[j]));
      }
      else {
        buffer += String.format("%.02f", v[j]);
      }
    }
    text(buffer, 16, (i + 7) * 16);
  }
}

void setup() {
  size(192, 192);
  textSize(16);
  noLoop();
  
  println("Building neural network ...");
  network.build(3, 10, 1, 4);
}

void draw() {
  background(0);
  printData();
  printResults();
}

void keyPressed() {
  if (key == 'L' || key == 'l') {
    println("Training neural network ...");
    network.train(examples, labels, 0, examples.length, 1000, 0.2);
    println(String.format("Final prediction accuracy on training data = %.03f", network.computeAccurary(examples, labels, 0, examples.length)));
    println(String.format("Final prediction accuracy on test data = %.03f", network.computeAccurary(examples, labels, 0, examples.length)));
    redraw();
  }
  else if (key == 'R' || key == 'r') {
    println("Reseting neural network ...");
    network.resetWeights();
    redraw();
  }
  else if (key == 'G' || key == 'g') {
    network.generateCode();
  }
}