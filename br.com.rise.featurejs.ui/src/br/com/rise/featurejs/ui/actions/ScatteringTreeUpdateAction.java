/**
 * 
 */
package br.com.rise.featurejs.ui.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import br.com.rise.featurejs.ui.FeatureJSUIPlugin;
import br.com.rise.featurejs.ui.views.ScatteringTreeView;
import br.com.rise.featurejs.ui.views.components.ScatteringTreeViewContentProvider;

/**
 * @author Alcemir Santos
 * 
 */
public class ScatteringTreeUpdateAction extends Action {
	
	public ScatteringTreeUpdateAction() {
		super("Update");
		setToolTipText("Update");
		setImageDescriptor(FeatureJSUIPlugin.getImageDescriptor("refresh.gif"));
	}

	public void run() {
		System.out.println("ScatteringTreeUpdateAction.run()");

		try {
			System.out.println(askByProject().getName());
		} catch (CoreException e) {
			System.err.println(e.getMessage());
		}
		
		List l = createViewInput(askByProject());
		
	}

	/**
	 * Asks the use which project should be read to update the view.
	 * 
	 * @return a flag indicating the project. <code>null</code> if the user
	 *         cancel the operation.
	 * @throws CoreException
	 */
	private IProject askByProject() throws CoreException {

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		Map<String, IProject> projectsMap = new HashMap<String, IProject>();

		// testing if the project is open
		for (int i = 0; i < projects.length; i++) {
			IProject aProject = projects[i];

			if (aProject.isOpen())
				if (aProject.isNatureEnabled("de.ovgu.featureide.core.featureProjectNature")){
//						if(aProject.isNatureEnabled("br.com.reconcavo.featurejs.FeatureJsComposer")) {
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
					"Updating Scattering Tree...",
					"You should select a project.");
		}

		Object[] result = dialog.getResult();
		if (result == null)
			return null; // user give up to update
		else
			return projectsMap.get(result[0]);
	}
}
