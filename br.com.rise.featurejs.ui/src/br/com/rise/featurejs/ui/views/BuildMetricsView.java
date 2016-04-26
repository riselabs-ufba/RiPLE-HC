package br.com.rise.featurejs.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class BuildMetricsView extends ViewPart{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.com.rise.riplehc.ui.views.BuildMetricsView";


	public BuildMetricsView() {
	}

	public void createPartControl(Composite parent) {
		Composite parentContainer = getFillLayoutContainer(parent);
		parentContainer.setBackground(new Color(Display.getCurrent(), new RGB(240, 240, 240)));
		Label label = new Label(parentContainer, SWT.BOLD);
		label.setText("Nothing to show yet.");
	}

	private Composite getFillLayoutContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.VERTICAL));
		return container;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}