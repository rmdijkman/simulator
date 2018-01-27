package nl.tue.simulatorgui.views;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import nl.tue.bpmn.parser.BPMNParseException;
import nl.tue.simulator_engine.core.ReplicationMonitor;
import nl.tue.simulator_engine.core.Simulator;
import nl.tue.simulatorgui.core.Environment;
import nl.tue.simulatorgui.executor.SimulatorScript;

public class SimulatorWithProgressDialog implements ReplicationMonitor{
	private ProgressMonitor progressMonitor;
	private SimulatorScript callback;
	
	public SimulatorWithProgressDialog(String filePath, long duration, long nrReplications, long warmup, boolean queueing, SimulatorScript callback){
		progressMonitor = new ProgressMonitor(Environment.getMainController().getMainView(), "Running simulation", "", 0, (int) nrReplications);
		this.callback = callback;
		
		BackgroundWorker backgrndWorker = new BackgroundWorker(filePath, duration, nrReplications, warmup, queueing, this);
		backgrndWorker.execute();
	}
	
	@Override
	public void setCurrentReplication(long number) {
		progressMonitor.setProgress((int) number);
	}
	
	public void resultProduced(String result){
		progressMonitor.close();
		callback.callBackResult(result);
	}

	public void exceptionProduced(String exception){
		progressMonitor.close();
		callback.callBackException(exception);
	}
}

class BackgroundWorker extends SwingWorker<Void, Void> {
	String filePath;
	long duration;
	long nrReplications;
	long warmup;
	boolean queueing;
	SimulatorWithProgressDialog dialog;
	
	String result;
	String exception;
	
	public BackgroundWorker(String filePath, long duration, long nrReplications, long warmup, boolean queueing, SimulatorWithProgressDialog dialog) {
		this.filePath = filePath;
		this.duration = duration;
		this.nrReplications = nrReplications;
		this.warmup = warmup;
		this.dialog = dialog;
		this.queueing = queueing;
		
		this.result = null;
		this.exception = null;
	}

	@Override
	protected Void doInBackground() throws Exception {
		try {
			this.result = Simulator.runSimulator(filePath, duration, nrReplications, warmup, queueing, dialog);
		} catch (BPMNParseException e) {
			this.exception = "There was an error reading the BPMN file: " + e.getMessage();
		}
		return null;
	}

	@Override
	protected void done() {
		if (result != null){
			dialog.resultProduced(result);
		}
		if (exception != null){
			dialog.exceptionProduced(exception);
		}
	}

}