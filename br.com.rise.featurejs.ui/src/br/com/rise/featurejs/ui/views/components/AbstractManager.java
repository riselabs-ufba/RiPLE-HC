/**
 * 
 */
package br.com.rise.featurejs.ui.views.components;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * Extending classes should implement a Singleton pattern.
 * 
 * @author Alcemir Santos
 *
 */
public abstract class AbstractManager<T> {

	private String currentItem;
	protected HashMap<String, T> map; 
	protected List<PropertyChangeListener> listeners;
	

	/**
	 * Returns a <code>Collection</code> of the existing objects.
	 * 
	 * @return
	 */
	public Collection<T> getObjects() {
		return map.values();
	}

	/**
	 * Returns a <code>Collection</code> of keys for the existing objects.
	 * 
	 * @return
	 */
	public Collection<String> getObjectKeys() {
		return map.keySet();
	}
	
	/**
	 * Adds an object to the map of objects and sets the current key.
	 * 
	 * @param projectName - the key to the object
	 * @param object - the object used to notify the listeners
	 */
	public void addObject(String projectName, T object) {
		if (map == null) {
			map = new HashMap<String, T>();
		}
		map.put(projectName, object);
		setCurrent(projectName);
	}
	
	/**
	 * Returns the current object to be treated by the view.
	 * 
	 * @return
	 */
	public T getActiveObject() {
		return map.get(currentItem);
	}
	
	/**
	 * Sets the current input to the manager.
	 * 
	 * @param key
	 */
	private void setCurrent(String key) {
		notifyListeners(this, "item", 
				map.get(currentItem),
				map.get(currentItem=key));
	}

	/**
	 * Returns the name of the key of the object being used.
	 * 
	 * @return
	 */
	protected String getCurrent() {
		return currentItem;
	}
	
	public void addChangeListener(PropertyChangeListener newListener){
		listeners.add(newListener);
	}
	
	protected abstract void notifyListeners(Object object, String property,
			T oldObject, T newObject);
	
}
