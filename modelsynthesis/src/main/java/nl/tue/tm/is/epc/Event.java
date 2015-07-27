package nl.tue.tm.is.epc;

public class Event extends Node {

	public Event() {
	}
	public Event(String id){
		this.id = id;
	}
	public Event(String id, String label){
		this.id = id;
		this.name = label;
		this.type = "basic";
	}

	public Event(String id, String label, String type){
		this.id = id;
		this.name = label;
		this.type = type;
	}
}
