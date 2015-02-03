/**
 * 
 */
package br.com.rise.featurejs.ui.model;

import java.util.List;

import org.eclipse.core.resources.IFile;

import br.com.riselabs.vparser.beans.CCVariationPoint;

/**
 * This class hold the link between a <code>module</code> and its <code>variation points</code>.
 * 
 * @author "Alcemir Santos"
 *
 */
public class ModuleToVariationPointsLink {

	private IFile module;
	private List<CCVariationPoint> vps;
	
	public IFile getModule() {
		return module;
	}
	public void setModule(IFile module) {
		this.module = module;
	}
	public List<CCVariationPoint> getVps() {
		return vps;
	}
	public void setVps(List<CCVariationPoint> vps) {
		this.vps = vps;
	}
	
	
}
