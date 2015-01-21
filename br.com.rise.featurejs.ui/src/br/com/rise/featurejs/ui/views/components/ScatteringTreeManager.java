/**
 * 
 */
package br.com.rise.featurejs.ui.views.components;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.widgets.Tree;

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

	private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

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
	 * 
	 * @return
	 */
	public Collection<TreeParent> getTrees() {
		// return graphs;
		return treesMap.values();
	}

	/**
	 * Returns a <code>Collection</code> of IDs for the existing trees.
	 * 
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
		setActiveTree(projectName);
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
	 * 
	 * @return
	 */
	public TreeParent getActiveTree() {
		return treesMap.get(currentTreeName);
	}

	/**
	 * Sets the active input to the <code>{@link ScatteringTreeView}</code>.
	 * 
	 * @param key
	 */
	public void setActiveTree(String key) {
		notifyListeners(this, "tree", 
				treesMap.get(currentTreeName),
				treesMap.get(currentTreeName=key));
	}

	
	public void addChangeListener(PropertyChangeListener newListener){
		listeners.add(newListener);
	}
	
	private void notifyListeners(Object object, String property,
			TreeParent oldTreee, TreeParent newTree) {
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(new PropertyChangeEvent(this, property,
					oldTreee, newTree));
		}
	}

}
