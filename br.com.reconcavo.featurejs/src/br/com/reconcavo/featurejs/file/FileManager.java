package br.com.reconcavo.featurejs.file;

import br.com.reconcavo.featurejs.featuretreat.FeatureTreatment;
import br.com.reconcavo.featurejs.featuretreat.FeatureTreatmentException;
import br.com.reconcavo.featurejs.featuretreat.PreProcessorDirectives;
import br.com.reconcavo.featurejs.metrics.FeatureMetrics;
import br.com.reconcavo.featurejs.metrics.FileFeature;
import de.ovgu.featureide.fm.core.configuration.SelectableFeature;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

public final class FileManager {
	private final String encoding = "UTF-8";
	private static FileManager INSTANCE = null;
	private File currentTreatingFile;
	private String configName;
	private FileFeature fileFeature;
	private Set<String> featuresToMaintain;
	private FeatureMetrics featureMetrics;

	private FileManager() {
		cleanMetrics();
	}

	private void cleanMetrics() {
		this.featureMetrics = new FeatureMetrics();
	}

	public static final FileManager configure(String configName,
			FileFeature fileFeature, List<SelectableFeature> featuresToRemove,
			Set<String> featuresToMaintain) {
		if (INSTANCE == null) {
			INSTANCE = new FileManager();
		}
		INSTANCE.configName = configName;
		INSTANCE.fileFeature = fileFeature;
		INSTANCE.featuresToMaintain = featuresToMaintain;

		return INSTANCE;
	}

	public void manageFile() throws FeatureTreatmentException {
		if (this.fileFeature == null) {
			throw new FeatureTreatmentException(
					"The file mustn't be null, otherwise it will be impossible to manager features' tags.");
		}

		if (this.featuresToMaintain == null) {
			this.featuresToMaintain = Collections.emptySet();
		}

		runFeatureTreatement(this.fileFeature, this.featuresToMaintain);
	}

	private void runFeatureTreatement(FileFeature fileFeature,
			Set<String> featuresToMaintain) {
		try {
			Set<File> files = fileFeature.getFilesOccurrencies().keySet();
			for (File treatingFile : files) {
				this.currentTreatingFile = treatingFile;
				String content = getContent(treatingFile);

				content = mineFileInterpreters(content, featuresToMaintain);

				writeInFile(treatingFile, content);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FeatureTreatmentException e) {
			e.printStackTrace();
		}
	}

	private String getContent(File file) throws IOException {
		FileInputStream fileIS = new FileInputStream(file);
		String content = IOUtils.toString(fileIS, "UTF-8");
		fileIS.close();
		return content;
	}

	private void writeInFile(File file, String content) throws IOException {
		FileOutputStream fileOS = new FileOutputStream(file);
		IOUtils.write(content, fileOS, "UTF-8");
		fileOS.close();
	}

	public String mineFileInterpreters(String content,
			Set<String> featuresToMaintain) throws FeatureTreatmentException {
		String newContent = content;
		Boolean hasDirectiveOccurency = Boolean.FALSE;
		Scanner textScan = new Scanner(content);
		FileInterpreter fileInterpreter = null;
		PreProcessorDirectives directivePattern = PreProcessorDirectives.NONE;
		while (textScan.hasNextLine()) {
			String line = textScan.nextLine();
			Matcher ifdefMatcher = ifdefMatcher(line);

			if (ifdefMatcher.find()) {
				fileInterpreter = matchIfdefOrIfndef(ifdefMatcher,
						fileInterpreter);
				directivePattern = fileInterpreter.getDirective();
			} else {
				Matcher ifndefMatcher = ifndefMatcher(line);

				if (ifndefMatcher.find()) {
					fileInterpreter = matchIfdefOrIfndef(ifndefMatcher,
							fileInterpreter);
					directivePattern = fileInterpreter.getDirective();
				} else {
					Matcher endifMatcher = endifMatcher(line);
					if (endifMatcher.find()) {
						directivePattern = PreProcessorDirectives.ENDIF;
					} else {
						Matcher elifMatcher = elifMatcher(line);

						if (elifMatcher.find()) {
							fileInterpreter = matchElif(elifMatcher,
									fileInterpreter);
							directivePattern = fileInterpreter.getDirective();
						}
					}
				}
			}

			FileInterpreter previousFileInterpreter = fileInterpreter;
			fileInterpreter = addLineToInterpreter(directivePattern,
					fileInterpreter, line);

			if ((!hasDirectiveOccurency.booleanValue())
					&& (fileInterpreter != null)) {
				hasDirectiveOccurency = Boolean.TRUE;
			} else if ((hasDirectiveOccurency.booleanValue())
					&& (fileInterpreter == null)) {
				hasDirectiveOccurency = Boolean.FALSE;
				newContent = removeInterpreterFromContent(newContent,
						previousFileInterpreter, featuresToMaintain);
			}

			directivePattern = PreProcessorDirectives.NONE;
		}
		textScan.close();
		return newContent;
	}

	private FileInterpreter addLineToInterpreter(
			PreProcessorDirectives directivePattern,
			FileInterpreter fileInterpreter, String line) {
		if (fileInterpreter != null) {
			fileInterpreter.addOriginalCode(line);
			if (directivePattern == PreProcessorDirectives.NONE)
				fileInterpreter.addInnerCode("\n" + line);
			else if (directivePattern == PreProcessorDirectives.ENDIF) {
				fileInterpreter = null;
			}
		}
		return fileInterpreter;
	}

	private String removeInterpreterFromContent(String content,
			FileInterpreter interpreter, Set<String> featuresToMaintain) {
		this.featureMetrics.addOneQtdDirectiveInCode();
		String newContent = content;
		FileInterpreter iterativeInterpreter = getRootFileInterpreter(interpreter);
		PreProcessorDirectives masterDirective = iterativeInterpreter
				.getDirective();
		String replacedContent = replaceInterpreter(newContent,
				iterativeInterpreter, featuresToMaintain, masterDirective);
		if (!replacedContent.isEmpty()) {
			this.featureMetrics.addOneQtdDirectiveApplied();
			newContent = replacedContent;
		} else {
			newContent = newContent.replaceAll(interpreter.getOriginalCode(),
					"");
		}

		return newContent;
	}

	private FileInterpreter matchIfdefOrIfndef(Matcher ifdefofIfndefMatcher,
			FileInterpreter fileInterpreter) throws FeatureTreatmentException {
		try {
			if (fileInterpreter == null) {
				FileInterpreter ifdefOrIfndefFileInterpreter = new FileInterpreter();

				String directiveName = ifdefofIfndefMatcher.group(1);
				PreProcessorDirectives directive = PreProcessorDirectives
						.getDirective(directiveName);

				String featureName = ifdefofIfndefMatcher.group(2);

				ifdefOrIfndefFileInterpreter.setFeatureName(featureName);
				ifdefOrIfndefFileInterpreter.setDirective(directive);

				return ifdefOrIfndefFileInterpreter;
			}
			String message = "The directive ";
			message = message
					+ fileInterpreter.getDirective().getDirectiveName();
			message = message + " isn't allowed the #ifdef directive.";
			throw new FeatureTreatmentException(message);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	private FileInterpreter matchElif(Matcher elifMatcher,
			FileInterpreter fileInterpreter) throws FeatureTreatmentException {
		if (fileInterpreter != null) {
			PreProcessorDirectives precedingDirective = fileInterpreter
					.getDirective();
			if ((precedingDirective != PreProcessorDirectives.IFDEF)
					&& (precedingDirective != PreProcessorDirectives.IFNDEF)
					&& (precedingDirective != PreProcessorDirectives.ELIF)) {
				String message = "The directive #elif must be preceded by a #ifdef, #ifndef or #elif.";
				throw new FeatureTreatmentException(message);
			}

			FileInterpreter parentFileInterpreter = fileInterpreter;
			FileInterpreter elifFileInterpreter = new FileInterpreter();

			elifFileInterpreter.setOriginalCode(parentFileInterpreter
					.getOriginalCode());
			elifFileInterpreter.setParentFileInterpreter(parentFileInterpreter);
			parentFileInterpreter.setChildFileInterpreter(elifFileInterpreter);

			String directiveName = elifMatcher.group(1);
			PreProcessorDirectives directive = PreProcessorDirectives
					.getDirective(directiveName);
			String featureName = elifMatcher.group(2);

			elifFileInterpreter.setFeatureName(featureName);
			elifFileInterpreter.setDirective(directive);

			return elifFileInterpreter;
		}
		String message = "There's no directive preceding the directive #elif. It must be preceded by a directive.";
		throw new FeatureTreatmentException(message);
	}

	private Matcher ifdefMatcher(String line) {
		String ifdefPattern = FeatureTreatment.generateGenericPatternIfdef();
		Matcher ifdefMatcher = getMatcher(line, ifdefPattern);
		return ifdefMatcher;
	}

	private Matcher ifndefMatcher(String line) {
		String ifndefPattern = FeatureTreatment.generateGenericPatternIfndef();
		Matcher ifndefMatcher = getMatcher(line, ifndefPattern);
		return ifndefMatcher;
	}

	private Matcher elifMatcher(String line) {
		String elifPattern = FeatureTreatment.generateGenericPatternElif();
		Matcher elifMatcher = getMatcher(line, elifPattern);
		return elifMatcher;
	}

	private Matcher endifMatcher(String line) {
		String endifPattern = FeatureTreatment.generateGenericPatternEndif();
		Matcher endifMatcher = getMatcher(line, endifPattern);
		return endifMatcher;
	}

	private Matcher getMatcher(String line, String stringPattern) {
		Pattern pattern = Pattern.compile(stringPattern);
		Matcher matcher = pattern.matcher(line);

		return matcher;
	}

	private String replaceInterpreter(String content,
			FileInterpreter interpreter, Set<String> featuresToMaintain,
			PreProcessorDirectives masterDirective) {
		String newContent = content;
		String featureName = interpreter.getFeatureName();

		if (((masterDirective == PreProcessorDirectives.IFDEF) && (featuresToMaintain
				.contains(featureName)))
				|| ((masterDirective == PreProcessorDirectives.IFNDEF) && (!featuresToMaintain
						.contains(featureName)))) {
			newContent = newContent.replaceAll(interpreter.getOriginalCode(),
					interpreter.getInnerCode());
			addFileFeature(featureName);
		} else {
			FileInterpreter newFileInterpreter = interpreter
					.getChildFileInterpreter();
			if (newFileInterpreter != null)
				newContent = replaceInterpreter(newContent, newFileInterpreter,
						featuresToMaintain, masterDirective);
			else {
				newContent = "";
			}
		}

		return newContent;
	}

	private void addFileFeature(String featureName) {
		FileFeature fileFeatureToAdd = new FileFeature();
		fileFeatureToAdd.setAssociateFeature(this.fileFeature
				.getAssociateFeature());
		fileFeatureToAdd.addFileOccurrencies(this.currentTreatingFile);
		this.featureMetrics.addAffectedFileAndDirective(featureName,
				fileFeatureToAdd);
	}

	private FileInterpreter getRootFileInterpreter(FileInterpreter interpreter)
			throws NullPointerException {
		FileInterpreter rootFileInterpreter = interpreter;
		FileInterpreter parentFileInterpreter = rootFileInterpreter
				.getParentFileInterpreter();
		while (parentFileInterpreter != null) {
			rootFileInterpreter = parentFileInterpreter;
			parentFileInterpreter = rootFileInterpreter
					.getParentFileInterpreter();
		}
		return rootFileInterpreter;
	}

	public static final void printMetricsInConsole() {
		String configName = INSTANCE.configName;
		INSTANCE.featureMetrics.setConfiguration(configName);
		INSTANCE.featureMetrics.printMetricsInConsole();
		INSTANCE.cleanMetrics();
	}

	public static void main(String[] args) {
		File file = new File(
				"D:/Temp/TesteTxtReplace/Feature_JS_Tests_Output.txt");
		String feature = "FooBliA";

		Set featuresToMaintain = new HashSet();
		featuresToMaintain.add(feature);
		FileFeature fileFeature = new FileFeature();
		fileFeature.addFile(file.toURI());
		fileFeature.setAssociateFeature(feature);
		FileManager fileManager = configure("A", fileFeature, null,
				featuresToMaintain);
		try {
			fileManager.manageFile();

			printMetricsInConsole();
		} catch (FeatureTreatmentException e) {
			e.printStackTrace();
		}
	}
}

/*
 * Location: /Users/alcemir/Documents/workspace/br.com.reconcavo.featurejs/
 * Qualified Name: br.com.reconcavo.featurejs.file.FileManager JD-Core Version:
 * 0.6.2
 */