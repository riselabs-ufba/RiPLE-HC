/**
 * 
 */
package br.com.rise.featurejs.ui.views.components;

import java.util.Collection;
import java.util.HashMap;

import br.com.rise.featurejs.ui.model.TreeParent;
import br.com.rise.featurejs.ui.views.ScatteringTreeView;

/**
 * @author "Alcemir Santos"
 * 
 */
public class ScatteringTreeManager {

	private static ScatteringTreeManager manager;
	
	private String currentTreeName;
	private HashMap<String, TreeParent> treesMap;

	/**
	 * 
	 */
	private ScatteringTreeManager() {
	}

	public static ScatteringTreeManager getInstance() {
		if (manager == null) {
			manager = new ScatteringTreeManager();
		}
		return manager;
	}

	/**
	 * Returns a <code>Collection</code> of the existing trees.
	 * @return
	 */
	public Collection<TreeParent> getTrees() {
		// return graphs;
		return treesMap.values();
	}

	/**
	 * Returns a <code>Collection</code> of IDs for the existing trees.
	 * @return
	 */
	public Collection<String> getGraphsIDs() {
		return treesMap.keySet();
	}

	/**
	 * Adds a tree to the current <code>Collection</code>.
	 * 
	 * @param g
	 */
	public void addTree(String projectName, TreeParent root) {
		if (treesMap == null) {
			treesMap = new HashMap<String, TreeParent>();
		}
		treesMap.put(projectName, root);
		setActiveGraph(projectName);
	}

	/**
	 * Returns the name of the project currently showing in the view.
	 * 
	 * @return
	 */
	public String getCurrentTree() {
		return currentTreeName;
	}

	/**
	 * Returns the tree currently showing in the view.
	 * @return
	 */
	public TreeParent getActiveTree() {
		return treesMap.get(currentTreeName);
	}

	/**
	 * Sets the active input to the <code>{@link ScatteringTreeView}</code>.
	 * @param key
	 */
	public void setActiveGraph(String key) {
		currentTreeName = key;
		ScatteringTreeView.setInput(treesMap.get(currentTreeName));
	}

}
