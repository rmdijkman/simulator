package nl.tue.simulator_engine.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.QueueBased;
import desmoj.core.simulator.TimeSpan;
import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.concepts.Node;
import nl.tue.bpmn.concepts.ResourceType;
import nl.tue.bpmn.concepts.Type;
import nl.tue.bpmn.parser.ConditionEvaluator;
import nl.tue.bpmn.parser.DistributionEvaluator;
import nl.tue.util.Tuple;

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
	
	//The fields that store the result data
	List<Double> sojournTimes;
	List<Double> processingTimes;
	List<Tuple<String, Double>> activityProcessingTimes;
	Map<String, Double> resourceTypeProcessingTime;
	Map<String, Double> resourceTypeIdleTime;
	
	//The warmup time during which no data is collected
	private long warmup;
	
	private static DistributionEvaluator distributionEvaluator = new DistributionEvaluator();	
	
	/**
	 * Creates a new simulator model.
	 * 
	 * @param owner			the owner of the model (typically null).
	 * @param modelName		the name of the model.
	 * @param showInReport	whether the model is shown in the report (typically true)
	 * @param showInTrace	whether the model is shown in execution traces (typically true)
	 */
	public SimulatorModel(Model owner, String modelName, boolean showInReport, boolean showInTrace, BPMNModel bpmn, long warmup) {
		super(owner, modelName, showInReport, showInTrace);
		
		activityQueuesByName = new HashMap<String,ProcessQueue<Case>>();
		namesOfActivityQueues = new HashMap<ProcessQueue<Case>,String>();
		resourceQueuesByName = new HashMap<String,ProcessQueue<Resource>>();
		namesOfResourceQueues = new HashMap<ProcessQueue<Resource>,String>();
		processingTimeForActivity = new HashMap<ProcessQueue<Case>,String>();
		
		this.bpmn = bpmn;
		
		sojournTimes = new ArrayList<Double>();
		processingTimes = new ArrayList<Double>();
		activityProcessingTimes = new ArrayList<Tuple<String, Double>>();
		resourceTypeProcessingTime = new HashMap<String, Double>();
		resourceTypeIdleTime = new HashMap<String, Double>();
		
		this.warmup = warmup;
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
		if (informationAttributes.length() != 0) {
			for (String attribute: informationAttributes.split(";")){
				int splitPoint = attribute.indexOf(':');
				String attributeName = attribute.substring(0, splitPoint).trim();
				String attributeDistribution = attribute.substring(splitPoint + 1);
				ce.setVariableValue(attributeName, distributionEvaluator.evaluate(attributeDistribution));
			}
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
	
	public Map<String,ProcessQueue<Case>> getActivityQueueByName(){
		return this.activityQueuesByName;
	}
	
	public Map<String,ProcessQueue<Resource>> getResourceQueueByName(){
		return this.resourceQueuesByName;
	}
	
	public BPMNModel getBBPMNModel(){
		return bpmn;
	}
	
	public void addSojournTime(double startTime, double endTime){
		if (startTime > warmup){
			double sojournTime = endTime - startTime;
			sojournTimes.add(sojournTime);
		}
	}
	
	public void addActivityProcessingTime(String activity, double startTime, double endTime){
		if (startTime > warmup){
			double processingTime = endTime - startTime;
			activityProcessingTimes.add(new Tuple<String,Double>(activity, processingTime));
		}
	}
	
	public void addResourceTypeProcessingTime(String resourceType, double startTime, double endTime){
		if (startTime > warmup){
			double processingTime = endTime - startTime;
			Double totalTime = resourceTypeProcessingTime.get(resourceType);
			if (totalTime == null){
				resourceTypeProcessingTime.put(resourceType, processingTime);
			}else{
				resourceTypeProcessingTime.put(resourceType, totalTime + processingTime);			
			}
		}
	}

	public void addResourceTypeIdleTime(String resourceType, double startTime, double endTime){
		if (startTime > warmup){
			double idleTime = endTime - startTime;
			Double totalTime = resourceTypeIdleTime.get(resourceType);
			if (totalTime == null){
				resourceTypeIdleTime.put(resourceType, idleTime);
			}else{
				resourceTypeIdleTime.put(resourceType, totalTime + idleTime);			
			}
		}
	}

	public void addProcessingTime(double startTime, double processingTime) {
		if (startTime > warmup){
			processingTimes.add(processingTime);
		}
	}
	
	public double meanSojournTime(){
		double result = 0;
		for (double v: sojournTimes){
			result += v;
		}
		return result/(double)sojournTimes.size();
	}

	public double meanProcessingTime(){
		double result = 0;
		for (double v: processingTimes){
			result += v;
		}
		return result/(double)processingTimes.size();
	}
	
	public Map<String,Double> meanActivityProcessingTimes(){
		Map<String,Double> total = new HashMap<String,Double>();
		Map<String,Double> count = new HashMap<String,Double>();
		for (Tuple<String,Double> v: activityProcessingTimes){
			Double t = total.get(v.e1);
			Double c = count.get(v.e1);
			if (t == null){
				t = 0.0;
				c = 0.0;
			}
			total.put(v.e1, t + v.e2);
			c += 1;
			count.put(v.e1, c);
		}
		Map<String,Double> result = new HashMap<String,Double>();
		for (Entry<String,Double> t: total.entrySet()){
			result.put(t.getKey(), t.getValue()/count.get(t.getKey()));
		}
		return result;
	}
	
	public Map<String,Double> meanActivityWaitingTimes(){
		Map<String,Double> result = new HashMap<String,Double>();		
		for (Node n: bpmn.getNodes()){
			ProcessQueue<Case> q = activityQueuesByName.get(n.getName());
			if (q != null){
				result.put(n.getName(), q.averageWaitTime().getTimeAsDouble());
			}
		}
		return result;		
	}

	public Map<String,Double> meanResourceTypeProcessingTimes(){
		Map<String,Double> total = new HashMap<String,Double>();
		Map<String,Double> count = new HashMap<String,Double>();
		for (Entry<String,Double> v: resourceTypeProcessingTime.entrySet()){
			Double t = total.get(v.getKey());
			Double c = count.get(v.getKey());
			if (t == null){
				t = 0.0;
				c = 0.0;
			}
			total.put(v.getKey(), t + v.getValue());
			c += 1;
			count.put(v.getKey(), c);
		}
		Map<String,Double> result = new HashMap<String,Double>();
		for (Entry<String,Double> t: total.entrySet()){
			result.put(t.getKey(), t.getValue()/count.get(t.getKey()));
		}
		return result;
	}

	public Map<String,Double> meanResourceTypeIdleTimes(){
		Map<String,Double> total = new HashMap<String,Double>();
		Map<String,Double> count = new HashMap<String,Double>();
		for (Entry<String,Double> v: resourceTypeIdleTime.entrySet()){
			Double t = total.get(v.getKey());
			Double c = count.get(v.getKey());
			if (t == null){
				t = 0.0;
				c = 0.0;
			}
			total.put(v.getKey(), t + v.getValue());
			c += 1;
			count.put(v.getKey(), c);
		}
		Map<String,Double> result = new HashMap<String,Double>();
		for (Entry<String,Double> t: total.entrySet()){
			result.put(t.getKey(), t.getValue()/count.get(t.getKey()));
		}
		return result;
	}
}
