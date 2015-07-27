package nl.tue.tm.is.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mallardsoft.tuple.Pair;

import nl.tue.tm.is.epc.EPC;
import nl.tue.tm.is.maths.MultiSet;
import nl.tue.tm.is.ptnet.PTNet;

/**
 * Efficient implementation of a simple graph: (Vertices, Edges, labels)
 * Only for reading, cannot be modified
 */
public class Graph {
	private Set<Integer> vertices;
	private MultiSet<Pair<Integer,Integer>> edges;
	private Map<Integer,String> labels;
	private Map<Integer,Map<String,String>> attributes;
	private int nextVertexId = 1;
	
	public static final String ATTRIBUTE_LABEL = "label";
	public static final String ATTRIBUTE_ID    = "id";
		
	public Graph(Set<Integer> vertices, MultiSet<Pair<Integer,Integer>> edges, Map<Integer,String> labels, Map<Integer,Map<String,String>> attributes){
		this.vertices = vertices;
		this.edges = edges;
		this.labels = labels;
		this.attributes = attributes;
	}
	
	public Graph(){
		vertices = new HashSet<Integer>();
		edges = new MultiSet<Pair<Integer,Integer>>();		
		labels = new HashMap<Integer,String>();
		attributes = new HashMap<Integer,Map<String,String>>();
	}
		
	/**
	 *  Initializes a simple graph from an EPC.
	 *  
	 */
	public Graph(EPC epc){
		this();
		
		Map<String,Integer> nodeId2vertex = new HashMap<String,Integer>();
		Map<Integer,String> vertex2nodeId = new HashMap<Integer,String>();
		
		for (nl.tue.tm.is.epc.Node n: epc.getNodes()){
			Integer vertexId = addVertex(n.getName().replace((CharSequence)"\\n"," "));
			nodeId2vertex.put(n.getId(), vertexId);
			vertex2nodeId.put(vertexId, n.getId());
			
			vertexId++;
		}
		
		for (Integer v:vertices){
			nl.tue.tm.is.epc.Node n = epc.findNode(vertex2nodeId.get(v));
			
			for (nl.tue.tm.is.epc.Node t: epc.getPost(n)){
				edges.add(new Pair<Integer,Integer>(v,nodeId2vertex.get(t.getId())));
			}
		}		
	}

	/**
	 *  Initializes a simple graph from a Petri net.
	 *  
	 */
	public Graph(PTNet ptnet){
		this();
		
		Map<String,Integer> nodeId2vertex = new HashMap<String,Integer>();
		Map<Integer,String> vertex2nodeId = new HashMap<Integer,String>();

		Set<nl.tue.tm.is.ptnet.Transition> transitions = new HashSet<nl.tue.tm.is.ptnet.Transition>(ptnet.transitions());
		
		for (nl.tue.tm.is.ptnet.Transition t: transitions){
			Integer vertexId = addVertex(t.getName());
			nodeId2vertex.put(t.getId(), vertexId);
			vertex2nodeId.put(vertexId, t.getId());
			for (Map.Entry<String, String> attrib: ptnet.getAttributes(t).entrySet()){
				setAttributeValue(vertexId, attrib.getKey(), attrib.getValue());
			}
		}
		
		for (nl.tue.tm.is.ptnet.Transition t: transitions){
			int corrVertex = nodeId2vertex.get(t.getId());

			if (ptnet.getPost(t).isEmpty() && ptnet.getPre(t).isEmpty()){
				removeVertex(nodeId2vertex.get(t.getId()));
				System.err.println("WARNING: Node (" + t.getId() + ") with name \"" + t.getName() + "\" has no incoming or outgoing arcs. It has been removed.");
			}
			for (nl.tue.tm.is.ptnet.Node p: ptnet.getPost(t)){
				for (nl.tue.tm.is.ptnet.Node t2: ptnet.getPost(p)){
					edges.add(new Pair<Integer,Integer>(corrVertex,nodeId2vertex.get(t2.getId())));
				}
			}
		}		
	}
	

	/**
	 * Note: attributes are not saved
	 * @param f
	 * @throws IOException
	 */
	public void save(File f) throws IOException{
		FileWriter fos = new FileWriter(f);
		BufferedWriter o = new BufferedWriter(fos);
		
		o.write("VERTICES:\n");
		for (Integer v: vertices){
			o.write(Integer.toString(v)+ "\n");
		}
		o.write("EDGES:\n");
		for (Pair<Integer,Integer> e: edges){
			o.write(Integer.toString(Pair.get1(e)) + "\n");
			o.write(Integer.toString(Pair.get2(e)) + "\n");
		}
		o.write("LABELS:\n");
		for (Map.Entry<Integer,String> l: labels.entrySet()){
			o.write(Integer.toString(l.getKey()) + "\n");
			o.write(l.getValue() + "\n");
		}
		
		o.flush(); o.close();		
	}
	
	/**
	 * Note: attributes are not loaded
	 * @param f
	 * @throws IOException
	 */
	public static Graph load(File f) throws IOException{
		Graph result = new Graph();
		
		BufferedReader fr = new BufferedReader(new FileReader(f));
		String l = fr.readLine();	
		int state = 0; //1 = reading vertices, 2 = reading edges, 3 = reading labels
		int maxVertexId = 0;
		while (l != null){
			switch(state){
			case 0:
				if (l.equals("VERTICES:")){
					state = 1;
				}
				break;
			case 1:
				if (l.equals("EDGES:")){
					state = 2;
				}else{
					int vertexId = Integer.parseInt(l);
					if (vertexId > maxVertexId){
						maxVertexId = vertexId;
					}
					result.vertices.add(vertexId);
				}
				break;
			case 2:
				if (l.equals("LABELS:")){
					state = 3;
				}else{
					int fromId = Integer.parseInt(l);
					String toL = fr.readLine();
					int toId = Integer.parseInt(toL);
					result.addEdge(new Pair<Integer,Integer>(fromId,toId));
				}
				break;
			case 3:
				int vId = Integer.parseInt(l);
				String vLabel = fr.readLine();
				result.setLabel(vId, vLabel);
				break;
			}
			l = fr.readLine();		
		}
		result.nextVertexId = maxVertexId + 1;
		fr.close();
		
		return result;
	}
	
	public Set<Integer> getVertices() {
		return vertices;
	}
	public Integer addVertex(String label){
		Integer newVertexId = addVertex();
		setLabel(newVertexId, label);
		return newVertexId;
	}
	public Integer addVertex(){
		assert (nextVertexId < Integer.MAX_VALUE);
		vertices.add(nextVertexId);
		nextVertexId++;
		return (nextVertexId-1);
	}
	public void removeVertex(Integer v){
		vertices.remove(v);
		labels.remove(v);
		attributes.remove(v);
		for (Pair<Integer,Integer> e: incomingEdges(v)){
			removeEdge(e);
		}
		for (Pair<Integer,Integer> e: outgoingEdges(v)){
			removeEdge(e);
		}
	}
	public void removeVertices(Set<Integer> vs){
		for (Integer v: vs){
			removeVertex(v);
		}
	}
	public MultiSet<Pair<Integer,Integer>> getEdges(){
		return edges;
	}
	public Pair<Integer,Integer> addEdge(Integer v1, Integer v2){
		Pair<Integer,Integer> e = new Pair<Integer,Integer>(v1,v2);
		edges.add(e);
		return e;
	}
	public void addEdge(Pair<Integer,Integer> e){
		edges.add(e);
	}
	public void removeEdge(Pair<Integer,Integer> e){
		edges.remove((Object)e);
	}
	public void removeEdges(MultiSet<Pair<Integer,Integer>> es){
		for (Pair<Integer,Integer> e: es){
			edges.remove((Object)e);
		}
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
	/**
	 * Note: does not change the label attribute if it exists
	 * @param vertex
	 * @param label
	 */
	public void setLabel(int vertex, String label){
		labels.put(vertex,label);
	}
	public Integer getVertex(String label){
		for (Integer v: vertices){
			if (labels.get(v).equals(label)){
				return v;
			}
		}
		return Integer.MAX_VALUE;
	}
	public Set<Integer> getPredecessorsOfVertex(Integer vertex) {
		Set<Integer> result = new HashSet<Integer>();
		for (Pair<Integer,Integer> edge: edges){
			if (Pair.get2(edge).equals(vertex)){
				result.add(Pair.get1(edge));
			}
		}
		return result;
	}
	
	public Set<Integer> getSuccessorsOfVertex(Integer vertex) {
		Set<Integer> result = new HashSet<Integer>();
		for (Pair<Integer,Integer> edge: edges){
			if (Pair.get1(edge).equals(vertex)){
				result.add(Pair.get2(edge));
			}
		}
		return result;
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
	
	public MultiSet<Pair<Integer,Integer>> outgoingEdges(Integer vertex){
		MultiSet<Pair<Integer,Integer>> result = new MultiSet<Pair<Integer,Integer>>();
		for (Pair<Integer,Integer> e: edges){
			if (Pair.get1(e).equals(vertex)){
				result.add(e);
			}
		}
		return result;
	}
	
	public MultiSet<Pair<Integer,Integer>> incomingEdges(Integer vertex){
		MultiSet<Pair<Integer,Integer>> result = new MultiSet<Pair<Integer,Integer>>();
		for (Pair<Integer,Integer> e: edges){
			if (Pair.get2(e).equals(vertex)){
				result.add(e);
			}
		}
		return result;
	}
	
	public String toString(){
		String result = "";
		for (Integer i: vertices){
			result += i + "(" + labels.get(i) + ")\n";
		}
		for (Pair<Integer,Integer> e: edges){
			result += "(" + Pair.get1(e) + "," + Pair.get2(e) + ")\n";
		}
		return result;
	}
		
	protected int getNextVertexId() {
		return nextVertexId;
	}

	protected void setNextVertexId(int nextVertexId) {
		this.nextVertexId = nextVertexId;
	}

	public Graph clone(){
		Map<Integer,Map<String,String>> newAttributes = new HashMap<Integer,Map<String,String>>();
		for (Map.Entry<Integer,Map<String,String>> vattrs: attributes.entrySet()){
			newAttributes.put(vattrs.getKey(), new HashMap<String,String>(vattrs.getValue()));
		}
		Graph gclone = new Graph(new HashSet<Integer>(vertices), new MultiSet<Pair<Integer,Integer>>(edges),new HashMap<Integer,String>(labels),newAttributes);
		gclone.setNextVertexId(this.getNextVertexId());
		return gclone;
	}
	
	public Set<Integer> sourceNodes(){
		Set<Integer> sourceNodes = new HashSet<Integer>(vertices); 
		for (Pair<Integer,Integer> e: edges){
			sourceNodes.remove(Pair.get2(e));
		}
		return sourceNodes;
	}
	
	public Set<Integer> sinkNodes(){
		Set<Integer> sinkNodes = new HashSet<Integer>(vertices); 
		for (Pair<Integer,Integer> e: edges){
			sinkNodes.remove(Pair.get1(e));
		}
		return sinkNodes;		
	}
	
	public Set<Integer> nodesInF(MultiSet<Pair<Integer,Integer>> F){
		Set<Integer> result = new HashSet<Integer>();
		for (Pair<Integer,Integer> e: F){
			result.add(Pair.get1(e));
			result.add(Pair.get2(e));
		}
		return result;
	}
	
	public Set<Integer> boundaryNodes(MultiSet<Pair<Integer,Integer>> F){
		Set<Integer> nodesInF = nodesInF(F);
		
		MultiSet<Pair<Integer,Integer>> edgesOutF = new MultiSet<Pair<Integer,Integer>>(edges);
		edgesOutF.removeAll(F);
		Set<Integer> nodesIncidentToFAndFComplement = new HashSet<Integer>(nodesInF);
		nodesIncidentToFAndFComplement.retainAll(nodesInF(edgesOutF));
		
		Set<Integer> sinkNodes = new HashSet<Integer>(nodesInF); 
		Set<Integer> sourceNodes = new HashSet<Integer>(nodesInF);
		for (Pair<Integer,Integer> e: edges){
			sinkNodes.remove(Pair.get1(e));
			sourceNodes.remove(Pair.get2(e));
		}
		
		Set<Integer> result = sinkNodes;
		result.addAll(sourceNodes);
		result.addAll(nodesIncidentToFAndFComplement);
		
		return result;
	}
		
	public Set<Integer> entryNodes(MultiSet<Pair<Integer,Integer>> F){
		Set<Integer> result = new HashSet<Integer>();
		
		for (Integer v: boundaryNodes(F)){
			MultiSet<Pair<Integer,Integer>> incomingEdges = incomingEdges(v);
			incomingEdges.retainAll(F);			
			if (incomingEdges.isEmpty()){
				result.add(v);
			}else if (F.containsAll(outgoingEdges(v))){
				result.add(v);					
			}
		}
		result.removeAll(sinkNodes());
		return result;		
	}
	
	public Set<Integer> exitNodes(MultiSet<Pair<Integer,Integer>> F){
		Set<Integer> result = new HashSet<Integer>();
		
		for (Integer v: boundaryNodes(F)){
			MultiSet<Pair<Integer,Integer>> outgoingEdges = outgoingEdges(v);
			outgoingEdges.retainAll(F);			
			if (outgoingEdges.isEmpty()){
				result.add(v);
			}else if (F.containsAll(incomingEdges(v))){
				result.add(v);					
			}
		}		
		result.removeAll(sourceNodes());
		return result;		
	}
	
	/**
	 * Version 2
	 * Creates a copy of of the split vertex. This copy has edges to/from all
	 * the nodes to/from which the split vertex has edges.
	 * 
	 * @param vertex Vertex to split
	 */
	public void splitVertex(Integer vertex){
		Integer cpV = addVertex(getLabel(vertex) + "_COPY");
		MultiSet<Pair<Integer,Integer>> incoming = incomingEdges(vertex);
		MultiSet<Pair<Integer,Integer>> outgoing = outgoingEdges(vertex);
		for (Pair<Integer,Integer> p: incoming){
			addEdge(Pair.get1(p),cpV);
		}
		for (Pair<Integer,Integer> p: outgoing){
			addEdge(cpV,Pair.get2(p));
		}
	}
	
	/**
	 * Splits the given vertex into two vertices A and B that are connected by an edge (A,B).
	 * A has all incoming edges of the original vertex.
	 * B has all outgoing edges of the original vertex.  
	 * 
	 * @param vertex Vertex to split/
	 */
	/*
	public void splitVertex(Integer vertex){
		Integer A = addVertex(getLabel(vertex) + "_A");
		Integer B = addVertex(getLabel(vertex) + "_B");
		addEdge(A,B);
		MultiSet<Pair<Integer,Integer>> incoming = incomingEdges(vertex);
		MultiSet<Pair<Integer,Integer>> outgoing = outgoingEdges(vertex);
		for (Pair<Integer,Integer> p: incoming){
			addEdge(Pair.get1(p),A);
		}
		for (Pair<Integer,Integer> p: outgoing){
			addEdge(B,Pair.get2(p));
		}
		removeVertex(vertex);
	}
	*/
	
	/**
	 * Returns -1 if the graph is biconnected.
	 * Returns the cut vertex if the graph is not biconnected.
	 *  
	 * @return vertex or -1.
	 */
	public Integer isBiconnected(){
		for (Integer cutVertex: vertices){
			MultiSet<Pair<Integer,Integer>> allEdges = new MultiSet<Pair<Integer,Integer>>(edges);
			
			Set<Integer> nodesToStudy = new HashSet<Integer>();			
			MultiSet<Pair<Integer,Integer>> edgesAdjacentToCut = incomingEdges(cutVertex);
			edgesAdjacentToCut.addAll(outgoingEdges(cutVertex));
			Set<Integer> nodesAdjacentToCut = nodesInF(edgesAdjacentToCut); 
			nodesAdjacentToCut.remove(cutVertex);			
			if (nodesAdjacentToCut.isEmpty()){
				return cutVertex;
			}else{
				nodesToStudy.add(nodesAdjacentToCut.iterator().next());
			}			
			Set<Integer> reachableNodes = new HashSet<Integer>();
			while (!nodesToStudy.isEmpty() && !allEdges.isEmpty()){
				Set<Integer> newNodesToStudy = new HashSet<Integer>();
				for (Integer n: nodesToStudy){
					MultiSet<Pair<Integer,Integer>> edgesToAdd = incomingEdges(n);
					edgesToAdd.addAll(outgoingEdges(n));
					edgesToAdd.retainAll(allEdges);
					allEdges.removeAll(edgesToAdd);
					for (Pair<Integer,Integer> e: edgesToAdd){
						newNodesToStudy.add(Pair.get1(e)); 
						newNodesToStudy.add(Pair.get2(e));
					}
					newNodesToStudy.remove(cutVertex);
					reachableNodes.addAll(newNodesToStudy);
				}
				nodesToStudy = newNodesToStudy;
			}
			if (reachableNodes.size() < vertices.size() - 1){
				return cutVertex;
			}
		}
		return -1;
	}

	public MultiSet<MultiSet<Pair<Integer,Integer>>> separationClasses(MultiSet<Pair<Integer,Integer>> F){
		Set<Integer> boundaryNodes = boundaryNodes(F);
		MultiSet<Pair<Integer,Integer>> allEdges = new MultiSet<Pair<Integer,Integer>>(F);
		MultiSet<MultiSet<Pair<Integer,Integer>>> result = new MultiSet<MultiSet<Pair<Integer,Integer>>>();		
		
		while (!allEdges.isEmpty()){
			MultiSet<Pair<Integer,Integer>> separationClass = new MultiSet<Pair<Integer,Integer>>();
			Pair<Integer,Integer> forEdge = allEdges.remove(0); 
			separationClass.add(forEdge);
			Set<Integer> nodesToStudy = new HashSet<Integer>();
			nodesToStudy.add(Pair.get1(forEdge)); nodesToStudy.add(Pair.get2(forEdge));
			nodesToStudy.removeAll(boundaryNodes);
			while (!allEdges.isEmpty() && !nodesToStudy.isEmpty()){
				Set<Integer> newNodesToStudy = new HashSet<Integer>();
				for (Integer n: nodesToStudy){
					MultiSet<Pair<Integer,Integer>> edgesToAdd = incomingEdges(n);
					edgesToAdd.removeAll(outgoingEdges(n)); //first remove the outgoing edges, such that an edge is not counted twice if it is both incoming and outgoing
					edgesToAdd.addAll(outgoingEdges(n));
					edgesToAdd.retainAll(allEdges);
					separationClass.addAll(edgesToAdd);
					allEdges.removeAll(edgesToAdd);
					for (Pair<Integer,Integer> e: edgesToAdd){
						newNodesToStudy.add(Pair.get1(e)); 
						newNodesToStudy.add(Pair.get2(e));						
					}
				}
				newNodesToStudy.removeAll(boundaryNodes);
				nodesToStudy = newNodesToStudy;
				
			}
			result.add(separationClass);
		}		
		return result;
	}
	
	public boolean isDirected(MultiSet<Pair<Integer,Integer>> F, Integer fromNode, Integer toNode){
		for (Pair<Integer,Integer> e: F){
			if ((Pair.get2(e).equals(fromNode)) || (Pair.get1(e).equals(toNode))){
				return false;
			}
		}
		return true;
	}
	
	public boolean isUnDirected(MultiSet<Pair<Integer,Integer>> F, Integer fromNode, Integer toNode){
		return !isDirected(F, fromNode, toNode) && !isDirected(F, toNode, fromNode); 
	}
	
	/**
	 * Given a set of separation classes with respect to (fromNode, toNode), returns the
	 * maximal pure bond fragment from fromNode to toNode, which can be formed from the
	 * separation classes. Returns null if no maximal pure bond can be formed.
	 * Note that a pure bond is always a fragment.
	 * 
	 * @param separationClasses A set of separation classes.
	 * @param fromNode The start node of the maximal pure bond.
	 * @param toNode The end node of the maximal pure bond.
	 * @return A maximal pure bond or null if none exists.
	 */
	public MultiSet<Pair<Integer,Integer>> maximalPureBond(MultiSet<MultiSet<Pair<Integer,Integer>>> separationClasses, Integer fromNode, Integer toNode){
		MultiSet<Pair<Integer,Integer>> result = new MultiSet<Pair<Integer,Integer>>();
		int separationClassesUsed = 0;
		
		for (MultiSet<Pair<Integer,Integer>> separationClass: separationClasses){
			if (isDirected(separationClass,fromNode,toNode)){
				result.addAll(separationClass);
				separationClassesUsed++;
			}
		}
		if (separationClassesUsed >= 2){
			return result;
		}else{
			return null;
		}
	}
	
	/**
	 * As maximal pure bond, but returns maximal semi-pure bond. 
	 */
	public MultiSet<Pair<Integer,Integer>> maximalSemiPureBond(MultiSet<MultiSet<Pair<Integer,Integer>>> separationClasses, Integer U, Integer V){
		MultiSet<Pair<Integer,Integer>> mspBondWithNoIncomingToU= new MultiSet<Pair<Integer,Integer>>();
		MultiSet<Pair<Integer,Integer>> mspBondWithNoOutgoingFromV= new MultiSet<Pair<Integer,Integer>>();
		int separationClassesUsedFor1 = 0;
		int separationClassesUsedFor2 = 0;
		
		for (MultiSet<Pair<Integer,Integer>> separationClass: separationClasses){
			if (isDirected(separationClass,U,V)){
				mspBondWithNoIncomingToU.addAll(separationClass);
				separationClassesUsedFor1++;
				mspBondWithNoOutgoingFromV.addAll(separationClass);
				separationClassesUsedFor2++;
			}else if (isUnDirected(separationClass,U,V)){
				MultiSet<Pair<Integer,Integer>> incomingToU = incomingEdges(U);
				incomingToU.retainAll(separationClass);
				if (incomingToU.isEmpty()){
					mspBondWithNoIncomingToU.addAll(separationClass);
					separationClassesUsedFor1++;					
				}
				MultiSet<Pair<Integer,Integer>> outgoingFromV = outgoingEdges(V);
				outgoingFromV.retainAll(separationClass);
				if (outgoingFromV.isEmpty()){
					mspBondWithNoOutgoingFromV.addAll(separationClass);
					separationClassesUsedFor2++;										
				}
			}
		}
		if ((separationClassesUsedFor1 >= 2) && isFragment(mspBondWithNoIncomingToU)){
			return mspBondWithNoIncomingToU;
		}else if ((separationClassesUsedFor2 >= 2) && isFragment(mspBondWithNoOutgoingFromV)){
			return mspBondWithNoOutgoingFromV;
		}else{
			return null;
		}
	}
	
	/**
	 * As maximal pure bond, but computes the maximal directed bond.
	 * Implementation is NP-complete. TODO: fix that. 
	 */
	public MultiSet<Pair<Integer,Integer>> maximalDirectedBond(MultiSet<MultiSet<Pair<Integer,Integer>>> separationClasses, Integer U, Integer V){
		MultiSet<MultiSet<Pair<Integer,Integer>>> directedAndUndirectedClasses = new MultiSet<MultiSet<Pair<Integer,Integer>>>();
		for (MultiSet<Pair<Integer,Integer>> sc: separationClasses){
			if (isDirected(sc,U,V) || isUnDirected(sc,U,V)){
				directedAndUndirectedClasses.add(sc);
			}
		}
		return mdbHelper(directedAndUndirectedClasses,U,V);
	}
	private MultiSet<Pair<Integer,Integer>> mdbHelper(MultiSet<MultiSet<Pair<Integer,Integer>>> scs, Integer U, Integer V){
		MultiSet<Pair<Integer,Integer>> union = new MultiSet<Pair<Integer,Integer>>();
		int separationClassesUsed = 0;
		for (MultiSet<Pair<Integer,Integer>> sc: scs){
			separationClassesUsed++;
			union.addAll(sc);
		}
		if (isFragment(union) && (separationClassesUsed >= 2)){
			return union;
		}
		for (MultiSet<Pair<Integer,Integer>> sc: scs){
			MultiSet<MultiSet<Pair<Integer,Integer>>> newScs = new MultiSet<MultiSet<Pair<Integer,Integer>>>(scs);
			newScs.remove(sc);
			MultiSet<Pair<Integer,Integer>> mdb = mdbHelper(newScs,U,V);
			if (mdb != null){
				return mdb;
			}
		}
		return null;
	}
	
	public boolean isFragment(MultiSet<Pair<Integer,Integer>> F){
		Set<Integer> entryNodes = entryNodes(F);
		Set<Integer> exitNodes = exitNodes(F);
		
		if ((entryNodes.size() == 1) && (exitNodes.size() == 1)){
			entryNodes.addAll(exitNodes);
			return entryNodes.equals(boundaryNodes(F));
		}
		
		return false;
	}
	
	public boolean equals(Object o){
		if (!(o instanceof Graph)){
			return false;
		}else{
			Graph g = (Graph) o;			
			if (!g.vertices.equals(this.vertices)){
				return false;
			}
			for (Integer v: g.vertices){
				if (!g.getLabel(v).equals(this.getLabel(v))){
					return false;
				}
			}
			return  g.edges.equals(this.edges) &&
					(g.nextVertexId == this.nextVertexId);
		}
	}
}