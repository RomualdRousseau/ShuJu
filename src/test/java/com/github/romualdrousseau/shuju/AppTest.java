package com.github.romualdrousseau.shuju;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;
import java.net.URISyntaxException;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import com.github.romualdrousseau.shuju.columns.NumericColumn;
import com.github.romualdrousseau.shuju.columns.StringColumn;
import com.github.romualdrousseau.shuju.math.Linalg;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.transforms.*;
import com.github.romualdrousseau.shuju.ml.knn.*;
import com.github.romualdrousseau.shuju.ml.slr.*;
import com.github.romualdrousseau.shuju.nlp.StringList;
import com.github.romualdrousseau.shuju.util.*;

public class AppTest {
    @Test
    public void testMinorAndDet() {
        Matrix M1 = new Matrix(new float[][] { { 2, 3, 1 }, { 4, 5, 6 }, { 7, 8, 1 } });
        Matrix M2 = new Matrix(new float[][] { { 5, 6 }, { 8, 1 } });
        assertTrue(M1.minor(0, 0).equals(M2));
        assertEquals(M1.det(), 25, 0);
    }

    @Test
    public void testInverseMatrix() {
        Matrix M1 = new Matrix(new float[][] { { 2, 3, 1 }, { 4, 5, 6 }, { 7, 8, 1 } });
        Matrix M2 = new Matrix(new float[][] { { -1.72f, 0.2f, 0.52f }, { 1.52f, -0.2f, -0.32f }, { -0.12f, 0.2f, -0.08f } });
        Matrix M3 = M1.inv();
        assertTrue(M3.equals(M2, 1e-2f));
        assertTrue(M1.cof().transpose().mult(1.0f / M1.det()).equals(M3, 1e-2f));
        assertTrue(M1.adj().mult(1.0f / M1.det()).equals(M3, 1e-2f));
        assertTrue(Linalg.Solve(M1, new Matrix(3, 3).identity()).equals(M3, 1e-2f));
    }

    @Test
    public void testLinalgSolve() {
        Matrix M1 = new Matrix(new float[][] { { 2, 3, 1 }, { 4, 5, 6 }, { 7, 8, 1 } });
        Matrix M2 = new Matrix(new float[] { 3, 5, 6 });
        Matrix M3 = Linalg.Solve(M1, M2);
        assertTrue(M1.transform(M3).equals(M2, 1e-2f));
    }


    @Test
    public void testLinalgQR() {
        Matrix M = new Matrix(new float[][] { { 12, -51, 4 }, { 6, 167, -68 }, { -4, 24, -41 }, { -5, 25, -42 } });
        Matrix[] tmp = Linalg.QR(M);
        Matrix R = tmp[0];
        Matrix Q = tmp[1];
        assertTrue(R.isUpper(1e-2f));
        assertTrue(Q.transpose().equals(Q.inv(), 1e-2f));
        assertTrue(Q.transform(R).equals(M, 1e-2f));
    }

    @Test
    public void testLinalgCholesky() {
        Matrix M = new Matrix(new float[][] { { 6, 3, 4, 8 }, { 3, 6, 5, 1 }, { 4, 5, 10, 7 }, { 8, 1, 7, 25 } });
        Matrix L = Linalg.Cholesky(M);
        Matrix U = L.transpose();
        assertTrue(L.isLower(1e-2f));
        assertTrue(U.isUpper(1e-2f));
        assertTrue(L.transform(U).equals(M, 1e-2f));
    }

    @Test
    public void testLinalgLU() {
        Matrix M = new Matrix(new float[][] { { 7, 3, -1, 2 }, { 3, 8, 1, -4 }, { -1, 1, 4, -1 }, { 2, -4, -1, 6 } });
        Matrix[] tmp = Linalg.LU(M);
        Matrix L = tmp[0];
        Matrix U = tmp[1];
        assertTrue(L.isLower(1e-2f));
        assertTrue(U.isUpper(1e-2f));
        assertTrue(L.transform(U).equals(M, 1e-2f));
    }

    @Test
    public void testFuzzyString() {
        assertThat(FuzzyString.Jaccard("MATERIAL CODE", "MATERIAL CODE"), is(greaterThanOrEqualTo(0.8f)));
        assertThat(FuzzyString.Jaccard("MATERIAL CODE", "MATERIAL COCE"), is(greaterThanOrEqualTo(0.8f)));
        assertThat(FuzzyString.Jaccard("MATERIAL CODE", "MAT. CODE"), is(greaterThanOrEqualTo(0.6f)));
        assertThat(FuzzyString.Jaccard("MATERIAL CODE", "MATERIAL NAME"), is(lessThan(0.8f)));
        assertThat(FuzzyString.Jaccard("MATERIAL CODE", "MAT. NAME"), is(lessThan(0.8f)));

        assertThat(FuzzyString.JaroWinkler("MATERIAL CODE", "MATERIAL CODE"), is(greaterThanOrEqualTo(0.9f)));
        assertThat(FuzzyString.JaroWinkler("MATERIAL CODE", "MATERIAL COCE"), is(greaterThanOrEqualTo(0.9f)));
        assertThat(FuzzyString.JaroWinkler("MATERIAL CODE", "MAT. CODE"), is(greaterThanOrEqualTo(0.7f)));
        assertThat(FuzzyString.JaroWinkler("MATERIAL CODE", "MATERIAL NAME"), is(lessThan(0.9f)));
        assertThat(FuzzyString.JaroWinkler("MATERIAL CODE", "MAT. NAME"), is(lessThan(0.9f)));
    }

    @Test
    public void testSepalLengthDataSummary() {
        final DataSet fisherset = loadFisherSet();

        final DataSummary summary = new DataSummary(fisherset, DataRow.FEATURES, 0);
        Vector var = DataStatistics.var(summary);
        Vector stdev = var.copy().sqrt();

        assertEquals("Count", 150, summary.count);
        assertEquals("Min", 4.300, summary.min.get(0), 0.001);
        assertEquals("Max", 7.900, summary.max.get(0), 0.001);
        assertEquals("Sum", 876.500, summary.sum.get(0), 0.001);
        assertEquals("Avg", 5.843, summary.avg.get(0), 0.001);
        assertEquals("Var", 0.685, var.get(0), 0.001);
        assertEquals("Stdev", 0.828, stdev.get(0), 0.001);
    }

    @Test
    public void testSepalWidthDataSummary() {
        final DataSet fisherset = loadFisherSet();

        final DataSummary summary = new DataSummary(fisherset, DataRow.FEATURES, 1);
        Vector var = DataStatistics.var(summary);
        Vector stdev = var.copy().sqrt();

        assertEquals("Count", 150, summary.count);
        assertEquals("Min", 2.000, summary.min.get(0), 0.001);
        assertEquals("Max", 4.400, summary.max.get(0), 0.001);
        assertEquals("Sum", 458.600, summary.sum.get(0), 0.001);
        assertEquals("Avg", 3.057, summary.avg.get(0), 0.001);
        assertEquals("Var", 0.189, var.get(0), 0.001);
        assertEquals("Stdev", 0.436, stdev.get(0), 0.001);
    }

    @Test
    public void testPetalLengthDataSummary() {
        final DataSet fisherset = loadFisherSet();

        final DataSummary summary = new DataSummary(fisherset, DataRow.FEATURES, 2);
        Vector var = DataStatistics.var(summary);
        Vector stdev = var.copy().sqrt();

        assertEquals("Count", 150, summary.count);
        assertEquals("Min", 1.000, summary.min.get(0), 0.001);
        assertEquals("Max", 6.900, summary.max.get(0), 0.001);
        assertEquals("Sum", 563.700, summary.sum.get(0), 0.001);
        assertEquals("Avg", 3.758, summary.avg.get(0), 0.001);
        assertEquals("Var", 3.116, var.get(0), 0.001);
        assertEquals("Stdev", 1.765, stdev.get(0), 0.001);
    }

    @Test
    public void testPetalWidthDataSummary() {
        final DataSet fisherset = loadFisherSet();

        final DataSummary summary = new DataSummary(fisherset, DataRow.FEATURES, 3);
        Vector var = DataStatistics.var(summary);
        Vector stdev = var.copy().sqrt();

        assertEquals("Count", 150, summary.count);
        assertEquals("Min", 0.100, summary.min.get(0), 0.001);
        assertEquals("Max", 2.500, summary.max.get(0), 0.001);
        assertEquals("Sum", 180.000, summary.sum.get(0), 0.001);
        assertEquals("Avg", 1.200, summary.avg.get(0), 0.001);
        assertEquals("Var", 0.580, var.get(0), 0.001);
        assertEquals("Stdev", 0.761, stdev.get(0), 0.001);
    }

    @Test
    public void testLengthCovariance() {
        final DataSet fisherset = loadFisherSet();

        Vector cov = DataStatistics.cov(new DataSummary(fisherset, DataRow.FEATURES, 0),
                new DataSummary(fisherset, DataRow.FEATURES, 2));

        assertEquals("Cov", 1.274, cov.get(0), 0.001);
    }

    @Test
    public void testWidthCovariance() {
        final DataSet fisherset = loadFisherSet();

        Vector cov = DataStatistics.cov(new DataSummary(fisherset, DataRow.FEATURES, 1),
                new DataSummary(fisherset, DataRow.FEATURES, 3));

        assertEquals("Cov", -0.121, cov.get(0), 0.001);
    }

    @Test
    public void testLengthCorellation() {
        final DataSet fisherset = loadFisherSet();

        Vector corr = DataStatistics.corr(new DataSummary(fisherset, DataRow.FEATURES, 0),
                new DataSummary(fisherset, DataRow.FEATURES, 2));

        assertEquals("Corr", 0.872, corr.get(0), 0.001);
    }

    @Test
    public void testSLR() {
        final DataSet training = loadLiquidStat();
        assert training != null;

        final SLR slr = new SLR();

        slr.fit(training.featuresAsVectorArray(), training.labelsAsVectorArray());

        DataRow test = new DataRow().addFeature(new Vector(1, 13.0f)).setLabel(new Vector(1, 1.157f));

        Vector result = slr.predict(test.featuresAsOneVector());

        assertEquals("Result", test.label().get(0), result.get(0), 0.001f);
    }

    @Test
    public void testKNN() {
        final DataSet fisherset = loadFisherSet();
        assert fisherset != null;

        // Scale all features
        fisherset.transform(new NumericScaler(new DataSummary(fisherset, DataRow.FEATURES, 0)), DataRow.FEATURES, 0)
                .transform(new NumericScaler(new DataSummary(fisherset, DataRow.FEATURES, 1)), DataRow.FEATURES, 1)
                .transform(new NumericScaler(new DataSummary(fisherset, DataRow.FEATURES, 2)), DataRow.FEATURES, 2)
                .transform(new NumericScaler(new DataSummary(fisherset, DataRow.FEATURES, 3)), DataRow.FEATURES, 3)
                .shuffle();

        final KNN knn = new KNN(6);
        final DataSet training = fisherset.subset(0, 110);

        knn.fit(training.featuresAsVectorArray(), training.labelsAsVectorArray());

        final DataSet test = fisherset.subset(110, 150);

        int total = 0;
        int success = 0;
        float error = 0.0f;

        for (DataRow row : test.rows()) {
            Vector result = knn.predict(row.featuresAsOneVector());
            assert !result.isNull();

            int bestLabel = result.argmax();

            if (bestLabel == row.label().argmax()) {
                success++;
            }

            error += 1.0f - result.get(bestLabel);
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
        NumericColumn c1 = new NumericColumn();
        NumericColumn c2 = new NumericColumn();
        NumericColumn c3 = new NumericColumn();
        NumericColumn c4 = new NumericColumn();
        StringColumn c5 = new StringColumn(
                new StringList(new String[] { "I. setosa", "I. versicolor", "I. virginica" }));

        List<String[]> rows = loadTable("/data/fisher's data.csv", "header");
        assert rows != null;

        for (String[] cells : rows) {
            fisherSet.addRow(new DataRow().addFeature(c1.valueOf(Float.valueOf(cells[1])))
                    .addFeature(c2.valueOf(Float.valueOf(cells[2]))).addFeature(c3.valueOf(Float.valueOf(cells[3])))
                    .addFeature(c4.valueOf(Float.valueOf(cells[4]))).setLabel(c5.valueOf(cells[5])));
        }

        return fisherSet;
    }

    private DataSet loadLiquidStat() {
        DataSet liquidSet = new DataSet();
        NumericColumn c1 = new NumericColumn();
        NumericColumn c2 = new NumericColumn();

        List<String[]> rows = loadTable("/data/x62.csv", "header");
        assert rows != null;

        for (String[] cells : rows) {
            liquidSet.addRow(new DataRow().addFeature(c1.valueOf(Float.valueOf(cells[1])))
                    .setLabel(c2.valueOf(Float.valueOf(cells[2]))));
        }

        return liquidSet;
    }

    private List<String[]> loadTable(String resourceName, String options) {
        try {
            URL resourceUrl = getClass().getResource(resourceName);
            assert resourceUrl != null;

            List<String> tmp = Files.readAllLines(Paths.get(resourceUrl.toURI()));

            if (options.equals("header")) {
                tmp.remove(0); // remove headers
            }

            ArrayList<String[]> table = new ArrayList<String[]>();
            for (String line : tmp) {
                String[] tokens = line.split(",");
                table.add(tokens);
            }
            table.trimToSize();

            return table;
        } catch (URISyntaxException x) {
            assert false : x.getMessage();
            return null;
        } catch (IOException x) {
            assert false : x.getMessage();
            return null;
        }
    }
}
