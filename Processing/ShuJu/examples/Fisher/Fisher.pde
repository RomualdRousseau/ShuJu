import romuald.*;
import romuald.knn.*;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

int success = 0;
double absError = 0.0;
double avgError = 0.0;

public DataSet loadTrainingSet(String fileName) throws IOException {
  DataSet result = new DataSet();

  Table table = loadTable(fileName, "header");  
  for (TableRow row : table.rows()) {
    DataRow tr = new DataRow();
    tr.addFeature(new NumericFeature(row.getDouble(1)))  // Sepal Length
      .addFeature(new NumericFeature(row.getDouble(2)))  // Sepal Width
      .addFeature(new NumericFeature(row.getDouble(3)))  // Petal Lenght
      .addFeature(new NumericFeature(row.getDouble(4)))  // Petal Width
      .setLabel(new StringFeature(row.getString(5)));   // Label
    result.addRow(tr);
  }
  
  return result;
}

void printSummary(DataSet ts) {
  double correct = (double) success * 100 / ts.rows().size();
  double incorrect = 100.0 - correct;
  
  println(String.format("Correctly Classified Instances\t\t%d\t%.2f%%", success, correct));
  println(String.format("Incorrectly Classified Instances\t%d\t%.2f%%", ts.rows().size() - success, incorrect));
  println(String.format("Mean error\t\t\t\t%f", avgError));
  println(String.format("Root mean squared error\t\t%f", Math.sqrt(avgError)));
  println(String.format("Absolute error\t\t\t%f", absError));
  println(String.format("Root absolute squared error\t\t%f", Math.sqrt(absError)));
  println(String.format("Total Number of Instances\t\t%d", ts.rows().size()));
}

void setup() {
  size(192, 192);
  textSize(16);
  noLoop();
  
  try {
    DataSet fisherSet = loadTrainingSet("fisher's data.csv");
    
    // transform
    for(DataRow row: fisherSet.rows()) {
      for(IFeature feature: row.features()) {
          DataSet.Statistic stat = feature.getStatistic();
          feature.normalize(stat.min, stat.max);
        }
    }
    fisherSet.shuffle();
        
    // Training samples
    DataSet training = fisherSet.subset(0, 110);
    // Test samples
    DataSet test = fisherSet.subset(110, 150);

    // classification
    KNN knn = new KNN(6, 1.0, 1.0); 
    knn.train(training);
    
    for(DataRow row: test.rows()) {
      IResult result = knn.predict(row);
      if(result.getLabel().equals(row.getLabel())) {
        success++;
      }
      double squaredError = result.getConfidence() * result.getConfidence();
      avgError += squaredError;
      absError = Math.max(absError, squaredError);
    }
    avgError /= test.rows().size();

    printSummary(test);    
  }
  catch(Exception x) {
    x.printStackTrace();
  }
}

void draw() {
  background(0);
}