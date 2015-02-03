/**
 * 
 */
package br.com.rise.featurejs.ui.helpers.managers;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
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
	 * might return null if the map is null or empty.
	 * 
	 * @param key
	 * @return
	 */
	public T getObject(String key){
		return (map == null || map.isEmpty() )? null : map.get(key);
	}
	
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
	 * @param name - the key to the object
	 * @param object - the object used to notify the listeners
	 */
	public void addObject(String name, T object) {
		if (map == null) {
			map = new HashMap<String, T>();
		}
		map.put(name, object);
		setCurrent(name);
	}
	
	/**
	 * Returns the current object to be treated by the view. The object returned might be null if the map is empty 
	 * or have not been initialized yet.
	 * 
	 * @return
	 */
	public T getActiveObject() {
		return (map == null || map.isEmpty() )? null : map.get(currentItem);
	}
	
	/**
	 * Sets the current input to the manager.
	 * 
	 * @param key
	 */
	public void setCurrent(String key) {
		notifyListeners(this, key, 
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
		if (listeners == null) {
			listeners = new ArrayList<PropertyChangeListener>();
		}
		if (listeners.contains(newListener)) 
			return;
		listeners.add(newListener);
	}
	
	/**
	 * Returns whether exists an object mapped to the given key or not.
	 *  
	 * @param key
	 * @return
	 */
	public boolean exists(String key){
		return map!=null? map.containsKey(key) : false;
	}
	
	protected abstract void notifyListeners(Object object, String property,
			T oldObject, T newObject);
	
}
