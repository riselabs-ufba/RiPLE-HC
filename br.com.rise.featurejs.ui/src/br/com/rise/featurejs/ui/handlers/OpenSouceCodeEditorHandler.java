/**
 * 
 */
package br.com.rise.featurejs.ui.handlers;

import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

import br.com.rise.featurejs.ui.model.TreeObject;
import br.com.rise.featurejs.ui.views.ScatteringTreeView;

/**
 * @author "Alcemir Santos"
 * 
 */
public class OpenSouceCodeEditorHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// get the view
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		ScatteringTreeView view = (ScatteringTreeView) page
				.findView(ScatteringTreeView.ID);
		// get the selection
		ISelection selection = view.getSite().getSelectionProvider()
				.getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			TreeObject obj = (TreeObject) ((IStructuredSelection) selection)
					.getFirstElement();
			IFile file = obj.getFile();

			@SuppressWarnings("rawtypes")
			HashMap map = new HashMap();
			map.put(IMarker.LINE_NUMBER, new Integer(obj.getLineMarker()));
			
			// a mark to open the file in a given position
			IMarker marker;
			
			// if we had a selection lets open the editor
			if (file != null) {
				try {
					marker = file.createMarker(IMarker.TEXT);
					marker.setAttributes(map);
					
					IDE.openEditor(page, marker);
					
					marker.delete();
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
