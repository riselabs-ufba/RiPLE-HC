package br.com.reconcavo.featurejs;

import de.ovgu.featureide.fm.core.AbstractCorePlugin;
import org.osgi.framework.BundleContext;

public class FeatureJsCorePlugin extends AbstractCorePlugin {
	public static final String PLUGIN_ID = "br.com.reconcavo.featurejs";
	private static FeatureJsCorePlugin plugin;

	public String getID() {
		return "br.com.reconcavo.featurejs";
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static FeatureJsCorePlugin getDefault() {
		return plugin;
	}
}
