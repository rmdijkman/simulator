package nl.tue.tm.is.epc;

public class Arc{
	
	String id;
	Node source;
	Node target;
	String type;
	
	public Arc(){
		this.type = "basic";
	}
	public Arc(String id, Node source, Node target){
		this.id = id;
		this.source = source;
		this.target = target;
		this.type = "basic";
	}
	
	public Arc(String id, Node source, Node target, String type){
		this.id = id;
		this.source = source;
		this.target = target;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String toString(){
		return "(" + source.toString() + "("+this.type+")" + target.toString() + ")";
	}
	
	public boolean equals(Object arg0) {
		if (arg0 instanceof Arc){
			return id.equals(((Arc)arg0).getId());
		}else{
			return false;
		}
	}
	
	public int hashCode() {
		return id.hashCode();
	}
	
}
