package br.com.rise.featurejs.ui.views.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import br.com.rise.featurejs.ui.model.OutlineNode;

public class TraceabilityContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {

	private List<OutlineNode> rootElements;
	private TraceabilityOutlineFilter filter;

	public TraceabilityContentProvider(TraceabilityOutlineFilter filter) {
		this.filter = filter;
	}


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
		if (this.rootElements.isEmpty()) {
			initWithFooModel();
		}
		return this.rootElements.toArray();
	}

	private void initWithFooModel() {
		OutlineNode featureA = new OutlineNode("FeatureA", OutlineNode.TYPE_FEATURE, 0, null);
		OutlineNode featureB = new OutlineNode("FeatureB", OutlineNode.TYPE_FEATURE, 0, null);
		OutlineNode featureC = new OutlineNode("FeatureC", OutlineNode.TYPE_FEATURE, 0, null);
		
		OutlineNode moduleA = new OutlineNode("a.js", OutlineNode.TYPE_MODULE, 0, featureA);
		OutlineNode moduleB = new OutlineNode("b.js", OutlineNode.TYPE_MODULE, 0, featureA);
		OutlineNode moduleC = new OutlineNode("c.js", OutlineNode.TYPE_MODULE, 0, featureB);
		OutlineNode moduleD = new OutlineNode("d.js", OutlineNode.TYPE_MODULE, 0, featureB);
		OutlineNode moduleE = new OutlineNode("e.js", OutlineNode.TYPE_MODULE, 0, featureB);
		OutlineNode moduleF = new OutlineNode("f.js", OutlineNode.TYPE_MODULE, 0, featureC);
		
		OutlineNode macroA = new OutlineNode("ifdef B", OutlineNode.TYPE_MACRO, 0, moduleA);
		OutlineNode macroB = new OutlineNode("ifdef C", OutlineNode.TYPE_MACRO, 0, moduleD);
		OutlineNode macroC = new OutlineNode("ifdef A", OutlineNode.TYPE_MACRO, 0, moduleE);
		OutlineNode macroD = new OutlineNode("ifdef B", OutlineNode.TYPE_MACRO, 0, moduleF);
		OutlineNode macroE = new OutlineNode("ifdef A", OutlineNode.TYPE_MACRO, 0, moduleC);

		featureA.addChild(moduleA);
		featureA.addChild(moduleB);
		
		featureB.addChild(moduleC);
		featureB.addChild(moduleD);
		featureB.addChild(moduleE);
		
		moduleA.addChild(macroA);
		moduleD.addChild(macroB);
		moduleE.addChild(macroC);
		moduleF.addChild(macroD);
		moduleC.addChild(macroE);
		
		
		List<OutlineNode> dumbModel = new ArrayList<OutlineNode>();
		dumbModel.add(featureA);
		dumbModel.add(featureB);
		dumbModel.add(featureC);
		
		this.rootElements = dumbModel;
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
