package it.unibz.transitions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TraceGenerator {

	List<Trace> traces = new ArrayList<Trace>();
	String[] randomTraces = new String[100];
	
	

	public List<Trace> getTraces() {
		return traces;
	}

	public void setTraces(List<Trace> traces) {
		this.traces = traces;
	}

	public String[] getRandomTraces() {
		return randomTraces;
	}

	public void setRandomTraces(String[] randomTraces) {
		this.randomTraces = randomTraces;
	}

	public TraceGenerator(File pathsfile) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(pathsfile));

		String sCurrentLine;

		while ((sCurrentLine = br.readLine()) != null) {
			String[] split = sCurrentLine.split(";");
			Trace t = new Trace(split[0],Integer.parseInt(split[1]));
			traces.add(t);
		}
		
		int index =0;
		for (Trace trace : traces) {
			for(int i =0; i< trace.getProbability(); i++){
				randomTraces[index]=trace.getPath();
				index++;
			}
		}
		
//		for(int i = 0; i<randomTraces.length; i++){
//			System.out.println(randomTraces[i]);
//		}
	}
	
	public String[] generateRandomTraces(){
		String[] randomselectedTraces = new String[1000];
		
		Random generator = new Random();
		
		for(int i=0; i< randomselectedTraces.length; i++){
			int randomIndex = generator.nextInt(randomTraces.length);
			randomselectedTraces[i] = randomTraces[randomIndex];
		}
		
		return randomselectedTraces;
//		for(int i = 0; i<randomselectedTraces.length; i++){
//			System.out.println(randomselectedTraces[i]);
//		}
		
	}

	public static void main(String[] args) throws NumberFormatException, IOException{
		File file = new File("/Users/nelioini/tmp/paths.txt");
		TraceGenerator generator = new TraceGenerator(file);
		generator.generateRandomTraces();
	}
}
