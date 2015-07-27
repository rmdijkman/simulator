package nl.tue.tm.is.graph;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.epc.EPC;
import nl.tue.tm.is.ptnet.PTNet;

/**
 * Efficient implementation of a simple graph: (Vertices, Edges, labels)
 * Only for reading, cannot be modified
 */
public class TransitiveGraph extends SimpleGraph{
	//private Set<Integer> vertices;
	//private Map<Integer,Set<Integer>> outgoingEdges;
	//private Map<Integer,Set<Integer>> incomingEdges;
	//private Map<Integer,String> labels;
	//private Map<Integer,Map<String,String>> attributes;
	//private int vertexId;
	
	private Map<Integer,Set<Integer>> outgoingTransitiveEdges;
	private Map<Integer,Set<Integer>> incomingTransitiveEdges;
	private Set<Integer> basicLoopVertices;
	private Set<Integer> transitiveLoopVertices;
	//public static final String ATTRIBUTE_LABEL = "label";
	
	

	public TransitiveGraph(){
		vertices = new HashSet<Integer>();
		setOutgoingEdges(new HashMap<Integer,Set<Integer>>());
		setIncomingEdges(new HashMap<Integer,Set<Integer>>());		
		labels = new HashMap<Integer,String>();	
		setAttributes(new HashMap<Integer,Map<String,String>>());
		vertexId = 0;
		
		setOutgoingTransitiveEdges(new HashMap<Integer,Set<Integer>>());
		incomingTransitiveEdges = new HashMap<Integer,Set<Integer>>();
		setBasicLoopVertices(new HashSet<Integer>());
		setTransitiveLoopVertices(new HashSet<Integer>());
	}
	
	public TransitiveGraph(SimpleGraph g){
		setOutgoingTransitiveEdges(new HashMap<Integer,Set<Integer>>());
		incomingTransitiveEdges = new HashMap<Integer,Set<Integer>>();
		setBasicLoopVertices(new HashSet<Integer>());
		setTransitiveLoopVertices(new HashSet<Integer>());
		
		
		vertices = g.vertices;
		setOutgoingEdges(g.outgoingEdges);
		setIncomingEdges(g.incomingEdges);		
		labels = g.labels;	
		setAttributes(g.attributes);
		vertexId = 0;
		
		for(Integer i : vertices){
			List<Integer> outqueue = new ArrayList<Integer>();
			Set<Integer> out = new HashSet<Integer>();
			if(outgoingEdges.containsKey(i)&&outgoingEdges.get(i)!=null){
				out = new HashSet<Integer>(this.outgoingEdges.get(i));
				for(Integer i1 : out){
					outqueue.add(i1);
					//outqueue.addAll(this.outgoingEdges.get(i1));
				}
				//outqueue.removeAll(out);
				//this.outgoingTransitiveEdges.put(i, out);
				while(!outqueue.isEmpty()){
					int j = outqueue.remove(0);
					if(outgoingEdges.containsKey(j)&&outgoingEdges.get(j)!=null)
						for(Integer k : this.outgoingEdges.get(j)){
							if(!out.contains(k)){
								out.add(k);
								outqueue.add(k);
							}
						}
				}
				outgoingTransitiveEdges.put(i,out);
			}
			
			
			
			
			List<Integer> inqueue = new ArrayList<Integer>();
			Set<Integer> in = new HashSet<Integer>();
			if(incomingEdges.containsKey(i)&&incomingEdges.get(i)!=null){
				in = new HashSet<Integer>(this.incomingEdges.get(i));
				for(Integer i1 : in){
					inqueue.add(i1);
					//outqueue.addAll(this.outgoingEdges.get(i1));
				}
				//outqueue.removeAll(out);
				//this.outgoingTransitiveEdges.put(i, out);
				while(!inqueue.isEmpty()){
					int j = inqueue.remove(0);
					if(incomingEdges.containsKey(j)&&incomingEdges.get(j)!=null)
						for(Integer k : this.incomingEdges.get(j)){
							if(!in.contains(k)){
								in.add(k);
								inqueue.add(k);
							}
						}
				}
				incomingTransitiveEdges.put(i,in);
			}
			
			if(incomingEdges.containsKey(i)&&incomingEdges.get(i)!=null)
				if(outgoingEdges.containsKey(i)&&outgoingEdges.get(i)!=null){
						if(this.incomingEdges.get(i).contains(i)||this.outgoingEdges.get(i).contains(i))
							basicLoopVertices.add(i);
						else if(in.contains(i)||out.contains(i))
							transitiveLoopVertices.add(i);
				}
			
		}
		
	}
	
	
	public TransitiveGraph(Set<Integer> vertices, Map<Integer,Set<Integer>> outgoingEdges, Map<Integer,Set<Integer>> incomingEdges,
			Map<Integer,Set<Integer>> outgoingTransitiveEdges, Map<Integer,Set<Integer>> incomingTransitiveEdges, 
			Map<Integer,String> labels, Map<Integer,Map<String,String>> attributes, Set<Integer> basicLoopVertices, Set<Integer> transitiveLoopVertices){
		
		this.vertices = vertices;
		this.setOutgoingEdges(outgoingEdges);
		this.setIncomingEdges(incomingEdges);
		this.labels = labels;
		this.setAttributes(attributes);
		
		this.setOutgoingTransitiveEdges(outgoingTransitiveEdges);
		this.incomingTransitiveEdges = incomingTransitiveEdges;
		this.setBasicLoopVertices(basicLoopVertices);
		this.setTransitiveLoopVertices(transitiveLoopVertices);
	}
	
	public TransitiveGraph(EPC epc){
		this();
		
		Map<String,Integer> nodeId2vertex = new HashMap<String,Integer>();
		Map<Integer,String> vertex2nodeId = new HashMap<Integer,String>();
				
		for (nl.tue.tm.is.epc.Node n: epc.getNodes()){
			
			vertices.add(vertexId);
			labels.put(vertexId,n.getName());
			setAttributeValue(vertexId, ATTRIBUTE_LABEL, n.getName());
			nodeId2vertex.put(n.getId(), vertexId);
			vertex2nodeId.put(vertexId, n.getId());
			
			
			if(epc.getEvents().contains(n)) 
				setAttributeValue(vertexId, ATTRIBUTE_NODE_TYPE, n.getType()+"Event");
			else if (epc.getFunctions().contains(n)) 
				setAttributeValue(vertexId, ATTRIBUTE_NODE_TYPE, n.getType()+"Function");
			else  
				setAttributeValue(vertexId, ATTRIBUTE_NODE_TYPE, n.getType()+n.getName()+"Gateway");
			

			vertexId++;
		}
		
		for (Integer v = 0; v < vertexId; v++){
			nl.tue.tm.is.epc.Node n = epc.findNode(vertex2nodeId.get(v));
			
			Set<Integer> incomingCurrent = new HashSet<Integer>();
			for (nl.tue.tm.is.epc.Node s: epc.getPre(n)){
				incomingCurrent.add(nodeId2vertex.get(s.getId()));
			}
			getIncomingEdges().put(v, incomingCurrent);
			
			Set<Integer> outgoingCurrent = new HashSet<Integer>();
			for (nl.tue.tm.is.epc.Node t: epc.getPost(n)){
				outgoingCurrent.add(nodeId2vertex.get(t.getId()));
			}
			getOutgoingEdges().put(v, outgoingCurrent);
		}		
		
		for (Integer v = 0; v < vertexId; v++){
			List<Integer> queue = new LinkedList<Integer>();
			//List<Integer> visited = new LinkedList<Integer>();
			for(Integer a: getOutgoingEdges().get(v)){
				if(a==v) getBasicLoopVertices().add(v);
				else queue.add(a);
			}
				
			Set<Integer> temp = new HashSet<Integer>();
			while (queue.size()>0){
				Integer a = queue.remove(0);
				for(Integer b: getOutgoingEdges().get(a)){
					if(b==v) getTransitiveLoopVertices().add(v);
					else if(!temp.contains(b)&&!getOutgoingEdges().get(v).contains(b))
						queue.add(b);
				}
				temp.addAll(getOutgoingEdges().get(a));									
			}
			getOutgoingTransitiveEdges().put(v, temp);
		}
		
		for (Integer v = 0; v < vertexId; v++){
			List<Integer> queue = new LinkedList<Integer>();
			for(Integer a: getIncomingEdges().get(v)){
				if(a==v) getBasicLoopVertices().add(v);
				else queue.add(a);
			}
			Set<Integer> temp = new HashSet<Integer>();
			while (queue.size()>0){
				Integer a = queue.remove(0);
				for(Integer b: getIncomingEdges().get(a)){
					if(b==v) getTransitiveLoopVertices().add(v);
					else if(!temp.contains(b)&&!getIncomingEdges().get(v).contains(b))
						queue.add(b);
				}
				temp.addAll(getIncomingEdges().get(a));									
			}
			getIncomingTransitiveEdges().put(v, temp);
		}
		
	}

	public TransitiveGraph(PTNet ptnet){
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
			getOutgoingEdges().put(corrVertex, outgoingCurrent);

			Set<Integer> incomingCurrent = new HashSet<Integer>();
			for (nl.tue.tm.is.ptnet.Node n: ptnet.getPre(t)){
				for (nl.tue.tm.is.ptnet.Node n2: ptnet.getPre(n)){
					incomingCurrent.add(transId2vertex.get(n2.getId()));
				}				
			}			
			getIncomingEdges().put(corrVertex, incomingCurrent);
		}		
	}
	
	public int addVertex(String label){
		vertices.add(vertexId);
		labels.put(vertexId, label);
		getIncomingEdges().put(vertexId, new HashSet<Integer>());
		getOutgoingEdges().put(vertexId, new HashSet<Integer>());
		vertexId++;
		return vertexId-1;
	}
	public void addEdge(int from, int to){
		getIncomingEdges().get(to).add(from);
		getOutgoingEdges().get(from).add(to);
	}
	
	public Set<Integer> getVertices() {
		return vertices;
	}
	public Set<TwoVertices> getEdges(){
		Set<TwoVertices> result = new HashSet<TwoVertices>();
		for (Integer src: vertices){
			if(getOutgoingEdges().containsKey(src)&&getOutgoingEdges().get(src)!=null){
				for (Integer tgt: getOutgoingEdges().get(src)){
					result.add(new TwoVertices(src,tgt));
				}
			}
			
		}
		return result;
	}

	public Set<Integer> postSet(int vertex) {
		return getAllSuccessorsOfVertex(vertex);
	}

	public Set<Integer> preSet(int vertex) {
		return getAllPredecessorsOfVertex(vertex);
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
		Map<String,String> vertexAttributes = getAttributes().get(vertex);
		if (vertexAttributes != null){
			return vertexAttributes.get(attribute);
		}else{
			return null;
		}
	}
	public void setAttributeValue(Integer vertex, String attribute, String attributeValue){
		Map<String,String> vertexAttributes = getAttributes().get(vertex);
		if (vertexAttributes == null){
			vertexAttributes = new HashMap<String,String>();
			getAttributes().put(vertex, vertexAttributes);
		}
		vertexAttributes.put(attribute,attributeValue);
	}
	
	/**
	 * @return vertices that do not have an incoming edge.
	 */
	public Set<Integer> sourceVertices(){
		Set<Integer> result = new HashSet<Integer>();
		for (Integer i: vertices){
			if (getIncomingEdges().get(i).isEmpty()){
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
			if (getOutgoingEdges().get(i).isEmpty()){
				result.add(i);
			}
		}
		return result;
	}
	
	public String toString(){
		String result = "";
		for (Integer i: vertices){
			result += i + "(" + labels.get(i) + ") {";
			for (Iterator<Integer> j = getIncomingEdges().get(i).iterator(); j.hasNext();){
				int vertex = j.next();
				result += vertex;// + "(" + labels.get(vertex) + ")";
				result += j.hasNext()?",":"";
			}
			result += "} {";
			for (Iterator<Integer> j = getOutgoingEdges().get(i).iterator(); j.hasNext();){
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
		
		for (Integer post: this.getBasicSuccessorsOfVertex(vertex)){
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
		
		for (Integer pre: this.getBasicPredecessorsOfVertex(vertex)){
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
	
	public int getShortestPathLength(Integer source, Integer target, int pathLength, int currentLength){
		
		//int currentLength = 10000;
		//if(source==target) return true;
		//visited.add(source);
		for(Integer node : this.getBasicSuccessorsOfVertex(source)){
			if(target==node && pathLength<currentLength){
				currentLength = pathLength;
			}
			if(pathLength<currentLength){
				pathLength++;
				getShortestPathLength(node, target, pathLength, currentLength);
			}
			
		}
		
		return currentLength;
		
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
	public TransitiveGraph removeVertices(Set<Integer> toRemove){
		Set<Integer> newVertices = new HashSet<Integer>(this.vertices);
		newVertices.removeAll(toRemove);

		Map<Integer,Set<Integer>> newOutgoingEdges = new HashMap<Integer,Set<Integer>>();
		Map<Integer,Set<Integer>> newIncomingEdges = new HashMap<Integer,Set<Integer>>();
		Map<Integer,String> newLabels = new HashMap<Integer,String>();
		Map<Integer,Map<String,String>> newAttributes = new HashMap<Integer,Map<String,String>>(); 
		
		for (Integer newVertex: newVertices){
			newOutgoingEdges.put(newVertex, nonSilentPostSet(newVertex,toRemove));
			newIncomingEdges.put(newVertex, nonSilentPreSet(newVertex,toRemove));
			newLabels.put(newVertex, labels.get(newVertex));
			newAttributes.put(newVertex, getAttributes().get(newVertex));
		}
		
		for (Integer node: this.vertices){
			if(newVertices.contains(node)){
				this.outgoingTransitiveEdges.get(node).removeAll(toRemove);
				this.incomingTransitiveEdges.get(node).removeAll(toRemove);
			}else{
				this.outgoingTransitiveEdges.remove(node);
				this.outgoingTransitiveEdges.remove(node);
			}
				
		}
		
		return new TransitiveGraph(newVertices, newOutgoingEdges, newIncomingEdges, this.outgoingTransitiveEdges, this.incomingTransitiveEdges, newLabels, newAttributes, newVertices, newVertices);
	}
	
	public boolean existPath(Integer source, Integer target){
		
		//if(source==target) return true;
		
		List<Integer> queue = new LinkedList<Integer>();
		Set<Integer> visited = new HashSet<Integer>();
		queue.add(source);
		while(!queue.isEmpty()){
			Integer a = queue.remove(0);
			visited.add(a);
			Set<Integer> targets = new HashSet<Integer>(this.getBasicSuccessorsOfVertex(a));
			if(targets.contains(target)) return true;
			else{
				targets.removeAll(visited);
				for(Integer b: targets){
					queue.add(b);
				}
			}
		}
		return false;
		
	}
	
	/**
	 * Given subset of vertices of this graph, the method builds the corresponding subgraph.
	 * 
	 * @param _vertices Set of vertices in the subgraph
	 * @return The subgraph
	 */
	/* TransitiveGraph subgraph(Set<Integer> _vertices) {
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
			newAttributes.put(newVertex, getAttributes().get(newVertex));
		}
		
		return new TransitiveGraph(newVertices, newOutgoingEdges, newIncomingEdges, newLabels, newAttributes);
	}*/
	
	public Set<Integer> getBasicPredecessorsOfVertex(Integer vertex) {
		return getIncomingEdges().get(vertex);
	}
	
	public Set<Integer> getBasicSuccessorsOfVertex(Integer vertex) {
		return getOutgoingEdges().get(vertex);
	}
	
	public Set<Integer> getTransitivePredecessorsOfVertex(Integer vertex) {
		return getIncomingTransitiveEdges().get(vertex);
	}
	
	public Set<Integer> getTransitiveSuccessorsOfVertex(Integer vertex) {
		return getOutgoingTransitiveEdges().get(vertex);
	}
	
	public Set<Integer> getAllPredecessorsOfVertex(Integer vertex) {
		Set<Integer> temp = new HashSet<Integer>(getBasicPredecessorsOfVertex(vertex));
		temp.addAll(getTransitivePredecessorsOfVertex(vertex));
		return temp;
	}
	
	public Set<Integer> getAllSuccessorsOfVertex(Integer vertex) {
		Set<Integer> temp = new HashSet<Integer>(getBasicSuccessorsOfVertex(vertex));
		temp.addAll(getTransitiveSuccessorsOfVertex(vertex));
		return temp;
	}
	
	public void setAttributes(Map<Integer,Map<String,String>> attributes) {
		this.attributes = attributes;
	}

	public Map<Integer,Map<String,String>> getAttributes() {
		return this.attributes;
	}

	public void setIncomingTransitiveEdges(Map<Integer,Set<Integer>> incomingTransitiveEdges) {
		this.incomingTransitiveEdges = incomingTransitiveEdges;
	}

	public Map<Integer,Set<Integer>> getIncomingTransitiveEdges() {
		return this.incomingTransitiveEdges;
	}

	public void setOutgoingTransitiveEdges(Map<Integer,Set<Integer>> outgoingTransitiveEdges) {
		this.outgoingTransitiveEdges = outgoingTransitiveEdges;
	}

	public Map<Integer,Set<Integer>> getOutgoingTransitiveEdges() {
		return this.outgoingTransitiveEdges;
	}

	public void setBasicLoopVertices(Set<Integer> loopVertices) {
		this.basicLoopVertices = loopVertices;
	}

	public Set<Integer> getBasicLoopVertices() {
		return this.basicLoopVertices;
	}
	
	public void setTransitiveLoopVertices(Set<Integer> loopVertices) {
		this.transitiveLoopVertices = loopVertices;
	}

	public Set<Integer> getTransitiveLoopVertices() {
		return this.transitiveLoopVertices;
	}

	public void setOutgoingEdges(Map<Integer,Set<Integer>> outgoingEdges) {
		this.outgoingEdges = outgoingEdges;
	}

	public Map<Integer,Set<Integer>> getOutgoingEdges() {
		return this.outgoingEdges;
	}

	public void setIncomingEdges(Map<Integer,Set<Integer>> incomingEdges) {
		this.incomingEdges = incomingEdges;
	}

	public Map<Integer,Set<Integer>> getIncomingEdges() {
		return this.incomingEdges;
	}

}
