package nl.tue.bpmn.concepts;

import java.util.HashSet;
import java.util.Set;

public class ResourceType {

	String name;
	int number;
	Set<Role> roles;
	
	public ResourceType(){
		roles = new HashSet<Role>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void addRole(Role role) {
		roles.add(role);
	}		
}
