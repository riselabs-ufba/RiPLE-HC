package br.com.rise.featurejs.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;

import br.com.rise.featurejs.ui.FeatureJSUIPlugin;
import br.com.rise.featurejs.ui.utils.Util;
import br.com.rise.featurejs.ui.views.components.MacrosOutlineInput;
import br.com.rise.featurejs.ui.views.components.TraceabilityOutlinePage;

public class TraceabilityOutlineUpdateAction extends Action{
	private TraceabilityOutlinePage outline;

	 public TraceabilityOutlineUpdateAction(TraceabilityOutlinePage outline)  {
	    super("Update");
	    setToolTipText("Update");

	    setImageDescriptor(FeatureJSUIPlugin.getImageDescriptor("refresh"));
	    this.outline = outline;
	  }

	  public void run() {
	    if (this.outline.isModelDirty()){
	    	MacrosOutlineInput input = Util.getMacrosInput();
	    	this.outline.update(input);
	    }
	  }
}
