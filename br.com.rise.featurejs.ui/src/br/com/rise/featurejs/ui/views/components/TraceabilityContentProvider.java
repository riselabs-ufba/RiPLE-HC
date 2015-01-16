package br.com.rise.featurejs.ui.views.components;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TraceabilityContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {

	private TraceabilityOutlineFilter filter;

	public TraceabilityContentProvider(TraceabilityOutlineFilter filter) {
		this.filter = filter;
	}

	private List<OutlineNode> rootElements;

	@Override
	public Object[] getChildren(Object parentElement) {
		OutlineNode node = (OutlineNode) parentElement;
		List<OutlineNode> children = node.getChildren();
		if ((children != null) && (children.size() != 0)) {
			return children.toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return ((OutlineNode) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		OutlineNode node = (OutlineNode) element;
		List<OutlineNode> children = node.getChildren();

		if ((children != null) && (children.size() != 0)) {
			for (OutlineNode n : children) {
				if (this.filter.select(null, element, n))
					return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return this.rootElements.toArray();
	}

	@Override
	public void dispose() {
		this.rootElements = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.rootElements = ((List) newInput);
	}
}
