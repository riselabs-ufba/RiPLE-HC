package br.com.reconcavo.featurejs.util;

import br.com.reconcavo.featurejs.metrics.FileFeature;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

public final class CollectionUtil {
	public static final void mergeMap(
			Map<String, FileFeature> fileFeaturesRoot,
			Map<String, FileFeature> fileFeaturesToAdd) {
		try {
			Map newFileFeatures = new WeakHashMap();

			for (Map.Entry fileFeatureEntry : fileFeaturesToAdd.entrySet()) {
				String associatedFeature = (String) fileFeatureEntry.getKey();
				FileFeature fileFeatureToAdd = (FileFeature) fileFeatureEntry
						.getValue();
				FileFeature mergeFileFeature = (FileFeature) fileFeaturesRoot
						.get(associatedFeature);

				if (mergeFileFeature != null) {
					Set filesToAdd = fileFeatureToAdd.getFilesOccurrencies()
							.keySet();
					mergeFileFeature.addFilesOccurencies(filesToAdd);
				} else {
					newFileFeatures.put(associatedFeature, fileFeatureToAdd);
				}
			}

			fileFeaturesRoot.putAll(newFileFeatures);
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void mergeSet(Set<FileFeature> fileFeaturesRoot,
			FileFeature fileFeatureToAdd) {
		try {
			Boolean shouldNOTAdd = Boolean.FALSE;
			if (!fileFeaturesRoot.isEmpty()) {
				for (FileFeature fileFeature : fileFeaturesRoot) {
					String associatedFeatureRoot = fileFeature
							.getAssociateFeature();
					String associatedFeatureToAdd = fileFeatureToAdd
							.getAssociateFeature();

					if (associatedFeatureRoot
							.equalsIgnoreCase(associatedFeatureToAdd)) {
						Map<File, Integer> filesOcurrencies = fileFeature
								.getFilesOccurrencies();
						Map<File, Integer> filesOcurrenciesToAdd = fileFeatureToAdd
								.getFilesOccurrencies();

						for (Entry<File, Integer> filesOcurrenciesEntry : filesOcurrenciesToAdd
								.entrySet()) {
							shouldNOTAdd = Boolean.TRUE;
							File fileKey = (File) filesOcurrenciesEntry
									.getKey();
							Integer qtdOcurrencies = (Integer) filesOcurrencies
									.get(fileKey);
							if (qtdOcurrencies != null)
								filesOcurrencies.put(
										fileKey,
										qtdOcurrencies = Integer
												.valueOf(qtdOcurrencies
														.intValue() + 1));
							else {
								filesOcurrencies.put(fileKey,
										Integer.valueOf(1));
							}
						}
					}
				}
			}

			if (!shouldNOTAdd.booleanValue())
				fileFeaturesRoot.add(fileFeatureToAdd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/*
 * Location: /Users/alcemir/Documents/workspace/br.com.reconcavo.featurejs/
 * Qualified Name: br.com.reconcavo.featurejs.util.CollectionUtil JD-Core
 * Version: 0.6.2
 */