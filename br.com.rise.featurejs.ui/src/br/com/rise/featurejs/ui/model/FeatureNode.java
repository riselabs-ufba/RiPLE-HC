package br.com.rise.featurejs.ui.model;

import java.util.ArrayList;
import java.util.List;

public class FeatureNode {

	private final String id;
	  private final String name;
	  private List<FeatureNode> connections;

	  public FeatureNode(String id, String name) {
	    this.id = id;
	    this.name = name;
	    this.connections = new ArrayList<FeatureNode>();
	  }

	  public String getId() {
	    return id;
	  }

	  public String getName() {
	    return name;
	  }

	  public List<FeatureNode> getConnectedTo() {
	    return connections;
	  }
}
