package br.com.rise.featurejs.ui.helpers.managers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import br.com.rise.featurejs.ui.model.ScatteringTraceabilityLink;


public class ScatteringTraceabilityLinksManager extends AbstractManager<List<ScatteringTraceabilityLink>> {

	private static ScatteringTraceabilityLinksManager instance;
	
	private ScatteringTraceabilityLinksManager(){}
	
	public static ScatteringTraceabilityLinksManager getInstance() {
		if (instance == null) {
			instance = new ScatteringTraceabilityLinksManager();
		}
		return instance;
	}


	@Override
	protected void notifyListeners(Object object, String property,
			List<ScatteringTraceabilityLink> oldObject,
			List<ScatteringTraceabilityLink> newObject) {
		
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(new PropertyChangeEvent(this, property,
					oldObject, newObject));
		}
	}
}
