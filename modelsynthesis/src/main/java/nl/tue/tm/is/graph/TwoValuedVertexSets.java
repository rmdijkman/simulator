package nl.tue.tm.is.graph;

import java.util.Set;

public class TwoValuedVertexSets extends TwoVertexSets {
	public double v;
	
	public TwoValuedVertexSets(Set<Integer> s1, Set<Integer> s2, double v) {
		super(s1, s2);
		this.v = v;
	}
	
	public String toString(){
		return "("+s1+","+s2+","+v+")";
	}

	public boolean equals(Object pair2){
		return pair2 instanceof TwoValuedVertexSets?(s1.equals(((TwoValuedVertexSets)pair2).s1) && s2.equals(((TwoValuedVertexSets)pair2).s2)) && (v == ((TwoValuedVertexSets)pair2).v):false;
	}

	public int hashCode(){
		return s1.hashCode() + s2.hashCode();
	}
}
