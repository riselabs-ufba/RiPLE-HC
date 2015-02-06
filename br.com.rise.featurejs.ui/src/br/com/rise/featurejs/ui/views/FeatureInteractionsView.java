/**
 * 
 */
package br.com.rise.featurejs.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import br.com.rise.featurejs.ui.FeatureJSUIPlugin;
import br.com.rise.featurejs.ui.handlers.WorkspaceChangeHandler;
import br.com.rise.featurejs.ui.helpers.managers.ScatteringTraceabilityLinksManager;
import br.com.rise.featurejs.ui.model.NodeModelContentProvider;
import br.com.rise.featurejs.ui.model.ScatteringTraceabilityLink;
import br.com.rise.featurejs.ui.views.components.ZestLabelProvider;
import br.com.rise.featurejs.ui.views.components.ZestNodeContentProvider;
import de.ovgu.featureide.core.IFeatureProject;

/**
 * @author "Alcemir Santos"
 * 
 */
public class FeatureInteractionsView extends ViewPart implements
IZoomableWorkbenchPart, PropertyChangeListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.com.rise.featurejs.ui.views.ScatteringTreeView";

	private Composite parentContainer;
	private Map<TabItem, GraphViewer> tabViewers;

	private TabFolder folder;

	private IFeatureProject currentProject;

	public FeatureInteractionsView() {
		ScatteringTraceabilityLinksManager.getInstance()
				.addChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		parentContainer = getFillLayoutContainer(parent);
		parentContainer.setBackground(new Color(Display.getCurrent(), new RGB(240, 240, 240)));
		Label label = new Label(parentContainer, SWT.BOLD);
		label.setText("Nothing to show yet.\nYou need to select a Feature JS project.");
	}

	private LayoutAlgorithm setLayout() {
		LayoutAlgorithm layout;
		// TODO create a toggle layout action.
//		 layout = new
//		 SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		layout = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
//		// layout = new
//		// GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
//		// layout = new
//		// HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
//		 layout = new
//		 RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		return layout;

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	private void fillToolBar() {
		ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(
				this);
		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(toolbarZoomContributionViewItem);
	}

	@Override
	public AbstractZoomableViewer getZoomableViewer() {
		return tabViewers.get(folder.getItem(folder.getSelectionIndex()));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		List<ScatteringTraceabilityLink> links = null;
		if (evt.getNewValue() instanceof List<?>)
			links = (List<ScatteringTraceabilityLink>) evt.getNewValue();

		IResource[] configFiles = null;
		try {
			if (currentProject == null)
				currentProject = WorkspaceChangeHandler.getInstance()
						.getCurrentProject();
			configFiles = currentProject.getConfigFolder().members();
		} catch (CoreException e) {
			FeatureJSUIPlugin.log("Configuration files not found", e);
		}

		if (configFiles == null || links == null)
			return;

		if (tabViewers == null) {
			initView(links, configFiles);
			this.parentContainer.layout();
			this.parentContainer.pack();
			this.parentContainer.redraw();
			this.parentContainer.update();
		} else
			setInput(links, configFiles);

	}

	public void setInput(List<ScatteringTraceabilityLink> newInput,
			IResource[] configFiles) {

		int i = 0;
		for (Entry<TabItem, GraphViewer> e : tabViewers.entrySet()) {
			if (!e.getKey().getText().toLowerCase().contains("product")) {
				e.getValue().setInput(
						new NodeModelContentProvider(newInput, null));
				e.getValue().applyLayout();
				e.getValue().refresh();
				continue;
			}

			e.getValue().setInput(
					new NodeModelContentProvider(newInput, configFiles[i++]));
			e.getValue().applyLayout();
			e.getValue().refresh();
		}
	}

	public void initView(List<ScatteringTraceabilityLink> links,
			IResource[] configFiles) {
		disposeChildren(parentContainer);
		folder = new TabFolder(parentContainer, SWT.NONE);
		List<TabItem> itens = new ArrayList<>();
		tabViewers = new HashMap<TabItem, GraphViewer>();

		TabItem projectTab = new TabItem(folder, SWT.NONE);
		projectTab.setText("Overall interaction");
		Composite container = getFillLayoutContainer(folder);
		tabViewers.put(projectTab, addContentTo(container, links, null));
		projectTab.setControl(container);

		for (int i = 0; i < configFiles.length; i++) {
			TabItem tab = new TabItem(folder, SWT.NONE);
			tab.setText("Product " + (i + 1));
			tab.setToolTipText("It shows the interactions of the "+configFiles[i].getName()+" product only.");
			container = getFillLayoutContainer(folder);
			tabViewers.put(tab, addContentTo(container, links, configFiles[i]));
			tab.setControl(container);
			
			itens.add(tab);
		}

		fillToolBar();
	}

	/**
	 * @param parent
	 * @param links
	 * @param configFile
	 * @return GraphViewer
	 */
	private GraphViewer addContentTo(Composite parent,
			List<ScatteringTraceabilityLink> links, IResource configFile) {
		GraphViewer viewer = new GraphViewer(parent, SWT.BORDER);
		viewer.setContentProvider(new ZestNodeContentProvider());
		viewer.setLabelProvider(new ZestLabelProvider());
		NodeModelContentProvider model = new NodeModelContentProvider(links,
				configFile);
		viewer.setInput(model);
		LayoutAlgorithm layout = setLayout();
		viewer.setLayoutAlgorithm(layout, true);
		viewer.applyLayout();
		return viewer;
	}

	private Composite getFillLayoutContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.VERTICAL));
		return container;
	}

	private void disposeChildren(Composite c) {
		for (Control child : c.getChildren()) {
			child.dispose();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}