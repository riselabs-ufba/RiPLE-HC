/**
 * 
 */
package br.com.rise.featurejs.ui.editors;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * @author "Alcemir Santos"
 *
 */
public class MyTextEditor extends TextEditor{
	public ISourceViewer getViewer(){
		return getSourceViewer();
	}
}
