# Simulator

An implementation of an advanced business process simulation engine. The simulation engine can take BPMN models as they are, for example, produced in [Signavio](https://www.signavio.com/) and simulate them. It contains some advanced functionality that existing BPMN simulation engines do not have. In particular:
* case data with distributions on their value;
* the ability to base an XOR-split on case data values;
* advanced resource simulation patterns;
* various queueing policies; and
* simulation parameters, in particular: number of replications and warm-up time.
