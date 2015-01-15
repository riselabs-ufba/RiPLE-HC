package br.com.reconcavo.featurejs.file;

import br.com.reconcavo.featurejs.featuretreat.PreProcessorDirectives;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class FileInterpreter {
	public static final String BACKSLASH = "\\";
	private PreProcessorDirectives directive;
	private String featureName;
	private String originalCode;
	private String innerCode;
	private FileInterpreter parentFileInterpreter;
	private FileInterpreter childFileInterpreter;

	public FileInterpreter() {
		this.directive = null;
		this.parentFileInterpreter = null;
		this.childFileInterpreter = null;
		this.innerCode = "\n";
		this.originalCode = "";
	}

	public final PreProcessorDirectives getDirective() {
		return this.directive;
	}

	public final void setDirective(PreProcessorDirectives directive) {
		this.directive = directive;
	}

	public final String getFeatureName() {
		return this.featureName;
	}

	public final void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public final String getOriginalCode() {
		return this.originalCode;
	}

	public final void setOriginalCode(String originalCode) {
		this.originalCode = originalCode;
	}

	public final String getInnerCode() {
		return this.innerCode;
	}

	public final void setInnerCode(String innerCode) {
		this.innerCode = innerCode;
	}

	public final FileInterpreter getParentFileInterpreter() {
		return this.parentFileInterpreter;
	}

	public final void setParentFileInterpreter(
			FileInterpreter parentFileInterpreter) {
		this.parentFileInterpreter = parentFileInterpreter;
	}

	public final FileInterpreter getChildFileInterpreter() {
		return this.childFileInterpreter;
	}

	public final void setChildFileInterpreter(
			FileInterpreter childFileInterpreter) {
		this.childFileInterpreter = childFileInterpreter;
	}

	public final void addOriginalCode(String codePart) {
		String treatedCode = "";
		try {
			Scanner textScan = new Scanner(codePart);
			while (textScan.hasNextLine()) {
				String word = textScan.next();
				word = replaceAllSpecialChars(word);
				treatedCode = treatedCode + "[\\s]*" + word;
			}
			textScan.close();
		} catch (NoSuchElementException localNoSuchElementException) {
		} finally {
			addUnModifiableOriginalCode(treatedCode);
		}
	}

	private final String replaceAllSpecialChars(String word) {
		String newWord = word;
		try {
			newWord = replaceSpecialChar(newWord, "\\");
			newWord = replaceSpecialChar(newWord, "(");
			newWord = replaceSpecialChar(newWord, ")");
			newWord = replaceSpecialChar(newWord, "*");
			newWord = replaceSpecialChar(newWord, ".");
			newWord = replaceSpecialChar(newWord, "?");
			newWord = replaceSpecialChar(newWord, "^");
			newWord = replaceSpecialChar(newWord, "$");
			newWord = replaceSpecialChar(newWord, "[");
			newWord = replaceSpecialChar(newWord, "]");
			newWord = replaceSpecialChar(newWord, "|");
			newWord = replaceSpecialChar(newWord, "{");
			newWord = replaceSpecialChar(newWord, "}");
			newWord = replaceSpecialChar(newWord, "/");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newWord;
	}

	private String replaceSpecialChar(String word, String character) {
		String newWord = word;
		String changeFragment = "\\" + character;
		newWord = newWord.replaceAll(changeFragment, "\\" + changeFragment);
		return newWord;
	}

	private final void addUnModifiableOriginalCode(String codePart) {
		this.originalCode += codePart;

		if (this.parentFileInterpreter != null)
			this.parentFileInterpreter.addUnModifiableOriginalCode(codePart);
	}

	public final void addInnerCode(String codePart) {
		this.innerCode += codePart;
	}

	@Override
	public String toString() {
		String toString = super.toString();
		toString = toString + " => FileInterpreter [directive: "
				+ this.directive;
		toString = toString + " | featureName: " + this.featureName;
		toString = toString + " | originalCode: " + this.originalCode;
		toString = toString + " | innerCode: " + this.innerCode;
		toString = toString + " | [parentFileInterpreter: "
				+ this.parentFileInterpreter + "]";
		toString = toString + " | [childFileInterpreter: "
				+ this.childFileInterpreter + "]";
		toString = toString + "]";

		return toString;
	}
}

/*
 * Location: /Users/alcemir/Documents/workspace/br.com.reconcavo.featurejs/
 * Qualified Name: br.com.reconcavo.featurejs.file.FileInterpreter JD-Core
 * Version: 0.6.2
 */