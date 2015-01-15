package br.com.reconcavo.featurejs.featuretreat;

public enum PreProcessorDirectives {
	IFDEF("#ifdef"), IFNDEF("#ifndef"), ELIF("#elif"), ENDIF("#endif"), NONE("");

	private String directiveName = "";

	private PreProcessorDirectives(String directiveName) {
		this.directiveName = directiveName;
	}

	public String getDirectiveName() {
		return this.directiveName;
	}

	public static PreProcessorDirectives getDirective(String directiveName)
			throws FeatureTreatmentException {
		if (directiveName.contains(IFDEF.directiveName))
			return IFDEF;
		if (directiveName.contains(IFNDEF.directiveName))
			return IFNDEF;
		if (directiveName.contains(ELIF.directiveName))
			return ELIF;
		if (directiveName.contains(ENDIF.directiveName))
			return ENDIF;
		if (directiveName.contains(NONE.directiveName)) {
			return NONE;
		}
		throw new FeatureTreatmentException(
				"There is no know Directive with the given name.");
	}

	@Override
	public String toString() {
		return super.toString() + ": [directiveName: " + this.directiveName
				+ "]";
	}

}
