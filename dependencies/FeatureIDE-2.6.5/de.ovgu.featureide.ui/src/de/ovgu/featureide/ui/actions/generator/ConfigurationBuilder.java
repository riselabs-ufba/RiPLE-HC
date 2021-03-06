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

import java.io.IOException;
import java.security.KeyStore.Builder;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.annotation.CheckForNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaProject;

import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.ConfigurationReader;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.ovgu.featureide.ui.UIPlugin;

/**
 * Builds all valid or current configurations for a selected feature project.
 * 
 * @author Jens Meinicke
 */
@SuppressWarnings("restriction")
public class ConfigurationBuilder implements IConfigurationBuilderBasics {
	IFeatureProject featureProject;
	private FeatureModel featureModel;
	
	/**
	 * This is the place where all configurations should be generated if 
	 * no new projects should be generated.
	 */
	IFolder folder;
	
	/**
	 * This is the configuration where the {@link ConfigurationReader} saves the read configuration.
	 */
	private Configuration configuration;
	private ConfigurationReader reader;
	
	/**
	 * The count of found configurations.
	 */
	private long confs;
	
	/**
	 * The count of valid configurations.<br>
	 * This is generated by the satsolver or the amount current configurations at the configurations folder. 
	 */
	private long configurationNumber = 0;
	
	/**
	 * A flag that indicates that the counting Job is running or has finished.
	 */
	private boolean counting = true;
	
	/**
	 * Saves the classpath entry for compilation.
	 */
	String classpath = "";
	
	/**
	 * This folder is used for compilation.
	 */
	IFolder tmp;
	
	/**
	 * This flag indicates if a new project should be created for each configuration.
	 */
	boolean createNewProjects;
	
	/**
	 * The count of how many configurations where already built
	 */
	private int built;
	
	/**
	 * The count of how many configurations where built since the last update.
	 */
	int builtConfigurations = 0; 
	
	/**
	 * The max size of <code>>configurations</code>
	 */
	private int maxSize = 500;

	/**
	 * This flag indicates that all jobs should be aborted.
	 */
	boolean cancelGeneratorJobs = false;
	
	/**
	 * Saves the time of start.
	 */
	protected long time;
	
	/**
	 * This flag indicates that no more configurations will be added and the {@link Generator} 
	 * jobs can finish.
	 */
	boolean finish = false;
	
	/**
	 * <code>true</code>: all valid configurations should be built.<br>
	 * <code>false</code>: all configurations at the configurations folder should be built.
	 */
	boolean buildAllValidConfigurations;
	
	/**
	 * This list contains all {@link Generator} jobs.
	 */
	ArrayList<Generator> generatorJobs = new ArrayList<Generator>();
	
	/**
	 * This list contains all found configurations to built.<br>
	 * Use <code>getConfiguration()</code> and <code>setConfiguration(c)</code> for synchronizing.
	 */
	LinkedList<BuilderConfiguration> configurations = new LinkedList<BuilderConfiguration>();

	
	/**
	 * Gets the first entry of configurations or <code>null</code> if there is none.
	 * @return The first entry
	 */
	@CheckForNull
	public synchronized BuilderConfiguration getConfiguration() {
		if (configurations.isEmpty()) {
			return null;
		}
		BuilderConfiguration c = configurations.getFirst();
		configurations.remove();
		return c;
	}
	
	/**
	 * Adds the given configuration to configurations.
	 * @param configuration
	 */
	public synchronized void addConfiguration(BuilderConfiguration configuration) {
		configurations.add(configuration);
	}

	/**
	 * Starts the build process for valid or current configurations for the given feature project.
	 * @param featureProject The feature project
	 * @param buildAllValidConfigurations <code>true</code> if all possible valid configurations should be build<br>
	 * <code>false</code> if all current configurations should be build
	 * @param createNewProjects <code>true</code> if the configurations should be built into separate projects
	 * @see BuildAllConfigurationsAction
	 * @see BuildAllValidConfigurationsAction
	 */
	ConfigurationBuilder(final IFeatureProject featureProject, final boolean buildAllValidConfigurations,
			final boolean createNewProjects) {
		this.featureProject = featureProject;
		this.createNewProjects = createNewProjects;
		this.buildAllValidConfigurations = buildAllValidConfigurations;
		featureModel = featureProject.getFeatureModel();
		if (!buildAllValidConfigurations) {
			configurationNumber = countConfigurations(this.featureProject.getConfigFolder());
		} else {
			Job number = new Job(JOB_TITLE_COUNT_CONFIGURATIONS) {
				public IStatus run(IProgressMonitor monitor) {
					configurationNumber = new Configuration(featureModel, false, false).number(1000000);
					if (configurationNumber < ((long)0)) {
						UIPlugin.getDefault().logWarning("Satsolver overflow");
						configurationNumber = Integer.MAX_VALUE;
					}
					
					return Status.OK_STATUS;
				}
			};
			number.setPriority(Job.LONG);
			number.schedule();
		}
		
		Job job = new Job(buildAllValidConfigurations ? JOB_TITLE : JOB_TITLE_CURRENT + "for " + featureProject.getProjectName()) {
			public IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask("" , 1);
					
					if (!init(monitor, buildAllValidConfigurations)) {
						return Status.OK_STATUS;
					}
					
					time = System.currentTimeMillis();
					
					monitor.setTaskName(getTaskName());
					
					if (featureProject.getComposer().canGeneratInParallelJobs()) {
						if (buildAllValidConfigurations) {
							newgeneratorJobs(Runtime.getRuntime().availableProcessors() * 2);
						} else {
							int contJobs = Runtime.getRuntime().availableProcessors() * 2;
							if (configurationNumber < contJobs) {
								contJobs = (int)configurationNumber;
							}
							newgeneratorJobs(contJobs);
						}
					} else {
						newgeneratorJobs(1);
					}
					
					if (buildAllValidConfigurations) {
						buildAll(featureModel.getRoot(), monitor);
					} else {
						buildActivConfigurations(featureProject, monitor);
					}
					
					finish();
					
					if (!createNewProjects) {
						try {
							folder.refreshLocal(IResource.DEPTH_INFINITE, null);
						} catch (CoreException e) {
							UIPlugin.getDefault().logError(e);
						}
					}
					showStatistics(monitor);
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}

			private void showStatistics(IProgressMonitor monitor) {
				synchronized (this) {
					while (!generatorJobs.isEmpty()) {
						try {
							if (monitor.isCanceled()) {
								cancelGenerationJobs();
							}
							monitor.setTaskName(getTaskName() + " (waiting for Generators to finish)");
							monitor.worked(builtConfigurations);
							built += builtConfigurations;
							builtConfigurations = 0;
							wait(1000);
						} catch (InterruptedException e) {
							UIPlugin.getDefault().logError(e);
						}
					}
				}

				long duration = System.currentTimeMillis() - time;
				
				long s = (duration/1000)%60;
				long min = (duration/(60 * 1000))%60;
				long h = duration/(60 * 60 * 1000);
				String t = h + "h " + (min < 10 ? "0" + min : min) + "min " + (s < 10 ? "0" + s : s) + "s.";

				if (built > configurationNumber) {
					built = (int) configurationNumber;
				}
				UIPlugin.getDefault().logInfo(built + (configurationNumber != 0 ? " of " + configurationNumber : "") + 
						" configurations built in " + t);
			}

		};
		job.setPriority(Job.LONG);
		job.schedule();
	}
	
	/**
	 * Initializes the configuration builder.<br>
	 * -Removes old products
	 * -Generates the build folder
	 * @param monitor
	 * @param buildAllValidConfigurations<code>true</code> if all possible valid configurations should be build<br>
	 * <code>false</code> if all current configurations should be build
	 */
	private boolean init(IProgressMonitor monitor, boolean buildAllValidConfigurations) {
		confs = 1;
		
		configuration = new Configuration(featureModel);
		reader = new ConfigurationReader(configuration);
		featureProject.getComposer().initialize(featureProject);
		
		if (!createNewProjects) {
			folder = featureProject.getProject().getFolder(buildAllValidConfigurations ? FOLDER_NAME : FOLDER_NAME_CURRENT);
			if (!folder.exists()) {
				try {
					folder.create(true, true, null);
				} catch (CoreException e) {
					UIPlugin.getDefault().logError(e);
				}
			} else {
				try {
					IResource[] members = folder.members();
					int countProducts = members.length;
					int current = 1;
					for (IResource res : members) {
						if (monitor.isCanceled()) {
							return false;
						}
						monitor.setTaskName("Remove old products : " + current + "/" + countProducts);
						current++;
						res.delete(true, null);
					}
				} catch (CoreException e) {
					UIPlugin.getDefault().logError(e);
				}
			}
			setClassPath();
		
			tmp = folder.getFolder(TEMPORARY_BIN_FOLDER);
			if (!tmp.exists()) {
				try {
					tmp.create(true, true, null);
				} catch (CoreException e) {
					UIPlugin.getDefault().logError(e);
				}
			}
		} else {
			try {
				for (IResource res : ResourcesPlugin.getWorkspace().getRoot().members()) {
					if (res instanceof IProject) {
						IProject p = (IProject)res;
						String projectName = p.getName(); 
						if (buildAllValidConfigurations) {
							if (projectName.startsWith(featureProject.getProjectName() + SEPARATOR_VARIANT)) {
								monitor.setTaskName("Remove old products : " + projectName);
								res.delete(true, null);
							}
						} else {
							if (projectName.startsWith(featureProject.getProjectName() + SEPARATOR_CONFIGURATION)) {
								monitor.setTaskName("Remove old products : " + projectName);
								res.delete(true, null);
							}
						}
					}
				}
			} catch (CoreException e) {
				UIPlugin.getDefault().logError(e);
			}
		}
		return true;
	}
	
	/**
	 * Sets the java classPath for compiling.
	 */
	private void setClassPath() {
		String sep = System.getProperty("path.separator");
		try {
			JavaProject proj = new JavaProject(featureProject.getProject(), null);
			IJavaElement[] elements = proj.getChildren();
			for (IJavaElement e : elements) {
				String path = e.getPath().toOSString();
				if (path.contains(":")) {
					classpath += sep + path;
					continue;
				}
				IResource resource = e.getResource();
				if (resource != null && "jar".equals(resource.getFileExtension())) {
					classpath += sep + resource.getRawLocation().toOSString();
				}
			}
		} catch (JavaModelException e) {
			
		}			
		classpath = classpath.length() > 0 ? classpath.substring(1) : classpath;
	}

	/**
	 * Builds all current configurations for the given feature project into the folder for current configurations.
	 * @param featureProject The feature project
	 * @param monitor 
	 */
	protected void buildActivConfigurations(IFeatureProject featureProject, IProgressMonitor monitor) {
		monitor.beginTask("" , (int) configurationNumber);
		try {
			for (IResource configuration : featureProject.getConfigFolder().members()) {
				if (monitor.isCanceled()) {
					finish();
					return;
				}
				if (isConfiguration(configuration)) {
					build(configuration, monitor);
				}
			}
		} catch (CoreException e) {
			UIPlugin.getDefault().logError(e);
		}
	}


	/**
	 * Builds the given configuration file into the folder for current configurations.
	 * @param configuration The configuration file
	 * @param monitor 
	 */
	private void build(IResource configuration, IProgressMonitor monitor) {
		try {
			reader.readFromFile((IFile)configuration);
			addConfiguration(new BuilderConfiguration(this.configuration, configuration.getName().split("[.]")[0]));
		} catch (CoreException e) {
			UIPlugin.getDefault().logError(e);
		} catch (IOException e) {
			UIPlugin.getDefault().logError(e);
		}
	}

	/**
	 * Counts the configurations at the given folder.
	 * @param configFolder The folder
	 * @return Number of configuration files
	 */
	private int countConfigurations(IFolder configFolder) {
		int i = 0;
		try {
			for (IResource res : configFolder.members()) {
				if (isConfiguration(res)) {
					i++;
				}		
			}
		} catch (CoreException e) {
			UIPlugin.getDefault().logError(e);
		}
		return i;
	}

	/**
	 * @param res A file.
	 * @return <code>true</code> if the given file is a configuration file
	 */
	private boolean isConfiguration(IResource res) {
		return res instanceof IFile && CorePlugin.getDefault().getConfigurationExtensions().contains(res.getFileExtension());
	}
	
	/**
	 * Builds all possible valid configurations for the feature project.<br>
	 * Iterates through the structure of the feature model and ignores constraints, to get a linear expenditure.<br>
	 * After collecting a configurations the satsolver tests its validity.<br>
	 * Then the found configuration will be build into the folder for all valid products.
	 * @param root The root feature of the feature model
	 * @param monitor
	 */
	private void buildAll(Feature root, IProgressMonitor monitor) {
		LinkedList<Feature> selectedFeatures2 = new LinkedList<Feature>();
		selectedFeatures2.add(root);
		build(root, "", selectedFeatures2, monitor);
	}

	private void build(Feature currentFeature, String selected, LinkedList<Feature> selectedFeatures2, IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			cancelGenerationJobs();
			return;
		}
				
		if ((configurationNumber != 0 && confs > configurationNumber)) {
			return;
		}
		
		if (!counting && configurationNumber != 0) {
			monitor.worked(builtConfigurations);
			built += builtConfigurations;
			builtConfigurations = 0;
		} else if (counting) {
			built = builtConfigurations;
		}
		
		monitor.setTaskName(getTaskName());
		
		if (selectedFeatures2.isEmpty()) {
			if (reader.readFromString(selected)) {
				if (configuration.valid()) {
					LinkedList<String> selectedFeatures3 = new LinkedList<String>();
					for (String f : selected.split("[ ]")) {
						if (!"".equals(f)) {
							selectedFeatures3.add(f);
						}
					}
					for (Feature f : configuration.getSelectedFeatures()) {
						if (f.isConcrete()) {
							if (!selectedFeatures3.contains(f.getName())) {
								return;
							}
						}
					}
					for (String f : selectedFeatures3) {
						if (configuration.getSelectablefeature(f).getSelection() != Selection.SELECTED) {
							return;
						}
					}
					
					monitor.setTaskName(getTaskName());
					addConfiguration(new BuilderConfiguration(configuration, confs));
					
					if (configurations.size() >= maxSize) {
						monitor.setTaskName(getTaskName() + " (waiting, buffer full)");
						synchronized (this) {	
							while (configurations.size() >= maxSize ) {
								if (monitor.isCanceled()) {
									return;
								}
								try {
									wait(1000);
								} catch (InterruptedException e) {
									UIPlugin.getDefault().logError(e);
								}
							}
						}
					}
					confs++; 
					monitor.setTaskName(getTaskName());
					if (counting && configurationNumber == 0) {
						built = builtConfigurations;
					}
					if (counting && configurationNumber != 0) {
						counting = false;
						monitor.beginTask("" , (int) configurationNumber);
						monitor.worked((int)builtConfigurations);
						built = builtConfigurations;
						builtConfigurations = 0;
					} else if (configurationNumber != 0) {
						monitor.worked(builtConfigurations);
						built += builtConfigurations;
						builtConfigurations = 0;
					}
				}
			}
			return;
		}
		
		if (currentFeature.isAnd()) {
			buildAnd(selected, selectedFeatures2, monitor);
		} else if (currentFeature.isOr()) {
			buildOr(selected, selectedFeatures2, monitor);
		} else if (currentFeature.isAlternative()) {
			buildAlternative(selected, selectedFeatures2, monitor);
		}				
	}

	/**
	 * @return a description of the state.
	 */
	private String getTaskName() {
		String t = "";
		if (configurationNumber != 0 && built != 0) {
			long duration = System.currentTimeMillis() - time;
			duration = (duration / built) * (configurationNumber - built);
			long s = (duration/1000)%60;
			long min = (duration/(60 * 1000))%60;
			long h = duration/(60 * 60 * 1000);
			t = " " + h + "h " + (min < 10 ? "0" + min : min) + "min " + (s < 10 ? "0" + s : s) + "s.";
		}
		long buffer = buildAllValidConfigurations ? configurations.size() : configurationNumber - built;
		return "Built configutations: "+ built + "/" + (configurationNumber == 0 ? "counting..." : configurationNumber)
				+ "(" + buffer + " buffered)" + " Expected time: " + t;
	}

	private void buildAlternative(String selected, LinkedList<Feature> selectedFeatures2, IProgressMonitor monitor) {
		Feature currentFeature = selectedFeatures2.getFirst();
		selectedFeatures2.removeFirst();
		LinkedList<Feature> selectedFeatures3 = new LinkedList<Feature>();
		if (currentFeature.isConcrete()) {
			if ("".equals(selected)) {
				selected = currentFeature.getName();
			} else {
				selected += " " + currentFeature.getName();
			}
		}
		if (!currentFeature.hasChildren()) {
			if (selectedFeatures2.isEmpty()) {
				currentFeature = null;
			} else {
				currentFeature = selectedFeatures2.getFirst();
			}
			selectedFeatures3.addAll(selectedFeatures2);
			build(currentFeature, selected, selectedFeatures3, monitor);
			return;
		}
		for (int i2 = 0;i2 < getChildren(currentFeature).size();i2++) {
			selectedFeatures3 = new LinkedList<Feature>();
			selectedFeatures3.add(getChildren(currentFeature).get(i2));
			selectedFeatures3.addAll(selectedFeatures2);
			build(selectedFeatures3.isEmpty() ? null : selectedFeatures3.getFirst(), selected, selectedFeatures3, monitor);
		}
	}


	private void buildOr(String selected, LinkedList<Feature> selectedFeatures2, IProgressMonitor monitor) {
		Feature currentFeature = selectedFeatures2.getFirst();
		selectedFeatures2.removeFirst();
		LinkedList<Feature> selectedFeatures3 = new LinkedList<Feature>();
		if (currentFeature.isConcrete()) {
			if ("".equals(selected)) {
				selected = currentFeature.getName();
			} else {
				selected += " " + currentFeature.getName();
			}
		}
		if (!currentFeature.hasChildren()) {
			if (selectedFeatures2.isEmpty()) {
				currentFeature = null;
			} else {
				currentFeature = selectedFeatures2.getFirst();
			}
			selectedFeatures3.addAll(selectedFeatures2);
			build(currentFeature, selected, selectedFeatures3, monitor);
			return;
		}
		int k2;
		int i2 = 1;
		if (getChildren(currentFeature).size() < currentFeature.getChildren().size()) {
			i2 = 0;
		}
		for (;i2 < (int)java.lang.Math.pow(2, getChildren(currentFeature).size());i2++) {
			k2 = i2;
			selectedFeatures3 = new LinkedList<Feature>();
			for (int j = 0;j < getChildren(currentFeature).size();j++) {
				if (k2%2 != 0) {
					selectedFeatures3.add(getChildren(currentFeature).get(j));
				}
				k2 = k2/2;
			}
			selectedFeatures3.addAll(selectedFeatures2);
			build(selectedFeatures3.isEmpty() ? null : selectedFeatures3.getFirst(), selected, selectedFeatures3, monitor);
		}
	}

	private void buildAnd(String selected, LinkedList<Feature> selectedFeatures2, IProgressMonitor monitor) {
		Feature currentFeature = selectedFeatures2.getFirst();
		selectedFeatures2.removeFirst();
		LinkedList<Feature> selectedFeatures3 = new LinkedList<Feature>();
		if (currentFeature.isConcrete()) {
			if ("".equals(selected)) {
				selected = currentFeature.getName();
			} else {
				selected += " " + currentFeature.getName();
			}
		}
		if (!currentFeature.hasChildren()) {
			if (selectedFeatures2.isEmpty()) {
				currentFeature = null;
			} else {
				currentFeature = selectedFeatures2.getFirst();
			}
			selectedFeatures3.addAll(selectedFeatures2);
			build(currentFeature, selected, selectedFeatures3, monitor);
			return;
		}
		int k2;
		LinkedList<Feature> optionalFeatures = new LinkedList<Feature>();
		for (Feature f : getChildren(currentFeature)) {
			if (f.isMandatory()) {
				selectedFeatures2.add(f);
			} else {
				optionalFeatures.add(f);
			}
		}
		for (int i2 = 0;i2 < (int)java.lang.Math.pow(2, optionalFeatures.size());i2++) {
			k2 = i2;
			selectedFeatures3 = new LinkedList<Feature>();
			for (int j = 0;j < optionalFeatures.size();j++) {
				if (k2%2 != 0) {
					selectedFeatures3.add(optionalFeatures.get(j));
				}
				k2 = k2/2;
			}
			selectedFeatures3.addAll(selectedFeatures2);
			build(selectedFeatures3.isEmpty() ? null : selectedFeatures3.getFirst(), selected, selectedFeatures3, monitor);
		}
		
	}

	/**
	 * Returns all children of a feature if it is a layer or if it has a child that is a layer.
	 * @param currentFeature The feature
	 * @return The children
	 */
	private LinkedList<Feature> getChildren(Feature currentFeature) {
		LinkedList<Feature> children = new LinkedList<Feature>();
		for (Feature child : currentFeature.getChildren()) {
			if (child.isConcrete() || hasLayerChild(child)) {
				children.add(child);
			}
		}
		return children;
	}

	/**
	 * @param feature The feature
	 * @return <code>true</code> if  the feature is a layer or if it has a child that is a layer
	 */
	private boolean hasLayerChild(Feature feature) {
		if (feature.hasChildren()) {
			for (Feature child : feature.getChildren()) {
				if (child.isConcrete() || hasLayerChild(child)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Creates new {@link Generator}s
	 * @param count The amount of Generators that will be created.
	 */
	private void newgeneratorJobs(int count) {
		if (count < 1) {
			return;
		}
		
		if (count == 1) {
			createNewGenerator(0);
			return;
		}
		
		for (;count > 0;count--) {
			createNewGenerator(count);
		}
	}
	
	/**
	 * Created a new {@link Generator} with the given number
	 * @param nr
	 */
	void createNewGenerator(int nr) {
		Generator g = new Generator(nr, this);
		generatorJobs.add(g);
		g.setPriority(Job.LONG);
		g.schedule();
	}
	
	/**
	 * This is called if the main job is canceled and
	 * all {@link Builder} and {@link Compiler} should finish.
	 */
	private void cancelGenerationJobs() {
		cancelGeneratorJobs = true;
	}
	
	/**
	 * This is called if the main job has finished and
	 * no more configurations will be added.
	 */
	private void finish() {
		finish  = true;
	}
	
}
