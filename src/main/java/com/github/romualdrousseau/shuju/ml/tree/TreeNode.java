package com.github.romualdrousseau.shuju.ml.tree;

import java.util.List;
import java.util.ArrayList;

import com.github.romualdrousseau.shuju.IFeature;

public class TreeNode
{
    public TreeNode setValue(IFeature<?> value) {
        this.value = value;
        return this;
    }

    public IFeature<?> getValue() {
        return this.value;
    }

    public boolean isLeaf() {
        return this.children.size() == 0;
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public List<TreeNode> children() {
        return this.children;
    }

    public List<TreeNode> siblings() {
        if(this.parent == null) {
            return null;
        }
        else {
            return this.parent.children;
        }
    }

	public TreeNode addChild(TreeNode child) {
		child.parent = this;
        this.children.add(child);
		return this;
	}

	public TreeNode findChild(IFeature<?> valueToFound) {
    	for(TreeNode child: this.children) {
    		if(child.value.getValue().equals(valueToFound.getValue())) {
    			return child;
    		}
    	}
    	return null;
    }

    public void simplify() {
        simplifyRec();
    }

    public void display() {
        displayRec(0);
    }

    private List<IFeature<?>> simplifyRec() {
        List<IFeature<?>> pool = new ArrayList<IFeature<?>>();

        if(this.children.size() == 0) {
            pool.add(this.value);
        }
        else {
            for(TreeNode child: this.children) {
                List<IFeature<?>> features = child.simplifyRec();
                for(IFeature<?> feature1: features) {
                    boolean foundFeature = false;
                    for(IFeature<?> feature2: pool) {
                        if(feature2.equals(feature1)) {
                            foundFeature = true;
                        }
                    }
                    if(!foundFeature) {
                        pool.add(feature1);
                    }
                }
            }

            if(pool.size() == 1 && this.children.size() > 0) {
                this.children.clear();
                this.addChild(new TreeNode().setValue(pool.get(0)));
            }
        }

        return pool;
    }

    private void displayRec(int level) {
        for(int i = 0; i < level; i++) System.out.print("\t");
        System.out.println("> " + getValue());
        for(TreeNode child: this.children) {
            child.displayRec(level + 1);
        }
    }

    private IFeature<?> value;
    private TreeNode parent = null;
	private List<TreeNode> children = new ArrayList<TreeNode>();
}
