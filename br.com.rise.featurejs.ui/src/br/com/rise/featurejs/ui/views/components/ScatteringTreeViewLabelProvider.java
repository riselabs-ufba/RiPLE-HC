package br.com.rise.featurejs.ui.views.components;

import java.io.File;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import br.com.rise.featurejs.ui.FeatureJSUIPlugin;
import br.com.rise.featurejs.ui.model.TreeObject;

public class ScatteringTreeViewLabelProvider extends LabelProvider {

	public String getText(Object obj) {
		return obj.toString();
	}

	public Image getImage(Object obj) {
		String imageKey;
		String ext = ".png";
		switch (((TreeObject) obj).getType()) {
		case FEATURE:
			imageKey = "feature" + ext;
			break;
		case MODULE:
			imageKey = "module" + ext;
			break;
		case MACRO:
			imageKey = "macro" + ext;
			break;
		default:
			imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(imageKey);
		}
		return FeatureJSUIPlugin.getImageDescriptor(imageKey).createImage();
		// AbstractUIPlugin.imageDescriptorFromPlugin(FeatureJSUIPlugin.PLUGIN_ID,
		// "icons/"+imageKey).createImage();
	}
}