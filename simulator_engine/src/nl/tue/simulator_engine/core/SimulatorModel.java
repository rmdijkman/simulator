package nl.tue.simulator_engine.core;

import java.util.HashMap;
import java.util.Map;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.QueueBased;
import desmoj.core.simulator.TimeSpan;
import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.concepts.Node;
import nl.tue.bpmn.concepts.ResourceType;
import nl.tue.bpmn.concepts.Role;
import nl.tue.bpmn.concepts.Type;
import nl.tue.bpmn.parser.ConditionEvaluator;
import nl.tue.bpmn.parser.DistributionEvaluator;

/**
 * A simulator model for a BPMN model, consisting of:
 * - a queue for each BPMN activity
 * - a queue for each resource type, filled with the number of resources specified for that type
 * - a processing time distribution for each BPMN activity that can be sampled
 * - an arrival rate distribution that can be sampled
 * - a function for instantiating a case of the BPMN process, in which the case attributes receive values
 * The simulator model can be created by first constructing it and subsequently calling init and doInitialSchedules 
 * as is typical in Desmo-J.
 */
public class SimulatorModel extends Model{

	//There is a queue for each activity in the BPMN model. The queue contains cases that are queueing up for the activity.
	private Map<String,ProcessQueue<Case>> activityQueuesByName;
	private Map<ProcessQueue<Case>,String> namesOfActivityQueues;
	
	//There is a queue for each resource type in the BPMN model. The queue contains resources that are queueing up to perfom activities for cases.
	private Map<String,ProcessQueue<Resource>> resourceQueuesByName;
	private Map<ProcessQueue<Resource>,String> namesOfResourceQueues;
	
	//There is a processing time distribution for each activity in the BPMN model.
	private Map<ProcessQueue<Case>,String> processingTimeForActivity;
	
	//There is an interarrival time. The interarrival
	private String interarrivalTimeDistribution;
	
	//There are information attributes that are specified for the process. These get a value when a case is instantiated. 
	private String informationAttributes;
	
	//The BPMN model for which to create the simulation model
	private BPMNModel bpmn;
	
	private static DistributionEvaluator distributionEvaluator = new DistributionEvaluator();	
	
	/**
	 * Creates a new simulator model.
	 * 
	 * @param owner			the owner of the model (typically null).
	 * @param modelName		the name of the model.
	 * @param showInReport	whether the model is shown in the report (typically true)
	 * @param showInTrace	whether the model is shown in execution traces (typically true)
	 */
	public SimulatorModel(Model owner, String modelName, boolean showInReport, boolean showInTrace, BPMNModel bpmn) {
		super(owner, modelName, showInReport, showInTrace);
		
		activityQueuesByName = new HashMap<String,ProcessQueue<Case>>();
		namesOfActivityQueues = new HashMap<ProcessQueue<Case>,String>();
		resourceQueuesByName = new HashMap<String,ProcessQueue<Resource>>();
		namesOfResourceQueues = new HashMap<ProcessQueue<Resource>,String>();
		processingTimeForActivity = new HashMap<ProcessQueue<Case>,String>();
		
		this.bpmn = bpmn;
	}
	
	/**
	 * Returns the queue of cases that are queueing for the activity with the specified label.
	 * 
	 * @param activityLabel	an activity label
	 * @return				a queue of cases
	 */
	public ProcessQueue<Case> queueForActivity(String activityLabel){
		return activityQueuesByName.get(activityLabel);
	}
	
	/**
	 * Returns the queue of resources for the given resource type. 
	 * 
	 * @param resourceTypeName	the name of a resource type
	 * @return					a queue of resources
	 */
	public ProcessQueue<Resource> queueForResourceType(String resourceTypeName){
		return resourceQueuesByName.get(resourceTypeName);
	}
	
	/**
	 * Returns the label of the activity to which the specified queue of cases belongs. 
	 * 
	 * @param queue	a queue of cases
	 * @return		an activity label
	 */
	public String activityLabelForQueue(ProcessQueue<Case> queue){
		return namesOfActivityQueues.get(queue);
	}
	
	/**
	 * Returns the name of the resource type to which the specified queue of resources belongs.
	 * 
	 * @param queue a queue of resources
	 * @return		a resource type name
	 */
	public String resourceTypeNameForQueue(ProcessQueue<Resource> queue){
		return namesOfResourceQueues.get(queue);
	}
	
	/**
	 * Sets the interarrival time distribution. The distribution has the format specified in DistributionGrammar.g4.
	 * (NOTE: normally it is not necessary to call this method, the interarrival time distribution is created via the BPMN model.) 
	 * 
	 * @param interarrivalTimeDistribution	a probability distribution
	 */
	public void setInterarrivalTimeDistribution(String interarrivalTimeDistribution){
		this.interarrivalTimeDistribution = interarrivalTimeDistribution;
	}
	
	/**
	 * Adds a queue for the resource type with the specified name.
	 * (NOTE: normally it is not necessary to call this method, the resource types are created via the BPMN model.)
	 * 
	 * @param resourceTypeName	the name of a resource type
	 */
	public void addResourceType(String resourceTypeName){
		ProcessQueue<Resource> queue = new ProcessQueue<Resource>(this, resourceTypeName, true, true);
		resourceQueuesByName.put(resourceTypeName, queue);
		namesOfResourceQueues.put(queue, resourceTypeName);
	}
	
	/**
	 * Adds a queue and a processing time distribution for the activity with the specified name and the specified distribution.
	 * The distribution has the format specified in DistributionGrammar.g4.
	 * (NOTE: normally it is not necessary to call this method, the queue and distribution is created via the BPMN model.)
	 * 
	 * @param activityName	an activity label
	 * @param distribution	a probability distribution
	 */
	public void addActivity(String activityName, String distribution, String queuestr){
		ProcessQueue<Case> queue = new ProcessQueue<Case>(this, activityName, true, true);
		if(queuestr != null ){
			if(queuestr.equals("LIFO")){
				queue.setQueueStrategy(QueueBased.LIFO);
			}else if(queuestr.equals("RNDM")){
				queue.setQueueStrategy(QueueBased.RANDOM);
			}else if(queuestr.equals("FIFO")){
				queue.setQueueStrategy(QueueBased.FIFO);
			}
		}
		activityQueuesByName.put(activityName, queue);
		namesOfActivityQueues.put(queue, activityName);
		processingTimeForActivity.put(queue, distribution);
	}
	
	/**
	 * Adds the specification of the information attributes to the model.
	 * Information attributes must have the form: "<name>: <distribution>;<name>: <distribution>;...".
	 * Each distribution has the format specified in DistributionGrammar.g4.
	 * (NOTE: normally it is not necessary to call this method, the information attributes are created via the BPMN model.)
	 * 
	 * @param informationAttributes	the specification of the information attributes
	 */
	public void setInformationAttributes(String informationAttributes){
		this.informationAttributes = informationAttributes;
	}
	
	/**
	 * Returns a sample from the interarrival time distribution.
	 * 
	 * @return a sample from a distribution
	 */
	public double interarrivalTimeSample(){
		return Double.parseDouble(distributionEvaluator.evaluate(interarrivalTimeDistribution));
	}
	
	/**
	 * Returns a sample from the processing time distribution of the activity with the given label.
	 * 
	 * @param activityLabel	an activity label
	 * @return a sample from a distribution
	 */
	public double processingTimeSampleFor(String activityLabel){
		return processingTimeSampleFor(activityQueuesByName.get(activityLabel));
	}
	
	/**
	 * Returns a sample from the processing time distribution of the activity to which the given queue belongs.
	 * 
	 * @param activityQueue	a queue of cases
	 * @return a sample from a distribution
	 */
	public double processingTimeSampleFor(ProcessQueue<Case> activityQueue){
		return Double.parseDouble(distributionEvaluator.evaluate(processingTimeForActivity.get(activityQueue)));
	}
	
	/**
	 * 'Instantiates' a case in the process. Does this by creating a value for each of the information attributes that
	 * are specified for the case. The values are added to a condition evaluator, such that conditions can easily be 
	 * evaluated for the case.
	 * 
	 * @return a condition evaluator instantiated with values for each of the information attributes.
	 */
	public ConditionEvaluator instantiateCase(){
		ConditionEvaluator ce = new ConditionEvaluator();
		for (String attribute: informationAttributes.split(";")){
			int splitPoint = attribute.indexOf(':');
			String attributeName = attribute.substring(0, splitPoint).trim();
			String attributeDistribution = attribute.substring(splitPoint + 1);
			ce.setVariableValue(attributeName, distributionEvaluator.evaluate(attributeDistribution));
		}
		return ce;
	}
	
	@Override
	public String description() {
		return "blah";
	}
	
	/**
	 * Instantiates the first model elements, specifically: the resources and the case generator.
	 */
	@Override
	public void doInitialSchedules() {
		//Instantiates the specified number of resources for each resource type
		for (ResourceType rt: bpmn.getResourceTypes()){
			ProcessQueue<Resource> queue = queueForResourceType(rt.getName());
			for (int i = 1; i <= rt.getNumber(); i++){
				queue.insert(new Resource(this, rt.getName() + "-" + i, true, rt));
			}
		}
		//The case generator is instantiated and started
		Generator generator = new Generator(this, "Arrival", true);
		generator.activate(new TimeSpan(0.0));
	}

	/**
	 * Initializes the model by creating queues and probability distributions as explained in the class documentation.
	 */
	@Override
	public void init() {
		//Create a queue for each activity
		//Create the interarrival time for the start event
		for (Node n: bpmn.getNodes()){
			if (n.getType() == Type.Task){
				addActivity(n.getName(), n.getProcessingTimeDistribution(), n.getQueuingStrategy());
			}else if (n.getType() == Type.Event && n.getIncoming().isEmpty()){
				this.setInterarrivalTimeDistribution(n.getInterArrivalTimeDistribution());
			}
		}
		//Create a queue for each resource type
		for (ResourceType rt: bpmn.getResourceTypes()){
			addResourceType(rt.getName());
		}
		//Set the information attribute schema
		setInformationAttributes(bpmn.getInformationAttributes());
	}
}
