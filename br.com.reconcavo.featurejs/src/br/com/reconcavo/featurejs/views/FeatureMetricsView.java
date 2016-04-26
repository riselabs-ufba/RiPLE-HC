package br.com.reconcavo.featurejs.views;

import br.com.reconcavo.featurejs.metrics.FileFeature;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class FeatureMetricsView extends ViewPart
{
  public static final String ID = "br.com.reconcavo.featurejs.views.FeatureMetricsView";
  private Composite parent;
  private Graph graph;
  private Map<String, GraphNode> featureGraphNode = new HashMap();

  public void createPartControl(Composite parent)
  {
    this.parent = parent;
    this.graph = new Graph(this.parent, 0);
    refreshGraph(null);
  }

  public void setViewTitle(String title) {
    this.parent.setToolTipText(title);

    Text txtTest = new Text(this.parent, 0);
    txtTest.setText(title);
    GridData gridData = new GridData(4, 4, Boolean.TRUE.booleanValue(), Boolean.FALSE.booleanValue(), 2, 1);
    txtTest.setLayoutData(gridData);
  }

  public void refreshGraph(Map<String, Set<FileFeature>> filesVsFeatures) {
    clearGraph();
    if ((filesVsFeatures != null) && (!filesVsFeatures.isEmpty()))
    {
      for (Map.Entry filesVsFeature : filesVsFeatures.entrySet()) {
        String feature = (String)filesVsFeature.getKey();
        Set fileFeatures = (Set)filesVsFeature.getValue();
        createNode(feature, fileFeatures);
      }

      applyLayout();
    }
  }

  public void clearGraph() {
    this.featureGraphNode.clear();
    this.graph.clear();
  }

  public void createNode(String feature, Set<FileFeature> fileFeatures) {
    Device parentDevice = this.parent.getDisplay().getSystemFont().getDevice();

    for (FileFeature fileFeature : fileFeatures) {
      GraphNode nodeFrom = (GraphNode)this.featureGraphNode.get(feature);
      if (nodeFrom == null) {
        nodeFrom = new GraphNode(this.graph, 0, feature);

        RGB blue = new RGB(244, 182, 100);
        Color c = new Color(parentDevice, blue);
        nodeFrom.setBackgroundColor(c);

        this.featureGraphNode.put(feature, nodeFrom);
      }

      String associateFeature = fileFeature.getAssociateFeature();
      GraphNode nodeTo = (GraphNode)this.featureGraphNode.get(associateFeature);
      if (nodeTo == null) {
        nodeTo = new GraphNode(this.graph, 0, associateFeature);
        this.featureGraphNode.put(associateFeature, nodeTo);
      }

      GraphConnection graphConnection = new GraphConnection(this.graph, 0, nodeFrom, nodeTo);

      Font font = new Font(parentDevice, "lineFont", 15, 1);
      graphConnection.setFont(font);

      Integer qtdFilesOccurencies = Integer.valueOf(fileFeature.getFilesOccurrencies().size());
      graphConnection.setText(String.valueOf(qtdFilesOccurencies));
      String text;
      if (qtdFilesOccurencies.intValue() > 1)
        text = "There are " + qtdFilesOccurencies + " files in this association.";
      else {
        text = "There is " + qtdFilesOccurencies + " file in this association.";
      }

      IFigure tooltip = new Label(text);
      graphConnection.setTooltip(tooltip);
    }
  }

  public void applyLayout() {
    TreeLayoutAlgorithm graphLayout = new TreeLayoutAlgorithm(1);
    this.graph.setLayoutAlgorithm(graphLayout, Boolean.TRUE.booleanValue());
  }

  public void setFocus()
  {
  }
}