/**
 * 
 */
package br.com.rise.featurejs.ui.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import br.com.rise.featurejs.ui.model.TreeObject;
import br.com.rise.featurejs.ui.model.TreeParent;
import br.com.rise.featurejs.ui.model.enums.TreeNodeType;
import br.com.rise.featurejs.ui.views.components.ScatteringTreeManager;
import br.com.riselabs.vparser.beans.CCVariationPoint;
import br.com.riselabs.vparser.exceptions.PluginException;
import br.com.riselabs.vparser.lexer.beans.Token;
import br.com.riselabs.vparser.parsers.JavaScriptParser;
import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;

/**
 * @author Alcemir Santos
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
		// gets the FeatureIDE project
		IFeatureProject ifProj = CorePlugin.getFeatureProject(selectedProject);
		TreeParent root = new TreeParent(ifProj.getProjectName());

		TreeParent featureNode;
		IResource[] featureFolders = null;
		IResource[] featureModules = null;
		try {
			// create feature nodes
			featureFolders = ifProj.getSourceFolder().members();
		} catch (CoreException e) {
			System.err.println(e.getMessage());
		}

		for (int i = 0; i < featureFolders.length; i++) {
			if (featureFolders[i] instanceof IFolder) {
				featureNode = new TreeParent(featureFolders[i].getName());
				featureNode.setType(TreeNodeType.FEATURE);

				try {
					//  create modules nodes
					// get modules from this feature
					featureModules = getModules(((IFolder) featureFolders[i])
							.members());
				} catch (CoreException e) {
					System.err.println(e.getMessage());
				}
				for (int j = 0; j < featureModules.length; j++) {
					if (featureModules[j] instanceof IFile) {
						IFile module = (IFile) featureModules[j];
						// parse the module to get macros
						TreeParent moduleNode = lookForChildren(module);
						featureNode.addChild(moduleNode);
					} else
						continue;
				}
			} else
				continue;
			// adding feature node to root
			root.addChild(featureNode);
		}
		// return dummyModel();
		return root;
	}

	private static IResource[] getModules(IResource[] members) {
		List<IResource> result = new ArrayList<>();
		for (int i = 0; i < members.length; i++) {
			if (members[i] instanceof IFile) {
				result.add((IResource) members[i]);
			} else if (members[i] instanceof IFolder) {
				try {
					result.addAll(new ArrayList<IResource>(
							Arrays.asList(getModules(((IFolder) members[i])
									.members()))));
				} catch (CoreException e) {
					System.err.println(e.getMessage());
				}
			} else
				continue;

		}
		
		IResource[] resources = new IResource[result.size()];
				int i = 0;
		for (IResource iResource : result) {
			resources[i] = iResource;
			i++;
		}
		return resources;
	}

	private static TreeParent lookForChildren(IFile module) {
		TreeParent father = new TreeParent(module.getName());
		father.setType(TreeNodeType.MODULE);
		father.setFile(module);
		
		List<CCVariationPoint> vps = null;
		try {
			JavaScriptParser p = new JavaScriptParser();
			vps = p.parse(module);
		} catch (PluginException e) {
			System.err.println(e.getMessage());
		}
		for (CCVariationPoint ccVariationPoint : vps) {
			// create macro nodes
			TreeObject leaf = new TreeObject(formatVPDeclaration(ccVariationPoint
					.getTokens()));
			leaf.setFile(module);
			leaf.setLineMarker(ccVariationPoint.getLineNumber());
			father.addChild(leaf);
		}
		return father;
	}

	private static String formatVPDeclaration(List<Token> tokens) {
		String declaration = "";
		for (Token token : tokens) {
			declaration += token.getValue() + " ";
		}
		return declaration;
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

}
