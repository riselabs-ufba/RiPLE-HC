package br.com.rise.featurejs.ui.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;

import br.com.rise.featurejs.ui.helpers.managers.ScatteringTraceabilityLinksManager;
import br.com.riselabs.vparser.beans.CCVariationPoint;
import br.com.riselabs.vparser.lexer.beans.Token;
import br.com.riselabs.vparser.lexer.enums.TokenType;

public class NodeModelContentProvider {

	private List<ScatteringTraceabilityLink> links;

	private List<InteractionEdge> edges;

	private IResource configFile;

	public NodeModelContentProvider(List<ScatteringTraceabilityLink> links,
			IResource configFile) {
		this.links = links;
		this.configFile = configFile;
		this.edges = new ArrayList<InteractionEdge>();
	}

	public List<FeatureNode> getNodes() {
		return configFile == null ? getAssociations()
				: getAssociations(configFile);
	}

	public List<FeatureNode> getNodes(IResource configFile) {
		return getAssociations(configFile);
	}

	/**
	 * Builds the graph for a given config file
	 * 
	 * @param iResource
	 * @return
	 */
	private List<FeatureNode> getAssociations(IResource iResource) {
		List<String> featuresSelected = getSelectedFeatures(iResource);
		List<ScatteringTraceabilityLink> newlist = new ArrayList<>();
		for (ScatteringTraceabilityLink link : links) {
			if (featuresSelected.contains(link.getFeature().getName()))
				newlist.add(link);
		}
		this.links = newlist;
		return getAssociations();
	}

	private List<FeatureNode> getAssociations() {
		// used to create unique instances of nodes.
		Map<String, FeatureNode> mNodes = new HashMap<String, FeatureNode>();

		List<FeatureAssociation> lAssociations = new ArrayList<FeatureAssociation>();

		// for each link creates a node and its associations
		int nodeCounter = 1, edgeCounter = 1;
		for (ScatteringTraceabilityLink link : links) {
			String sourceName = link.getFeature().getName();
			FeatureNode from = new FeatureNode(String.valueOf(nodeCounter++),
					sourceName);
			FeatureAssociation association = new FeatureAssociation(sourceName);
			if (!mNodes.containsKey(sourceName))
				mNodes.put(sourceName, from);

			// make the edges for each link
			for (ModuleToVariationPointsLink mlink : link.getModulevpLinks()) {
				for (CCVariationPoint vp : mlink.getVps()) {
					if (vp.isSingleVP()) {
						String destination = getFeatureName(vp.getTokens());
						FeatureNode to = new FeatureNode(
								String.valueOf(nodeCounter++), destination);
						if (!mNodes.containsKey(destination))
							mNodes.put(destination, to);

						association.add(destination);

						InteractionEdge edge = new InteractionEdge(
								String.valueOf(edgeCounter++),
								String.valueOf(link.getModulevpLinks().size()),
								mNodes.get(sourceName), mNodes.get(destination));

						edge.getSource().getConnectedTo()
								.add(edge.getDestination());

					}
				}
			}
			lAssociations.add(association);
		}

		int i = 1;
		for (FeatureAssociation f : lAssociations) {
			for (String to : f.getAssociations().keySet()) {
				this.edges.add(new InteractionEdge(String.valueOf(i++), String
						.valueOf(f.getAssociations().get(to)), mNodes.get(f
						.getFeature()), mNodes.get(to)));
			}

		}
		return new ArrayList<FeatureNode>(mNodes.values());
	}

	private String getFeatureName(List<Token> tokens) {
		for (Token token : tokens) {
			if (token.getLexeme() != TokenType.TAG)
				continue;
			return token.getValue();
		}
		return null;
	}

	private List<String> getSelectedFeatures(IResource iResource) {
		List<String> l = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(iResource
				.getLocation().toOSString()))) {
			String line = br.readLine();

			while (line != null) {
				l.add(line);
				line = br.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return l;
	}

	public List<InteractionEdge> getEdges() {
		return this.edges;
	}
}
