package nl.tue.bpmn.concepts;

public class Arc {
	String condition;
	Node source;
	Node target;
	
	public Arc(String condition, Node source, Node target) {
		this.condition =  condition;
		this.source = source;
		this.target = target;
	}
	
	public Arc(String condition) {
		this.condition = condition;
	}

	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public Node getSource() {
		return source;
	}
	public void setSource(Node source) {
		this.source = source;
	}
	public Node getTarget() {
		return target;
	}
	public void setTarget(Node target) {
		this.target = target;
	}
}
