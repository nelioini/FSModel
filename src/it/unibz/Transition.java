package it.unibz;

public class Transition {

	private String input;
	private State nextState;
	
	public Transition(String input, State nextState){
		this.input = input;
		this.nextState = nextState;
	}
	
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public State getNextState() {
		return nextState;
	}
	public void setNextState(State nextState) {
		this.nextState = nextState;
	}
	
	
}
