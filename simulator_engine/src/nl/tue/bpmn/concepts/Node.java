package nl.tue.bpmn.concepts;

import java.util.HashSet;
import java.util.Set;

public class Node {
	
	String name;
	String processingTimeDitribution;
	Type type;
	TypeGtw typeGtw;
	Set<Arc> incoming;
	Set<Arc> outgoing;
	String interArrivalTimeDistribution;
	String queuingStrategy;
	String resourceDependency;
	Set<String> stractDependency;
	Set<Node> activityDependency;
	
	public Node(String name, Type type, TypeGtw typeGtw, Set<Arc> incoming, Set<Arc> outgoing) {
		this.name =  name;
		this.type = type;
		this.typeGtw = typeGtw;
		this.incoming = incoming;
		this.outgoing = outgoing;
	}
	public Node(String name, Type type, TypeGtw typeGtw) {
		this.name =  name;
		this.type = type;
		this.typeGtw = typeGtw;
		incoming = new HashSet<Arc>();
		outgoing = new HashSet<Arc>();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public TypeGtw getTypeGtw() {
		return typeGtw;
	}
	public void setTypeGtw(TypeGtw typeGtw) {
		this.typeGtw = typeGtw;
	}
	public Set<Arc> getIncoming() {
		return incoming;
	}
	public void setIncoming(Set<Arc> incoming) {
		this.incoming = incoming;
	}
	public Set<Arc> getOutgoing() {
		return outgoing;
	}
	public void setOutgoing(Set<Arc> outgoing) {
		this.outgoing = outgoing;
	}
	public void addIncoming(Arc arc){
		this.incoming.add(arc);
	}
	public void addOutgoing(Arc arc){
		this.outgoing.add(arc);
	}
	public String getProcessingTimeDistribution() {
		return processingTimeDitribution;
	}
	public void setProcessingTimeDistribution(String processingTimeDitribution){
		this.processingTimeDitribution = processingTimeDitribution;
	}
	public String getInterArrivalTimeDistribution() {
		return interArrivalTimeDistribution;
	}
	public void setInterArrivalTimeDistribution(String interArrivalTimeDistribution) {
		this.interArrivalTimeDistribution = interArrivalTimeDistribution;
	}
	public String getQueuingStrategy() {
		return queuingStrategy;
	}
	public void setQueuingStrategy(String queuingStrategy) {
		this.queuingStrategy = queuingStrategy;
	}
	public String getResourceDependency() {
		return resourceDependency;
	}
	public void setResourceDependency(String resourceDependency) {
		this.resourceDependency = resourceDependency;
	}
	public Set<Node> getActivityDependency() {
		return activityDependency;
	}
	public void setActivityDependency(Set<Node> activityDependency) {
		this.activityDependency = activityDependency;
	}
	public Set<String> getStractDependency() {
		return stractDependency;
	}
	public void setStractDependency(Set<String> stractDependency) {
		this.stractDependency = stractDependency;
	}

}
