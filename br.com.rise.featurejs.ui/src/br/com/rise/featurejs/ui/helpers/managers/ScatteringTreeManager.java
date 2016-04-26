/**
 * 
 */
package br.com.rise.featurejs.ui.helpers.managers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import br.com.rise.featurejs.ui.helpers.ScatteringTreeHelper;
import br.com.rise.featurejs.ui.model.ScatteringTraceabilityLink;
import br.com.rise.featurejs.ui.model.TreeParent;

/**
 * @author Alcemir Santos
 * 
 */
public class ScatteringTreeManager extends AbstractManager<TreeParent> implements PropertyChangeListener{

	private static ScatteringTreeManager instance;

	/**
	 * 
	 */
	private ScatteringTreeManager() {
	}

	public static ScatteringTreeManager getInstance() {
		if (instance == null) {
			instance = new ScatteringTreeManager();
			ScatteringTraceabilityLinksManager.getInstance().addChangeListener(instance);
		}
		return instance;
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
		List<ScatteringTraceabilityLink> links = (List<ScatteringTraceabilityLink>)evt.getNewValue();
		String projectName = links.get(0).getProject().getProjectName();
		addObject(projectName, new ScatteringTreeHelper().buildTree(links));
	}

}
