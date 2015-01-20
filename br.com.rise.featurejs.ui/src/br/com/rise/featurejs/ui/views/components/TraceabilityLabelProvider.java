package br.com.rise.featurejs.ui.views.components;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import br.com.rise.featurejs.ui.model.OutlineNode;

public class TraceabilityLabelProvider extends LabelProvider {

	public String getText(Object obj) {
		return obj.toString();
	}

	public Image getImage(Object obj) {
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		Integer objType = ((OutlineNode) obj).getType(); 
		if (obj instanceof OutlineNode)
			if (objType == OutlineNode.TYPE_FEATURE)
				imageKey = "feature";
			else if(objType == OutlineNode.TYPE_MODULE)
				imageKey = "module";
			else if(objType == OutlineNode.TYPE_MACRO)
				imageKey = "macro";
				
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(imageKey);
	}
	
}
