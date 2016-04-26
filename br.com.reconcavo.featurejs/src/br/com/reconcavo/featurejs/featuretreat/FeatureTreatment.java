package br.com.reconcavo.featurejs.featuretreat;

public final class FeatureTreatment {
	public static final String DEFAULT_REPLACE = "";

	public static final String generateGenericPatternIfdef() {
		return generateGenericPattern(PreProcessorDirectives.IFDEF);
	}

	public static final String generateGenericPatternIfndef() {
		return generateGenericPattern(PreProcessorDirectives.IFNDEF);
	}

	public static final String generateGenericPatternElif() {
		return generateGenericPattern(PreProcessorDirectives.ELIF);
	}

	public static final String generateGenericPatternEndif() {
		return generateGenericPattern(PreProcessorDirectives.ENDIF);
	}

	public static final String generateGenericPattern(
			PreProcessorDirectives directive) {
		String pattern = "(//";
		pattern = pattern + "[\\s]*";
		pattern = pattern + directive.getDirectiveName();
		pattern = pattern + "[\\s]*)";
		pattern = pattern + "([\\w]*)";
		return pattern;
	}
}
