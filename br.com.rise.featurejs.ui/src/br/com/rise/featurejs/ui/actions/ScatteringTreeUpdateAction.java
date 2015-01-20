/**
 * 
 */
package br.com.rise.featurejs.ui.actions;

import org.eclipse.jface.action.Action;

import br.com.rise.featurejs.ui.FeatureJSUIPlugin;

/**
 * @author Alcemir Santos
 *
 */
public class ScatteringTreeUpdateAction extends Action {
	public ScatteringTreeUpdateAction(){
		super("Update");
		setToolTipText("Update");
		setImageDescriptor(FeatureJSUIPlugin.getImageDescriptor("refresh.gif"));
	}
	public void run(){
	System.out.println("ScatteringTreeUpdateAction.run()");
	}
}
