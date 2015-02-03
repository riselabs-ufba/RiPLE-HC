/**
 * 
 */
package br.com.rise.featurejs.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.layout.GridData;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.LayoutStyles;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import br.com.rise.featurejs.ui.handlers.WorkspaceChangeHandler;
import br.com.rise.featurejs.ui.helpers.managers.ScatteringTraceabilityLinksManager;
import br.com.rise.featurejs.ui.model.ModuleToVariationPointsLink;
import br.com.rise.featurejs.ui.model.ScatteringTraceabilityLink;
import br.com.riselabs.vparser.beans.CCVariationPoint;
import br.com.riselabs.vparser.lexer.beans.Token;
import br.com.riselabs.vparser.lexer.enums.TokenType;
import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;

/**
 * @author "Alcemir Santos"
 * 
 */
public class FeatureInteractionsView extends ViewPart implements
		PropertyChangeListener {
	private Composite parent;
	private Composite myContents;
	
	private IFeatureProject currentProject;

	private static TabFolder tabFolder;
	private Map<String, TabItem> configTabs;

	private WorkspaceChangeHandler wHandler;

	ISelectionListener pListener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart part, ISelection sel) {
           if (!(sel instanceof IStructuredSelection))
              return;
           IStructuredSelection ss = (IStructuredSelection) sel;
           Object o = ss.getFirstElement();
           if (o instanceof IProject){
        	   IFeatureProject f = CorePlugin.getFeatureProject((IProject) o);
        	   currentProject = f;
        	   wHandler.getInstance().setTargetProject(f);
           }
        }
     };
     
	public FeatureInteractionsView(){
		ScatteringTraceabilityLinksManager.getInstance().addChangeListener(this);
	}
	
	public void createPartControl(Composite parent) {
		this.parent = parent;
		getSite().getPage().addSelectionListener(pListener);
		
		hookProjectTab();
		hookConfigTabs();

		// TODO try this... InteractionGraphsManager.getInstance().addChangeListener(this);
	}
	
	/**
	 * creates the tab for the project
	 */
	private void hookProjectTab() {
		myContents = new Composite(parent, SWT.NONE);
		myContents.setLayout(new FillLayout(SWT.VERTICAL));
		myContents.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder = new TabFolder(myContents, SWT.NONE);
		TabItem pTab = new TabItem(tabFolder, SWT.NONE);
		pTab.setText("Project Interaction");
		pTab.setToolTipText("Show the interaction of the entire project.");
		pTab.setControl(getProjectTabControl());
	}
	
	/**
	 * returns the graph to show in the project tab 
	 * @return
	 */
	private Control getProjectTabControl() {
		if(currentProject == null)
			return createEmptyControl();
		return new InteractionsGraphHelper()
		.buildProjectGraph(ScatteringTraceabilityLinksManager
				.getInstance().getObject(currentProject.getProjectName()));
	}
	
	/**
	 * creates the tabs for the products
	 */
	private void hookConfigTabs() {
		configTabs = new HashMap<String, TabItem>();
		if (currentProject == null) 
			return;
		
		IResource[] configFiles = null;
		try {
			configFiles = currentProject.getConfigFolder().members();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		int count = 1;
		for (IResource cFile : configFiles) {
			if(!cFile.getFileExtension().equals("config"))
				continue;
			// Create each tab and set its text, tool tip text,
			// image, and control
			TabItem aTab = new TabItem(tabFolder, SWT.NONE);
			aTab.setText("Product "+count++);
			aTab.setToolTipText("This graph show the existing interaction in the "
					+ cFile.getName() + " file.");
			aTab.setControl(getTabControl(cFile));
			configTabs.put(cFile.getName(), aTab);
		}
	}

	/**
	 * returns the graph to show in the products tab
	 * @param iResource
	 * @return
	 */
	private Control getTabControl(IResource iResource) {
		return  new InteractionsGraphHelper().buildGraph(ScatteringTraceabilityLinksManager
						.getInstance().getObject(currentProject.getProjectName()), iResource);
	}

	/**
	 * used in case that the project isn't initialized yet.
	 * @return
	 */
	private Control createEmptyControl() {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
//		Graph graph = new Graph(composite, SWT.NONE);
		return composite;
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		List<ScatteringTraceabilityLink> links = null;
		Object o = evt.getNewValue();
		if (o instanceof List<?>)
			links = (List<ScatteringTraceabilityLink>) o;

		this.currentProject = links.get(0).getProject();

		disposeTabFolder();
		
		hookProjectTab();
		hookConfigTabs();
//		this.myContents.layout(true);
		this.parent.layout();
		this.parent.pack();
//		InteractionsGraphHelper helper = new InteractionsGraphHelper();
//		helper.buildProjectGraph(links);
//		try {
//			for (IResource configFile: this.currentProject.getConfigFolder().members()) {
//				if(!configFile.getFileExtension().equals("config"))
//					continue;
//				helper.buildGraph(links, configFile);
//			}
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}

	}

	private void disposeTabFolder() {
		myContents.dispose();
	}

	class InteractionsGraphHelper {

		Composite composite;
		Graph g;
		Map<String, GraphNode> nodes = new HashMap<String, GraphNode>();

		/**
		 * Builds the graph for a given config file
		 * 
		 * @param links
		 * @param iResource
		 * @return
		 */
		public Control buildGraph(List<ScatteringTraceabilityLink> links,
				IResource iResource) {
			List<String> featuresSelected = getSelectedFeatures(iResource);
			List<ScatteringTraceabilityLink> newlist = new ArrayList<>();
			for (ScatteringTraceabilityLink link : links) {
				if( !featuresSelected.contains(link.getFeature().getName()) )
					newlist.add(link);
			}
			return buildProjectGraph(newlist);
		}

		/**
		 * Builds a graph to the project
		 * 
		 * @param links
		 * @return
		 */
		public Control buildProjectGraph(List<ScatteringTraceabilityLink> links) {
			composite = getComposite();
			// Graph will hold all other objects
			g = new Graph(composite, SWT.NONE);
			g.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);
			
			List<FeatureAssociation> associations = getAssociations(links);

			// now a few nodes

			for (FeatureAssociation link : associations) {
				// create a node if it does not exists yet.
				GraphNode from = getNode(link.getFeature());

				// make associations
				for (String s : link.getAssociations().keySet()) {
					// create a node if it does not exists yet.
					GraphNode to = getNode(s);

					// Lets have a directed connection
					String edgeLabel = String.valueOf(link.getAssociations()
							.get(s));
					GraphConnection con = getEdge(from, to, edgeLabel);
				}

			}

			return composite;
		}

		/**
		 * return a node from the map of nodes
		 * 
		 * @param name
		 * @return
		 */
		private GraphNode getNode(String name) {
			if (!nodes.containsKey(name))
				nodes.put(name, new GraphNode(g, SWT.NONE, name));
			return nodes.get(name);
		}

		/**
		 * creates a directed connection between two nodes with a given label.
		 * 
		 * @param from
		 * @param to
		 * @param label
		 * @return
		 */
		private GraphConnection getEdge(GraphNode from, GraphNode to,
				String label) {
			GraphConnection con = new GraphConnection(g,
					ZestStyles.CONNECTIONS_DIRECTED, from, to);
			con.setText(label);
			Font font = new Font(composite.getDisplay().getSystemFont()
					.getDevice(), "lineFont", 15, 1);
			con.setFont(font);
			return con;
		}

		private List<FeatureAssociation> getAssociations(
				List<ScatteringTraceabilityLink> links) {
			List<FeatureAssociation> lAssociations = new ArrayList<FeatureAssociation>();
			// para cada feature cria uma entrada no mapa
			for (ScatteringTraceabilityLink link : links) {
				FeatureAssociation anAssociation = new FeatureAssociation(link
						.getFeature().getName());

				// pra cada modulelink incrementa as associacoes
				for (ModuleToVariationPointsLink mlink : link
						.getModulevpLinks()) {
					for (CCVariationPoint vp : mlink.getVps()) {
						if (vp.isSingleVP()) {
							anAssociation.add(getFeatureName(vp.getTokens()));
						}
					}

				}
				lAssociations.add(anAssociation);
			}

			return lAssociations;
		}

		private String getFeatureName(List<Token> tokens) {
			for (Token token : tokens) {
				if (token.getLexeme() != TokenType.TAG)
					continue;
				return token.getValue();
			}
			return null;
		}

		private Composite getComposite() {
			Composite c = new Composite(tabFolder, SWT.NONE);
			c.setLayout(new FillLayout(SWT.VERTICAL));
			return c;
		}

		private List<String> getSelectedFeatures(IResource iResource) {
			List<String> l = new ArrayList<>();
			 try(BufferedReader br = new BufferedReader(new FileReader(iResource.getLocation().toOSString()))) {
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

	}

	class FeatureAssociation {
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
	
	@Override
	public void dispose(){
//		workspace.removeResourceChangeListener(wListener);
//		getSite().getPage().removeSelectionListener(pListener);
		super.dispose();
	}

}
