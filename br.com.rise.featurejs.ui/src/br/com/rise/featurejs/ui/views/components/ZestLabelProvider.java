package br.com.rise.featurejs.ui.views.components;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.IEntityConnectionStyleProvider;
import org.eclipse.zest.core.widgets.ZestStyles;

import br.com.rise.featurejs.ui.model.FeatureNode;
import br.com.rise.featurejs.ui.model.InteractionEdge;


public class ZestLabelProvider extends LabelProvider  implements IEntityConnectionStyleProvider{
	
	  @Override
	  public String getText(Object element) {
	    if (element instanceof FeatureNode) {
	      FeatureNode association = (FeatureNode) element;
	      return association.getName();
	    }
	    if (element instanceof InteractionEdge) {
	      InteractionEdge myConnection = (InteractionEdge) element;
	      return myConnection.getLabel();
	    }

	    if (element instanceof EntityConnectionData) {
	      EntityConnectionData test = (EntityConnectionData) element;
	      return "";
	    }
	    throw new RuntimeException("Wrong type: "
	        + element.getClass().toString());
	  }
	  
	  @Override
		public int getConnectionStyle(Object src, Object dest) {
			return ZestStyles.CONNECTIONS_DIRECTED;
		}

		@Override
		public Color getColor(Object src, Object dest) {
			return null;
		}

		@Override
		public Color getHighlightColor(Object src, Object dest) {
			return null;
		}

		@Override
		public int getLineWidth(Object src, Object dest) {
			return 0;
		}

		@Override
		public IFigure getTooltip(Object entity) {
			return null;
		}
} 
