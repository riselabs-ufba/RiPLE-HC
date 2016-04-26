package br.com.rise.featurejs.ui;

import java.io.File;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import br.com.rise.featurejs.ui.handlers.WorkspaceChangeHandler;


/**
 * The activator class controls the plug-in life cycle.
 * 
 * <p>The FeatureJS UI plug-in is supposed to contribute to the UI of the
 *  <code>{@link FeatureJsCorePlugin}</code> </p>
 * 
 * @author Alcemir Santos
 */
public class FeatureJSUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "br.com.rise.featurejs.ui"; //$NON-NLS-1$

	// The shared instance
	private static FeatureJSUIPlugin plugin;

	protected static IWorkbenchWindow currentWindow;

	/**
	 * The constructor
	 */
	public FeatureJSUIPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static FeatureJSUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, "icons"+File.separator+path);
	}

	public static IWorkbenchPage getCurrentWorkbenchPage() {
		IWorkbench workbench = getDefault().getWorkbench();

		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null) {
			Display display = workbench.getDisplay();
			display.syncExec(new Runnable() {
				public void run() {
					FeatureJSUIPlugin.currentWindow = FeatureJSUIPlugin
							.getDefault().getWorkbench()
							.getActiveWorkbenchWindow();
				}
			});
			window = currentWindow;
		}

		return window.getActivePage();
	}

	public static String getPluginID() {
		return getDefault().getBundle().getSymbolicName();
	}

	public static void log(String msg, Throwable t) {
		log(msg, t, 4);
	}

	public static void log(String msg, Throwable t, int level) {
		IStatus stat = new Status(level, getPluginID(), level, msg, t);
		getDefault().getLog().log(stat);
	}
}
