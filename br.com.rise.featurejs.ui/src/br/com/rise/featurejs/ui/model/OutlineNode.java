/**
 * 
 */
package br.com.rise.featurejs.ui.model;

import java.util.ArrayList;

import org.eclipse.jface.text.Position;

import org.eclipse.core.resources.IFile;

/**
 * @author "Alcemir Santos"
 * 
 */
public class OutlineNode {
	public static final int TYPE_FEATURE = 0;
	public static final int TYPE_MODULE = 1;
	public static final int TYPE_MACRO = 2;

	private String name;
	private int type;
	private int beginLine;
	private int endLine;
	private int offsetOnLine;
	private int declarationLength;
	private OutlineNode parent;
	private ArrayList<OutlineNode> children;
	private Position position;
	private IFile file;

	public OutlineNode(String name, int type, int beginLine, OutlineNode parent) {
		this.name = name;
		this.type = type;
		this.beginLine = beginLine;
		this.parent = parent;
	}

	public OutlineNode(String name, int type, int beginLine, int offset,
			int length) {
		this.name = name;
		this.type = type;
		this.beginLine = beginLine;
		this.offsetOnLine = offset;
		this.declarationLength = length;
	}

	public OutlineNode copy(IFile texFile) {
		OutlineNode on = new OutlineNode(this.name, this.type, this.beginLine,
				this.offsetOnLine, this.declarationLength);
		on.endLine = this.endLine;
		on.position = this.position;
		on.file = texFile;
		return on;
	}

	public void addChild(OutlineNode child) {
		if (this.children == null)
			this.children = new ArrayList();
		this.children.add(child);
	}

	public void addChild(OutlineNode child, int index) {
		if (this.children == null)
			this.children = new ArrayList();
		this.children.add(index, child);
	}

	public boolean deleteChild(OutlineNode child) {
		return this.children.remove(child);
	}

	public ArrayList<OutlineNode> getChildren() {
		return this.children;
	}

	public void setChildren(ArrayList<OutlineNode> children) {
		this.children = children;
	}

	public boolean hasChildren() {
		return (this.children != null) && (this.children.size() > 0);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OutlineNode getParent() {
		return this.parent;
	}

	public void setParent(OutlineNode parent) {
		this.parent = parent;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getBeginLine() {
		return this.beginLine;
	}

	public void setBeginLine(int beginLine) {
		this.beginLine = beginLine;
	}

	public int getEndLine() {
		return this.endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public Position getPosition() {
		return this.position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public IFile getIFile() {
		return this.file;
	}

	public void setIFile(IFile file) {
		this.file = file;
	}

	public int getDeclarationLength() {
		return this.declarationLength;
	}

	public int getOffsetOnLine() {
		return this.offsetOnLine;
	}

	public String toString() {
		if (this.position == null) {
			return this.type + " " + this.name + " (null position) "
					+ super.toString();
		}

		return this.type + " " + this.name + " " + this.position.getOffset()
				+ " " + this.position.getLength() + super.toString();
	}

	public static int getSmallerType(int type) {
		if (type <= 2) {
			return type - 1;
		}
		return 0;
	}
	
	
	
	

}
