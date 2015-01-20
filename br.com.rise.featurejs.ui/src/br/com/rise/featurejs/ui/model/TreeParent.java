package br.com.rise.featurejs.ui.model;

import java.util.ArrayList;

public class TreeParent extends TreeObject {
	private ArrayList children;
	private int treeDepth;

	public TreeParent(String name) {
		super(name);
		setTreeDepth(-1);
		children = new ArrayList();
	}

	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}

	public TreeObject[] getChildren() {
		return (TreeObject[]) children.toArray(new TreeObject[children
				.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public void setTreeDepth(int treeDepth) {
		this.treeDepth = treeDepth;
	}

	public int getTreeDepth() {
		if (this.treeDepth == -1)
			calculateTreeDepth();
		return this.treeDepth;
	}

	public void calculateTreeDepth() {
		this.treeDepth = 0;
		for (Object node : this.children) {
			int localDepth = handleNode(node, 0);
			if (localDepth > this.treeDepth)
				this.treeDepth = localDepth;
		}
	}

	private int handleNode(Object node, int parentDepth) {
		TreeObject[] children=null;
		if (node instanceof TreeParent) {
			children = ((TreeParent)node).getChildren();
		}
		int maxDepth = parentDepth + 1;
		if (children != null) {
			for (TreeObject child : children) {
				int localDepth = handleNode(child, parentDepth + 1);
				if (localDepth > maxDepth) {
					maxDepth = localDepth;
				}
			}
		}
		return maxDepth;
	}
}