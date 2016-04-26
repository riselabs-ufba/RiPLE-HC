package br.com.reconcavo.featurejs.model;

import br.com.reconcavo.featurejs.FeatureJsCorePlugin;
import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.core.fstmodel.FSTFeature;
import de.ovgu.featureide.core.fstmodel.FSTField;
import de.ovgu.featureide.core.fstmodel.FSTMethod;
import de.ovgu.featureide.core.fstmodel.FSTModel;
import de.ovgu.featureide.core.fstmodel.FSTRole;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class FeatureJsModelBuilder {
	private FSTModel model;
	private IFeatureProject featureProject;
	private FSTRole currentRole;
	private IFolder tempFolder;

	public FeatureJsModelBuilder(IFeatureProject featureProject,
			IFolder tempFolder) {
		this.tempFolder = tempFolder;
		this.model = new FSTModel(featureProject);
		featureProject.setFSTModel(this.model);
		this.featureProject = featureProject;
	}

	public void resetModel() {
		this.model.reset();
	}

	public boolean buildModel() {
		LinkedList<IFile> infoFiles = getInfoFiles();
		if (infoFiles.isEmpty()) {
			return false;
		}
		for (IFile file : infoFiles) {
			buildModel(file);
		}
		return true;
	}

	public void buildModel(IFile file) {
		LinkedList<String> infos = getInfo(file);
		String className = ((String) infos.getFirst()).split("[;]")[2] + "."
				+ "js";

		for (String info : infos) {
			String[] array = info.split("[;]");
			this.currentRole = this.model.addRole(array[0], className, null);
			this.currentRole.setFile(getFile(className));
			if (array.length == 7)
				addField(array);
			else
				addMethod(array);
		}
	}

	private void addField(String[] array) {
		this.currentRole.add(new FSTField(array[4], array[5], array[6]));
	}

	private void addMethod(String[] array) {
		this.currentRole.add(new FSTMethod(array[4], getParameter(array),
				array[5], array[6]));
	}

	private LinkedList<String> getParameter(String[] array) {
		LinkedList parameter = new LinkedList();
		for (int i = 8; i < array.length; i++) {
			parameter.add(array[i]);
		}
		return parameter;
	}

	private IFile getFile(String className) {
		return this.featureProject.getSourceFolder()
				.getFolder(this.currentRole.getFeature().getName())
				.getFile(className);
	}

	private LinkedList<String> getInfo(IFile file) {
		LinkedList informations = new LinkedList();
		Scanner scanner = null;
		try {
			scanner = new Scanner(file.getRawLocation().toFile(), "UTF-8");
			while (scanner.hasNext())
				informations.add(scanner.nextLine());
		} catch (FileNotFoundException e) {
			FeatureJsCorePlugin.getDefault().logError(e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return informations;
	}

	private LinkedList<IFile> getInfoFiles() {
		LinkedList files = new LinkedList();

		if (!this.tempFolder.exists()) {
			return files;
		}
		try {
			for (IResource res : this.tempFolder.members()) {
				if (((res instanceof IFile)) && (res.getName().endsWith(".js")))
					files.add((IFile) res);
			}
		} catch (CoreException e) {
			FeatureJsCorePlugin.getDefault().logError(e);
		}
		return files;
	}
}
