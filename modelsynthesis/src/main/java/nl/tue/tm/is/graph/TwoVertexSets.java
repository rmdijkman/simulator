package nl.tue.tm.is.graph;

import java.util.Set;

public class TwoVertexSets {
	public Set<Integer> s1;
	public Set<Integer> s2;

	public TwoVertexSets(Set<Integer> s1, Set<Integer> s2) {
		this.s1 = s1;
		this.s2 = s2;
	}

	public String toString(){
		return "("+s1+","+s2+")";
	}

	public boolean equals(Object pair2){
		return pair2 instanceof TwoVertexSets?(s1.equals(((TwoVertexSets)pair2).s1) && s2.equals(((TwoVertexSets)pair2).s2)):false;
	}

	public int hashCode(){
		return s1.hashCode() + s2.hashCode();
	}

}
