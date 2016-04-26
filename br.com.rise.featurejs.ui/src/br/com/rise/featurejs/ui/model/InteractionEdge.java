package br.com.rise.featurejs.ui.model;

public class InteractionEdge {

	final String id;
	String label;
	final FeatureNode source;
	final FeatureNode destination;

	public InteractionEdge(String id, String label, FeatureNode source,
			FeatureNode destination) {
		this.id = id;
		this.label = label;
		this.source = source;
		this.destination = destination;
	}

	public String getLabel() {
		return label;
	}

	public FeatureNode getSource() {
		return source;
	}

	public FeatureNode getDestination() {
		return destination;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof InteractionEdge))
			return false;

		InteractionEdge obj = (InteractionEdge) o;
		if (this.source.getName().equals(obj.getSource().getName())
				&& this.destination.getName().equals(
						obj.getDestination().getName())) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return "From "+this.source.getName()+" to "+this.destination.getName()+". ["+this.label+"x]";
	}

}
