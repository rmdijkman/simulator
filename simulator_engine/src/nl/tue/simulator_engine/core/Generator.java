package nl.tue.simulator_engine.core;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

public class Generator extends SimProcess{
	
	public Generator(Model owner, String name, boolean showInTrace){
		super(owner, name, showInTrace);
	}
	
	public void lifeCycle(){
		
		SimulatorModel model = Simulator.simmodel;
		
		while(true){
			Case cs = new Case(model, "Case", true);
			cs.activate();
			hold(new TimeSpan(model.interarrivalTimeSample()));
		}
	}

}
