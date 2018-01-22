package nl.tue.bpmn.concepts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BPMNModel {

	private Set<Arc> arcs;
	private Set<Node> nodes;
	private Set<Role> roles;
	private Set<ResourceType> resourceTypes;
	private String informationAttributes;
	
	private boolean nodeByNameDirty;
	private Map<String,Node> nodeByName;
	private boolean roleByNameDirty;
	private Map<String,Role> roleByName;
	
	public BPMNModel(){
		arcs = new HashSet<Arc>();
		nodes = new HashSet<Node>();
		roles = new HashSet<Role>();
		resourceTypes = new HashSet<ResourceType>();
		
		nodeByNameDirty = true;
		nodeByName = new HashMap<String,Node>();
		roleByNameDirty = true;
		roleByName = new HashMap<String,Role>();
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
		roleByNameDirty = true;
	}
	
	public void addNode(Node node){
		nodes.add(node);
		nodeByNameDirty = true;
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
		if (roleByNameDirty) {
			for (Role r: roles){
				if (r.getName() != null){
					roleByName.put(r.getName(), r);
				}
			}
			roleByNameDirty = false;
		}
		return roleByName.get(name);
	}
	
	/**
	 * Returns the node with the given name.
	 * 
	 * @param name	a name
	 * @return 		the node with the given name or null if no such node can be found
	 */
	public Node nodeByName(String name){
		if (nodeByNameDirty) {
			for (Node n: nodes){
				if (n.getName() != null){
					nodeByName.put(n.getName(), n);
				}
			}
			nodeByNameDirty = false;
		}
		return nodeByName.get(name);
	}
	
}
