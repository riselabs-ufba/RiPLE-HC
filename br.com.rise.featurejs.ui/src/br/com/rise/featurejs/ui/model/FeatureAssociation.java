package br.com.rise.featurejs.ui.model;

import java.util.HashMap;
import java.util.Map;


/**
 * this class holds the associations among features.
 * <p>
 * it aims to serve as model to the {@link NodeModelContentProvider}.
 * 
 * @author Alcemir Santos
 */
public class FeatureAssociation {
		String feature;
		Map<String, Integer> associations;

		public FeatureAssociation(String fname) {
			feature = fname;
			associations = new HashMap<String, Integer>();
		}

		public void add(String feature) {
			if (associations.containsKey(feature)) {
				associations.put(feature, associations.get(feature) + 1);
			} else {
				associations.put(feature, 1);
			}
		}

		public String getFeature() {
			return feature;
		}

		public Map<String, Integer> getAssociations() {
			return associations;
		}
	}