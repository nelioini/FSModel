package it.unibz;

import javax.print.attribute.standard.Finishings;

public class FSM {

	public static void main(String[] args){
		
		State q0 = new State("q0", State.StateType.INITIAL);
		State q1 = new State("q1", State.StateType.INTERMEDIATE);
		State q2 = new State("q2", State.StateType.INTERMEDIATE);
		State q3 = new State("q3", State.StateType.INTERMEDIATE);
		//State q4 = new State("q4", State.StateType.INTERMEDIATE);
		State q5 = new State("q5", State.StateType.FINAL);
		
		Transition t0 = new Transition("a", q1);
		Transition t1 = new Transition("a", q2);
		Transition t2 = new Transition("a", q3);
		//Transition t3 = new Transition("a", q4);
		Transition t4 = new Transition("a", q5);
		
		q0.addTransition(t0);
		q1.addTransition(t1);
		q1.addTransition(t2);
		q2.addTransition(t4);
		q1.addTransition(t4);
		
		//printFSM(q0);
		
		State tmp = findState("q1", q0);
		if(tmp != null){
			System.out.println("State Name: " + tmp.getName());
			for(int i =0; i<tmp.getTransitions().size(); i++){
				System.out.println("connected state " + i +": " + tmp.getTransitions().get(i).getNextState().getName());
			}
		}
	}
	
	public static void printFSM(State initialState){
		
		initialState.printState();
	}
	
	
	public static State findState(String stateName ,State initialState){
		if(initialState.getName().equals(stateName)){
			return initialState;
		}else{
			for(int i =0; i<initialState.getTransitions().size(); i++){
				return FSM.findState(stateName, initialState.getTransitions().get(i).getNextState());
			}
		}
		return null;
		
	}
}
