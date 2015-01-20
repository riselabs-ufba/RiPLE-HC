package br.com.rise.featurejs.ui.views.components;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import br.com.rise.featurejs.ui.model.TreeObject;
import br.com.rise.featurejs.ui.model.TreeParent;
import br.com.rise.featurejs.ui.model.enums.TreeNodeType;
import br.com.rise.featurejs.ui.views.ScatteringTreeView;


/**
 * The content provider class is responsible for providing objects to the
 * view. It can wrap existing objects in adapters or simply return objects
 * as-is. These objects may be sensitive to the current input of the view,
 * or ignore it and always show the same content (like Task List, for
 * example).
 * 
 * @author Alcemir Santos
 */
public class ScatteringTreeViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {
	/**
	 * 
	 */
	private ScatteringTreeView view;

	/**
	 * @param scatteringTreeView
	 */
	public ScatteringTreeViewContentProvider(ScatteringTreeView scatteringTreeView) {
		view = scatteringTreeView;
	}

	private TreeParent invisibleRoot;

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(view.getViewSite())) {
			if (invisibleRoot == null)
				initialize();
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

	/*
	 * We will set up a dummy model to initialize tree heararchy. In a real
	 * code, you will connect to a real model and expose its hierarchy.
	 */
	public void initialize() {
		TreeObject to1 = new TreeObject("Leaf 1");
		TreeObject to2 = new TreeObject("Leaf 2");
		TreeObject to3 = new TreeObject("Leaf 3");
		TreeParent p1 = new TreeParent("Parent 1");
		p1.setType(TreeNodeType.MODULE);
		p1.addChild(to1);
		p1.addChild(to2);
		p1.addChild(to3);

		TreeObject to4 = new TreeObject("Leaf 4");
		TreeParent p2 = new TreeParent("Parent 2");
		p2.setType(TreeNodeType.MODULE);
		p2.addChild(to4);

		TreeParent root = new TreeParent("Root");
		root.setType(TreeNodeType.FEATURE);
		root.addChild(p1);
		root.addChild(p2);

		TreeObject too1 = new TreeObject("Leaf 1");
		TreeObject too2 = new TreeObject("Leaf 2");
		TreeObject too3 = new TreeObject("Leaf 3");
		TreeParent po1 = new TreeParent("Parent 1");
		po1.setType(TreeNodeType.MODULE);
		po1.addChild(too1);
		po1.addChild(too2);
		po1.addChild(too3);

		TreeObject too4 = new TreeObject("Leaf 4");
		TreeParent po2 = new TreeParent("Parent 2");
		po2.setType(TreeNodeType.MODULE);
		po2.addChild(too4);

		TreeParent rooot = new TreeParent("Root");
		rooot.setType(TreeNodeType.FEATURE);
		rooot.addChild(po1);
		rooot.addChild(po2);

		invisibleRoot = new TreeParent("");
		invisibleRoot.addChild(root);
		invisibleRoot.addChild(rooot);

	}
}