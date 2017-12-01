package nl.tue.simulatorgui.core.viewstate;

public interface ViewStateSerializable{

	public ViewState getState();
	public void restoreState(ViewState state);
	
}
