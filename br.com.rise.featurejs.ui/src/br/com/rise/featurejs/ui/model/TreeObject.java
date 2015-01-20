package br.com.rise.featurejs.ui.model;

import org.eclipse.core.runtime.IAdaptable;

import br.com.rise.featurejs.ui.model.enums.TreeNodeType;
import br.com.rise.featurejs.ui.views.ScatteringTreeView;

public class TreeObject implements IAdaptable {
	private String name;
	protected TreeParent parent;

	protected TreeNodeType type;

	public TreeObject(String name) {
		this.name = name;
		this.type = TreeNodeType.MACRO;
	}

	public String getName() {
		return name;
	}

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}

	public TreeParent getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}

	public Object getAdapter(Class key) {
		return null;
	}

	public void setType(TreeNodeType t){
		this.type = t;
	}
	
	public TreeNodeType getType() {
		return type;
	}
}