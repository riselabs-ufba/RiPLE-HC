/**
 * 
 */
package br.com.rise.featurejs.ui.helpers.managers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef4.zest.core.widgets.Graph;

import br.com.rise.featurejs.ui.model.ScatteringTraceabilityLink;
import br.com.rise.featurejs.ui.views.FeatureInteractionsView;

/**
 * @author "Alcemir Santos"
 * 
 */
public class InteractionGraphsManager extends AbstractManager<Graph> implements
		PropertyChangeListener {

	private static InteractionGraphsManager instance;

	private InteractionGraphsManager() {
	}

	public static InteractionGraphsManager getInstance() {
		if (instance == null) {
			instance = new InteractionGraphsManager();
			ScatteringTraceabilityLinksManager.getInstance().addChangeListener(
					instance);
		}
		return instance;
	}

	@Override
	protected void notifyListeners(Object object, String property,
			Graph oldObject, Graph newObject) {
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(new PropertyChangeEvent(this,
					"interactions graphs", null, map));
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object o = evt.getNewValue();
		List<ScatteringTraceabilityLink> links = o instanceof List ? (List<ScatteringTraceabilityLink>) o
				: null;
		if (links == null)
			return;
		IResource[] configFiles = null;
		try {
			configFiles = links.get(0).getProject().getConfigFolder().members();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		for (IResource cFile : configFiles) {
// TODO			map.put(cFile.getName(), null);
		}

//		notifyListeners(this, null, null, null);
	}

}
