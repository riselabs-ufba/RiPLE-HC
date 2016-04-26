package br.com.rise.featurejs.ui.views.components;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import br.com.rise.featurejs.ui.model.TreeObject;
import br.com.rise.featurejs.ui.model.TreeParent;
import br.com.rise.featurejs.ui.views.ScatteringTreeView;

/**
 * The content provider class is responsible for providing objects to the view.
 * It can wrap existing objects in adapters or simply return objects as-is.
 * These objects may be sensitive to the current input of the view, or ignore it
 * and always show the same content (like Task List, for example).
 * 
 * @author Alcemir Santos
 */
public class ScatteringTreeViewContentProvider implements
		IStructuredContentProvider, ITreeContentProvider {

	private TreeParent invisibleRoot;
	private ViewerFilter filter;

	/**
	 * 
	 * @param aView
	 * @param aFilter
	 */
	public ScatteringTreeViewContentProvider( ViewerFilter aFilter) {
		this.filter = aFilter;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		this.invisibleRoot = (TreeParent) newInput;
	}

	public void dispose() {
		this.invisibleRoot = null;
	}

	public Object[] getElements(Object parent) {
		return getChildren(invisibleRoot);
	}

	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject) child).getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent) parent).getChildren();
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent) {
			TreeParent node = (TreeParent) parent;
			TreeObject[] list = node.getChildren();
			
			if((list!=null)&&(list.length>0)){
				for (TreeObject treeObject : list) {
					if (this.filter.select(null, parent, treeObject)) return true;
				}
				return false;
			}
		}
		return false;
	}

}