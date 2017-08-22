package it.unibz.transitions;

public class Transition {

	private String name;
	private State from;
	private State to;
	
	public Transition(String name, State from, State to){
		this.name = name;
		this.from = from;
		this.to = to;
		
	} 
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public State getFrom() {
		return from;
	}
	public void setFrom(State from) {
		this.from = from;
	}
	public State getTo() {
		return to;
	}
	public void setTo(State to) {
		this.to = to;
	}
	
	
}
