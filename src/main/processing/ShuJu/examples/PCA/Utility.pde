
static final String[] flowerNames = { "I. setosa", "I. versicolor", "I. virginica" };

public DataSet loadTrainingSet(String fileName) {
    DataSet result = new DataSet();
  
    NumericColumn sepalLengthCol = new NumericColumn();
    NumericColumn sepalWidthCol = new NumericColumn();
    NumericColumn petalLengthCol = new NumericColumn();
    NumericColumn petalWidthCol = new NumericColumn();
    StringColumn flowerNameCol = new StringColumn(new com.github.romualdrousseau.shuju.nlp.StringList(flowerNames));
  
    Table table = loadTable(fileName, "header");
    for (TableRow row : table.rows()) {
      result.addRow(new DataRow()
        .addFeature(sepalLengthCol.valueOf(row.getFloat(1)))
        .addFeature(sepalWidthCol.valueOf(row.getFloat(2)))
        .addFeature(petalLengthCol.valueOf(row.getFloat(3)))
        .addFeature(petalWidthCol.valueOf(row.getFloat(4)))
        .setLabel(flowerNameCol.valueOf(row.getString(5).replaceAll("\\u00a0|\\s", " ")))
      );
    }
  
    return result;
}
