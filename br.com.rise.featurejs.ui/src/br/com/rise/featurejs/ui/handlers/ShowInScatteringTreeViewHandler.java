/**
 * 
 */
package br.com.rise.featurejs.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.tweaklets.DummyPrefPageEnhancer;

import br.com.rise.featurejs.ui.model.TreeObject;
import br.com.rise.featurejs.ui.model.TreeParent;
import br.com.rise.featurejs.ui.model.enums.TreeNodeType;
import br.com.rise.featurejs.ui.views.components.ScatteringTreeManager;

/**
 * @author "Alcemir Santos"
 * 
 */
public class ShowInScatteringTreeViewHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject selectedProject = getSelectedProject(event);

		String projectName = selectedProject.getName();

		// TODO create scattering tree
		TreeParent root = createScatteringTree(selectedProject);

		// add tree to the manager
		ScatteringTreeManager.getInstance().addTree(projectName, root);

		// show mensage of confirmation
		showMessage("Show in Scattering Tree View was successfully executed.");
		return null;
	}

	/**
	 * This method returns the selected project in the ProjectExplorer View
	 * 
	 * @param event
	 * @return
	 * @throws ExecutionException
	 */
	private IProject getSelectedProject(ExecutionEvent event)
			throws ExecutionException {
		IProject selectedProject = null;

		// get workbench window
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		// set selection service
		ISelectionService service = window.getSelectionService();
		// set structured selection
		IStructuredSelection structured = (IStructuredSelection) service
				.getSelection();

		Object objectSelected = structured.iterator().next();

		selectedProject = ((IProject) objectSelected).getProject();

		return selectedProject;
	}
	
	public static TreeParent createScatteringTree(IProject selectedProject) {
		// TODO Auto-generated method stub
		MessageDialog.openInformation( PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), "FeatureJS",
				"You just triggered this action from context menu in the project: " 
				+ selectedProject.getName());
		return dummyModel();
	}

	/**
	 * Sets a dummy model to initialize tree heararchy. Afterwards, it should be
	 * updated.
	 */
	public static TreeParent dummyModel() {
		TreeObject to1 = new TreeObject("Leaf 1");
		TreeParent p1 = new TreeParent("Parent 1");
		p1.setType(TreeNodeType.MODULE);
		p1.addChild(to1);

		TreeObject to4 = new TreeObject("Leaf 4");
		TreeParent p2 = new TreeParent("Parent 2");
		p2.setType(TreeNodeType.MODULE);
		p2.addChild(to4);

		TreeParent root = new TreeParent("Root");
		root.setType(TreeNodeType.FEATURE);
		root.addChild(p1);
		root.addChild(p2);

		TreeObject too1 = new TreeObject("Leaf 1");
		TreeParent po1 = new TreeParent("Parent 1");
		po1.setType(TreeNodeType.MODULE);
		po1.addChild(too1);

		TreeObject too4 = new TreeObject("Leaf 4");
		TreeParent po2 = new TreeParent("Parent 2");
		po2.setType(TreeNodeType.MODULE);
		po2.addChild(too4);

		TreeParent po3 = new TreeParent("Parent 3");
		po3.setType(TreeNodeType.MODULE);
		
		TreeParent rooot = new TreeParent("Root");
		rooot.setType(TreeNodeType.FEATURE);
		rooot.addChild(po1);
		rooot.addChild(po2);
		rooot.addChild(po3);
		
		TreeParent rot = new TreeParent("Root");
		rot.setType(TreeNodeType.FEATURE);
		
		TreeParent invisibleRoot = new TreeParent("");
		invisibleRoot.addChild(root);
		invisibleRoot.addChild(rooot);
		invisibleRoot.addChild(rot);
		
		return invisibleRoot;
	}

	/**
	 * 
	 */
	private void showMessage(String message) {
		MessageDialog.openInformation( PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), "FeatureJS",	message);
	}


}
