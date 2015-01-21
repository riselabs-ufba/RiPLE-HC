package br.com.rise.featurejs.ui.views;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;

import br.com.rise.featurejs.ui.actions.ScatteringTreeUpdateAction;
import br.com.rise.featurejs.ui.handlers.ShowInScatteringTreeViewHandler;
import br.com.rise.featurejs.ui.model.TreeObject;
import br.com.rise.featurejs.ui.model.TreeParent;
import br.com.rise.featurejs.ui.model.enums.TreeNodeType;
import br.com.rise.featurejs.ui.views.components.ScatteringTreeViewContentProvider;
import br.com.rise.featurejs.ui.views.components.ScatteringTreeViewLabelProvider;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class ScatteringTreeView extends ViewPart implements IShowInTarget {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.com.rise.featurejs.ui.views.ScatteringTreeView";

	private static TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action updateTreeAction;
	private Action action2;
	private Action doubleClickAction;

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public ScatteringTreeView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ScatteringTreeViewContentProvider(this));
		viewer.setLabelProvider(new ScatteringTreeViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(initialize());

		// Create the help context id for the viewer's control
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(viewer.getControl(), "br.com.rise.featurejs.ui.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ScatteringTreeView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(updateTreeAction);
		manager.add(new Separator());
		manager.add(action2);

		MenuManager subMenu = new MenuManager("Update with", null);
		subMenu.add(updateTreeAction);
		subMenu.add(action2);

		manager.add(subMenu);

	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(updateTreeAction);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(updateTreeAction);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		updateTreeAction = new ScatteringTreeUpdateAction();

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * Sets a dummy model to initialize tree heararchy. Afterwards, it should be
	 * updated.
	 */
	public TreeParent initialize() {
		TreeObject to1 = new TreeObject("Leaf 1");
		TreeObject to2 = new TreeObject("Leaf 2");
		TreeObject to3 = new TreeObject("Leaf 3");
		TreeParent p1 = new TreeParent("Parent 1");
		p1.setType(TreeNodeType.MODULE);
		p1.addChild(to1);
		p1.addChild(to2);
		p1.addChild(to3);

		TreeObject to4 = new TreeObject("Leaf 4");
		TreeParent p2 = new TreeParent("Parent 2");
		p2.setType(TreeNodeType.MODULE);
		p2.addChild(to4);

		TreeParent root = new TreeParent("Root");
		root.setType(TreeNodeType.FEATURE);
		root.addChild(p1);
		root.addChild(p2);

		TreeObject too1 = new TreeObject("Leaf 1");
		TreeObject too2 = new TreeObject("Leaf 2");
		TreeObject too3 = new TreeObject("Leaf 3");
		TreeParent po1 = new TreeParent("Parent 1");
		po1.setType(TreeNodeType.MODULE);
		po1.addChild(too1);
		po1.addChild(too2);
		po1.addChild(too3);

		TreeObject too4 = new TreeObject("Leaf 4");
		TreeParent po2 = new TreeParent("Parent 2");
		po2.setType(TreeNodeType.MODULE);
		po2.addChild(too4);

		TreeParent rooot = new TreeParent("Root");
		rooot.setType(TreeNodeType.FEATURE);
		rooot.addChild(po1);
		rooot.addChild(po2);

		TreeParent invisibleRoot = new TreeParent("");
		invisibleRoot.addChild(root);
		invisibleRoot.addChild(rooot);
		return invisibleRoot;
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Scattering Tree", message);
	}

	public static void setInput(TreeParent rootNode) {
		viewer.setInput(rootNode);
		viewer.refresh();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public boolean show(ShowInContext context) {
		if (viewer == null || context == null)
			return false;
		ISelection sel = context.getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) sel;
			Object first = ss.getFirstElement();
			if (first instanceof IProject) {
				TreeParent root = ShowInScatteringTreeViewHandler
						.createScatteringTree((IProject) first);
				viewer.setInput(root);
				return true;
			}
		}
		return false;
	}

}