package nl.tue.bpmn.concepts;

import java.util.HashSet;
import java.util.Set;

public class BPMNModel {

	private Set<Arc> arcs;
	private Set<Node> nodes;
	private Set<Role> roles;
	private Set<ResourceType> resourceTypes;
	private String informationAttributes;
	
	public BPMNModel(){
		arcs = new HashSet<Arc>();
		nodes = new HashSet<Node>();
		roles = new HashSet<Role>();
		resourceTypes = new HashSet<ResourceType>();
	}
	
	public Set<Arc> getArcs() {
		return arcs;
	}
	
	public Set<Node> getNodes() {
		return nodes;
	}
	
	public Set<Role> getRoles() {
		return roles;
	}

	public Set<ResourceType> getResourceTypes() {
		return resourceTypes;
	}

	public void addRole(Role role){
		roles.add(role);
	}
	
	public void addNode(Node node){
		nodes.add(node);
	}
	
	public void addArc(Arc arc){
		arcs.add(arc);
	}

	public void addResourceType(ResourceType resourceType){
		resourceTypes.add(resourceType);
	}

	public String toString(){
		String result = "";
		for (Role role: roles){
			result += role.getName() + ":\n";
			for (Node node: role.getContainedNodes()){
				result += "\t" + node.toString() + "\n";
			}
		}
		for (Arc arc: arcs){
			result += arc.getSource().toString() + " -> " + arc.getTarget().toString() + "\n";
		}
		return result;
	}

	public String getInformationAttributes() {
		return informationAttributes;
	}
	
	public void setInformationAttributes(String informationAttributes) {
		this.informationAttributes = informationAttributes;
	}
	
	/**
	 * Returns the role with the given name. 
	 * 
	 * @param name	a name
	 * @return		the role with the given name or null if no such role can be found
	 */
	public Role roleByName(String name){
		for (Role r: roles){
			if ((r.getName() != null) && (r.getName().equals(name))){
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Returns the node with the given name.
	 * 
	 * @param name	a name
	 * @return 		the node with the given name or null if no such node can be found
	 */
	public Node nodeByName(String name){
		for (Node n: nodes){
			if ((n.getName() != null) && (n.getName().equals(name))){
				return n;
			}
		}
		return null;		
	}
	
//	public void actDepBySet(){
//		for(Node n : nodes){
//			Set<Node> sn = new HashSet<Node>();
//			if(!n.stractDependency.isEmpty() || !n.stractDependency.equals(null)){
//				for(String s : n.stractDependency){
//					sn.add(nodeByName(s));
//				}	
//			}
//			
//		}
//	}
}
