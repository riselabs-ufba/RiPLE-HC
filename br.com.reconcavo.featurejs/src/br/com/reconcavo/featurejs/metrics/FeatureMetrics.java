package br.com.reconcavo.featurejs.metrics;

import br.com.reconcavo.featurejs.util.CollectionUtil;
import br.com.reconcavo.featurejs.util.ConsoleUtil;
import br.com.reconcavo.featurejs.view.FeatureMetricsView;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsole;

public class FeatureMetrics {
	public static long initialTime;
	private static final MessageConsole classConsole = ConsoleUtil
			.findConsole(FeatureMetrics.class.getName());
	private FeatureMetricsView currentMetricsView;
	private Integer featureID = Integer.valueOf(1);
	private String configuration;
	private Integer qtdDirectivesInCode = Integer.valueOf(0);
	private Integer qtdDirectivesApplied = Integer.valueOf(0);
	private Map<String, Set<FileFeature>> affectFilesVsFeatures;
	private Set<File> allAffectedFiles;

	public FeatureMetrics() {
		this.affectFilesVsFeatures = new HashMap();
		this.allAffectedFiles = new HashSet();
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public void addOneQtdDirectiveApplied() {
		addQtdDirectivesApplied(Integer.valueOf(1));
	}

	public void addQtdDirectivesApplied(Integer qtdNumber) {
		this.qtdDirectivesApplied = addQtdNumber(this.qtdDirectivesApplied,
				qtdNumber);
	}

	public void addOneQtdDirectiveInCode() {
		addQtdDirectivesInCode(Integer.valueOf(1));
	}

	public void addQtdDirectivesInCode(Integer qtdNumber) {
		this.qtdDirectivesInCode = addQtdNumber(this.qtdDirectivesInCode,
				qtdNumber);
	}

	private Integer addQtdNumber(Integer qtdTotal, Integer qtdNumber) {
		Integer newQtdTotal = Integer.valueOf(qtdTotal.intValue()
				+ qtdNumber.intValue());
		return newQtdTotal;
	}

	public void addAffectedFileAndDirective(String feature,
			FileFeature fileFeature) {
		Set affectedFilesForDirective = (Set) this.affectFilesVsFeatures
				.get(feature);

		if (affectedFilesForDirective == null) {
			affectedFilesForDirective = new HashSet();
			this.affectFilesVsFeatures.put(feature, affectedFilesForDirective);
		}
		CollectionUtil.mergeSet(affectedFilesForDirective, fileFeature);

		this.allAffectedFiles.addAll(fileFeature.getFilesOccurrencies()
				.keySet());
	}

	public Integer getQtdDirectivesApplied() {
		return this.qtdDirectivesApplied;
	}

	public Integer getQtdDirectivesInCode() {
		return this.qtdDirectivesInCode;
	}

	public Integer getQtdAffectedFiles() {
		return Integer.valueOf(this.allAffectedFiles.size());
	}

	public final Map<String, Set<FileFeature>> getAffectFilesVsFeatures() {
		return this.affectFilesVsFeatures;
	}

	public final void printMetricsInConsole() {
		final String configurationTitle = "Configuration for the set: "
				+ this.configuration + ".";
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					IWorkbenchWindow[] workbenchWindows = PlatformUI
							.getWorkbench().getWorkbenchWindows();
					IWorkbenchWindow workbenchWindow = null;
					if ((workbenchWindows != null)
							&& (workbenchWindows.length > 0)) {
						workbenchWindow = workbenchWindows[0];
					}

					String systemMilis = String.valueOf(System.currentTimeMillis());
					IViewPart viewPart = workbenchWindow
							.getActivePage()
							.showView(
									"br.com.reconcavo.featurejs.view.FeatureMetricsView",
									systemMilis, 2);

					FeatureMetrics.this.currentMetricsView = ((FeatureMetricsView) viewPart);
					FeatureMetrics.this.currentMetricsView.clearGraph();

					String viewTitle = "FeatureJS ID " + systemMilis + " - "
							+ configurationTitle;
					FeatureMetrics.this.currentMetricsView
							.setViewTitle(viewTitle);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Integer qtdDirectivesInCode = getQtdDirectivesInCode();
		String qtdDirectivesInCodeMessage = "Total of directives logical blocks in code: "
				+ qtdDirectivesInCode;

		Integer qtdDirectivesApplied = getQtdDirectivesApplied();
		String qtdDirectivesAppliedMessage = "Total of directives logical blocks applied: "
				+ qtdDirectivesApplied;

		Integer qtdAffectedFiles = getQtdAffectedFiles();
		String qtdAffectedFilesMessage = "Total of affected files: "
				+ qtdAffectedFiles;

		Map affectFilesVsFeatures = getAffectFilesVsFeatures();
		String affectedFilesFeaturesMessage = "Features Vs Files:\n";

		Set affectFilesVsFeaturesSet = affectFilesVsFeatures.entrySet();
		int setSize = affectFilesVsFeaturesSet.size();
		Iterator affectFilesVsFeaturesIT = affectFilesVsFeatures.entrySet()
				.iterator();
		for (int i = 0; i < setSize; i++) {
			Map.Entry affectedFilesVsFeature = (Map.Entry) affectFilesVsFeaturesIT
					.next();
			final String feature = (String) affectedFilesVsFeature.getKey();
			final Set<FileFeature> fileFeatures = (Set) affectedFilesVsFeature.getValue();

			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					FeatureMetrics.this.currentMetricsView.createNode(feature,
							fileFeatures);
				}
			});
			affectedFilesFeaturesMessage = affectedFilesFeaturesMessage
					+ feature + "\n";
			for (FileFeature fileFeature : fileFeatures) {
				affectedFilesFeaturesMessage = affectedFilesFeaturesMessage
						+ "- " + fileFeature.formatedStructure() + ",\n";
			}
			affectedFilesFeaturesMessage = affectedFilesFeaturesMessage
					.substring(0, affectedFilesFeaturesMessage.length() - 2);

			if (i + 1 == setSize)
				affectedFilesFeaturesMessage = affectedFilesFeaturesMessage
						+ ".";
			else {
				affectedFilesFeaturesMessage = affectedFilesFeaturesMessage
						+ ";\n\n";
			}
		}

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				FeatureMetrics.this.currentMetricsView.applyLayout();
				FeatureMetrics tmp14_11 = FeatureMetrics.this;
				tmp14_11.featureID = Integer.valueOf(tmp14_11.featureID
						.intValue() + 1);
			}
		});
		printConsoleMessage("\n\n#################");

		Double totalTimeInSec = getTotalTimeInSecondes();
		String totalTimeMessage = "The build process spent " + totalTimeInSec
				+ " seconds.";
		printConsoleMessage(totalTimeMessage);

		printConsoleMessage(configurationTitle);
		printConsoleMessage(qtdDirectivesInCodeMessage);
		printConsoleMessage(qtdDirectivesAppliedMessage);
		printConsoleMessage(qtdAffectedFilesMessage);
		printConsoleMessage(affectedFilesFeaturesMessage);
		printConsoleMessage("#################");
	}

	private Double getTotalTimeInSecondes() {
		Long finalTime = Long.valueOf(System.currentTimeMillis());
		Long totalTime = Long.valueOf(finalTime.longValue() - initialTime);
		Double totalTimeInSec = Double
				.valueOf(totalTime.doubleValue() / 1000.0D);
		BigDecimal bd = new BigDecimal(totalTimeInSec.doubleValue());
		bd = bd.setScale(2, 4);
		return Double.valueOf(bd.doubleValue());
	}

	public static final void printConsoleMessage(String messageToConsole) {
		ConsoleUtil.printMessage(classConsole, messageToConsole);
	}
}

/*
 * Location: /Users/alcemir/Documents/workspace/br.com.reconcavo.featurejs/
 * Qualified Name: br.com.reconcavo.featurejs.metrics.FeatureMetrics JD-Core
 * Version: 0.6.2
 */