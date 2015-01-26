/**
 * 
 */
package br.com.riselabs.vparser.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import br.com.riselabs.vparser.beans.CCVariationPoint;
import br.com.riselabs.vparser.exceptions.PluginException;
import br.com.riselabs.vparser.lexer.Lexer;
import br.com.riselabs.vparser.lexer.beans.Token;

/**
 * @author "Alcemir Santos"
 *
 */
public class JavaScriptParser extends AbstractParser implements
		ISourceCodeParser {

	/**
	 * 
	 */
	public JavaScriptParser() {	}

	/* (non-Javadoc)
	 * @see br.com.riselabs.vparser.parsers.ISourceCodeParser#parse(org.eclipse.core.resources.IFile)
	 */
	@Override
	public List<CCVariationPoint> parse(IFile file) throws PluginException {

		Map<Integer, String> inputLines;
		try {
			inputLines = getLinesMappingFrom(file.getContents());
		} catch (CoreException e) {
			throw new PluginException("This method fails. Reasons include: \n"
					+ "- This resource does not exist.\n"
					+ "- This resource is not local. \n"
					+ "- The file-system resource is not a file. \n"
					+ "- The workspace is not in sync with the corresponding "
					+ "location in the local file system.");
		}

		Map<Integer,List<Token>> tokensPerLine = getTokensPerLine(inputLines);
		
		List<CCVariationPoint> list = new ArrayList<>();
		for (Entry<Integer, List<Token>> e: tokensPerLine.entrySet()){
			list.add(new CCVariationPoint(file.getLocation(), e.getKey(), e.getValue()));
		}
		return list;
	}

	private Map<Integer, List<Token>> getTokensPerLine(Map<Integer, String> inputLines) {
		Map<Integer, List<Token>> result = new HashMap<Integer, List<Token>>();
		for (int i = 1; i <= inputLines.size(); i++) {
			List<Token> tokens = Lexer.tokenize(inputLines.get(i), Lexer.FileType.JAVASCRIPT);
			if (tokens.isEmpty() || tokens == null) {
				continue;
			}else{
				result.put(i, tokens);
			}
		}
		return result;
	}

}
