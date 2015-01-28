package br.com.rise.featurejs.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;

import br.com.rise.featurejs.ui.FeatureJSUIPlugin;
import br.com.rise.featurejs.ui.actions.ScatteringTreeUpdateAction;
import br.com.rise.featurejs.ui.handlers.ShowInScatteringTreeViewHandler;
import br.com.rise.featurejs.ui.model.TreeObject;
import br.com.rise.featurejs.ui.model.TreeParent;
import br.com.rise.featurejs.ui.views.components.ScatteringTreeFilter;
import br.com.rise.featurejs.ui.views.components.ScatteringTreeManager;
import br.com.rise.featurejs.ui.views.components.ScatteringTreeViewContentProvider;
import br.com.rise.featurejs.ui.views.components.ScatteringTreeViewLabelProvider;

/**
 * This class show the scattering of a given project.
 * <p>
 */

public class ScatteringTreeView extends ViewPart implements IShowInTarget,
		PropertyChangeListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.com.rise.featurejs.ui.views.ScatteringTreeView";

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action updateTreeAction;
	private Action action2;
	private ScatteringTreeFilter filter;

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public ScatteringTreeView() {
		ScatteringTreeManager.getInstance().addChangeListener(this);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		this.filter = new ScatteringTreeFilter();
		viewer.addFilter(this.filter);
		viewer.setContentProvider(new ScatteringTreeViewContentProvider(this.filter));
		viewer.setLabelProvider(new ScatteringTreeViewLabelProvider());
		viewer.setSorter(new NameSorter());

		getSite().setSelectionProvider(viewer);

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

		action2 = makeHideModularFeatureNodesAction();
		
	}

	private Action makeHideModularFeatureNodesAction() {
		String description = "Show Modular Features";
		
		Action action = new Action(description,
				IAction.AS_CHECK_BOX) {

			public void run() {
				boolean oldState = ScatteringTreeView.this.filter
						.isShowingAll();
				ScatteringTreeView.this.filter.toggleExhibition();
				TreeViewer viewer = ScatteringTreeView.this.getTreeViewer();
				if (!oldState) {
					ScatteringTreeView.this.revealNodes();
				}
				viewer.refresh();
			}
		};
		
		action.setText(description);
		action.setToolTipText(description);
		action.setImageDescriptor(FeatureJSUIPlugin.getImageDescriptor("filter.png"));
		
		return action;
	}

	private void revealNodes() {
		for (TreeObject node : ScatteringTreeManager.getInstance()
				.getActiveObject().getChildren()) {
			getTreeViewer().reveal(node);
		}
	}

	private TreeViewer getTreeViewer() {
		return this.viewer;
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IHandlerService handlerService = (IHandlerService) getSite()
						.getService(IHandlerService.class);
				try {
					handlerService
							.executeCommand(
									"br.com.rise.featurejs.ui.command.OpenEditor",
									null);
				} catch (ExecutionException | NotDefinedException
						| NotEnabledException | NotHandledException e) {
					e.printStackTrace();
				}
			}
		});
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		TreeParent newInput = null;
		if (evt.getNewValue() instanceof TreeParent) {
			newInput = (TreeParent) evt.getNewValue();
		}
		this.viewer.setInput(newInput);
	}

}