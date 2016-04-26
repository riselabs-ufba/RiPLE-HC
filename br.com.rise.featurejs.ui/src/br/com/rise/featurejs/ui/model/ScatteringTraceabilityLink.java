/**
 * 
 */
package br.com.rise.featurejs.ui.model;

import java.util.List;

import org.eclipse.core.resources.IFolder;

import br.com.rise.featurejs.ui.views.FeatureInteractionsView;
import br.com.rise.featurejs.ui.views.ScatteringTreeView;
import de.ovgu.featureide.core.IFeatureProject;

/**
 * This class aims to hold links among <code>Features</code>, <code>Modules</code>, 
 * and <code>Variation Points</code>.
 * <p>
 * Such links are helpful to build the visualizations from the <code>{@link ScatteringTreeView}</code> 
 * and the <code>{@link FeatureInteractionsView}</code>.
 * <p>
 * 
 * @author Alcemir Santos
 */
public class ScatteringTraceabilityLink {

	private IFeatureProject project;
	private IFolder feature;
	private List<ModuleToVariationPointsLink> modulevpLinks;
	
	/**
	 * 
	 */
	public ScatteringTraceabilityLink() {
	}

	public IFolder getFeature() {
		return feature;
	}

	public void setFeature(IFolder feature) {
		this.feature = feature;
	}

	public List<ModuleToVariationPointsLink> getModulevpLinks() {
		return modulevpLinks;
	}

	public void setModulevpLinks(List<ModuleToVariationPointsLink> modulevpLinks) {
		this.modulevpLinks = modulevpLinks;
	}

	public IFeatureProject getProject() {
		return this.project;
	}
	
	public void setProject(IFeatureProject aProject){
		this.project = aProject;
	}

}
