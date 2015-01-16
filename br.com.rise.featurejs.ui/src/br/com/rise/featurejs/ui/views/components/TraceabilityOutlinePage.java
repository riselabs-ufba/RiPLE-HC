/**
 * 
 */
package br.com.rise.featurejs.ui.views.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import br.com.rise.featurejs.ui.FeatureJSUIPlugin;
import br.com.rise.featurejs.ui.actions.TraceabilityOutlineUpdateAction;
import br.com.rise.featurejs.ui.editors.MyTextEditor;

/**
 * @author "Alcemir Santos"
 * 
 */
public class TraceabilityOutlinePage extends ContentOutlinePage {

	private static final String ACTION_UPDATE = "update";
	private static final String ACTION_COLLAPSE = "collapse";
	private static final String ACTION_EXPAND = "expand";

	private static final String ACTION_HIDE_MACROS = "hideMacros";

	private MacrosOutlineInput input;
	private TraceabilityOutlineFilter filter;
	private Map<String, IAction> outlineActions;
	private int expandLevel;
	private MyTextEditor editor;

	public TraceabilityOutlinePage(MyTextEditor editor) {
		this.editor = editor;

		this.expandLevel = 1;
		this.outlineActions = new HashMap<String, IAction>();

		// TODO set the preference page for the traceability view afterwards...
		//
		// FeatureJSUIPlugin.getDefault().getPreferenceStore()
		// .addPropertyChangeListener(new IPropertyChangeListener() {
		// public void propertyChange(PropertyChangeEvent event) {
		// String property = event.getProperty();
		// if (("outlinePart".equals(property))
		// || ("outlineChapter".equals(property))
		// || ("outlineSection".equals(property))
		// || ("outlineSubSection".equals(property))
		// || ("outlineSubSubSection".equals(property))
		// || ("outlineParagraph".equals(property))
		// || ("outlineEnvs".equals(property))) {
		// TraceabilityOutlinePage.this
		// .getOutlinePreferences();
		// TraceabilityOutlinePage.this.resetToolbarButtons();
		// TreeViewer viewer = TraceabilityOutlinePage.this
		// .getTreeViewer();
		// if (viewer != null) {
		// Control control = viewer.getControl();
		// if ((control != null)
		// && (!control.isDisposed()))
		// viewer.refresh();
		// }
		// }
		// }
		// });
	}

	public void createPartControl(Composite parent) {
		super.createControl(parent);

		createActions();

		TreeViewer viewer = getTreeViewer();
		this.filter = new TraceabilityOutlineFilter();
		viewer.setContentProvider(new TraceabilityContentProvider(this.filter));
		viewer.setLabelProvider(new TraceabilityLabelProvider());
		viewer.setComparer(new TraceabilityOutlineNodeComparer());

		// getOutlinePreferences();
		viewer.addFilter(this.filter);

		viewer.addSelectionChangedListener(this);

		createToolbar();
		resetToolbarButtons();
		createContextMenu();

		if (this.input != null) {
			viewer.setInput(this.input.getRootNodes());

			updateContextMenuActions(true);
		}
	}

	/**
	 * Used to switch the status of the context menu actions. Use the param
	 * status equal to <code>true</code> only when the model is updated. Use
	 * <code>false</code> otherwise.
	 * 
	 * @param status
	 */
	private void updateContextMenuActions(boolean status) {
		// I believe such actions won't be necessary...

		((IAction) this.outlineActions.get("update")).setEnabled(!status);
//		((IAction) this.outlineActions.get("copy")).setEnabled(status);
//		((IAction) this.outlineActions.get("cut")).setEnabled(status);
//		((IAction) this.outlineActions.get("paste")).setEnabled(status);
//		((IAction) this.outlineActions.get("delete")).setEnabled(status);
	}

	public void setFocus() {
		getTreeViewer().getTree().setFocus();
	}

	private void resetToolbarButtons() {
		((IAction) this.outlineActions.get(ACTION_HIDE_MACROS)).setChecked(!this.filter
				.isTypeVisible(OutlineNode.TYPE_MACRO));
	}

	private void getOutlinePreferences() {
		this.filter.reset();

		boolean modules = FeatureJSUIPlugin.getDefault().getPreferenceStore()
				.getBoolean("outlineModules");
		boolean macros = FeatureJSUIPlugin.getDefault().getPreferenceStore()
				.getBoolean("outlineMacros");

		if (macros) {
			this.filter.toggleType(2, true);
		}

		if (modules) {
			this.filter.toggleType(1, true);
		}
	}

	private void fillContextMenu(IMenuManager mgr) {
//		mgr.add((IAction) this.outlineActions.get("copy"));
//		mgr.add((IAction) this.outlineActions.get("cut"));
//		mgr.add((IAction) this.outlineActions.get("paste"));
//		mgr.add(new Separator());
//		mgr.add((IAction) this.outlineActions.get("delete"));
	}

	public TextEditor getEditor() {
		return this.editor;
	}

	public void setEditor(MyTextEditor editor) {
		this.editor = editor;
	}

	private void createContextMenu() {
		  MenuManager menuMgr = new MenuManager();
		    menuMgr.setRemoveAllWhenShown(true);
		    menuMgr.addMenuListener(new IMenuListener() {
		      public void menuAboutToShow(IMenuManager mgr) {
		        TraceabilityOutlinePage.this.fillContextMenu(mgr);
		      }
		    });
		    Menu menu = menuMgr.createContextMenu(getTreeViewer().getControl());
		    getTreeViewer().getControl().setMenu(menu);
	}


	private void createToolbar() {
		 IToolBarManager toolbarManager = getSite().getActionBars().getToolBarManager();
		    toolbarManager.add((IAction)this.outlineActions.get(ACTION_COLLAPSE));
		    toolbarManager.add((IAction)this.outlineActions.get(ACTION_EXPAND));
		    toolbarManager.add((IAction)this.outlineActions.get(ACTION_UPDATE));
		    toolbarManager.add((IAction)this.outlineActions.get(ACTION_HIDE_MACROS));
	}

	public void switchTreeViewerSelectionChangeListener(
			ISelectionChangedListener listener) {
		getTreeViewer().removeSelectionChangedListener(this);
		getTreeViewer().addSelectionChangedListener(listener);
	}

	public void update(MacrosOutlineInput input) {
		this.input = input;

		TreeViewer viewer = getTreeViewer();
		if (viewer != null) {
			Control control = viewer.getControl();
			if ((control != null) && (!control.isDisposed())) {
				control.setRedraw(false);

				viewer.getTree().deselectAll();

				Object[] expandedElements = viewer.getExpandedElements();

				viewer.setInput(input.getRootNodes());

				viewer.setExpandedElements(expandedElements);

				control.setRedraw(true);

				updateContextMenuActions(true);
			}
		}

	}

	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);

		ISelection selection = event.getSelection();
		if (selection.isEmpty()) {
			this.editor.resetHighlightRange();
		} else {
			OutlineNode node = (OutlineNode) ((IStructuredSelection) selection)
					.getFirstElement();
			Position position = node.getPosition();
			if (position != null)
				try {
					this.editor.setHighlightRange(position.getOffset(),
							position.getLength(), true);
					this.editor.getViewer().revealRange(position.getOffset(),
							position.getLength());
				} catch (IllegalArgumentException localIllegalArgumentException) {
					this.editor.resetHighlightRange();
				}
			else
				this.editor.resetHighlightRange();
		}
	}

	private IAction createHideAction(String desc, final int nodeType,
			ImageDescriptor img) {
		IAction action = new Action(desc, 2) {
			public void run() {
				boolean oldState = TraceabilityOutlinePage.this.filter
						.isTypeVisible(nodeType);
				TraceabilityOutlinePage.this.filter.toggleType(nodeType,
						!oldState);
				TreeViewer viewer = TraceabilityOutlinePage.this
						.getTreeViewer();
				if (!oldState) {
					TraceabilityOutlinePage.this.revealNodes(nodeType);
				}
				viewer.refresh();
			}
		};
		action.setToolTipText(desc);
		action.setImageDescriptor(img);
		return action;
	}

	private void createActions() {
		// TexOutlineActionCut cut = new TexOutlineActionCut(this);
		// this.outlineActions.put("cut", cut);
		//
		// TexOutlineActionCopy copy = new TexOutlineActionCopy(this);
		// this.outlineActions.put("copy", copy);
		//
		// TexOutlineActionPaste paste = new TexOutlineActionPaste(this);
		// this.outlineActions.put("paste", paste);
		//
		// TexOutlineActionDelete delete = new TexOutlineActionDelete(this);
		// this.outlineActions.put("delete", delete);

		TraceabilityOutlineUpdateAction update = new TraceabilityOutlineUpdateAction(
				this);
		this.outlineActions.put(ACTION_UPDATE, update);

		Action collapse = new Action("Collapse one level", 1) {
			public void run() {
				if (TraceabilityOutlinePage.this.expandLevel > 1) {
					TraceabilityOutlinePage.this.expandLevel -= 1;
					TraceabilityOutlinePage.this.getTreeViewer().collapseAll();
					TraceabilityOutlinePage.this.getTreeViewer().expandToLevel(
							TraceabilityOutlinePage.this.expandLevel);
				}
			}
		};
		collapse.setToolTipText("Collapse one level");
		collapse.setImageDescriptor(FeatureJSUIPlugin
				.getImageDescriptor("collapse"));
		this.outlineActions.put(ACTION_COLLAPSE, collapse);

		Action expand = new Action("Expand one level", 1) {
			public void run() {
				if (TraceabilityOutlinePage.this.expandLevel < TraceabilityOutlinePage.this.input
						.getTreeDepth()) {
					TraceabilityOutlinePage.this.expandLevel += 1;
				}
				TraceabilityOutlinePage.this.getTreeViewer().collapseAll();
				TraceabilityOutlinePage.this.getTreeViewer().expandToLevel(
						TraceabilityOutlinePage.this.expandLevel);
			}
		};
		expand.setToolTipText("Expand one level");
		expand.setImageDescriptor(FeatureJSUIPlugin
				.getImageDescriptor("expand"));
		this.outlineActions.put(ACTION_EXPAND, expand);

		IAction action = createHideAction("Hide macros", OutlineNode.TYPE_MACRO,
				FeatureJSUIPlugin.getImageDescriptor("hide_macros"));
		this.outlineActions.put(ACTION_HIDE_MACROS, action);

	}

	public boolean isModelDirty() {
		// TODO take a time to think its needs...
		return false;
	}

	public void reset() {
		this.expandLevel=1;
	}

	public void modelGotDirty() {
		updateContextMenuActions(false);
	}

	private void revealNodes(int nodeType) {
		 List<OutlineNode> nodeList = this.input.getTypeList(nodeType);
		    if (nodeList != null)
		      for (OutlineNode node : nodeList)
		        getTreeViewer().reveal(node);
	}
}
