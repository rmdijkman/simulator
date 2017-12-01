package nl.tue.bpmn.concepts;

import java.util.HashSet;
import java.util.Set;

public class Role {

	String name;
	Set<Node> containedNodes;
	
	public Role(String name){
		this.name = name;
		containedNodes = new HashSet<Node>();
	}
	
	public Set<Node> getContainedNodes(){
		return containedNodes;
	}
	
	public void addContainedNode(Node node){
		containedNodes.add(node);
	}
	
	public String getName(){
		return name;
	}
}
