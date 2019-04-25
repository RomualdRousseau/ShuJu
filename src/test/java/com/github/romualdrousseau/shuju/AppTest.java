package com.github.romualdrousseau.shuju;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;
import java.net.URISyntaxException;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import com.github.romualdrousseau.shuju.features.*;
import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.transforms.*;
import com.github.romualdrousseau.shuju.ml.knn.*;
import com.github.romualdrousseau.shuju.ml.slr.*;
import com.github.romualdrousseau.shuju.util.*;

public class AppTest
{
	@Test
	public void testFuzzyString() {
		assertThat(FuzzyString.similarity("MATERIAL CODE", "MATERIAL CODE", " "), is(greaterThanOrEqualTo(0.8)));
		assertThat(FuzzyString.similarity("MATERIAL CODE", "MATERIAL COCE", " "), is(greaterThanOrEqualTo(0.8)));
		assertThat(FuzzyString.similarity("MATERIAL CODE", "MAT. CODE", " "), is(greaterThanOrEqualTo(0.8)));
		assertThat(FuzzyString.similarity("MATERIAL CODE", "MATERIAL NAME", " "),is(lessThan(0.8)));
		assertThat(FuzzyString.similarity("MATERIAL CODE", "MAT. NAME", " "), is(lessThan(0.8)));

		assertThat(FuzzyString.distance("MATERIAL CODE", "MATERIAL CODE", " "), is(lessThanOrEqualTo(0.2)));
		assertThat(FuzzyString.distance("MATERIAL CODE", "MATERIAL COCE", " "), is(lessThanOrEqualTo(0.2)));
		assertThat(FuzzyString.distance("MATERIAL CODE", "MAT. CODE", " "), is(lessThanOrEqualTo(0.2)));
		assertThat(FuzzyString.distance("MATERIAL CODE", "MATERIAL NAME", " "), is(greaterThan(0.2)));
		assertThat(FuzzyString.distance("MATERIAL CODE", "MAT. NAME", " "), is(greaterThan(0.2)));
	}

	@Test
	public void testSepalLengthDataSummary() {
		final DataSet fisherset = loadFisherSet();

		final DataSummary summary = new DataSummary(fisherset, 0);
		float var = DataStatistics.var(fisherset, 0, summary);
		float stdev = Scalar.sqrt(var);

		assertEquals("Count", 150, summary.count);
		assertEquals("Min", 4.300, summary.min, 0.001);
		assertEquals("Max", 7.900, summary.max, 0.001);
		assertEquals("Sum", 876.500, summary.sum, 0.001);
		assertEquals("Avg", 5.843, summary.avg, 0.001);
		assertEquals("Var", 0.685, var, 0.001);
		assertEquals("Stdev", 0.828, stdev, 0.001);
	}

	@Test
	public void testSepalWidthDataSummary() {
		final DataSet fisherset = loadFisherSet();

		final DataSummary summary = new DataSummary(fisherset, 1);
		float var = DataStatistics.var(fisherset, 1, summary);
		float stdev = Scalar.sqrt(var);

		assertEquals("Count", 150, summary.count);
		assertEquals("Min", 2.000, summary.min, 0.001);
		assertEquals("Max", 4.400, summary.max, 0.001);
		assertEquals("Sum", 458.600, summary.sum, 0.001);
		assertEquals("Avg", 3.057, summary.avg, 0.001);
		assertEquals("Var", 0.189, var, 0.001);
		assertEquals("Stdev", 0.436, stdev, 0.001);
	}

	@Test
	public void testPetalLengthDataSummary() {
		final DataSet fisherset = loadFisherSet();

		final DataSummary summary = new DataSummary(fisherset, 2);
		float var = DataStatistics.var(fisherset, 2, summary);
		float stdev = Scalar.sqrt(var);

		assertEquals("Count", 150, summary.count);
		assertEquals("Min", 1.000, summary.min, 0.001);
		assertEquals("Max", 6.900, summary.max, 0.001);
		assertEquals("Sum", 563.700, summary.sum, 0.001);
		assertEquals("Avg", 3.758, summary.avg, 0.001);
		assertEquals("Var", 3.116, var, 0.001);
		assertEquals("Stdev", 1.765, stdev, 0.001);
	}

	@Test
	public void testPetalWidthDataSummary() {
		final DataSet fisherset = loadFisherSet();

		final DataSummary summary = new DataSummary(fisherset, 3);
		float var = DataStatistics.var(fisherset, 3, summary);
		float stdev = Scalar.sqrt(var);

		assertEquals("Count", 150, summary.count);
		assertEquals("Min", 0.100, summary.min, 0.001);
		assertEquals("Max", 2.500, summary.max, 0.001);
		assertEquals("Sum", 180.000, summary.sum, 0.001);
		assertEquals("Avg", 1.200, summary.avg, 0.001);
		assertEquals("Var", 0.580, var, 0.001);
		assertEquals("Stdev", 0.761, stdev, 0.001);
	}

	@Test
	public void testLengthCovariance() {
		final DataSet fisherset = loadFisherSet();

		float cov = DataStatistics.cov(fisherset, 0, 2);

		assertEquals("Cov", 1.274, cov, 0.001);
	}

	@Test
	public void testWidthCovariance() {
		final DataSet fisherset = loadFisherSet();

		float cov = DataStatistics.cov(fisherset, 1, 3);

		assertEquals("Cov", -0.121, cov, 0.001);
	}

	@Test
	public void testLengthCorellation() {
		final DataSet fisherset = loadFisherSet();

		float corr = DataStatistics.corr(fisherset, 0, 2);

		assertEquals("Corr", 0.872, corr, 0.001);
	}

	@Test
	public void testSLR() {
		final DataSet training = loadLiquidStat();

		final IClassifier slr = new SLR();
		slr.train(training);

		DataRow test = new DataRow()
			.addFeature(new NumericFeature(13.0f))
			.setLabel(new NumericFeature(1.157f));

		Result result = slr.predict(test);

		assertEquals("Result", (Float) test.getLabel().getValue(), (Float) result.getLabel().getValue(), 0.001f);
	}

	@Test
	public void testKNN() {
		final DataSet fisherset = loadFisherSet();

		// Scale all features
		fisherset
			.transform(new NumericScaler(new DataSummary(fisherset, 0)), 0)
			.transform(new NumericScaler(new DataSummary(fisherset, 1)), 1)
			.transform(new NumericScaler(new DataSummary(fisherset, 2)), 2)
			.transform(new NumericScaler(new DataSummary(fisherset, 3)), 3)
			.shuffle();

		final IClassifier knn = new KNN(6);
		final DataSet training = fisherset.subset(0, 110);
		knn.train(training);

		final DataSet test = fisherset.subset(110, 150);

		int total = 0;
		int success = 0;
		float error = 0.0f;

		for(DataRow row: test.rows()) {
			Result result = knn.predict(row);
			if(result.getLabel().equals(row.getLabel())) {
				success++;
			}
			error += result.getError();
			total++;
		}

		float correct = (float) success / (float) total;
		float incorrect = 1.0f - correct;
		error /= (float) total;

		assertEquals("Correctly Classified Instances", 1.0, correct, 0.1);
		assertEquals("Incorrectly Classified Instances", 0.0, incorrect, 0.1);
		assertEquals("Mean error", 0.0001f, error, 0.001);
		assertEquals("Total Number of Instances", total, test.rows().size());
	}

	private DataSet loadFisherSet() {
		DataSet fisherSet = new DataSet();

		List<String> lines = loadTable("/data/fisher's data.csv", "header");

		for(String line: lines) {
			String[] tokens = line.split(",");
			fisherSet.addRow(new DataRow()
				.addFeature(new NumericFeature(Float.valueOf(tokens[1])))
				.addFeature(new NumericFeature(Float.valueOf(tokens[2])))
				.addFeature(new NumericFeature(Float.valueOf(tokens[3])))
				.addFeature(new NumericFeature(Float.valueOf(tokens[4])))
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
				.addFeature(new NumericFeature(Float.valueOf(tokens[1])))
				.setLabel(new NumericFeature(Float.valueOf(tokens[2]))));
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
