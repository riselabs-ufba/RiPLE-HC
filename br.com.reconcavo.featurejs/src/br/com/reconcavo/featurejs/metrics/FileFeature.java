package br.com.reconcavo.featurejs.metrics;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FileFeature {
	private Map<File, Integer> filesOccurrencies;
	private String associateFeature;

	public FileFeature() {
		this.filesOccurrencies = new HashMap();
	}

	public FileFeature(FileFeature fileFeatureToClone) {
		this();
		this.associateFeature = fileFeatureToClone.getAssociateFeature();
	}

	public void addFile(URI fileURI) {
		File file = new File(fileURI);
		addFileOccurrencies(file);
	}

	public void addFilesOccurencies(Collection<File> files) {
		for (File file : files)
			addFileOccurrencies(file);
	}

	public void addFileOccurrencies(File file) {
		Integer fileQtdOccurrency = (Integer) this.filesOccurrencies.get(file);
		if (fileQtdOccurrency != null)
			this.filesOccurrencies.put(
					file,
					fileQtdOccurrency = Integer.valueOf(fileQtdOccurrency
							.intValue() + 1));
		else
			this.filesOccurrencies.put(file, Integer.valueOf(1));
	}

	public Map<File, Integer> getFilesOccurrencies() {
		return this.filesOccurrencies;
	}

	public String getAssociateFeature() {
		return this.associateFeature;
	}

	public void setAssociateFeature(String associateFeature) {
		this.associateFeature = associateFeature;
	}

	public String formatedStructure() {
		String formatedStructure = this.associateFeature + ": ";
		formatedStructure = formatedStructure + this.filesOccurrencies.size()
				+ " ";
		formatedStructure = formatedStructure + "(";
		for (Map.Entry fileOccurrency : this.filesOccurrencies.entrySet()) {
			formatedStructure = formatedStructure
					+ ((File) fileOccurrency.getKey()).getName();
			formatedStructure = formatedStructure + " ["
					+ fileOccurrency.getValue() + "], ";
		}
		formatedStructure = formatedStructure.substring(0,
				formatedStructure.length() - 2);
		formatedStructure = formatedStructure + ")";

		return formatedStructure;
	}

	public String toString() {
		String toString = "=> FileFeature [files: " + this.filesOccurrencies;
		toString = toString + " | associateFeature: " + this.associateFeature;
		toString = toString + "]";

		return toString;
	}
}

/*
 * Location: /Users/alcemir/Documents/workspace/br.com.reconcavo.featurejs/
 * Qualified Name: br.com.reconcavo.featurejs.metrics.FileFeature JD-Core
 * Version: 0.6.2
 */