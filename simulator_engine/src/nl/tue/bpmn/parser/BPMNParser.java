package nl.tue.bpmn.parser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import nl.tue.bpmn.concepts.*;

public class BPMNParser extends DefaultHandler{
	
	private static final String CASE_ATTRIBUTES_TAG = "case_attributes"; //The label by which case attributes are identified in the pool documentation
	private static final String RESOURCE_TYPES_TAG = "resource_types"; //The label by which resource types are identified in the pool documentation
	private static final String INTERARRIVAL_TIME_TAG = "interarrival_time"; //The label by which interarrival time is identified in the start event documentation
	private static final String PROCESSING_TIME_TAG = "processing_time"; //The label by which processing time is identified in the task documentation
	private static final String QUEUING_STRATEGY_TAG = "queueing_strategy"; //The label by which the queuing strategy is identified in the task documentation
	private static final String RESOURCE_DEPENDENCY_TAG = "resource_dependency"; //The label by which the resource dependency is identified in the task documentation
	private static final String ACTIVITY_DEPENDENCY_TAG = "activity_dependency"; //The label by which the activities in the resource dependency is identified in the task documentation
	
	private XMLErrorHandler errorHandler;
	private boolean hasPool;
	private Role roleBeingParsed;
	private String nodeRefBeingParsed;
	private String documentationBeingParsed;
	private Map<Role, List<String>> role2containedIds;
	private Map<String, Node> id2node;
	private Map<Arc,String> arc2sourceId;
	private Map<Arc,String> arc2targetId;
	private BPMNModel result;
	private List<String> errors;
	private Node nodeBeingParsed;
	private boolean poolBeingParsed;
	private String resourceTypesBeingParsed;
	
	public BPMNParser(){		
		errorHandler = new XMLErrorHandler();
		hasPool = false;
		roleBeingParsed = null;
		nodeRefBeingParsed = null;
		documentationBeingParsed = null;
		role2containedIds = new HashMap<Role, List<String>>();
		id2node = new HashMap<String, Node>();
		arc2sourceId = new HashMap<Arc,String>();
		arc2targetId = new HashMap<Arc,String>();		
		result = new BPMNModel();
		errors = new ArrayList<String>();
	}
	
	/* Elements can have a documentation element. Documentation contains can contain multiple lines.
	 * Each lines should be of the form <property>:<value>
	 * THis method parses it into a property-value map.
	 */
	private Map<String,String> parseDocumentation(String documentation){
		Map<String,String> result = new HashMap<String,String>();
		for (String pv: documentation.split("\\n|\\r")){			
			int splitPoint = pv.indexOf(':');
			if ((splitPoint < 0)||(splitPoint+1 >= pv.length())){
				continue; //skip the line if there is no : or the : is the last character on the line
			}
			String property = pv.substring(0, splitPoint).trim();
			String value = pv.substring(splitPoint + 1).trim();
			result.put(property, value);
		}
		return result;
	}
	
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		// XML qNames to ignore
		if (qName.equals("definitions") || qName.equals("process") || qName.equals("extensionElements") ||
			qName.equals("outgoing") || qName.equals("incoming") || qName.equals("collaboration") || qName.equals("laneSet") || 
			qName.startsWith("signavio:") || qName.startsWith("bpmndi:") || qName.startsWith("omgdi:") || qName.startsWith("omgdc:")){
			return;
		}
		
		String id = null;
		if (qName.equals("startEvent") || qName.equals("endEvent") || qName.equals("task") || qName.equals("exclusiveGateway") || qName.equals("parallelGateway")){
			id = atts.getValue("id");
			if (id == null){
				result = null;
				throw new SAXException("Unexpected error: the model contains an event that has no identifier.");
			}
			if (id2node.get(id) != null){
				result = null;
				throw new SAXException("Unexpected error: the model contains a two nodes with the same identifier.");				
			}
		}
		
		if (qName.equals("participant")){
			if (hasPool){
				errors.add("The model contains more than one pool.");
			}else{
				hasPool = true;
				poolBeingParsed = true;
			}
		}else if (qName.equals("lane")){
			String name = atts.getValue("name");
			if ((name == null) || (name.length() == 0)){
				errors.add("The model contains a lane that has no name.");
			}
			Role role = new Role(name);
			result.addRole(role);
			roleBeingParsed = role;
			//A lane contains flowNodeRef tags that contain the IDs of nodes that are contained in that lane.
			//Create a map that maps each lane (role) to the contained IDs. 
			role2containedIds.put(role, new ArrayList<String>());
		}else if (qName.equals("flowNodeRef")){
			nodeRefBeingParsed = "";
		}else if (qName.equals("startEvent") || qName.equals("endEvent")){
			String name = atts.getValue("name");
			name = (name == null)?"":name;
			nodeBeingParsed = new Node(name, Type.Event, TypeGtw.None);
			result.addNode(nodeBeingParsed);
			id2node.put(id, nodeBeingParsed);
		}else if (qName.equals("task")){
			String name = atts.getValue("name");
			if ((name == null) || (name.length() == 0)){
				errors.add("The model contains a task that has no name.");
			}
			nodeBeingParsed = new Node(name, Type.Task, TypeGtw.None);
			result.addNode(nodeBeingParsed);
			id2node.put(id, nodeBeingParsed);
			//TODO EDIT
		}else if (qName.equals("exclusiveGateway")){
			nodeBeingParsed = new Node("", Type.Gateway, TypeGtw.XSplit); //The gateway must later be classified as a split or join 
			result.addNode(nodeBeingParsed);
			id2node.put(id, nodeBeingParsed);
		}else if (qName.equals("parallelGateway")){
			nodeBeingParsed = new Node("", Type.Gateway, TypeGtw.ParSplit); //The gateway must later be classified as a split or join
			result.addNode(nodeBeingParsed);
			id2node.put(id, nodeBeingParsed);
		}else if (qName.equals("sequenceFlow")){
			//flow (sequenceFlow), read sourceRef, targetRef attributes that contain id, read name that can contain condition
			String name = atts.getValue("name");
			name = ((name == null) || (name.length() == 0))?null:name;
			if (name != null){
				ConditionEvaluator ce = new ConditionEvaluator();
				errors.addAll(ce.validate(name));
			}
			Arc arc = new Arc(name);
			result.addArc(arc);
			String sourceRef = atts.getValue("sourceRef");
			String targetRef = atts.getValue("targetRef");
			if ((sourceRef == null) || (sourceRef.length() == 0) || (targetRef == null) || (targetRef.length() == 0)){
				errors.add("The model contains an arc that is not connected at the beginning or at the end.");
			}
			arc2sourceId.put(arc, sourceRef);
			arc2targetId.put(arc,targetRef);
		}else if (qName.equals("documentation")){
			documentationBeingParsed = "";			
		}else{
			errors.add("The model contains an illegal model element: " + qName + ".");
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length){
		if (nodeRefBeingParsed != null){
			for (int i = start; i < start+length; i++){
				nodeRefBeingParsed += ch[i];
			}
		}else if (documentationBeingParsed != null){			
			for (int i = start; i < start+length; i++){
				documentationBeingParsed += ch[i];
			}
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName){
		if (qName.equals("lane")){
			roleBeingParsed = null;
		}else if (qName.equals("flowNodeRef")){
			//A lane contains flowNodeRef tags that contain the IDs of nodes that are contained in that lane.
			//Create a map that maps each lane (role) to the contained IDs. 
			if (roleBeingParsed != null){
				role2containedIds.get(roleBeingParsed).add(nodeRefBeingParsed.trim());
			}
			nodeRefBeingParsed = null;
		}else if (qName.equals("documentation")){
			Map<String,String> processDocumentation = parseDocumentation(documentationBeingParsed);
			if (poolBeingParsed){
				String caseAttributes = processDocumentation.get(CASE_ATTRIBUTES_TAG);
				if (caseAttributes != null){
					result.setInformationAttributes(caseAttributes);
				}
				resourceTypesBeingParsed = processDocumentation.get(RESOURCE_TYPES_TAG);
			}else if (nodeBeingParsed != null){
				if (nodeBeingParsed.getType() == Type.Event){
					String interArrivalTimeDistribution = processDocumentation.get(INTERARRIVAL_TIME_TAG);
					if (interArrivalTimeDistribution != null){
						List<String> parseErrors = new DistributionEvaluator().validate(interArrivalTimeDistribution);
						if (!parseErrors.isEmpty()){
							errors.add("The interarrival time is specified incorrectly: " + String.join(",", parseErrors));
						}else{
							nodeBeingParsed.setInterArrivalTimeDistribution(interArrivalTimeDistribution);
						}
					}					
				}else if (nodeBeingParsed.getType() == Type.Task){
					String processingTimeDistribution = processDocumentation.get(PROCESSING_TIME_TAG);
					if (processingTimeDistribution != null){
						List<String> parseErrors = new DistributionEvaluator().validate(processingTimeDistribution);
						if (!parseErrors.isEmpty()){
							errors.add("The processing time of task " + nodeBeingParsed.getName() + " is specified incorrectly: " + String.join(",", parseErrors));
						}else{
							nodeBeingParsed.setProcessingTimeDistribution(processingTimeDistribution);
						}
					}
					String queuingStrategy = processDocumentation.get(QUEUING_STRATEGY_TAG);
					if (queuingStrategy != null){
						List<String> parseErrors = new ArrayList<String>();
						if(!queuingStrategy.equals("FIFO") && !queuingStrategy.equals("LIFO") && !queuingStrategy.equals("RNDM")){
							parseErrors.add("Error in resource dependency expression " + queuingStrategy);
						}
						if(!parseErrors.isEmpty()){
							errors.add("The queuing strategy of task " + nodeBeingParsed.getName() + " is specified incorrectly: " + String.join(",", parseErrors));
						}else{
							nodeBeingParsed.setQueuingStrategy(queuingStrategy);
						}
					}
					String resourceDependency = processDocumentation.get(RESOURCE_DEPENDENCY_TAG);
					if (resourceDependency != null){
						List<String> parseErrors = new ArrayList<String>();
						if(!resourceDependency.equals("NONE") && !resourceDependency.equals("SOFD") && !resourceDependency.equals("CASE")){
							parseErrors.add("Error in resource dependency expression " + resourceDependency);
						}
						if(!parseErrors.isEmpty()){
							errors.add("The resource dependency of task " + nodeBeingParsed.getName() + " is specified incorrectly: " + String.join(",", parseErrors));
						}else{
							nodeBeingParsed.setResourceDependency(resourceDependency);
						}
					}
					String activityDependency = processDocumentation.get(ACTIVITY_DEPENDENCY_TAG);
					if (activityDependency != null){
						List<String> parseErrors = new ArrayList<String>();
						String [] acd = activityDependency.split("\\{");
						String [] ace = acd[1].split("\\}");
						String [] act = ace[0].split(",");
						Set <String> actset = new HashSet<String>();
						for(String a : act){
							actset.add(a);
						}
						if(!parseErrors.isEmpty()){
							errors.add("The activity dependency of task " + nodeBeingParsed.getName() + " is specified incorrectly: " + String.join(",", parseErrors));		
						}else{
							nodeBeingParsed.setStractDependency(actset);
						}
					}
					
				}
			}
			documentationBeingParsed = null;			
		}else if (qName.equals("participant")){
			poolBeingParsed = false;
		}else if (qName.equals("startEvent") || qName.equals("endEvent") || qName.equals("task") || qName.equals("exclusiveGateway") || qName.equals("parallelGateway")){
			nodeBeingParsed = null;
		}
	}
	
	private List<String> caseAttributesCorrect(String caseAttributes){
		List<String> parseErrors = new ArrayList<String>();
		DistributionEvaluator de = new DistributionEvaluator();
		for (String attribute: caseAttributes.split(";")){
			int splitPoint = attribute.indexOf(':');
			if (splitPoint < 0){
				parseErrors.add("The following case attribute does not contain ':' '" + attribute + "'");
			}else if (splitPoint+1 >= caseAttributes.length()){
				parseErrors.add("The following case attribute only has a ':' at the end, so no distribution was found for the attribute '" + attribute + "'");				
			}
			String label = attribute.substring(0, splitPoint).trim();
			if (!label.matches("[A-Z][a-zA-Z]*")){
				parseErrors.add("The following case attribute has an incorrectly formatted label (the label should contain only letters and start with a capital letter) '" + attribute + "'");								
			}
			String attributeDistribution = attribute.substring(splitPoint + 1);
			parseErrors.addAll(de.validate(attributeDistribution));
		}
		return parseErrors;
	}
		
	/*	When the XML is read, the elements are created, but arcs are not attached to their source/target and nodes
	 *  are not attached to their lanes. This is to allow lanes/arcs/nodes to be specified in any order in the XML.
	 *  This method connects the nodes/arcs/lanes.
	 *  The method also checks if a gateway is a join or a split and sets the gateway type accordingly.
	 */
	private void connectElements() throws BPMNParseException{
		for (Map.Entry<Role, List<String>> rcid: role2containedIds.entrySet()){
			Role role = rcid.getKey(); 
			for (String cid: rcid.getValue()){
				Node node = id2node.get(cid);
				if (node == null){
					result = null;
					throw new BPMNParseException("Unexpected error: lane '" + role.getName() + "' contains the identifier '" + cid + "' of a node that cannot be found." + ((errors.isEmpty()?"":" This may be caused by the following errors:\n"+errorsToString())));					
				}
				role.addContainedNode(node);
			}
		}
		for (Map.Entry<Arc, String> asid: arc2sourceId.entrySet()){
			String sid = asid.getValue();
			Node node = id2node.get(sid);
			if (node == null){
				result = null;
				throw new BPMNParseException("Unexpected error: an arc contains the identifier of a node that cannot be found."  + ((errors.isEmpty()?"":" This may be caused by the following errors:\n"+errorsToString())));					
			}
			Arc arc = asid.getKey();
			node.addOutgoing(arc);
			arc.setSource(node);
		}
		for (Map.Entry<Arc, String> atid: arc2targetId.entrySet()){
			String tid = atid.getValue();
			Node node = id2node.get(tid);
			if (node == null){
				result = null;
				throw new BPMNParseException("Unexpected error: an arc contains the identifier of a node that cannot be found."  + ((errors.isEmpty()?"":" This may be caused by the following errors:\n"+errorsToString())));					
			}
			Arc arc = atid.getKey();
			node.addIncoming(arc);
			arc.setTarget(node);			
		}
		for (Node node: result.getNodes()){
			if (node.getType() == Type.Gateway){
				if ((node.getIncoming().size() > 1) && (node.getOutgoing().size() == 1)){
					node.setTypeGtw((node.getTypeGtw() == TypeGtw.ParSplit)?TypeGtw.ParJoin:TypeGtw.XJoin);					
				}
			}
		}
		for (Node node: result.getNodes()){
			Set<String> sact =  node.getStractDependency();
			if(sact != null){
				for (String act : sact) {
					boolean eql = false;
					for (Node n : result.getNodes()) {
						if (n.getName().equals(act)) {
							eql = true;
							if (node.getActivityDependency() == null) {
								Set<Node> sn = new HashSet<Node>();
								sn.add(n);
								node.setActivityDependency(sn);
							} else {
								Set<Node> se = node.getActivityDependency();
								se.add(n);
								node.setActivityDependency(se);
							}
							break;
						}
					}
					if (!eql) {
						errors.add("Unexpected error: The following node has an incorrect activity dependency set: " + node.getName()
								+ " where activity : " + act + " does not exist in the model.");
					}
				}
			}
		}
		for(Node node: result.getNodes()){
			//Check if if there is a resource dependency tag there are activities defined
			if(node.getResourceDependency() != null && !node.getResourceDependency().equals("NONE")){
				if(node.getActivityDependency() == null){
					errors.add("The following node has no activities involved in the resource dependency: "+ node.getName());
				}else if(node.getActivityDependency().size() == 0){
					errors.add("The following node has no activities involved in the resource dependency: "+ node.getName());
				}else if(node.getActivityDependency().size() == 1){
					errors.add("The following node has only one activity involved in the resource dependency: "+ node.getName());
				}
			}
			//Check if there is a resource dependency tag if there are activities defined
			if(node.getActivityDependency() != null){
				if(node.getResourceDependency() == null || node.getResourceDependency().equals("NONE")){
					errors.add("The following node has no resource dependency indicated, but has dependent activities defined: " + node.getName());
				}
				//Check if all activities have the resource dependency if they are mentioned
				for(Node n: node.getActivityDependency()){
					if(!(node.getActivityDependency().equals(n.getActivityDependency()))){
						errors.add("The following node is missing activities in the activity dependency set: "+ node.getName());
					}
					if(!(node.getResourceDependency().equals(n.getResourceDependency()))){
						errors.add("The following node has a conflict in resource dependency type with an activity in the activity dependency set: " + node.getName());
					}
				}
			}
			
		}
		if (resourceTypesBeingParsed != null) {
			for (String resourceType: resourceTypesBeingParsed.split(";")){
				int splitPoint = resourceType.indexOf(':');
				if (splitPoint < 0){
					errors.add("The following resource type does not contain ':' '" + resourceType + "'");
				}else if (splitPoint+1 >= resourceType.length()){
					errors.add("The following resource type only has a ':' at the end, so no number of resources was found '" + resourceType + "'");				
				}
				String name = resourceType.substring(0, splitPoint).trim();
				String number = resourceType.substring(splitPoint + 1).trim();			
				ResourceType rt = new ResourceType();
				rt.setName(name);
				try{
					rt.setNumber(Integer.parseInt(number));
				}catch (Exception e){
					errors.add("The following resource type has an incorrectly formatted number of resources '" + resourceType + "'");								
				}
				for (String role: name.split(",")){
					Role r = result.roleByName(role);
					if (r == null){
						errors.add("The following resource type specifies a role '" + role + "' that does not exist '" + resourceType + "'");													
					}else{
						rt.addRole(r);
					}
				}
				if (rt.getRoles().size() == 0){
					errors.add("The following resource type specifies no roles '" + resourceType + "'");												
				}else{
					result.addResourceType(rt);
				}
			}
		}
	}
	
	/* Check the semantics of the BPMN model:
	 * - has at least one lane
	 * - no two roles with the same name
	 * - no two tasks with the same name
	 * - each node must be in a lane 
	 * - a gateway either has one incoming and multiple outgoing arcs, or multiple incoming and one outgoing arcs
	 * - tasks have exactly one incoming and one outgoing arc
	 * - events either have one incoming, one outgoing or one incoming and one outgoing arc
	 * - has exactly one start event
	 */
	private void checkSemantics() throws BPMNParseException{
		if (result.getInformationAttributes() == null){
			result.setInformationAttributes("");
		}else{
			List<String> parseErrors = caseAttributesCorrect(result.getInformationAttributes());
			if (!parseErrors.isEmpty()){
				errors.add("The case attributes are not formatted correctly: " + String.join(",", parseErrors));						
			}			
		}
		if (result.getResourceTypes() == null){
			for (Role r: result.getRoles()) {
				ResourceType rt = new ResourceType();
				rt.setName(r.getName());
				rt.addRole(r);
			    Pattern p = Pattern.compile("\\((\\d+)\\)");
			    Matcher m = p.matcher(r.getName());
			    if (m.find()) {
			    	rt.setNumber(Integer.parseInt(m.group(1)));
			    }else {
			    	rt.setNumber(1);
			    }
			}
		}
		if (result.getRoles().size() == 0){
			errors.add("The model has no roles.");
		}
		Set<String> roleNames = new HashSet<String>();
		for (Role role: result.getRoles()){
			if (roleNames.contains(role.getName())){
				errors.add("There are two roles that have the name '" + role.getName() + "'.");
			}
			roleNames.add(role.getName());
		}
		Set<String> taskNames = new HashSet<String>();
		for (Node node: result.getNodes()){
			if (node.getType() == Type.Task){
				if (taskNames.contains(node.getName())){
					errors.add("There are two tasks that have the name '" + node.getName() + "'.");
				}
				taskNames.add(node.getName());
			}
		}
		for (Node node: result.getNodes()){
			boolean inRole = false;
			for (Role role: result.getRoles()){
				if (role.getContainedNodes().contains(node)){
					inRole = true;
					break;
				}
			}
			if (!inRole){
				errors.add(((node.getType() == Type.Task)?("The task '" + node.getName() + "'"):"There is a node that") + " is not contained in a lane.");
			}
		}
		for (Node node: result.getNodes()){
			if (node.getType() == Type.Task){
				if ((node.getIncoming().size() != 1) || (node.getOutgoing().size() != 1)){
					errors.add("Task '" + node.getName() + "' does not have exactly one incoming and one outgoig arc.");
				}
				if (node.getProcessingTimeDistribution() == null){
					errors.add("Task '" + node.getName() + "' has no processing time specified.");					
				}
			}else if (node.getType() == Type.Gateway){
				if (node.getIncoming().size() == 0){
					errors.add("The model contains a gateway without incoming arcs.");
				}				
				if (node.getOutgoing().size() == 0){
					errors.add("The model contains a gateway without outgoing arcs.");
				}
				if ((node.getIncoming().size() == 1) && (node.getOutgoing().size() == 1)){
					errors.add("The model contains a gateway with only one incoming and one outgoing arc.");
				}
				if ((node.getIncoming().size() > 1) && (node.getOutgoing().size() > 1)){
					errors.add("The model contains a gateway with multiple incoming and outgoing arcs.");
				}
			}else if (node.getType() == Type.Event){
				if ((node.getIncoming().size() == 0) && (node.getOutgoing().size() == 0)){
					errors.add("The model contains an event without incoming or outgoing arcs.");
				}
				if ((node.getIncoming().size() > 1) || (node.getOutgoing().size() > 1)){
					errors.add("The model contains an event with multiple incoming or outgoing arcs.");
				}
			}
		}		
		boolean hasStart = false;
		for (Node node: result.getNodes()){
			if ((node.getType() == Type.Event) && (node.getIncoming().size() == 0)){
				if (hasStart){
					errors.add("The model contains multiple start events.");
				}
				hasStart = true;
				if (node.getInterArrivalTimeDistribution() == null){
					errors.add("The start event has no specified interarrival_time.");					
				}
			}
		}
		if (!hasStart){
			errors.add("The model has no start event.");
		}
	}

	public void parse(String fileName) throws BPMNParseException{
		result = new BPMNModel();
		SAXParserFactory spf = SAXParserFactory.newInstance();
	    spf.setNamespaceAware(true);
	    
	    try{
			Source[] BPMN_XSD = new Source[5];
			BPMN_XSD[0] = new StreamSource(Paths.get(BPMNParser.class.getResource("/nl/tue/bpmn/specification/BPMN20.xsd").toURI()).toString());
			BPMN_XSD[1] = new StreamSource(Paths.get(BPMNParser.class.getResource("/nl/tue/bpmn/specification/BPMNDI.xsd").toURI()).toString());
			BPMN_XSD[2] = new StreamSource(Paths.get(BPMNParser.class.getResource("/nl/tue/bpmn/specification/DC.xsd").toURI()).toString());
			BPMN_XSD[3] = new StreamSource(Paths.get(BPMNParser.class.getResource("/nl/tue/bpmn/specification/DI.xsd").toURI()).toString());
			BPMN_XSD[4] = new StreamSource(Paths.get(BPMNParser.class.getResource("/nl/tue/bpmn/specification/Semantic.xsd").toURI()).toString());

	    	SAXParser saxParser = spf.newSAXParser();
	    	SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	    	Schema schema = schemaFactory.newSchema(BPMN_XSD);
	    	Validator validator = schema.newValidator();
	    	validator.setErrorHandler(errorHandler);
	    	validator.validate(new StreamSource(fileName));

	    	if (!errorHandler.hasErrors()){
	    		XMLReader xmlReader = saxParser.getXMLReader();
	    		xmlReader.setContentHandler(this);
	    		xmlReader.setErrorHandler(errorHandler);
	    		xmlReader.parse(fileName);
	    	}
	    }catch (SAXException e){
	    	result = null;
	    	throw new BPMNParseException("An unexpected error occurred while reading the BPMN file '" + fileName + "'.", e);
	    }catch (URISyntaxException e){
	    	result = null;
	    	throw new BPMNParseException("An unexpected error occurred while reading the BPMN file '" + fileName + "'.", e);
	    }catch (ParserConfigurationException e){
	    	result = null;
	    	throw new BPMNParseException("An unexpected error occurred while reading the BPMN file '" + fileName + "'.", e);
	    }catch (IOException e){
	    	result = null;
	    	throw new BPMNParseException("The BPMN file '" + fileName + "' could not be read.", e);	    	
	    }
	    if (errorHandler.hasErrors()){
	    	result = null;
	    	throw new BPMNParseException("The BPMN file '" + fileName + "' contains unexpected errors:\n" + errorHandler.errorsAsString());
	    }
	    connectElements();
	    checkSemantics();
	    if (!errors.isEmpty()){
	    	result = null;
	    	throw new BPMNParseException("The BPMN Model contains the following error(s):\n" + errorsToString());
	    }
	}
	
	private String errorsToString(){
		String errorString = "";
    	for (Iterator<String> errorIterator = errors.iterator(); errorIterator.hasNext();){
    		errorString += "- " + errorIterator.next() + ((errorIterator.hasNext())?"\n":"");
    	}		
    	return errorString;
	}
	
	public BPMNModel getParsedModel(){
		return result;
	}
}
