package com.github.romualdrousseau.shuju.ml.tree;

import com.github.romualdrousseau.shuju.IClassifier;
import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.DataRow;
import com.github.romualdrousseau.shuju.DataSet;
import com.github.romualdrousseau.shuju.Result;

public class NaiveTree implements IClassifier {
    public NaiveTree() {
        this.minProbability = 1.0;
    }

    public NaiveTree(double minProbability) {
        this.minProbability = minProbability;
    }

    public DataSet getTrainingSet() {
        return this.trainingSet;
    }

    public IClassifier train(DataSet trainingSet) {
        this.trainingSet = trainingSet;
        this.root = new TreeNode();
        for (DataRow row : trainingSet.rows()) {
            pushDataRow(this.root, row, 0);
        }
        this.root.simplify();
        // this.root.display();
        return this;
    }

    public Result predict(DataRow features) {
        Result result = new Result(features, null, 0.0);
        if (this.root != null && features != null) {
            for (TreeNode child : root.children()) {
                predictRec(child, features, 1.0, result, 0);
            }
        }
        return result;
    }

    private void predictRec(TreeNode root, DataRow features, double p, Result result, int level) {
        if (root.children().size() == 0) {
            if (p > result.getProbability()) {
                boolean firstChild = true;
                for (TreeNode sibling : root.siblings()) {
                    if (firstChild) {
                        result.setLabel(sibling.getValue()).setProbability(p);
                        firstChild = false;
                    } else {
                        result.addLabel(sibling.getValue()).setProbability(p);
                    }
                }
            }
        } else {
            p *= 1.0 - Math.sqrt(root.getValue().costFunc(features.features().get(level)));
            if (p >= this.minProbability) {
                for (TreeNode child : root.children()) {
                    predictRec(child, features, p, result, level + 1);
                }
            }
        }
    }

    private void pushDataRow(TreeNode root, DataRow row, int level) {
        if (level >= row.features().size()) {
            pushLabel(root, row);
            return;
        }

        TreeNode child = root.findChild(row.features().get(level));
        if (child == null) {
            IFeature<?> feature = row.features().get(level);
            feature.setProbability(1.0);
            child = new TreeNode().setValue(feature);
            root.addChild(child);
        }

        pushDataRow(child, row, level + 1);
    }

    private void pushLabel(TreeNode root, DataRow row) {
        TreeNode child = root.findChild(row.getLabel());
        if (child == null) {
            root.addChild(new TreeNode().setValue(row.getLabel()));
        }
    }

    private TreeNode root;
    private double minProbability;
    private DataSet trainingSet;
}
