package br.com.riselabs.vparser.exceptions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import br.com.riselabs.vparser.VParser;

public class PluginException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PluginException(final String message) {
		super(message);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(new Shell(), VParser.PLUGIN_NAME,
						message);
			}
		});
	}

}
