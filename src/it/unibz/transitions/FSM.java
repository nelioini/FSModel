package it.unibz.transitions;

import it.unibz.transitions.State.StateType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class FSM {

	Hashtable<String, List<Transition>> fsm = null;
	Hashtable<String, State> states = null;
	String[] traces = null;
	TraceGenerator generator = null;

	String[] extraPaths = null;

	public String[] getExtraPaths() {
		return extraPaths;
	}

	public void setExtraPaths(String[] extraPaths) {
		this.extraPaths = extraPaths;
	}

	public String[] getTraces() {
		return traces;
	}

	public void setTraces(String[] traces) {
		this.traces = traces;
	}

	public TraceGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(TraceGenerator generator) {
		this.generator = generator;
	}

	public Hashtable<String, List<Transition>> getFsm() {
		return fsm;
	}

	public void setFsm(Hashtable<String, List<Transition>> fsm) {
		this.fsm = fsm;
	}

	public Hashtable<String, State> getStates() {
		return states;
	}

	public void setStates(Hashtable<String, State> states) {
		this.states = states;
	}

	public enum errors {
		MISSING_STATE, MISSING_TRANSITION, OK, NOT_FINAL, UNKNOWN
	};

	public FSM() throws NumberFormatException, IOException {
		fsm = new Hashtable<>();

		generator = new TraceGenerator(
				new File("/Users/nelioini/unibz/workspaces/inellij/FSM/data/paths.txt"));
		traces = generator.generateRandomTraces();

		BufferedReader br = new BufferedReader(new FileReader(new File(
				"/Users/nelioini/unibz/workspaces/inellij/FSM/data/extrapaths.txt")));
		extraPaths = new String[10];
		String sCurrentLine;
		int index = 0;
		while ((sCurrentLine = br.readLine()) != null) {
			extraPaths[index] = sCurrentLine;
			index++;
		}
	}

	public void generate(File file) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));

		String sCurrentLine;

		states = new Hashtable<>();

		if ((sCurrentLine = br.readLine()) != null) {
			State state = new State(sCurrentLine, State.StateType.INITIAL);
			states.put(sCurrentLine, state);
		}

		if ((sCurrentLine = br.readLine()) != null) {
			String[] split = sCurrentLine.split(" ");
			for (int i = 0; i < split.length; i++) {
				State state = new State(split[i], State.StateType.FINAL);
				states.put(split[i], state);
			}

		}

		while ((sCurrentLine = br.readLine()) != null) {
			String[] split = sCurrentLine.split(" ");
			if (!states.containsKey(split[0])) {
				State s = new State(split[0], State.StateType.INTERMEDIATE);
				states.put(split[0], s);
			}

			if (!states.containsKey(split[2])) {
				State s = new State(split[2], State.StateType.INTERMEDIATE);
				states.put(split[2], s);
			}

			System.out.println();
			Transition t = new Transition(split[1], states.get(split[0]),
					states.get(split[2]));
			if (fsm.containsKey(split[0]))
				fsm.get(split[0]).add(t);
			else {
				List<Transition> transitions = new ArrayList<Transition>();
				transitions.add(t);
				fsm.put(split[0], transitions);
			}
		}

	}

	public void printFSM() {
		Iterator<List<Transition>> iter = fsm.values().iterator();
		while (iter.hasNext()) {
			List<Transition> ts = (List<Transition>) iter.next();
			for (Transition transition : ts) {
				System.out.println(transition.getFrom().getName() + " "
						+ transition.getName() + " "
						+ transition.getTo().getName());
			}

		}
	}

	public void checkTraces(File trace) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(trace));

		String sCurrentLine;

		while ((sCurrentLine = br.readLine()) != null) {
			checkTraceLine(sCurrentLine);
		}
	}

	public errors checkTraceLine(String line) {
		String[] split = line.split(" ");
		for (int i = 0; i < split.length; i++) {
			if (fsm.containsKey(split[i])) {
				if (i < split.length - 1) {
					List<Transition> transitions = fsm.get(split[i]);
					boolean foundTransition = false;
					for (int j = 0; j < transitions.size(); j++) {
						// transitions[j].
						if (fsm.containsKey(split[i + 1])) {
							if (transitions.get(j).getTo().getName()
									.equals(split[i + 1]))
								;
							foundTransition = true;
						}
					}
					if (foundTransition)
						return errors.OK;
					else
						return errors.MISSING_STATE;
				} else {
					List<Transition> transitions = fsm.get(split[i]);
					if (transitions.get(0).getFrom().getType() != StateType.FINAL) {
						return errors.NOT_FINAL;
					}
				}
			} else {
				return errors.MISSING_STATE;
			}
		}
		return errors.UNKNOWN;
	}

	public List<String> checkMissingStates(String line) {
		String[] split = line.split(" ");
		List<String> missingStates = new ArrayList<String>();
		for (String traceElement : split) {
			if (!states.containsKey(traceElement)) {
				missingStates.add(traceElement);
				State s = new State(traceElement, StateType.INTERMEDIATE);
				addState(s);
			}
		}
		return missingStates;
	}

	/**
	 * Checks if there are missing transitions in the model which exist in the
	 * traces
	 * 
	 * @param line
	 * @return
	 */
	public List<Transition> checkMissingTransitions(String line, boolean adapt) {
		String[] split = line.split(" ");
		List<Transition> missingTransitions = new ArrayList<Transition>();
		for (int i = 0; i < split.length - 1; i++) {
			List<Transition> transitions = fsm.get(split[i]);
			boolean found = false;
			if (transitions != null) {
				for (Transition transition : transitions) {
					if (transition.getTo().getName().equals(split[i + 1])) {
						found = true;
					}
				}
			}
			if (!found) {
				Transition t = new Transition("tmp", states.get(split[i]),
						states.get(split[i + 1]));
				missingTransitions.add(t);
				if (adapt)
					addTransition(t);
			}
		}

		return missingTransitions;

	}

	/**
	 * check if the trace contains an initial state
	 * 
	 * @param line
	 * @return
	 */
	public boolean isValidInitialState(String line) {
		String[] split = line.split(" ");
		State s = states.get(split[0]);
		if (s.getType().equals(State.StateType.INITIAL))
			return true;
		else
			return false;
	}

	/**
	 * check if the trace contains a final state
	 * 
	 * @param line
	 * @return
	 */
	public boolean isValidFinalState(String line) {
		String[] split = line.split(" ");
		State s = states.get(split[split.length - 1]);
		if (s.getType().equals(State.StateType.FINAL))
			return true;
		else
			return false;
	}

	/**
	 * check if the traces is missing the initial or the final state
	 * 
	 * @param line
	 * @return
	 */
	public void isPartialPath(String line) {
		boolean isValideInitial = isValidInitialState(line);
		boolean isValidFinal = isValidFinalState(line);
		if (!isValideInitial) {
			System.out.println("Missing initial state");
		}

		if (!isValidFinal) {
			System.out.println("Missing finale state");
		}

		if (!isValidFinal || !isValideInitial) {
			if (checkMissingTransitions(line, false).size() == 0
					&& checkMissingStates(line).size() == 0) {
				System.out.println("This is a partial path");
			}
		}
	}

	public void addState(State s) {
		if (!states.containsKey(s.getName())) {
			states.put(s.getName(), s);
			System.out.println("State: " + s.getName() + " Added");
		}
	}

	public void addTransition(Transition t) {

		if (fsm.containsKey(t.getFrom().getName()))
			fsm.get(t.getFrom().getName()).add(t);
		else {
			List<Transition> transitions = new ArrayList<Transition>();
			transitions.add(t);
			fsm.put(t.getFrom().getName(), transitions);
		}

		// System.out.println("Transition: " + t.getFrom().getName() + " -> "
		// + t.getTo().getName() + " Added");
	}

	public static void main(String[] args) throws IOException {

		Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(
						"/Users/nelioini/tmp/result.txt"), "utf-8"));
		writer.write("perturbed,refined\n");
		// writer.println("The second line");
		// writer.close();

		for (int y = 0; y < 1000; y++) {

			// load the initial model into the FSM
			FSM fsm = new FSM();
			fsm.generate(new File("/Users/nelioini/unibz/workspaces/inellij/FSM/data/experiment.txt"));

			System.out.println("----- Inital check ------");

			System.out.println("######## PRINT ORIGINAL ##########");
			fsm.printFSM();
			System.out.println("######## PRINT ORIGINAL ##########");
			System.out.println("number of path: " + fsm.numberOfPaths(fsm));

			// generate the random traces
			// TraceGenerator generator = new TraceGenerator(new File(
			// "/Users/nelioini/tmp/paths.txt"));
			String[] traces = fsm.getTraces();

			// for (int i = 0; i < traces.length; i++) {
			// System.out.println("Trace: " + traces[i]);
			//
			// List<Transition> missingTransitions = fsm
			// .checkMissingTransitions(traces[i]);
			// for (Transition missingTransition : missingTransitions) {
			// System.out.println("Missing transition: "
			// + missingTransition.getFrom().getName() + " -> "
			// + missingTransition.getTo().getName());
			// }
			// }

			// fsm.printFSM();

			// FSM f1 = fsm.removePath(fsm);
			System.out.println("--------- model perturbation -------");
			FSM f1 = fsm.perturb(fsm, 40);

			List<String> inconsistentTraces = new ArrayList<String>();

			for (int i = 0; i < traces.length; i++) {
				System.out.println("Trace: " + traces[i]);

				if (f1.isInconsistent(traces[i])) {
					inconsistentTraces.add(traces[i]);
				}

			}

			System.out.println("######## perturbed ##########");
			// f1.printFSM();
			System.out.println("number of total path: " + f1.numberOfPaths(f1));
			System.out.println("number of Original path: "
					+ f1.numberOfOriginalPaths(f1));
			writer.write(f1.numberOfOriginalPaths(f1) + ",");
			System.out.println("######## PRINT adapted ##########");
			// f1.printFSM();
			//
			//
			//
			// System.out.println("--------- checking -------");
			//
			// for (int i = 0; i < traces.length; i++) {
			// System.out.println("Trace: " + traces[i]);
			//
			// List<Transition> missingTransitions = f1
			// .checkMissingTransitions(traces[i], false);
			// for (Transition missingTransition : missingTransitions) {
			// // System.out.println("Missing transition: "
			// // + missingTransition.getFrom().getName() + " -> "
			// // + missingTransition.getTo().getName());
			// }
			// }

			// System.out.println("--------- adapting -------");
			//
			for (int i = 0; i < inconsistentTraces.size(); i++) {
				// System.out.println("Trace: " + traces[i]);

				List<Transition> missingTransitions = f1
						.checkMissingTransitions(inconsistentTraces.get(i),
								true);
				for (Transition missingTransition : missingTransitions) {
					// System.out.println("Missing transition: "
					// + missingTransition.getFrom().getName() + " -> "
					// + missingTransition.getTo().getName());
				}
			}

			// System.out.println("--------- result -------");
			//
			// for (int i = 0; i < traces.length; i++) {
			// System.out.println("Trace: " + traces[i]);
			//
			// List<Transition> missingTransitions = f1
			// .checkMissingTransitions(traces[i], false);
			// for (Transition missingTransition : missingTransitions) {
			// System.out.println("Missing transition: "
			// + missingTransition.getFrom().getName() + " -> "
			// + missingTransition.getTo().getName());
			// }
			// }

			System.out.println("######## PRINT adapted ##########");
			// f1.printFSM();
			System.out.println("number of total path: " + f1.numberOfPaths(f1));
			System.out.println("number of Original path: "
					+ f1.numberOfOriginalPaths(f1));
			writer.write(f1.numberOfOriginalPaths(f1) + "\n");
			System.out.println("######## PRINT adapted ##########");
		}

		writer.close();
	}

	public FSM randomaize(FSM fsm) {

		int randomNum = 0 + (int) (Math.random() * fsm.getFsm().size());

		List keys = new ArrayList(fsm.getFsm().keySet());
		for (int i = 0; i < randomNum; i++) {
			int random = 0 + (int) (Math.random() * fsm.getFsm().size());
			Object obj = keys.get(random);
			fsm.getFsm().remove(obj);
			// do stuff here
		}

		fsm.printFSM();
		return fsm;

	}

	public FSM perturb(FSM fsm, int percentage) throws IOException {

		int numberOfChanges = (percentage * 20) / 100;
		System.out.println("number of changes: " + numberOfChanges);

		String[] randomPaths = new String[numberOfChanges];
		for (int i = 0; i < numberOfChanges; i++) {
			int randomNum = 0 + (int) (Math.random() * extraPaths.length);
			randomPaths[i] = extraPaths[randomNum];
		}

		FSM f0 = removePath(fsm, numberOfChanges);
		FSM f1 = addPath(f0, randomPaths);

		return f1;

	}

	private FSM removePath(FSM fsm, int numberOfPaths) {

		for (int x = 0; x < numberOfPaths; x++) {
			int randomNum = 0 + (int) (Math.random() * traces.length);
			String randomTrace = traces[randomNum];
			System.out.println("------");
			System.out.println(randomTrace);
			System.out.println("------");

			String[] split = randomTrace.split(" ");
			// List<Transition> transitions = fsm.getFsm().get(split[0]);

			for (int i = 0; i < split.length - 1; i++) {
				List<Transition> transitions = fsm.getFsm().get(split[i]);
				for (int j = 0; j < transitions.size(); j++) {
					if (transitions.get(j).getTo().getName()
							.equals(split[i + 1])) {
						System.out.println("Transition: "
								+ transitions.get(j).getFrom().getName()
								+ " -> " + transitions.get(j).getTo().getName()
								+ " Removed");
						fsm.getFsm().get(split[i]).remove(j);
					}
				}
			}
		}

		return fsm;
	}

	private FSM addPath(FSM fsm, String[] randompaths) throws IOException {

		for (int j = 0; j < randompaths.length; j++) {
			System.out.println("------");
			System.out.println("Path:" + randompaths[j]);
			System.out.println("------");
			String[] split = randompaths[j].split(" ");

			for (int i = 0; i < split.length - 1; i++) {
				List<Transition> transitions = fsm.getFsm().get(split[i]);
				boolean found = false;
				if (transitions != null) {
					for (Transition transition : transitions) {
						if (transition.getTo().getName().equals(split[i + 1])) {
							found = true;
						}
					}
				}
				if (!found) {
					Transition t = new Transition("tmp", states.get(split[i]),
							states.get(split[i + 1]));
					addTransition(t);
				}
			}
		}

		return fsm;
	}

	private int numberOfPaths(FSM fsm) {
		int pathCounter = 0;

		for (int i = 0; i < generator.getTraces().size(); i++) {
			if (fsm.checkMissingTransitions(
					generator.getTraces().get(i).getPath(), false).size() == 0) {
				pathCounter++;
			}
		}

		for (int i = 0; i < extraPaths.length; i++) {
			if (fsm.checkMissingTransitions(extraPaths[i], false).size() == 0) {
				pathCounter++;
			}
		}

		return pathCounter;
	}

	private int numberOfOriginalPaths(FSM fsm) {
		int pathCounter = 0;

		for (int i = 0; i < generator.getTraces().size(); i++) {
			if (fsm.checkMissingTransitions(
					generator.getTraces().get(i).getPath(), false).size() == 0) {
				pathCounter++;
			}
		}

		return pathCounter;
	}

	public boolean isInconsistent(String line) {
		boolean found = false;
		String[] split = line.split(" ");
		List<Transition> missingTransitions = new ArrayList<Transition>();
		for (int i = 0; i < split.length - 1; i++) {
			List<Transition> transitions = fsm.get(split[i]);

			if (transitions != null) {
				for (Transition transition : transitions) {
					if (transition.getTo().getName().equals(split[i + 1])) {
						found = true;
					}
				}
			}

		}

		if (!found) {
			return true;
		} else
			return false;

	}

}
