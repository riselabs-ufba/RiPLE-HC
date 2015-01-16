package br.com.rise.featurejs.ui.views;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import br.com.rise.featurejs.ui.FeatureJSUIPlugin;
import br.com.rise.featurejs.ui.editors.MyTextEditor;
import br.com.rise.featurejs.ui.views.components.MacrosOutlineInput;
import br.com.rise.featurejs.ui.views.components.OutlineNode;
import br.com.rise.featurejs.ui.views.components.TraceabilityOutlinePage;

/**
 * This class show the traceability tree of the entire project. Such tree
 * contains the features of the SPL as roots and each file that have
 * <i>conditional compilation</i> macros inside as children.
 * 
 * @author Alcemir Santos
 */
public class TraceabilityOutlineTreeView extends ViewPart implements
		ISelectionChangedListener, IPartListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.com.rise.featurejs.ui.views.TraceabilityTreeView";

	private static FeatureJSUIPlugin plugin;

	private TraceabilityOutlinePage outline;

	/**
	 * The constructor.
	 */
	public TraceabilityOutlineTreeView() {
		this.outline = new TraceabilityOutlinePage(null);
		// TODO create an JSeditor to improve the results of the view.
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		PageSite site = new PageSite(getViewSite());
		this.outline.init(site);
		this.outline.createControl(parent);
		((SubActionBars) site.getActionBars()).activate(true);
		this.outline.switchTreeViewerSelectionChangeListener(this);

		getSite().getPage().addPartListener(this);

	}

	/**
	 * Updates the view.
	 * 
	 * @param input
	 */
	public void update(MacrosOutlineInput input) {
		this.outline.update(input);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		this.outline.setFocus();
	}

	public void projectChanged(){
		this.outline.reset();
	}
	
	public boolean isModelDirty(){
		return this.outline.isModelDirty();
	}
	
	public void modelGotDirty(){
		this.outline.modelGotDirty();
	}
	
	public AbstractTextEditor getEditor(){
		return this.outline.getEditor();
	}
	
	private void setEditor(MyTextEditor e) {
		this.outline.setEditor(e);
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		// test if is EditorPart and register this view
		if ((part instanceof AbstractTextEditor)) {
			if (this.outline.getEditor() != null) {
				// TODO??? this.outline.getEditor().unregisterFullOutline(this);
			}
			MyTextEditor e = (MyTextEditor) part;
			// TODO??? e.registerFullOutline(this);
			setEditor(e);
		}
	}
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		partActivated(part);
	}

	@Override
	public void partClosed(IWorkbenchPart part) {}

	@Override
	public void partDeactivated(IWorkbenchPart part) {}

	@Override
	public void partOpened(IWorkbenchPart part) {}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO what to do in the "Traceability Outline" when the selection
		// change? Open the editor??.

		ISelection selection = event.getSelection();
		if (selection.isEmpty()) {
			this.outline.getEditor().resetHighlightRange();
		} else {
			OutlineNode node = (OutlineNode) ((IStructuredSelection) selection)
					.getFirstElement();
			this.outline.getEditor().resetHighlightRange();
			if (node.getIFile() != null) {
				FileEditorInput input = new FileEditorInput(node.getIFile());
				try {
					IWorkbenchPage cPage = FeatureJSUIPlugin
							.getCurrentWorkbenchPage();
					TextEditor e = (TextEditor) cPage.findEditor(input);
					if (e == null)
						e = (TextEditor) cPage.openEditor(input,
								"net.sourceforge.texlipse.TexEditor");
					if (cPage.getActiveEditor() != e)
						cPage.activate(e);
					IDocument doc = e.getDocumentProvider().getDocument(
							e.getEditorInput());
					int beginOffset = doc
							.getLineOffset(node.getBeginLine() - 1);
					int length;
					if (node.getEndLine() - 1 == doc.getNumberOfLines()) {
						length = doc.getLength() - beginOffset;
					} else
						length = doc.getLineOffset(node.getEndLine() - 1)
								- beginOffset;
					e.setHighlightRange(beginOffset, length, true);
				} catch (PartInitException e) {
					FeatureJSUIPlugin.log("Can't open editor.", e);
				} catch (BadLocationException localBadLocationException) {
					this.outline.getEditor().resetHighlightRange();
				}
			}
		}
	}

	
}