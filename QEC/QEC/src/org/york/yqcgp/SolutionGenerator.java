package org.york.yqcgp;

import java.util.ArrayList;
import java.util.HashMap;

import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;
import org.york.yqcgp.maths.TraceRun;
import org.york.yqcgp.operator.Operator;
import org.york.yqcgp.operator.OperatorLibrary;
import org.york.yqcgp.operator.Tag;

public class SolutionGenerator {

	public int humanCompetitiveCircuit;
	public int humanCompetitiveGraph;
	HashMap<Integer, ArrayList<ComplexMatrix>> inputs = new HashMap<Integer, ArrayList<ComplexMatrix>>();
	HashMap<Integer, ArrayList<TraceRun>> outputs = new HashMap<Integer, ArrayList<TraceRun>>();
	boolean tpEval = false;
	ArrayList<ComplexMatrix> genericIn;
	ArrayList<TraceRun> genericOut;
	
	public ArrayList<ComplexMatrix> getInputs(int sessionID){
		return inputs.get(sessionID);
	}
	
	public ArrayList<TraceRun> getOutputs(int sessionID){
		return outputs.get(sessionID);
	}
	
	public boolean notifyGeneration(int sessionID){
		return false;
	}
	
	public void startSession(int sessionID){
		buildIOPairs(sessionID);
	}
	
	public void buildIOPairs(int sessionID){
		inputs.put(sessionID, genericIn);
		outputs.put(sessionID, genericOut);
	}
	
	public void dropSession(int sessionID){
		inputs.remove(sessionID);
		outputs.remove(sessionID);
	}
	
	public static SolutionGenerator CNOT(int checks){
		SolutionGenerator sg = new SolutionGenerator();
		ArrayList<ComplexMatrix> in = new ArrayList<ComplexMatrix>();
		ArrayList<TraceRun> ou = new ArrayList<TraceRun>();
		QuantumSystem q = new QuantumSystem();
		q.prepareLayer();
		q.addOperator(OperatorLibrary.CNOT().generateMatrix(2,2,0.0), null);
		q.buildLayer();
		
		for(int i = 0; i < checks; i++){
			ComplexMatrix input = Utils.generateRandomState(2);
			TraceRun output = q.runTrace(input);
			in.add(input);
			ou.add(output);
		}
		sg.genericIn = in;
		sg.genericOut = ou;
		sg.humanCompetitiveCircuit = 1;
		sg.humanCompetitiveGraph = 2;
		return sg;
	}
	

	public static SolutionGenerator TP(int checks, int genReset){
		SolutionGenerator sg = new SolutionGenerator(){
			HashMap<Integer, Integer> counters = new HashMap<Integer, Integer>();
			int counterReset = genReset;
			int check = checks;
			
			@Override
			public void startSession(int sessionID){
				super.startSession(sessionID);
				counters.put(sessionID, 0);
			}
			
			@Override
			public boolean notifyGeneration(int sessionID){
				counters.put(sessionID, counters.get(sessionID) + 1);
				if(counters.get(sessionID) == counterReset){
					buildIOPair(sessionID);
					counters.put(sessionID, 0);
					return true;
				}
				return false;
			}
			
			@Override
			public void buildIOPairs(int sessionID){
				ArrayList<ComplexMatrix> in = new ArrayList<ComplexMatrix>();
				ArrayList<TraceRun> ou = new ArrayList<TraceRun>();
				QuantumSystem q = tpCircuit();
				
				for(int i = 0; i < check; i++){
					ComplexMatrix input = Utils.generateRandomState(1);
					ComplexMatrix zero = new ComplexMatrix(1, 2);
					zero.setElement(0, 0, new Complex(1.0));
					input = input.tensor(zero);
					input = input.tensor(zero);
					TraceRun output = q.runTrace(input);
					in.add(input);
					ou.add(output);
				}
				inputs.put(sessionID, in);
				outputs.put(sessionID, ou);
			}
			
			public void buildIOPair(int sessionID){
				this.inputs.get(sessionID).remove(0);
				this.outputs.get(sessionID).remove(0);
				QuantumSystem q = tpCircuit();
				
				ComplexMatrix input = Utils.generateRandomState(1);
				ComplexMatrix zero = new ComplexMatrix(1, 2);
				zero.setElement(0, 0, new Complex(1.0));
				input = input.tensor(zero);
				input = input.tensor(zero);
				TraceRun output = q.runTrace(input);
				this.inputs.get(sessionID).add(input);
				this.outputs.get(sessionID).add(output);
			}
		};
		
		sg.humanCompetitiveCircuit = 7;
		sg.humanCompetitiveGraph = 7;
		sg.tpEval = true;
		return sg;
	}
	

	public static QuantumSystem tpCircuit(){

		QuantumSystem tp = new QuantumSystem();
		
		Operator wire = OperatorLibrary.Wire();
		Operator h = OperatorLibrary.Hadamard();
		Operator CNOT = OperatorLibrary.CNOT();
		Operator CZ = OperatorLibrary.CZ();
		Operator swap = OperatorLibrary.Swap();
		
		
		tp.prepareLayer();
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.addOperator(h.generateMatrix(1, 1, 0.0), null);
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.buildLayer();
		
		tp.prepareLayer();
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.addOperator(CNOT.generateMatrix(2, 2, 0.0), null);
		tp.buildLayer();

		tp.prepareLayer();
		tp.addOperator(CNOT.generateMatrix(2, 2, 0.0), null);
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.buildLayer();
		
		tp.prepareLayer();
		tp.addOperator(h.generateMatrix(1, 1, 0.0), null);
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.buildLayer();

		

		tp.prepareLayer();
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.buildLayer();
		
		tp.prepareLayer();
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), new Tag("Measurement", null));
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), new Tag("Measurement", null));
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.buildLayer();
		
		tp.prepareLayer();
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.addOperator(CNOT.generateMatrix(2, 2, 0.0), null);
		tp.buildLayer();
		
		tp.prepareLayer();
		tp.addOperator(swap.generateMatrix(2, 2, 0.0), null);
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.buildLayer();
		
		tp.prepareLayer();
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.addOperator(CZ.generateMatrix(2, 2, 0.0), null);
		tp.buildLayer();
		
		tp.prepareLayer();
		tp.addOperator(swap.generateMatrix(2, 2, 0.0), null);
		tp.addOperator(wire.generateMatrix(1, 1, 0.0), null);
		tp.buildLayer();
		
		return tp;
	}
	

	public static SolutionGenerator BellPair(int checks){
		SolutionGenerator sg = new SolutionGenerator();
		ArrayList<ComplexMatrix> in = new ArrayList<ComplexMatrix>();
		ArrayList<TraceRun> ou = new ArrayList<TraceRun>();
		QuantumSystem q = new QuantumSystem();
		q.prepareLayer();
		q.addOperator(OperatorLibrary.Hadamard().generateMatrix(1,1,0.0), null);
		q.addOperator(OperatorLibrary.Wire().generateMatrix(1,1,0.0), null);
		q.buildLayer();
		q.prepareLayer();
		q.addOperator(OperatorLibrary.CNOT().generateMatrix(2,2,0.0), null);
		q.buildLayer();
		
		for(int i = 0; i < checks; i++){
			ComplexMatrix input = Utils.generateRandomState(2);
			TraceRun output = q.runTrace(input);
			in.add(input);
			ou.add(output);
		}
		sg.genericIn = in;
		sg.genericOut = ou;
		sg.humanCompetitiveCircuit = 2;
		sg.humanCompetitiveGraph = 3;
		return sg;
	}
	

	public static SolutionGenerator QFT(int checks, int qubits, int genReset){
		SolutionGenerator sg = new SolutionGenerator(){
			HashMap<Integer, Integer> counters = new HashMap<Integer, Integer>();
			int counterReset = genReset;
			
			@Override
			public void startSession(int sessionID){
				super.startSession(sessionID);
				counters.put(sessionID, 0);
			}
			
			@Override
			public boolean notifyGeneration(int sessionID){
				counters.put(sessionID, counters.get(sessionID) + 1);
				if(counters.get(sessionID) >= counterReset){
					buildIOPair(sessionID);
					counters.put(sessionID, 0);
					return true;
				}
				return false;
			}
			
			public void buildIOPair(int sessionID){
				double N = Math.pow(2, qubits);
				ComplexMatrix Fn = new ComplexMatrix((int)Math.pow(2, qubits), (int)Math.pow(2, qubits));
				for(int i = 0; i < (int)Math.pow(2, qubits); i++){
					for(int j = 0; j < (int)Math.pow(2, qubits); j++){
						Fn.setElement(i, j, Complex.ePow((2.0 * Math.PI * i * j) / N).times(1.0 / Math.sqrt(N)));
					}
				}
				ComplexMatrix input = Utils.generateRandomState(qubits);
				TraceRun output = new TraceRun(Fn.times(input));
				this.inputs.get(sessionID).remove(0);
				this.outputs.get(sessionID).remove(0);
				this.inputs.get(sessionID).add(input);
				this.outputs.get(sessionID).add(output);
			}
		};
		ArrayList<ComplexMatrix> in = new ArrayList<ComplexMatrix>();
		ArrayList<TraceRun> ou = new ArrayList<TraceRun>();

		double N = Math.pow(2, qubits);
		ComplexMatrix Fn = new ComplexMatrix((int)Math.pow(2, qubits), (int)Math.pow(2, qubits));
		for(int i = 0; i < (int)Math.pow(2, qubits); i++){
			for(int j = 0; j < (int)Math.pow(2, qubits); j++){
				Fn.setElement(i, j, Complex.ePow((2.0 * Math.PI * i * j) / N).times(1.0 / Math.sqrt(N)));
			}
		}
		for(int i = 0; i < checks; i++){
			ComplexMatrix input = Utils.generateRandomState(qubits);
			
			ComplexMatrix output = Fn.times(input);
			in.add(input);
			ou.add(new TraceRun(output));
		}
		sg.genericIn = in;
		sg.genericOut = ou;
		sg.humanCompetitiveCircuit = 10;
		sg.humanCompetitiveGraph = 20;
		return sg;
	}
	
	
}
