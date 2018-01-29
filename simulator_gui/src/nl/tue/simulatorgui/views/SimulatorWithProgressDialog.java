package nl.tue.simulatorgui.views;

import java.util.concurrent.ExecutionException;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

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
	
	public BackgroundWorker(String filePath, long duration, long nrReplications, long warmup, boolean queueing, SimulatorWithProgressDialog dialog) {
		this.filePath = filePath;
		this.duration = duration;
		this.nrReplications = nrReplications;
		this.warmup = warmup;
		this.dialog = dialog;
		this.queueing = queueing;
		
		this.result = null;
	}

	@Override
	protected Void doInBackground() throws Exception {
		this.result = Simulator.runSimulator(filePath, duration, nrReplications, warmup, queueing, dialog);
		return null;
	}

	@Override
	protected void done() {
		try {
			get();
			dialog.resultProduced(result);
		} catch (ExecutionException e) {
			String exception = "There was an error processing the simulation: " + e.getCause().getMessage();
			e.printStackTrace();
			dialog.exceptionProduced(exception);
		} catch (InterruptedException e) {
			
		}
	}

}