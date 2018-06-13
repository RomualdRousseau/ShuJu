package com.github.romualdrousseau.shuju.ml.tree;

import com.github.romualdrousseau.shuju.IClassifier;
import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.DataRow;
import com.github.romualdrousseau.shuju.DataSet;
import com.github.romualdrousseau.shuju.Result;

public class NaiveTree extends IClassifier
{	
    public NaiveTree() {
        this.minProbability = 1.0;
    }

	public NaiveTree(double minProbability) {
        this.minProbability = minProbability;
	}

	public IClassifier train(DataSet trainingSet) {
        this.root = new TreeNode();
        for(DataRow row: trainingSet.rows()) {   
            pushDataRow(this.root, row, 0);
        }
        return this;
    }

    public Result predict(DataRow features) {
    	Result result = new Result(features, null, 0.0);
    	for(TreeNode child: root.children()) {
    		predictRec(child, features, 1.0, result, 0);
    	}
        return result;
    }

    private void predictRec(TreeNode root, DataRow features, double p, Result result, int level) {
        if(root.children().size() == 0) {
            if(p > result.getProbability()) {
            	boolean firstChild = true;
            	for(TreeNode sibling: root.siblings()) {
            		if(firstChild) {
            			result.setLabel(sibling.getValue()).setProbability(p);
            			firstChild = false;
            		}
            		else {
            			result.addLabel(sibling.getValue()).setProbability(p);	
            		}	
            	}
            }
        }
        else {
        	p *=  1.0 - Math.sqrt(root.getValue().costFunc(features.features().get(level)));
            if(p >= this.minProbability) {
            	for(TreeNode child: root.children()) {
                    predictRec(child, features, p, result, level + 1);
            	}
            }
        }
    }

    private void pushDataRow(TreeNode root, DataRow row, int level) {
        if(level >= row.features().size()) {
            pushLabel(root, row);
            return;
        }

        TreeNode child = root.findChild(row.features().get(level));
        if(child == null) {
            IFeature feature = row.features().get(level);
            feature.setProbability(1.0);
            child = new TreeNode().setValue(feature);
            root.addChild(child);
        }

        /*
        boolean onlyOneDecision = true;;
        for(TreeNode child: root.children()) {
            for(TreeNode sibling: child.siblings()) {
                if(child != sibling && !child.getValue().equals(sibling.getValue())) {
                    onlyOneDecision = false;
                }
            }
        }
        if(onlyOneDecision) {
            pushLabel(root, row);
            return;
        }
        */

        pushDataRow(child, row, level + 1);
    }

    private void pushLabel(TreeNode root, DataRow row) {
        TreeNode child = root.findChild(row.getLabel());
        if(child == null) {
            root.addChild(new TreeNode().setValue(row.getLabel()));
        }
    } 

    private TreeNode root;
    private double minProbability;
}