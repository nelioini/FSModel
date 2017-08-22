package it.unibz.transitions;

public class Trace {

	private String path;
	private int probability;
	
	public Trace(String path, int probability){
		this.path = path;
		this.probability= probability;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getProbability() {
		return probability;
	}
	public void setProbability(int probability) {
		this.probability = probability;
	}
}
