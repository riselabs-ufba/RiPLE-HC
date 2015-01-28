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

import br.com.rise.featurejs.ui.model.TreeParent;
import br.com.rise.featurejs.ui.views.ScatteringTreeView;

/**
 * @author Alcemir Santos
 * 
 */
public class ScatteringTreeManager extends AbstractManager<TreeParent> implements PropertyChangeListener{

	private static ScatteringTreeManager manager;

	/**
	 * 
	 */
	private ScatteringTreeManager() {	}

	public static ScatteringTreeManager getInstance() {
		if (manager == null) {
			manager = new ScatteringTreeManager();
		}
		return manager;
	}
	
	@Override
	protected void notifyListeners(Object object, String property,
			TreeParent oldTreee, TreeParent newTree) {
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(new PropertyChangeEvent(this, property,
					oldTreee, newTree));
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO thinking about to add a listener to FileManager.
//		FileManager manager = (FileManager) evt.getNewValue();
//		manager.getFeatureMetrics().
	}

}
