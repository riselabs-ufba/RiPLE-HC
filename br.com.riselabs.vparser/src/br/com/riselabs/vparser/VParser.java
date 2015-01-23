/**
 * 
 */
package br.com.riselabs.vparser;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author Alcemir Santos
 * 
 */
public class VParser extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "br.com.riselabs.vparser"; //$NON-NLS-1$
	public static final String PLUGIN_NAME = "VParser";

	/**
	 * Returns the plug-in ID as specified at the plug-in manifest.
	 * 
	 * @return the plug-in id
	 */
	public String getID() {
		return PLUGIN_ID;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}


}
