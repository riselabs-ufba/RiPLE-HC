package br.com.riselabs.vparser.parsers;

import java.util.List;

import org.eclipse.core.resources.IFile;

import br.com.riselabs.vparser.beans.CCVariationPoint;
import br.com.riselabs.vparser.exceptions.PluginException;

public interface ISourceCodeParser extends IParser{
	
	public List<CCVariationPoint> parse(IFile file) throws PluginException;
}
