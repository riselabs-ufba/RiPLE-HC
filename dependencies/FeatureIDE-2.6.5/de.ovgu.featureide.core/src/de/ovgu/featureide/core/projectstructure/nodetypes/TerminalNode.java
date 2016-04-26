/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2013  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package de.ovgu.featureide.core.projectstructure.nodetypes;

/**
 * This class represents a terminal node of a file
 * 
 * @author Janet Feigenspan
 */
public class TerminalNode extends Node{
	
	/**
	 * the content of this node (e.g. the method definition)
	 */
	private String content;

	public TerminalNode (String type, String name, String identifier) {
		super.setIdentifier(identifier);
		super.setName(name);
		super.setType(type);
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);		
	}
	
	public String toString() {
		return "[T -> " + super.toString() + " | content: " + this.getContent() + "]";
	}

}
