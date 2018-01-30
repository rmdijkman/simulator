package nl.tue.simulator_engine.core;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

public class Generator extends SimProcess{

	SimulatorModel model;
	
	public Generator(Model owner, String name, boolean showInTrace){
		super(owner, name, showInTrace);
		model = (SimulatorModel) owner;
	}
	
	@Override
	public void lifeCycle(){
		
		while(true){
			Case cs = new Case(model, "Case", true);
			cs.activate();
			hold(new TimeSpan(model.interarrivalTimeSample()));
		}
	}

}
