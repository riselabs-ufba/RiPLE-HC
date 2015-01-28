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

		// create scattering tree
		TreeParent root = createScatteringTree(selectedProject);

		// add tree to the manager
		ScatteringTreeManager.getInstance().addObject(projectName, root);

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

}
