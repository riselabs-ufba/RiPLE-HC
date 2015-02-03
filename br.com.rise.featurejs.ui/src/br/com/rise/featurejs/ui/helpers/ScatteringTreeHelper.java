/**
 * 
 */
package br.com.rise.featurejs.ui.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;
import br.com.rise.featurejs.ui.model.ModuleToVariationPointsLink;
import br.com.rise.featurejs.ui.model.ScatteringTraceabilityLink;
import br.com.rise.featurejs.ui.model.TreeObject;
import br.com.rise.featurejs.ui.model.TreeParent;
import br.com.rise.featurejs.ui.model.enums.TreeNodeType;
import br.com.riselabs.vparser.beans.CCVariationPoint;
import br.com.riselabs.vparser.exceptions.PluginException;
import br.com.riselabs.vparser.lexer.beans.Token;
import br.com.riselabs.vparser.parsers.JavaScriptParser;

/**
 * @author Alcemir Santos
 *
 */
public class ScatteringTreeHelper {

	public TreeParent buildTree(List<ScatteringTraceabilityLink> links) {
		return createScatteringTree(links);
	}

	private TreeParent createScatteringTree(List<ScatteringTraceabilityLink> aList) {
		TreeParent root = new TreeParent(aList.get(0).getProject().getProjectName());
		for (ScatteringTraceabilityLink link : aList) {
			root.addChild(makeChildren(link.getModulevpLinks(), link.getFeature().getName()));
		}
		return root;
	}
	
	private TreeParent makeChildren(List<ModuleToVariationPointsLink> moduleLinks, String featureName) {
		TreeParent result = new TreeParent(featureName);
		result.setType(TreeNodeType.FEATURE);

		for (ModuleToVariationPointsLink mLink : moduleLinks) {
			TreeParent father = new TreeParent(mLink.getModule().getName());
			father.setFile(mLink.getModule());
			father.setParent(result);
			father.setType(TreeNodeType.MODULE);
			
			for (CCVariationPoint vp : mLink.getVps()) {
				TreeObject child = new TreeObject(vp.toString());
				child.setFile(mLink.getModule());
				child.setLineMarker(vp.getLineNumber());
				child.setParent(father);
				child.setType(TreeNodeType.MACRO);
				
				father.addChild(child);
			}
			result.addChild(father);
		}
		return result;
	}

	private TreeParent createScatteringTree(IFeatureProject selectedProject) {
		// gets the FeatureIDE project
		TreeParent root = new TreeParent(selectedProject.getProjectName());

		TreeParent featureNode;
		IResource[] featureFolders = null;
		IResource[] featureModules = null;
		try {
			// create feature nodes
			featureFolders = selectedProject.getSourceFolder().members();
		} catch (CoreException e) {
			System.err.println(e.getMessage());
		}

		for (int i = 0; i < featureFolders.length; i++) {
			if (featureFolders[i] instanceof IFolder) {
				featureNode = new TreeParent(featureFolders[i].getName());
				featureNode.setType(TreeNodeType.FEATURE);

				try {
					// create modules nodes
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

	private IResource[] getModules(IResource[] members) {
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

	private TreeParent lookForChildren(IFile module) {
		TreeParent father = new TreeParent(module.getName());
		father.setType(TreeNodeType.MODULE);
		father.setFile(module);

		List<CCVariationPoint> vps = null;
		try {
			vps = new JavaScriptParser().parse(module);
		} catch (PluginException e) {
			System.err.println(e.getMessage());
		}
		for (CCVariationPoint ccVariationPoint : vps) {
			// create macro nodes
			TreeObject leaf = new TreeObject(
					formatVPDeclaration(ccVariationPoint.getTokens()));
			leaf.setFile(module);
			leaf.setLineMarker(ccVariationPoint.getLineNumber());
			father.addChild(leaf);
		}
		return father;
	}

	private  String formatVPDeclaration(List<Token> tokens) {
		String declaration = "";
		for (Token token : tokens) {
			declaration += token.getValue() + " ";
		}
		return declaration;
	}
}
