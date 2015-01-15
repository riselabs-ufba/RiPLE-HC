/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2013  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package de.ovgu.featureide.ui.actions.generator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.FMCorePlugin;

/**
 * Builds all valid configurations for a selected feature project.
 * 
 * @author Jens Meinicke
 */
public class BuildAllValidConfigurationsAction implements
		IObjectActionDelegate, IConfigurationBuilderBasics {
	
	private ISelection selection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		IFeatureProject featureProject;
		if (obj instanceof IResource) {
			IResource res = (IResource) obj;
			featureProject = CorePlugin.getFeatureProject(res);
			if (featureProject == null) {
				return;
			}
		} else {
			return;
		}
		MessageDialogWithToggle dialog = openDialog();
		if (dialog.getReturnCode() == MessageDialogWithToggle.OK) {
			new ConfigurationBuilder(featureProject, true,
					dialog.getToggleState());
		}
		setToggleState(dialog.getToggleState());
	}

	/**
	 * Opens a dialog before building all valid configuration.
	 * 
	 * @return true if all valid configurations should be build.
	 */
	private MessageDialogWithToggle openDialog() {
		return MessageDialogWithToggle.openOkCancelConfirm(null, MESSAGE_TITLE_VALID,
				MESSAGE_START, TOGGLE_MESSAGE, getToggleState(), null, "");
	}
	
	/**
	 * Gets the toggle state from persistent properties
	 */
	private static boolean getToggleState() {
		try {
			return TRUE.equals(ResourcesPlugin.getWorkspace().getRoot().getPersistentProperty(TOGGLE_STATE));
		} catch (CoreException e) {
			FMCorePlugin.getDefault().logError(e);
		}
		return false;
	}

	/**
	 * Saves the toggle state of the dialog at persistent properties
	 */
	private static void setToggleState(boolean value) {
		try {
			ResourcesPlugin.getWorkspace().getRoot().setPersistentProperty(TOGGLE_STATE, value ? TRUE : FALSE);
		} catch (CoreException e) {
			FMCorePlugin.getDefault().logError(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
	 * action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

}
