import org.shuju.*;
import org.shuju.slr.*;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

DataSet training;
DataSet test;

public DataSet loadTrainingSet(String fileName) throws IOException {
  DataSet result = new DataSet();

  Table table = loadTable(fileName, "header");  
  for (TableRow row : table.rows()) {
    result.addRow(new DataRow()
      .addFeature(new NumericFeature(row.getDouble(1) - 1.0)) // Time
      .setLabel(new NumericFeature(row.getDouble(2)))   // Data
    );
  }
  
  return result;
}

void setup() {
  size(500, 500);
  textSize(16);
  noLoop();
  
  try {
    training = loadTrainingSet("x62.csv");
  } catch(IOException x) {
    x.printStackTrace();
  }
  
  final IClassifier slr = new SLR();
  slr.train(training);

  test = new DataSet();
  for(int i = 0; i < 24; i++) {
    test.addRow(new DataRow()
      .addFeature(new NumericFeature((double) i))
    );
  }

  for(DataRow row: test.rows()) {
    Result result = slr.predict(row);
    row.setLabel(result.getLabel());
  }
}

void draw() {
  final double scaleX = width / (24 - 2);
  final double scaleY = height / (24 - 2);

  background(255);
  
  // Draw axises
  strokeWeight(1); 
  stroke(200, 200, 200);
  line(0, height / 2, width, height / 2);
  for(int i = 0; i < width; i += scaleX) {
    line(i, height / 2 - 3, i, height / 2 + 3);
  }
  line(0, 0, 0, height);
  for(int i = 0; i < height; i += scaleY) {
    line(-3, i, 3, i);
  }
  
  // Plot training set
  strokeWeight(5);
  stroke(0, 0, 255);
  for(DataRow row: training.rows()) {
    double x = (Double) row.features().get(0).getValue() * scaleX;
    double y = (Double) row.getLabel().getValue() * scaleY;
    point((float) x, height / 2 - (float) y);
  }
  
  // Plot prediction set
  strokeWeight(1); 
  stroke(255, 0, 0);
  double prevX = 0.0;
  double prevY = 0.0;
  boolean firstPoint = true;
  for(DataRow row: test.rows()) {
    if(firstPoint) {
      prevX = (Double) row.features().get(0).getValue() * scaleX;
      prevY = (Double) row.getLabel().getValue() * scaleY;
      firstPoint = false;
    }
    else {
      double x = (Double) row.features().get(0).getValue() * scaleX;
      double y = (Double) row.getLabel().getValue() * scaleY;
      line((float) prevX, height / 2 - (float) prevY, (float) x, height / 2 - (float) y);
      prevX = x;
      prevY = y;
    }
  }
}
