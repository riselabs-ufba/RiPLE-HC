package br.com.rise.featurejs.ui.views.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.rise.featurejs.ui.model.OutlineNode;

public class TraceabilityOutlineInput {

	private List<OutlineNode> rootNodes;
	private int treeDepth;
	private Map<Integer, List<OutlineNode>> typeLists;

	public TraceabilityOutlineInput(List<OutlineNode> rootNodes) {
		this.rootNodes = rootNodes;
		this.typeLists = new HashMap();
		this.treeDepth = -1;
	}

	public void addNode(OutlineNode node) {
		List<OutlineNode> typeList = this.typeLists.get(Integer.valueOf(node
				.getType()));
		if (typeList == null) {
			typeList = new ArrayList();
			this.typeLists.put(Integer.valueOf(node.getType()), typeList);
		}

		typeList.add(node);
	}

	public Object getRootNodes() {
		return this.rootNodes;
	}

	public int getTreeDepth() {
		if (this.treeDepth == -1)
		      calculateTreeDepth();
		    return this.treeDepth;
		  }

		  public void setTreeDepth(int treeDepth)
		  {
		    this.treeDepth = treeDepth;
		  }

		  public void calculateTreeDepth()
		  {
		    this.treeDepth = 0;
		    for (OutlineNode node : this.rootNodes) {
		      int localDepth = handleNode(node, 0);
		      if (localDepth > this.treeDepth)
		        this.treeDepth = localDepth;
		    }
		  }

		  private int handleNode(OutlineNode node, int parentDepth)
		  {
		    List<OutlineNode> children = node.getChildren();
		    int maxDepth = parentDepth + 1;
		    if (children != null) {
		      for (OutlineNode child : children) {
		        int localDepth = handleNode(child, parentDepth + 1);
		        if (localDepth > maxDepth) {
		          maxDepth = localDepth;
		        }
		      }
		    }
		    return maxDepth;
		  }

	public List<OutlineNode> getTypeList(int nodeType) {
		return this.typeLists.get(nodeType);
	}

}
