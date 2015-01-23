package br.com.riselabs.vparser.parsers;

import java.util.List;

import org.eclipse.core.resources.IFile;

import br.com.riselabs.vparser.beans.FCLConstraint;
import br.com.riselabs.vparser.exceptions.PluginException;

public interface IConstraintsSourceParser extends IParser{
	public List<FCLConstraint> parse(IFile file) throws PluginException;
}
