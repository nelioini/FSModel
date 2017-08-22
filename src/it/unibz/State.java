package it.unibz;

import java.util.ArrayList;
import java.util.List;

public class State {

	public enum StateType {INITIAL, FINAL, INTERMEDIATE};
	
	
	private String name;
	private StateType type;
	private List<Transition> transitions;
	
	public State(String name, StateType type){
		this.name = name;
		this.type = type;
		transitions = new ArrayList<Transition>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Transition> getTransitions() {
		return transitions;
	}
	public void addTransition(Transition transition) {
		this.transitions.add(transition);
	}
	public StateType getType() {
		return type;
	}
	public void setType(StateType type) {
		this.type = type;
	}

	public void printState(){
		System.out.print("State: " + name);
		for(int i= 0; i<transitions.size(); i++){
			System.out.print("-->");
			transitions.get(i).getNextState().printState();
			//System.out.println("---");
		}
	}
	
	
}
