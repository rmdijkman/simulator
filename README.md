# Simulator

An implementation of an advanced business process simulation engine. The simulation engine can take BPMN models as they are, for example, produced in [Signavio](https://www.signavio.com/) and simulate them. It contains some advanced functionality that existing BPMN simulation engines do not have. In particular:
* case data with distributions on their value;
* the ability to base an XOR-split on case data values;
* advanced resource simulation patterns;
* various queueing policies; and
* simulation parameters, in particular: number of replications and warm-up time.

# Binary

The binary of the simulator is downloadable from [the build folder in the simulator_gui project](https://github.com/rmdijkman/simulator/raw/master/simulator_gui/build/Simulator.jar). This binary takes the form of an executable jar.

The program should be self-explanatory. Several test models are provided as part of the simulator_engine project. A comprehensive test model, for example, is the [BPMN Example Model](https://raw.githubusercontent.com/rmdijkman/simulator/master/simulator_engine/resources/tests/Example%20Process%20BPMN.bpmn). The models are made in [Signavio](https://www.signavio.com/) and can be imported there for viewing. Arrival distributions, processing time distributions, resources, etc. can be specified as part of the documentation of tasks and pools in the BPMN model. The syntax for doing this is explained below.

# Syntax

The BPMN Models need to be enhanced in order to make them suitable for simulation. In order to do so the documentation section of many elements will be used to specify properties of the model in order to build the simulation model. On the pool level of a process the following need to be specified:
* case_attributes: [Name]: D(var) ; ... ; [Name]: D(var)
  For each case attribute the name of the attribute and the distribution needs to be defined. Distributions can be denoted by the normal distribution: N(mu,sigma) or the exponential distribution: exp(lambda). Also a categorical distribution is supported by: [{item, n%}, ... ,{item,n%}]. Case attributes are split by a semicolon. 
* resource_types: [Role]: #n ; ... ; [Role]: #n
  For each resource type the amount of resources of that type need to be specified in an integer. Furthermore combined types can be defined by [Role1, ... ,RoleN] for the Role field, the resource has than all roles and will serve Role1 first and RoleN last in the execution of the process. A type of resource can be up to all roles defined in the process model combined. If a role does not exist in the process there it cannot be associated to any resource type.

In the model several elements need specifications in order to make the model suitable for simulation. These are the start event, the activities and data-based XOR choices. For the start event an inter-arrival time distribution should be provided in the properties of the start event:
* interarrival_time: D(var)
  For the start event a distribution is denoted by either the normal distribution: N(mu,sigma) or the exponential distribution: exp(lambda).

For the activities in the model the following parameters can be provided in the properties, only the processing time per activity is mandatory to provide:
* processing_time: D(var)
  For each activity in the model the processing time should be provided. The distribution can be denoted by either the normal distribution: N(mu,sigma) or the exponential distribution: exp(lambda).
* queueing_strategy: [Strategy]
  Each activity has a queue in the simulation model, the queueing strategy of this queue is by default First In, First Out (FIFO). If another queueing principle is applied this can be specified per activity. The queueing principle can have the values FIFO, LIFO or RNDM. LIFO stands for Last In, First Out and RNDM is a random order of sorting the cases in a queue for an activity. 
* resource_dependency: [Dependency]
  Two or more activities can be involved in a resource dependency, this can be either Case Handling (CASE) or Separations of Duties (SOFD). Each activity has no resource dependency by default (NONE). All activities involved in a resource dependency should be tagged with the correct label for the specific resource dependency. Next to that the activities involved in the resource dependency should be denoted in the activity_dependency variable. 
* activity_dependency: {activity1,...,activityN}
  If an activity is involved in a resource dependency, then also the activities involved in this dependency should be denoted in this variable. If there is no resource dependency, then this variable should be empty. For the resource dependency all names of activities involved, including the activity for which the variable is set, should be denoted in set notation. 

If there are XOR splits in the model, the choice is made based upon data. In order to make this choice the inscriptions on the arc of the XOR split denotes the condition which determines if a case takes that branch in the model. Conditions are based on case attributes and have the following structure: Attribute [operator] value. The attribute is one of the case attributes defined in the case_attributes parameter on the pool level. The operator can be any of the following mathematical operators for comparison of a numerical value: =, >, <, >= or <=. If the comparison is of a nominal value the operator is IN and the value is a set of values: IN {type1,...,typeN}. Multiple conditions for one arc can be linked together by using logic AND or OR operators and if preceded by a NOT operator it becomes true if the complement of the condition is true. Conditions in linkage by logic AND or OR operators should be surrounded by round brackets to ensure proper evaluation.

The simulation parameters can be specified in the GUI of the simulator. The duration, number of replications and warm-up time can be determined in minutes before the simulator is executed.

# Source

The source consists of two (linked) Eclipse packages that can be imported into Eclipse from GitHub as a whole.

# License

The code uses the [Desmo-J project](http://desmoj.sourceforge.net/), which is bound by the Desmo-J licence.