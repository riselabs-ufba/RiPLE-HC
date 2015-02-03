/**
 * 
 */
package br.com.rise.featurejs.ui.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import br.com.rise.featurejs.ui.helpers.managers.InteractionGraphsManager;
import br.com.rise.featurejs.ui.helpers.managers.ScatteringTraceabilityLinksManager;
import br.com.rise.featurejs.ui.helpers.managers.ScatteringTreeManager;
import br.com.rise.featurejs.ui.model.ModuleToVariationPointsLink;
import br.com.rise.featurejs.ui.model.ScatteringTraceabilityLink;
import br.com.riselabs.vparser.exceptions.PluginException;
import br.com.riselabs.vparser.parsers.JavaScriptParser;
import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;

/**
 * This aims to help the views in the JavaScript modules parsing tasks.
 * 
 * @author Alcemir Santos
 * 
 */
public class WorkspaceChangeHandler implements IResourceChangeListener, ISelectionListener {

	private static WorkspaceChangeHandler instance;

	private IFeatureProject targetProject;
	private static IPath CONFIGS_PATH;
	private static IPath FEATURE_PATH;

	private WorkspaceChangeHandler() {
	}

	public static WorkspaceChangeHandler getInstance(){
		if(instance == null)
			instance = new WorkspaceChangeHandler();
		return instance;
	}
	
	public void setTargetProject(IFeatureProject targetProject) {
		init(targetProject);
		treatProject();
	}

	private void init(IFeatureProject aProject){
		this.targetProject = aProject;
		FEATURE_PATH = this.targetProject.getSourceFolder().getFullPath();
		CONFIGS_PATH = this.targetProject.getConfigFolder().getFullPath();
	}
	
	public IFeatureProject getCurrentProject(){
		return this.targetProject;
	}
	
//	ISelectionListener pListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection sel) {
			if (!(sel instanceof IStructuredSelection))
				return;
			IStructuredSelection ss = (IStructuredSelection) sel;
			Object o = ss.getFirstElement();
			if (o instanceof IProject) {
				init(CorePlugin.getFeatureProject((IProject) o));
				String pName = targetProject.getProjectName();

				// if it exists do not treat project twice
				if (ScatteringTreeManager.getInstance().exists(pName))
					ScatteringTreeManager.getInstance().setCurrent(pName);
				// if it exists do not treat project twice
				if (InteractionGraphsManager.getInstance().exists(pName))
					InteractionGraphsManager.getInstance().setCurrent(pName);
				// if it exists do not treat project twice
				if (ScatteringTraceabilityLinksManager.getInstance().exists(pName))
					ScatteringTraceabilityLinksManager.getInstance().setCurrent(pName);
				
				treatProject();
			}
			if (o instanceof IFeatureProject) {
				System.out.println("WorkspaceChangeHandler.enclosing_method()");
			}
		}

//	};

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		List<ScatteringTraceabilityLink> links = null;
		switch (event.getType()) {
		case IResourceChangeEvent.POST_BUILD:
			break;
		case IResourceChangeEvent.POST_CHANGE:

//			ArrayList<IResource> resourcesChanged = new ArrayList<IResource>();
//			final String jsType = "js";
//			final String configType = "config";
//
//			// get javascript resources that may have changed
//			resourcesChanged = getResourcesChanged(event, FEATURE_PATH, jsType);
//
//			updateLinks(resourcesChanged, jsType);
//
//			// get configuration file that may have changed
//			resourcesChanged = getResourcesChanged(event, CONFIGS_PATH,	configType);
//
//			updateLinks(resourcesChanged, configType);
			break;
		case IResourceChangeEvent.PRE_BUILD:
			break;
		case IResourceChangeEvent.PRE_CLOSE:
			break;
		case IResourceChangeEvent.PRE_DELETE:
			break;
		case IResourceChangeEvent.PRE_REFRESH:
			break;
		default:
			break;
		}
	}

	/**
	 * @param resourcesChanged
	 * @param fileType
	 */
	private void updateLinks(ArrayList<IResource> resourcesChanged,
			final String fileType) {
		List<ScatteringTraceabilityLink> links;
		// if any javascript resource have changed, then treat it.
		if (resourcesChanged != null && resourcesChanged.size() > 0) {

			links = treatDeltaResources(resourcesChanged, fileType);
			if(links != null)
				// post this up ate to the links manager
				ScatteringTraceabilityLinksManager.getInstance().addObject(
					resourcesChanged.get(0).getProject().getName(), links);
		}
	}

	private void treatProject() {
		List<ScatteringTraceabilityLink> links = new ArrayList<ScatteringTraceabilityLink>();
		// TODO build links from the the given project
		IResource[] featuresFolder = null;
		try {
			featuresFolder = targetProject.getSourceFolder().members();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (featuresFolder == null)
			return;
		for (IResource folder : featuresFolder) {
			if (folder instanceof IFolder) {
				ScatteringTraceabilityLink aLink = new ScatteringTraceabilityLink();
				aLink.setProject(targetProject);
				aLink.setFeature(getFeature(folder));
				aLink.setModulevpLinks(getmoduleVPLinks(folder));
				links.add(aLink);
			}
		}
		ScatteringTraceabilityLinksManager.getInstance().addObject(
				targetProject.getProjectName(), links);
	}

	/**
	 * might return null if 'folder' is not an instance of IFolder.
	 * 
	 * @param folder
	 * @return
	 */
	private List<ModuleToVariationPointsLink> getmoduleVPLinks(IResource folder) {
		List<ModuleToVariationPointsLink> result = new ArrayList<>();
		IFolder f = folder instanceof IFolder ? (IFolder) folder : null;
		if (f == null)
			return null;
		try {
		for (IResource r : f.members()) {
			if (r instanceof IFile) {
				if (((IFile) r).getFileExtension().equals("js")) {
					result.add(handleJSModule((IFile) r));
				}
			} else if (r instanceof IFolder) {
					result.addAll(new ArrayList<ModuleToVariationPointsLink>(
							getmoduleVPLinks(r)));
			} else
				continue;

		}
		} catch (CoreException e) {
			System.err.println(e.getMessage());
		}

		return result;
	}

	private ModuleToVariationPointsLink handleJSModule(IFile aModule) {
		ModuleToVariationPointsLink aLink = new ModuleToVariationPointsLink();
		aLink.setModule(aModule);
		try {
			aLink.setVps(new JavaScriptParser().parse(aModule));
		} catch (PluginException e) {
			e.printStackTrace();
		}
		return aLink;
	}

	private IFolder getFeature(IResource folder) {
		return folder instanceof IFolder ? (IFolder) folder : null;
	}

	private List<ScatteringTraceabilityLink> treatDeltaResources(
			ArrayList<IResource> resourcesChanged, String type) {
		List<ScatteringTraceabilityLink> result = null;
		switch (type) {
		case "js":
			JavaScriptParser parser = new JavaScriptParser();
			for (IResource iResource : resourcesChanged) {
				if (iResource instanceof IFile) {
					try {
						parser.parse((IFile) iResource);
					} catch (PluginException e) {
						e.printStackTrace();
					}
				}
			}

			break;
		case "config":
			break;
		default:
			break;
		}
		return result;
	}

	private ArrayList<IResource> getResourcesChanged(
			IResourceChangeEvent event, IPath aPath, final String fileExtension) {
		IResourceDelta rootDelta = event.getDelta();
		// get the delta, if any, for the documentation directory
		IResourceDelta configsDelta = rootDelta.findMember(aPath);

		if (configsDelta == null)
			return null;

		final ArrayList<IResource> modulesChanged = new ArrayList<IResource>();

		IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
			public boolean visit(IResourceDelta delta) {
				// only interested in changed resources (not added or
				// removed)
				if (delta.getKind() != IResourceDelta.CHANGED)
					return true;
				// only interested in content changes
				if ((delta.getFlags() & IResourceDelta.CONTENT) == 0)
					return true;
				IResource resource = delta.getResource();
				// interested in files with the "s" extension
				if (resource.getType() == IResource.FILE
						&& fileExtension.equalsIgnoreCase(resource
								.getFileExtension())) {
					modulesChanged.add(resource);
				}
				return true;
			}
		};
		try {
			configsDelta.accept(visitor);
		} catch (CoreException e) {
			// open error dialog with syncExec or print to plugin log file
		}
		return modulesChanged;
	}

	/**
	 * Asks the use which project should be read to update the view.
	 * 
	 * @return a flag indicating the project. <code>null</code> if the user
	 *         cancel the operation.
	 * @throws CoreException
	 */
	public static IProject askByProject() throws CoreException {

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		Map<String, IProject> projectsMap = new HashMap<String, IProject>();

		// testing if the project is open
		for (int i = 0; i < projects.length; i++) {
			IProject aProject = projects[i];

			if (aProject.isOpen())
				if (aProject
						.isNatureEnabled("de.ovgu.featureide.core.featureProjectNature")) {
					// if(aProject.isNatureEnabled("br.com.reconcavo.featurejs.FeatureJsComposer"))
					// {
					// only opened featurejs projects to the list
					projectsMap.put(aProject.getName(), aProject);
				}
		}

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				Display.getCurrent().getActiveShell(), new LabelProvider());

		String[] names = new String[projectsMap.keySet().size()];
		int i = 0;
		for (String s : projectsMap.keySet()) {
			names[i] = s;
			i++;
		}

		dialog.setElements(names);
		dialog.setTitle("Which project?");
		// enquanto o usuário não disser qual é o oracle
		while (dialog.open() != Window.OK) {
			MessageDialog.openInformation(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					"Selecting a project...", "You should select a project.");
		}

		Object[] result = dialog.getResult();
		if (result == null)
			return null; // user give up to update
		else
			return projectsMap.get(result[0]);
	}

}
