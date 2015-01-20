package br.com.rise.featurejs.ui.model.enums;

public enum TreeNodeType {
	FEATURE("feature"), MODULE("module"), MACRO("macro");

	private String description;

	private TreeNodeType(String desc) {
		this.description = desc;
	}

	public String getDescription() {
		return this.description;
	}
}