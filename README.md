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

# Source

The source consists of two (linked) Eclipse packages that can be imported into Eclipse from GitHub as a whole.

# License

The code uses the [Desmo-J project](http://desmoj.sourceforge.net/), which is bound by the Desmo-J licence.