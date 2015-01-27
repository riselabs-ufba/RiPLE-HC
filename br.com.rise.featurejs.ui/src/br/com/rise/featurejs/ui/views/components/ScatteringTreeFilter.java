/**
 * 
 */
package br.com.rise.featurejs.ui.views.components;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import br.com.rise.featurejs.ui.model.TreeObject;
import br.com.rise.featurejs.ui.model.TreeParent;
import br.com.rise.featurejs.ui.model.enums.TreeNodeType;

/**
 * @author Alcemir Santos
 * 
 */
public class ScatteringTreeFilter extends ViewerFilter {

	private boolean showAll;

	/**
	 * 
	 */
	public ScatteringTreeFilter() {
		reset();
	}

	public void toggleExhibition(){
		this.showAll = !this.showAll;
	}
	
	public void reset(){
		this.showAll=false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
	 * .Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		if (showAll) {
			return true;
		} else {
			// return true only if there is any ifdef in the feature folder
			if (element instanceof TreeParent) {
				TreeParent node = (TreeParent) element;
				// the element is the feature folders elements
				if (node.getTreeDepth()==2) {
					return true;
				}else if(node.getType()==TreeNodeType.FEATURE){
					return false;
				}else{
					return select(viewer, ((TreeParent)parentElement).getParent(), parentElement);
				}
			}else{
				return select(viewer, ((TreeParent)parentElement).getParent(), parentElement);
			}
		}
	}

	/**
	 * Returns <code>true</code> if showing all nodes and <code>false</code>, otherwise.
	 * 
	 * @return showAll a flag of the status.
	 */
	public boolean isShowingAll() {
		return showAll;
	}

}
