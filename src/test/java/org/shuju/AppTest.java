package org.shuju;

import org.shuju.*;
import org.shuju.knn.*;
import org.shuju.slr.*;

import java.util.List;
import java.util.function.Consumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;
import java.net.URISyntaxException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testSepalLengthSummary() {
		final DataSet fisherset = loadFisherSet();

		final Summary summary = new Summary(fisherset, 0);
		double var = Statistics.var(fisherset, 0, summary);
		double stdev = Math.sqrt(var);
		
		assertEquals("Count", 150, summary.count);
		assertEquals("Min", 4.300, summary.min, 0.001);
		assertEquals("Max", 7.900, summary.max, 0.001);
		assertEquals("Sum", 876.500, summary.sum, 0.001);
		assertEquals("Avg", 5.843, summary.avg, 0.001);
		assertEquals("Var", 0.685, var, 0.001); 
		assertEquals("Stdev", 0.828, stdev, 0.001);
	}
	
	public void testSepalWidthSummary() {
		final DataSet fisherset = loadFisherSet();

		final Summary summary = new Summary(fisherset, 1);
		double var = Statistics.var(fisherset, 1, summary);
		double stdev = Math.sqrt(var);

		assertEquals("Count", 150, summary.count);
		assertEquals("Min", 2.000, summary.min, 0.001);
		assertEquals("Max", 4.400, summary.max, 0.001);
		assertEquals("Sum", 458.600, summary.sum, 0.001);
		assertEquals("Avg", 3.057, summary.avg, 0.001);
		assertEquals("Var", 0.189, var, 0.001);
		assertEquals("Stdev", 0.436, stdev, 0.001);
	}
	
	public void testPetalLengthSummary() {
		final DataSet fisherset = loadFisherSet();

		final Summary summary = new Summary(fisherset, 2);
		double var = Statistics.var(fisherset, 2, summary);
		double stdev = Math.sqrt(var);
		
		assertEquals("Count", 150, summary.count);
		assertEquals("Min", 1.000, summary.min, 0.001);
		assertEquals("Max", 6.900, summary.max, 0.001);
		assertEquals("Sum", 563.700, summary.sum, 0.001);
		assertEquals("Avg", 3.758, summary.avg, 0.001);
		assertEquals("Var", 3.116, var, 0.001);
		assertEquals("Stdev", 1.765, stdev, 0.001);
	}
	
	public void testPetalWidthSummary() {
		final DataSet fisherset = loadFisherSet();

		final Summary summary = new Summary(fisherset, 3);
		double var = Statistics.var(fisherset, 3, summary);
		double stdev = Math.sqrt(var);

		assertEquals("Count", 150, summary.count);
		assertEquals("Min", 0.100, summary.min, 0.001);
		assertEquals("Max", 2.500, summary.max, 0.001);
		assertEquals("Sum", 180.000, summary.sum, 0.001);
		assertEquals("Avg", 1.200, summary.avg, 0.001);
		assertEquals("Var", 0.580, var, 0.001);
		assertEquals("Stdev", 0.761, stdev, 0.001);
	}
	
	public void testLengthCovariance() {
		final DataSet fisherset = loadFisherSet();

		double cov = Statistics.cov(fisherset, 0, 2);
		
		assertEquals("Cov", 1.274, cov, 0.001);
	}
	
	public void testWidthCovariance() {
		final DataSet fisherset = loadFisherSet();

		double cov = Statistics.cov(fisherset, 1, 3);
		
		assertEquals("Cov", -0.121, cov, 0.001);
	}
	
	public void testLengthCorellation() {
		final DataSet fisherset = loadFisherSet();

		double corr = Statistics.corr(fisherset, 0, 2);
		
		assertEquals("Corr", 0.872, corr, 0.001);
	}
	
	public void testSLR() {
		final DataSet training = loadLiquidStat();

		final IClassifier slr = new SLR();
		slr.train(training);

		DataRow test = new DataRow();
		test.addFeature(new NumericFeature(13.0));
		test.setLabel(new NumericFeature(1.157));
		
		Result result = slr.predict(test);
		
		assertEquals("Result", (Double) test.getLabel().getValue(), (Double) result.getLabel().getValue(), 0.001);
	}
	
	public void testKNN() {
		final DataSet fisherset = loadFisherSet();

		// Scale all features
		for(int i = 0; i < fisherset.rows().get(0).features().size(); i++) {
			final ITransform scaler = new NumericScaler(new Summary(fisherset, i));
			for(DataRow row: fisherset.rows()) {
				scaler.apply(row.features().get(i));
			}
		}
		fisherset.shuffle();

		// Training samples
		DataSet training = fisherset.subset(0, 110);
		// Test samples
		DataSet test = fisherset.subset(110, 150);

		// classification
		final IClassifier knn = new KNN(6, 1.0, 1.0);
		knn.train(training);

		int total = 0;
		int success = 0;
		double error = 0.0;
		for(DataRow row: test.rows()) {
			Result result = knn.predict(row);
			if(result.getLabel().equals(row.getLabel())) {
				success++;
			}
			error += result.getConfidence() * result.getConfidence();
			total++;
		}

		double correct = (double) success / (double) total;
		double incorrect = 1.0 - correct;
		error /= (double) total;

		assertEquals("Correctly Classified Instances", 1.0, correct, 0.1);
		assertEquals("Incorrectly Classified Instances", 0.0, incorrect, 0.1);
		assertEquals("Mean error", 0.0001, error, 0.001);
		assertEquals("Total Number of Instances", total, test.rows().size());
	}
	
	private DataSet loadFisherSet() {
		DataSet fisherSet = new DataSet();
		
		List<String> lines = loadTable("/data/fisher's data.csv", "header");

		for(String line: lines) {
			String[] tokens = line.split(",");
			fisherSet.addRow(new DataRow()
				.addFeature(new NumericFeature(Double.valueOf(tokens[1])))
				.addFeature(new NumericFeature(Double.valueOf(tokens[2])))
				.addFeature(new NumericFeature(Double.valueOf(tokens[3])))
				.addFeature(new NumericFeature(Double.valueOf(tokens[4])))
				.setLabel(new StringFeature(tokens[5])));
		}
			
		return fisherSet;
	}
	
	private DataSet loadLiquidStat() {
		DataSet liquidSet = new DataSet();
		
		List<String> lines = loadTable("/data/x62.csv", "header");

		for(String line: lines) {
			String[] tokens = line.split(",");
			liquidSet.addRow(new DataRow()
				.addFeature(new NumericFeature(Double.valueOf(tokens[1])))
				.setLabel(new NumericFeature(Double.valueOf(tokens[2]))));
		}

		return liquidSet;
	}
	
	private List<String> loadTable(String resourceName, String options) {
		try {
			URL resourceUrl = getClass().getResource(resourceName);
			assert resourceUrl != null;
			
			List<String> table = Files.readAllLines(Paths.get(resourceUrl.toURI()));
			
			if(options.equals("header")) {
				table.remove(0); // remove headers
			}
			
			return table;
		}
		catch(URISyntaxException x) {
			assert false : x.getMessage();
			return null;
		}
		catch(IOException x) {
			assert false : x.getMessage();
			return null;
		}
	}
}
