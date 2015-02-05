package br.com.rise.featurejs.ui.views.components;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphEntityRelationshipContentProvider;

import br.com.rise.featurejs.ui.model.FeatureNode;
import br.com.rise.featurejs.ui.model.InteractionEdge;
import br.com.rise.featurejs.ui.model.NodeModelContentProvider;

public class ZestNodeContentProvider  implements IGraphEntityRelationshipContentProvider{

	private NodeModelContentProvider model;

	@Override
	public Object[] getRelationships(Object source, Object dest) {
		if(source==null || dest == null)
			return null;
		FeatureNode src = (FeatureNode) source;
		FeatureNode destination =  (FeatureNode) dest;
		List<InteractionEdge> l = new ArrayList<>();
		for (InteractionEdge edge : model.getEdges()) {
			if (edge.getSource()==src && edge.getDestination()==destination) {
				l.add(edge);
			}
			
//			if (edge.getSource()==destination && edge.getDestination()==src) {
//				l.add(edge);
//			}
		}
		return l.toArray();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return ((NodeModelContentProvider)inputElement).getNodes().toArray();
	}

	@Override
	public void dispose() {
		this.model=null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.model = (NodeModelContentProvider) newInput;
	}
	
	
// TIP: to use this method this provider must implement IGraphEntityContentProvider
//	@Override
//	  public Object[] getConnectedTo(Object entity) {
//	    if (entity instanceof FeatureNode) {
//	      FeatureNode node = (FeatureNode) entity;
//	      return node.getConnectedTo().toArray();
//	    }
//	    throw new RuntimeException("Type not supported");
//	  }


}
