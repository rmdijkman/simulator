package nl.tue.tm.is.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.epc.Arc;
import nl.tue.tm.is.epc.EPC;
import nl.tue.tm.is.ptnet.PTNet;

/**
 * Efficient implementation of a simple graph: (Vertices, Edges, labels)
 * Only for reading, cannot be modified
 */
public class QueryGraph extends SimpleGraph{
	
	
	private Map<Integer,String> vTypes;
	private Map<TwoVertices,String> eTypes;
	
	//public static final String ATTRIBUTE_LABEL = "label";

	public QueryGraph(){
		vertices = new HashSet<Integer>();
		outgoingEdges = new HashMap<Integer,Set<Integer>>();
		incomingEdges = new HashMap<Integer,Set<Integer>>();
		labels = new HashMap<Integer,String>();	
		attributes = new HashMap<Integer,Map<String,String>>();
		vertexId = 0;
		setVTypes(new HashMap<Integer,String>());
		setETypes(new HashMap<TwoVertices,String>());	
	}
	
	public QueryGraph(Set<Integer> vertices, Map<Integer,Set<Integer>> outgoingEdges, Map<Integer,Set<Integer>> incomingEdges, 
			Map<Integer,String> labels, Map<Integer,Map<String,String>> attributes, Map<Integer,String> vTypes, Map<TwoVertices,String> eTypes){
		this.vertices = vertices;
		this.outgoingEdges = outgoingEdges;
		this.incomingEdges = incomingEdges;
		this.labels = labels;
		this.attributes = attributes;
		
		this.setVTypes(vTypes);
		this.setETypes(eTypes);
	}
	
	public QueryGraph(EPC epc){
		this();
		
		Map<String,Integer> nodeId2vertex = new HashMap<String,Integer>();
		Map<Integer,String> vertex2nodeId = new HashMap<Integer,String>();
				
		for (nl.tue.tm.is.epc.Node n: epc.getNodes()){
			vertices.add(vertexId);
			labels.put(vertexId,n.getName());
			
			getVTypes().put(vertexId, n.getType());
			
			setAttributeValue(vertexId, ATTRIBUTE_LABEL, n.getName());
			nodeId2vertex.put(n.getId(), vertexId);
			vertex2nodeId.put(vertexId, n.getId());
			
			vertexId++;
		}
		
		for(Arc a: epc.getArcs()){
			
			getETypes().put(new TwoVertices(nodeId2vertex.get(a.getSource().getId()), nodeId2vertex.get(a.getTarget().getId())), a.getType());
			//System.out.println(a);
		}
		
		for (Integer v = 0; v < vertexId; v++){
			nl.tue.tm.is.epc.Node n = epc.findNode(vertex2nodeId.get(v));
			
			Set<Integer> incomingCurrent = new HashSet<Integer>();
			for (nl.tue.tm.is.epc.Node s: epc.getPre(n)){
				incomingCurrent.add(nodeId2vertex.get(s.getId()));
			}
			incomingEdges.put(v, incomingCurrent);
			
			Set<Integer> outgoingCurrent = new HashSet<Integer>();
			for (nl.tue.tm.is.epc.Node t: epc.getPost(n)){
				outgoingCurrent.add(nodeId2vertex.get(t.getId()));
			}
			outgoingEdges.put(v, outgoingCurrent);
		}		
	}

	/*public QueryGraph(PTNet ptnet){
		this();
		
		Map<String,Integer> transId2vertex = new HashMap<String,Integer>();
		Map<Integer,String> vertex2transId = new HashMap<Integer,String>();
		
		for (nl.tue.tm.is.ptnet.Transition t: ptnet.transitions()){
			vertices.add(vertexId);
			if (!t.getName().equals(nl.tue.tm.is.ptnet.Transition.SILENT_LABEL)){
				labels.put(vertexId,t.getName());
				setAttributeValue(vertexId, ATTRIBUTE_LABEL, t.getName());
			}else{
				labels.put(vertexId,"");
				setAttributeValue(vertexId, ATTRIBUTE_LABEL, "");
			}
			transId2vertex.put(t.getId(), vertexId);
			vertex2transId.put(vertexId, t.getId());
			for (Map.Entry<String, String> kv: ptnet.getAttributes(t).entrySet()){
				this.setAttributeValue(vertexId, kv.getKey(), kv.getValue());
			}
			
			vertexId++;
		}
		
		for (nl.tue.tm.is.ptnet.Transition t: ptnet.transitions()){
			int corrVertex = transId2vertex.get(t.getId());

			Set<Integer> outgoingCurrent = new HashSet<Integer>();
			for (nl.tue.tm.is.ptnet.Node n: ptnet.getPost(t)){
				for (nl.tue.tm.is.ptnet.Node n2: ptnet.getPost(n)){
					outgoingCurrent.add(transId2vertex.get(n2.getId()));
				}				
			}
			outgoingEdges.put(corrVertex, outgoingCurrent);

			Set<Integer> incomingCurrent = new HashSet<Integer>();
			for (nl.tue.tm.is.ptnet.Node n: ptnet.getPre(t)){
				for (nl.tue.tm.is.ptnet.Node n2: ptnet.getPre(n)){
					incomingCurrent.add(transId2vertex.get(n2.getId()));
				}				
			}			
			incomingEdges.put(corrVertex, incomingCurrent);
		}		
	}*/
	
	public int addVertex(String label,String type){
		vertices.add(vertexId);
		labels.put(vertexId, label);
		getVTypes().put(vertexId,type);
		incomingEdges.put(vertexId, new HashSet<Integer>());
		outgoingEdges.put(vertexId, new HashSet<Integer>());
		vertexId++;
		return vertexId-1;
	}
	public void addEdge(int from, int to, String type){
		incomingEdges.get(to).add(from);
		outgoingEdges.get(from).add(to);
		getETypes().put(new TwoVertices(from,to), type);
	}
	
	public Set<Integer> getVertices() {
		return vertices;
	}
	public Set<TwoVertices> getEdges(){
		Set<TwoVertices> result = new HashSet<TwoVertices>();
		for (Integer src: vertices){
			for (Integer tgt: outgoingEdges.get(src)){
				result.add(new TwoVertices(src,tgt));
			}
		}
		return result;
	}

	public Set<Integer> postSet(int vertex) {
		return outgoingEdges.get(vertex);
	}

	public Set<Integer> preSet(int vertex) {
		return incomingEdges.get(vertex);
	}

	public LinkedList<String> getLabels(){
		return new LinkedList<String>(labels.values());
	}
	
	public String getLabel(int vertex) {
		return labels.get(vertex);
	}
	public Set<String> getLabels(Set<Integer> nodes){
		Set<String> result = new HashSet<String>();
		
		for (Integer node: nodes){
			result.add(getLabel(node));
		}
		
		return result;
	}
	public Integer getVertex(String label){
		for (Integer v: vertices){
			if (labels.get(v).equals(label)){
				return v;
			}
		}
		return Integer.MAX_VALUE;
	}
	public Map<String,String> getAttributes(Integer vertex){
		return attributes.get(vertex);
	}
	public String getAttributeValue(Integer vertex, String attribute){
		Map<String,String> vertexAttributes = attributes.get(vertex);
		if (vertexAttributes != null){
			return vertexAttributes.get(attribute);
		}else{
			return null;
		}
	}
	public void setAttributeValue(Integer vertex, String attribute, String attributeValue){
		Map<String,String> vertexAttributes = attributes.get(vertex);
		if (vertexAttributes == null){
			vertexAttributes = new HashMap<String,String>();
			attributes.put(vertex, vertexAttributes);
		}
		vertexAttributes.put(attribute,attributeValue);
	}
	
	/**
	 * @return vertices that do not have an incoming edge.
	 */
	public Set<Integer> sourceVertices(){
		Set<Integer> result = new HashSet<Integer>();
		for (Integer i: vertices){
			if (incomingEdges.get(i).isEmpty()){
				result.add(i);
			}
		}
		return result;
	}
	
	/**
	 * @return vertices that do not have an outgoing edge.
	 */	
	public Set<Integer> sinkVertices(){
		Set<Integer> result = new HashSet<Integer>();
		for (Integer i: vertices){
			if (outgoingEdges.get(i).isEmpty()){
				result.add(i);
			}
		}
		return result;
	}
	
	public String toString(){
		String result = "";
		for (Integer i: vertices){
			result += i + "(" + labels.get(i) + ") {";
			for (Iterator<Integer> j = incomingEdges.get(i).iterator(); j.hasNext();){
				int vertex = j.next();
				result += vertex;// + "(" + labels.get(vertex) + ")";
				result += j.hasNext()?",":"";
			}
			result += "} {";
			for (Iterator<Integer> j = outgoingEdges.get(i).iterator(); j.hasNext();){
				int vertex = j.next();
				result += vertex;// + "(" + labels.get(vertex) + ")";
				result += j.hasNext()?",":"";
			}
			result += "}\n";
		}
		return result;
	}
	
	/**
	 * @param vertex Vertex to determine the postSet for
	 * @param silent Set of vertices that should not be considered
	 * @return the postSet(vertex), in which all v \in silent are (recursively) replaced by their postSet(v)
	 */
	public Set<Integer> nonSilentPostSet(Integer vertex, Set<Integer> silent){
		return nonSilentPostSetHelper(vertex, silent, new HashSet<Integer>()); 
	}
	private Set<Integer> nonSilentPostSetHelper(Integer vertex, Set<Integer> silent, Set<Integer> visited){
		Set<Integer> result = new HashSet<Integer>();
		Set<Integer> visitedP = new HashSet<Integer>(visited);
		visitedP.add(vertex);
		
		for (Integer post: postSet(vertex)){
			if (!visited.contains(post)){
				if (silent.contains(post)){
					result.addAll(nonSilentPostSetHelper(post,silent,visitedP));
				}else{
					result.add(post);
				}
			}
		}
		return result;
	}
	
	/**
	 * @param vertex Vertex to determine the preSet for
	 * @param silent Set of vertices that should not be considered
	 * @return the preSet(vertex), in which all v \in silent are (recursively) replaced by their preSet(v)
	 */
	public Set<Integer> nonSilentPreSet(Integer vertex, Set<Integer> silent){
		return nonSilentPreSetHelper(vertex, silent, new HashSet<Integer>()); 
	}
	private Set<Integer> nonSilentPreSetHelper(Integer vertex, Set<Integer> silent, Set<Integer> visited){
		Set<Integer> result = new HashSet<Integer>();
		Set<Integer> visitedP = new HashSet<Integer>(visited);
		visitedP.add(vertex);
		
		for (Integer pre: preSet(vertex)){
			if (!visited.contains(pre)){
				if (silent.contains(pre)){
					result.addAll(nonSilentPreSetHelper(pre,silent,visitedP));
				}else{
					result.add(pre);
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns A COPY OF the graph, such that all vertices from the given set are removed.
	 * All paths (v1,v),(v,v2) via a vertex v from that set are replaced by direct arcs (v1,v2). 
	 * 
	 * Formally: for G = (V, E, l)
	 * return (V-vertices, E', l-(vertices x labels)), where
	 * E' = E - ((V x vertices) U (vertices X V))
	 *    U {(v1, v2)|v \in vertices, (v1,v) \in E \land (v,v2) \in E}    
	 */
	/*public QueryGraph removeVertices(Set<Integer> toRemove){
		Set<Integer> newVertices = new HashSet<Integer>(vertices);
		newVertices.removeAll(toRemove);

		Map<Integer,Set<Integer>> newOutgoingEdges = new HashMap<Integer,Set<Integer>>();
		Map<Integer,Set<Integer>> newIncomingEdges = new HashMap<Integer,Set<Integer>>();;
		Map<Integer,String> newLabels = new HashMap<Integer,String>();
		Map<Integer,Map<String,String>> newAttributes = new HashMap<Integer,Map<String,String>>(); 
		
		for (Integer newVertex: newVertices){
			newOutgoingEdges.put(newVertex, nonSilentPostSet(newVertex,toRemove));
			newIncomingEdges.put(newVertex, nonSilentPreSet(newVertex,toRemove));
			newLabels.put(newVertex, labels.get(newVertex));
			newAttributes.put(newVertex, attributes.get(newVertex));
		}
		
		return new QueryGraph(newVertices, newOutgoingEdges, newIncomingEdges, newLabels, newAttributes);
	}*/
	
	/**
	 * Given subset of vertices of this graph, the method builds the corresponding subgraph.
	 * 
	 * @param _vertices Set of vertices in the subgraph
	 * @return The subgraph
	 */
	/*public QueryGraph subgraph(Set<Integer> _vertices) {
		Set<Integer> newVertices = new HashSet<Integer>(vertices);
		newVertices.removeAll(_vertices);

		Map<Integer,Set<Integer>> newOutgoingEdges = new HashMap<Integer,Set<Integer>>();
		Map<Integer,Set<Integer>> newIncomingEdges = new HashMap<Integer,Set<Integer>>();;
		Map<Integer,String> newLabels = new HashMap<Integer,String>();
		Map<Integer,Map<String,String>> newAttributes = new HashMap<Integer,Map<String,String>>(); 
		
		for (Integer newVertex: newVertices) {
			HashSet<Integer> vertexSet = new HashSet<Integer>();
			for (Integer source: preSet(newVertex))
				if (newVertices.contains(source))
					vertexSet.add(source);
			newIncomingEdges.put(newVertex, vertexSet);
			
			vertexSet = new HashSet<Integer>();
			for (Integer target: postSet(newVertex))
				if (newVertices.contains(target))
					vertexSet.add(target);
			newOutgoingEdges.put(newVertex, vertexSet);
			
			newLabels.put(newVertex, labels.get(newVertex));
			newAttributes.put(newVertex, attributes.get(newVertex));
		}
		
		return new QueryGraph(newVertices, newOutgoingEdges, newIncomingEdges, newLabels, newAttributes);
	}*/
	
	public Set<Integer> getPredecessorsOfVertex(Integer vertex) {
		return this.incomingEdges.get(vertex);
	}
	
	public Set<Integer> getSuccessorsOfVertex(Integer vertex) {
		return this.outgoingEdges.get(vertex);
	}

	public void setVTypes(Map<Integer,String> vTypes) {
		this.vTypes = vTypes;
	}

	public Map<Integer,String> getVTypes() {
		return this.vTypes;
	}

	public void setETypes(Map<TwoVertices,String> eTypes) {
		this.eTypes = eTypes;
	}

	public Map<TwoVertices,String> getETypes() {
		return this.eTypes;
	}

}
