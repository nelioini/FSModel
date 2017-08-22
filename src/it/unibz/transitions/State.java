package it.unibz.transitions;

public class State {

	
	public enum StateType {INITIAL, FINAL, INTERMEDIATE}
	
	private String name;
	private StateType type;
	
	public State(String name, StateType type){
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public StateType getType() {
		return type;
	}

	public void setType(StateType type) {
		this.type = type;
	};
	
	
	
}
