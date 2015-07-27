package nl.tue.tm.is.syntheticgraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;



public class Pattern4MultiNodes {
	
	Set<HashSet<Integer>> basicConnections;
	Set<HashSet<Integer>> transitiveConnections;
	Set<Integer> separateNodes;
	Set<Integer> terminativeNodes;
	
	String gateway;
	List<Integer> nodeList;
	//Hashtable<Integer,HashSet<Integer>> Connection2Nodes;
	//Hashtable<HashSet<Integer>,Integer> Nodes2Connection;
	
	
	Pattern4MultiNodes(){
		basicConnections = new HashSet<HashSet<Integer>>();
		transitiveConnections = new HashSet<HashSet<Integer>>();
		separateNodes = new HashSet<Integer>();
		terminativeNodes = new HashSet<Integer>();
		gateway = "";
		nodeList = new ArrayList<Integer>();
		//Connection2Nodes = new Hashtable<Integer,HashSet<Integer>>();
		//Nodes2Connection = new Hashtable<HashSet<Integer>,Integer>();
	}
	
	public String toString(){
		String string = "gateway: "+gateway+", nodeList: "+nodeList+"...";
		string += "basic connections: ";
		for(HashSet<Integer> set:basicConnections){
			string += set.toString();
			string += "," ;
		}
		//string += "\n" ;
		
		string += "transitive connections: ";
		for(HashSet<Integer> set:transitiveConnections){
			string += set.toString();
			string +=",";
		}
		//string += "\n" ;
		
		string += "separate nodes:" ;
		string += separateNodes.toString();
		//string += "\n" ;
		
		string += "terminative nodes:" ;
		string += terminativeNodes.toString();
		//string += "\n" ;
		string += "\n";
		
		return string;
	}
}
