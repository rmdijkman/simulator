package nl.tue.tm.is.syntheticgraph;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import net.didion.jwnl.JWNLException;
import nl.tue.tm.is.graph.QueryGraph;
import nl.tue.tm.is.graph.SimpleGraph;
import nl.tue.tm.is.graph.TransitiveGraph;
import nl.tue.tm.is.graph.TwoVertices;

import org.dom4j.*;
import org.dom4j.io.SAXReader;





public class XML {
	
	
	
	
	public static void main(String[] args) throws IOException, JWNLException {
		
		System.out.println("start");
		XML xml = new XML();		
		String prefix = "bpmnmodels/";
		File dir = new File(prefix);
		for(String filename : dir.list()){
			System.out.println(filename);
			SimpleGraph g = xml.BPMNXML2SimpleGraph(prefix, filename);
			System.out.println(g.getVertices());
			for(Integer i:g.getVertices()){
				System.out.println(g.getPredecessorsOfVertex(i));
				System.out.println(g.getSuccessorsOfVertex(i));
			}
			
			;
			System.out.println(g);
		}
		
		System.out.println("end");
		/*for(int i=0; i<100; i++){
		
			CreateRandomGraphs cgp = new CreateRandomGraphs();
			cgp.createGraphs();
			int j=0;
			for(SimpleGraph g: cgp.getResult()){
				//Document document = xml.createDocument(g);
				j++;				
				xml.simpleGraph2XML(g,Matches.prefix1,""+(i*10+j)+".xml");
				//xml.simpleGraph2XML(g,Matches.prefix0,""+(i+1)+".xml");
					  //Element legend = xml.FindElement(document);
					  //System.out.println(i+";"+j);			
			}
		}*/
		
		//System.out.println(xml.XML2SimpleGraph(Matches.prefix1,"1.xml").toString());

	}
	
	/*public List<TransitiveGraph> loadBPMNfiles(XML xml, String prefix){
		List<TransitiveGraph> gList = new ArrayList<TransitiveGraph>();
		File dir = new File(prefix);
		for(String filename : dir.list()){
			//System.out.println(filename);
			TransitiveGraph g = new TransitiveGraph(xml.BPMNXML2SimpleGraph(prefix, filename));
			
			gList.add(g);
		}
		return gList;
	}*/
	public List<SimpleGraph> loadBPMNfiles(XML xml, String prefix){
		List<SimpleGraph> gList = new ArrayList<SimpleGraph>();
		File dir = new File(prefix);
		for(String filename : dir.list()){
			//System.out.println(filename);
			SimpleGraph g = xml.BPMNXML2SimpleGraph(prefix, filename);
			
			gList.add(g);
		}
		return gList;
	}
	
		 /*
		  * Create a XML Document
		  */
	public void simpleGraph2XML(SimpleGraph g, String prefix,String file){
		XML xml = new XML();
		Document document = xml.createDocument4BPMN(g);
		try {
			xml.FileWrite(document,prefix,file);
		} catch (Exception e) {
			
		}
	}
		 
	private Document createDocument(SimpleGraph graph)
	{
		 Document document = DocumentHelper.createDocument();
		  
		 Element simplegraph = document.addElement("simplegraph");
		  //Element vertice;
		 for(Integer id : graph.getVertices()){
			 Element vertice = simplegraph.addElement("vertice");
			 vertice.addAttribute("id", id.toString());
			 Element label = vertice.addElement("label");
			 label.addText(graph.getLabel(id));
			 Element outgoingedges = vertice.addElement("outgoingedges");
			 outgoingedges.addText(graph.getSuccessorsOfVertex(id).toString());
			 Element incomingEdges = vertice.addElement("incomingEdges");
			 incomingEdges.addText(graph.getPredecessorsOfVertex(id).toString());
			 Element type = vertice.addElement("type");
			 type.addText(graph.getAttributeValue(id, SimpleGraph.ATTRIBUTE_NODE_TYPE));
			 
		 }
		  
		 return document;
	}
	
	private Document createDocument4BPMN(SimpleGraph graph)
	{
		 Document document = DocumentHelper.createDocument();
		 
		 Element file = document.addElement("semantic:definitions");
		 //file.addNamespace("semantic","http://yourdnsname/"); 
		 file.addNamespace("semantic", "http://www.omg.org/spec/BPMN/20100524/MODEL");
		 
		 Element process = file.addElement("semantic:process");
		  //Element vertice;
		 for(Integer id : graph.getVertices()){
			 boolean flag = false;
			 String type = graph.getAttributeValue(id, SimpleGraph.ATTRIBUTE_NODE_TYPE);
			 Element object = null;
			 if(type.toLowerCase().contains("boundaryEvent") ){
				 Set<Integer> preSet = graph.getPredecessorsOfVertex(id);
				 
				 if(preSet.size()>0)
				 for(Integer i : graph.getPredecessorsOfVertex(id)){
					 String attachType = graph.getAttributeValue(i, SimpleGraph.ATTRIBUTE_NODE_TYPE);
				 	 if(attachType.contains("task")||attachType.contains("Task")){
				 		 object = process.addElement("semantic:"+type);
						 object.addAttribute("attachedToRef", i.toString());
						 flag = true;
						 break;
				 	 }
					 
				 }
				 if(!flag)
					 object = process.addElement("semantic:intermediateThrowEvent");
			 }else
				 object = process.addElement("semantic:"+type);
			 
			 object.addAttribute("id", id.toString());
			 object.addAttribute("name", graph.getLabel(id));
			 
			 for(Integer i : graph.getSuccessorsOfVertex(id)){
				 Element outgoing = object.addElement("semantic:outgoing");
				 outgoing.addText(id.toString()+"_"+i.toString());
			 }
			 
			 if(!flag)
				 for(Integer i : graph.getPredecessorsOfVertex(id)){
					 
					 Element incoming = object.addElement("semantic:incoming");
					 incoming.addText(i.toString()+"_"+id.toString());
					 
					 Element flow = process.addElement("semantic:sequenceFlow");
					 flow.addAttribute("sourceRef", i.toString());
					 flow.addAttribute("targetRef", id.toString());
					 flow.addAttribute("id", i.toString()+"_"+id.toString());
				 }			 
		 }
		 
		  
		 return document;
	}
	
	public SimpleGraph XML2SimpleGraph(String prefix, String file){
		
		SimpleGraph graph = new SimpleGraph();
		Set<Integer> vertices = new HashSet<Integer>();
		Map<Integer,Set<Integer>> outgoingEdges = new HashMap<Integer,Set<Integer>>();
		Map<Integer,Set<Integer>> incomingEdges = new HashMap<Integer,Set<Integer>>();		
		Map<Integer,String> labels = new HashMap<Integer,String>();	
		Map<Integer,Map<String,String>> attributes = new HashMap<Integer,Map<String,String>>();
		
		File f = new File(prefix+file);
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(f);
			Element simplegraph =  document.getRootElement();
			for(Iterator<Element> i=simplegraph.elementIterator("vertice");i.hasNext();){
				
				Element vertice = i.next();
				int id = Integer.valueOf(vertice.attributeValue("id"));
				vertices.add(id);
				
				labels.put(id, vertice.elementText("label"));
				
				String edges = vertice.elementText("outgoingedges");
				Set<Integer> edgeset = new HashSet<Integer>();
				if(edges.length()>2){					
					edges = edges.substring(1, edges.length()-1);
					if(edges.contains(",")){
						for(String e: edges.split(","))
							edgeset.add(Integer.valueOf(e.trim()));
					}
					else
						edgeset.add(Integer.valueOf(edges.trim()));
				}
							
				outgoingEdges.put(id, edgeset);
				
				
				edges = vertice.elementText("incomingEdges");
				edgeset = new HashSet<Integer>();
				if(edges.length()>3){
					
					edges = edges.substring(1, edges.length()-1);
					if(edges.contains(",")){
						for(String e: edges.split(","))
							edgeset.add(Integer.valueOf(e.trim()));
					}
					else
						{
						edgeset.add(Integer.valueOf(edges.trim()));}
				}
				
				
				incomingEdges.put(id, edgeset);
				
			}
			
			graph = new SimpleGraph(vertices,outgoingEdges,incomingEdges,labels,attributes);
			
		} catch (DocumentException e) {
		}
	
		return graph;
		
	}
	
	
/*private String elementnameStandardize(String elementName){
	
	elementName = elementName.toLowerCase();
	if(elementName.contains("semantics"))
		elementName.substring("semantic:".length(), elementName.length());
	return elementName;
	
}*/

private void edgeRecord(Map<Integer,Set<Integer>> edges, int a, int b){
	Set<Integer> set = new HashSet<Integer>();
	if(edges.containsKey(a)) 
		set = edges.get(a);
	set.add(b);
	edges.put(a, set);
		
}
	
public SimpleGraph BPMNXML2SimpleGraph(String prefix, String file){
		
		SimpleGraph graph = new SimpleGraph();
		Set<Integer> vertices = new HashSet<Integer>();
		Map<Integer,Set<Integer>> outgoingEdges = new HashMap<Integer,Set<Integer>>();
		Map<Integer,Set<Integer>> incomingEdges = new HashMap<Integer,Set<Integer>>();		
		Map<Integer,String> labels = new HashMap<Integer,String>();	
		Map<Integer,Map<String,String>> attributes = new HashMap<Integer,Map<String,String>>();
		graph.setVertexID(0);
		
		
		File f = new File(prefix+file);
		SAXReader reader = new SAXReader();
		Document document;
		
		try {
			document = reader.read(f);
			Element bpmn =  document.getRootElement();
			//bpmn.elements().get(0).toString()
			String processElementName = null;
			if(bpmn.getName().contains("semantic:"))
				processElementName="semantic:process";
			else
				processElementName="process";
			
			for(Iterator<Element> i=bpmn.elementIterator(processElementName);i.hasNext();){
				
				Element process = i.next();
				Map<String,Integer> idMapping = new HashMap <String,Integer>();
				List<Element> flows = new ArrayList<Element>();
				List<Element> boundaryEvents = new ArrayList<Element>();
				for(Iterator<Element> e=process.elementIterator();e.hasNext();){
					
					Element element = e.next();
					//String elementName = elementnameStandardize(element.getName());
					String elementName = element.getName();
					int id = graph.getVertexID();
					//System.out.println(id+";;;"+elementName);
					
					if(elementName.contains("boundaryEvent"))
							boundaryEvents.add(element);
					if(elementName.contains("Event")||elementName.contains("task")||elementName.contains("Task")||elementName.contains("activity")||elementName.contains("Activity")||elementName.contains("Gateway")||elementName.contains("subProcess")||elementName.contains("transaction")||elementName.contains("Transaction")){
						//add node id, label and type;
						vertices.add(id);
						incomingEdges.put(id, new HashSet<Integer>());
						outgoingEdges.put(id, new HashSet<Integer>());
						String name = element.attributeValue("name");
						if(name!=null&&name!="")
							labels.put(id, name);
						else 
							labels.put(id, elementName);
							//labels.put(id, elementName);
						
						Map<String,String> vertexAttributes = attributes.get(id);
						if (vertexAttributes == null || vertexAttributes.isEmpty()){
							vertexAttributes = new HashMap<String,String>();
						}
						vertexAttributes.put(SimpleGraph.ATTRIBUTE_NODE_TYPE,elementName);
						attributes.put(id, vertexAttributes);
						
						idMapping.put(element.attributeValue("id"), id);
						//System.out.println(element.attributeValue("id"));
						graph.setVertexID(id+1);
						
					}
					/*else if(elementName.contains("task")||elementName.contains("activity")){
						
					}
					else if(elementName.contains("gateway")){
						
					}*/
					else if(elementName.contains("Flow")){
						flows.add(element);
						//System.out.println(idMapping
						
					}
						
					
				}
				if(!flows.isEmpty()){
					for(Element element:flows){
						int source = idMapping.get(element.attributeValue("sourceRef"));
						int target = idMapping.get(element.attributeValue("targetRef"));
						edgeRecord(outgoingEdges,source,target);
						edgeRecord(incomingEdges,target,source);
					}
				}
				if(!boundaryEvents.isEmpty()){
					for(Element element:boundaryEvents){
						int source = idMapping.get(element.attributeValue("attachedToRef"));
						int target = idMapping.get(element.attributeValue("id"));
						edgeRecord(outgoingEdges,source,target);
						edgeRecord(incomingEdges,target,source);
					}
				}
				
				
			}
			//System.out.println(attributes);
			//System.out.println(incomingEdges);
			graph = new SimpleGraph(vertices,outgoingEdges,incomingEdges,labels,attributes);
			
		} catch (DocumentException e) {
		}
	
		return graph;
		
	}
	
	
	private void setETypes(String e, int id, Set<Integer> edgeset, Map<TwoVertices,String> eTypes){
		String e1,e2,e3;
		e1 = e.trim();
		e2 = e1.replace('*', ' ').trim();
		e3 = e2.replace('!', ' ').trim();
		
		//System.out.println(e1+";"+e2+";"+e3);
		edgeset.add(Integer.valueOf(e3));
		if(e1.length()>e2.length()&&e2.length()>e3.length())
			eTypes.put(new TwoVertices(id,Integer.valueOf(e3)), "negtransitive");
		else if(e1.length()>e2.length())
			eTypes.put(new TwoVertices(id,Integer.valueOf(e3)), "transitive");
		else if(e2.length()>e3.length())
			eTypes.put(new TwoVertices(id,Integer.valueOf(e3)), "negative");
		else
			eTypes.put(new TwoVertices(id,Integer.valueOf(e3)), "basic");
	}
	
	
public QueryGraph XML2QueryGraph(String prefix, String file){
		
		QueryGraph graph = new QueryGraph();
		Set<Integer> vertices = new HashSet<Integer>();
		Map<Integer,Set<Integer>> outgoingEdges = new HashMap<Integer,Set<Integer>>();
		Map<Integer,Set<Integer>> incomingEdges = new HashMap<Integer,Set<Integer>>();		
		Map<Integer,String> labels = new HashMap<Integer,String>();	
		Map<Integer,Map<String,String>> attributes = new HashMap<Integer,Map<String,String>>();
		
		Map<Integer,String> vTypes = new HashMap<Integer,String>();
		Map<TwoVertices,String> eTypes = new HashMap<TwoVertices,String>();
		
		
		
		File f = new File(prefix+file);
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(f);
			Element simplegraph =  document.getRootElement();
			for(Iterator<Element> i=simplegraph.elementIterator("vertice");i.hasNext();){
				
				
				
				
				Element vertice = i.next();
				int id = Integer.valueOf(vertice.attributeValue("id"));
				vertices.add(id);
				String label = vertice.elementText("label");
				labels.put(id, label);
				
				
				//System.out.println(label);
				
				
				if(label.contains("***")){
					vTypes.put(id, "wildcard");
					//System.out.println("wildcard");
				}
				else
					vTypes.put(id, "basic");
					
				
				String edges = vertice.elementText("outgoingedges");
				//System.out.println(edges);
				Set<Integer> edgeset = new HashSet<Integer>();
				String e1,e2,e3;
				if(edges.length()>2){					
					edges = edges.substring(1, edges.length()-1);
					if(edges.contains(",")){
						for(String e: edges.split(",")){
							setETypes(e,id,edgeset,eTypes); // set types of edges by checking outgoing edges
							//edgeset.add(Integer.valueOf(e.trim()));
						}
					}
					else
						setETypes(edges,id,edgeset,eTypes);
						//edgeset.add(Integer.valueOf(edges.trim()));
				}
							
				outgoingEdges.put(id, edgeset);
				
				
				edges = vertice.elementText("incomingEdges");
				
				edgeset = new HashSet<Integer>();
				if(edges.length()>3){
					edges = edges.substring(1, edges.length()-1);
					if(edges.contains(",")){
						for(String e: edges.split(","))
							edgeset.add(Integer.valueOf(e.trim()));
					}
					else
						{
						edgeset.add(Integer.valueOf(edges.trim()));}
				}
				
				
				incomingEdges.put(id, edgeset);
				
			}
			
			graph = new QueryGraph(vertices,outgoingEdges,incomingEdges,labels,attributes,vTypes,eTypes);
			
		} catch (DocumentException e) {
		}
	
		return graph;
		
	}
	
	
	public TransitiveGraph XML2TransitiveGraph(String prefix, String file){
		
		TransitiveGraph graph = new TransitiveGraph();
		Set<Integer> vertices = new HashSet<Integer>();
		Map<Integer,Set<Integer>> outgoingEdges = new HashMap<Integer,Set<Integer>>();
		Map<Integer,Set<Integer>> incomingEdges = new HashMap<Integer,Set<Integer>>();		
		Map<Integer,String> labels = new HashMap<Integer,String>();	
		Map<Integer,Map<String,String>> attributes = new HashMap<Integer,Map<String,String>>();
		
		Map<Integer,Set<Integer>> outgoingTransitiveEdges = new HashMap<Integer,Set<Integer>>();
		Map<Integer,Set<Integer>> incomingTransitiveEdges = new HashMap<Integer,Set<Integer>>();
		Set<Integer> basicLoopVertices = new HashSet<Integer>();
		Set<Integer> transitiveLoopVertices = new HashSet<Integer>();
		
		
		File f = new File(prefix+file);
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(f);
			Element simplegraph =  document.getRootElement();
			for(Iterator<Element> i=simplegraph.elementIterator("vertice");i.hasNext();){
				
				
				
				
				Element vertice = i.next();
				int id = Integer.valueOf(vertice.attributeValue("id"));
				vertices.add(id);
				
				labels.put(id, vertice.elementText("label"));
				
				String edges = vertice.elementText("outgoingedges");
				Set<Integer> edgeset = new HashSet<Integer>();
				if(edges.length()>2){					
					edges = edges.substring(1, edges.length()-1);
					if(edges.contains(",")){
						for(String e: edges.split(","))
							edgeset.add(Integer.valueOf(e.trim()));
					}
					else
						edgeset.add(Integer.valueOf(edges.trim()));
				}
							
				outgoingEdges.put(id, edgeset);
				
				
				edges = vertice.elementText("incomingEdges");
				edgeset = new HashSet<Integer>();
				if(edges.length()>3){
					
					edges = edges.substring(1, edges.length()-1);
					if(edges.contains(",")){
						for(String e: edges.split(","))
							edgeset.add(Integer.valueOf(e.trim()));
					}
					else
						{
						edgeset.add(Integer.valueOf(edges.trim()));}
				}
				
				
				incomingEdges.put(id, edgeset);
				
			}
			
			for (Integer v = 0; v < vertices.size(); v++){
				List<Integer> queue = new LinkedList<Integer>();
				//List<Integer> visited = new LinkedList<Integer>();
				for(Integer a: outgoingEdges.get(v)){
					if(a==v) basicLoopVertices.add(v);
					else queue.add(a);
				}
					
				Set<Integer> temp = new HashSet<Integer>();
				while (queue.size()>0){
					Integer a = queue.remove(0);
					for(Integer b: outgoingEdges.get(a)){
						if(b==v) transitiveLoopVertices.add(v);
						else if(!temp.contains(b)&&!outgoingEdges.get(v).contains(b))
							queue.add(b);
					}
					temp.addAll(outgoingEdges.get(a));									
				}
				outgoingTransitiveEdges.put(v, temp);
			}
			
			for (Integer v = 0; v < vertices.size(); v++){
				List<Integer> queue = new LinkedList<Integer>();
				for(Integer a: incomingEdges.get(v)){
					if(a==v) basicLoopVertices.add(v);
					else queue.add(a);
				}
				Set<Integer> temp = new HashSet<Integer>();
				while (queue.size()>0){
					Integer a = queue.remove(0);
					for(Integer b: incomingEdges.get(a)){
						if(b==v) transitiveLoopVertices.add(v);
						else if(!temp.contains(b)&&!incomingEdges.get(v).contains(b))
							queue.add(b);
					}
					temp.addAll(incomingEdges.get(a));									
				}
				incomingTransitiveEdges.put(v, temp);
			}
			
			graph = new TransitiveGraph(vertices,outgoingEdges,incomingEdges,outgoingTransitiveEdges,incomingTransitiveEdges,labels,attributes,basicLoopVertices,transitiveLoopVertices);
			
		} catch (DocumentException e) {
		}
	
		return graph;
		
	}
	
	public Element FindElement(Document document)
	{
		 Element root = document.getRootElement();
		 Element legend = null;
		 for(Iterator i=root.elementIterator("legend");i.hasNext();)
		 {
		   legend = (Element)i.next();
		 }
		 return legend;
	}
		 
		 /*
		  * Write a XML file
		  */
		 public void FileWrite(Document document, String prefix, String file) throws IOException
		 {
		  FileWriter out = new FileWriter(prefix+file);
		  document.write(out);
		  out.close();
		 }
		 
		 /*
		  * Write a XML format file
		  */
		 /*public void XMLWrite(Document document) throws IOException
		 {
		  XMLWriter writer = new XMLWriter(new FileWriter(Matches.prefix1+""));
		  writer.write(document);
		  writer.close();
		 }*/

}
