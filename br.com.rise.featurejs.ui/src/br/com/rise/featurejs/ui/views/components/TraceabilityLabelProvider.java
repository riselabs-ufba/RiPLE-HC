package br.com.rise.featurejs.ui.views.components;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import br.com.reconcavo.featurejs.views.TraceabilityOutlineTreeView.TreeParent;

public class TraceabilityLabelProvider extends LabelProvider {

	public String getText(Object obj) {
		return obj.toString();
	}

	public Image getImage(Object obj) {
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		if (obj instanceof TreeParent)
			imageKey = ISharedImages.IMG_OBJ_FOLDER;
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(imageKey);
	}
	
}
