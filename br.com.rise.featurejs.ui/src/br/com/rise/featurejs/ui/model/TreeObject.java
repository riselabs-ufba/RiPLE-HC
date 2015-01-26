package br.com.rise.featurejs.ui.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;

import br.com.rise.featurejs.ui.model.enums.TreeNodeType;

public class TreeObject implements IAdaptable {
	private String name;
	protected TreeParent parent;

	protected TreeNodeType type;
	private IFile file;
	private int lineMarker;

	public TreeObject(String name) {
		this.name = name;
		this.type = TreeNodeType.MACRO;
		this.lineMarker = 0;
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

	public void setFile(IFile file){
		this.file = file;
	}
	
	public IFile getFile() {
		return this.file;
	}

	public void setLineMarker(int line){
		this.lineMarker = line;
	}
	
	public int getLineMarker() {
		return this.lineMarker;
	}
}