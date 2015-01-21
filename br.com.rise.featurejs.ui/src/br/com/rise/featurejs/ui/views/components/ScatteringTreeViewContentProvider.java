package br.com.rise.featurejs.ui.views.components;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import br.com.rise.featurejs.ui.model.TreeObject;
import br.com.rise.featurejs.ui.model.TreeParent;
import br.com.rise.featurejs.ui.model.enums.TreeNodeType;
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
	/**
	 * 
	 */
	private ScatteringTreeView view;
	private TreeParent invisibleRoot;

	/**
	 * 
	 * TODO check
	 * http://www.eclipse.org/articles/Article-TreeViewer/TreeViewerArticle.htm
	 * 
	 * @param scatteringTreeView
	 */
	public ScatteringTreeViewContentProvider(
			ScatteringTreeView scatteringTreeView) {
		view = scatteringTreeView;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		this.invisibleRoot = (TreeParent) newInput;
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(view.getViewSite())) {
			if (invisibleRoot == null)
				invisibleRoot = view.initialize();
			return getChildren(invisibleRoot);
		}
		return getChildren(parent);
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
		if (parent instanceof TreeParent)
			return ((TreeParent) parent).hasChildren();
		return false;
	}

}