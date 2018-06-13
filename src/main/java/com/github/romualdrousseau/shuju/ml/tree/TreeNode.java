package com.github.romualdrousseau.shuju.ml.tree;

import java.util.List;
import java.util.ArrayList;

import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.Result;
import com.github.romualdrousseau.shuju.DataRow;

public class TreeNode
{
    public TreeNode setValue(IFeature value) {
        this.value = value;
        return this;
    }

    public IFeature getValue() {
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

	public TreeNode findChild(IFeature value) {
    	for(TreeNode child: this.children) {
    		if(child.value.equals(value)) {
    			return child;
    		}
    	}
    	return null;
    }

    public void display() {
        displayRec(0);
    }

    public void displayRec(int level) {
        for(int i = 0; i < level; i++) System.out.print("\t");
        System.out.println("> " + getValue());
        for(TreeNode child: this.children) {
            child.displayRec(level + 1);    
        }
    }

    private IFeature value;
    private TreeNode parent = null;
	private List<TreeNode> children = new ArrayList<TreeNode>();
}