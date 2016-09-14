package org.york.yqcgp;

import java.util.ArrayList;

import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;
import org.york.yqcgp.maths.TraceRun;

public class GroverEvaluator extends YabukiEvaluator{

	ArrayList<ComplexMatrix> matrices  = new ArrayList<ComplexMatrix>();
	public GroverEvaluator(int qubits) {
		super(buildSG(qubits));
		this.buildMatrices(qubits);
	}
	
	@Override
	public double evaluateSystem(int sessionID, QuantumSystem q){
		counters.put(sessionID, counters.get(sessionID) + 1);
		ArrayList<ComplexMatrix> in = sg.getInputs(sessionID);
		ArrayList<TraceRun> ou = sg.getOutputs(sessionID);
		double errorSum = 0.0;
		for(int i = 0; i < in.size(); i++){
			
			ComplexMatrix input = in.get(i);
			TraceRun target = ou.get(i);
			ComplexMatrix output = input;
			for(int n = 0; n < Math.ceil(Math.sqrt(input.getHeight())); n++){
				output = matrices.get(i).times(output);
				output = q.runTrace(output).results.get(0);
			}
			TraceRun real = new TraceRun(output);
			if(real.results.size() == 1 && target.results.size() == 1){
				ComplexMatrix res = real.results.get(0);
				errorSum += target.results.get(0).minus(res).size();
			}
			else if (sg.tpEval){
				double error = 0.0;
				for(String outcome : target.labels){
					Double probDiff = Math.sqrt(Math.pow(target.getProbability(outcome) - real.getProbability(outcome), 2));
					Double localError = real.getOutcome(outcome).minus(target.getOutcome(outcome)).size();
					if(localError.equals(Double.NaN) || probDiff.equals(Double.NaN)){
						error += 1000;
					}
					else{
						error += (1.0 + probDiff) * localError;
					}
				}
				errorSum += error;
			}
			else{
				System.out.println("No evaluation");
			}
		}
		return 1.0 / (1.0 + errorSum);
	}
	
	public static SolutionGenerator buildSG(int qubits){
		ArrayList<ComplexMatrix> in = new ArrayList<ComplexMatrix>();
		ArrayList<TraceRun> ou = new ArrayList<TraceRun>();
		for(int i = 0; i < (int)Math.pow(2, qubits); i++){
			ComplexMatrix inM = new ComplexMatrix(1, (int)Math.pow(2, qubits));
			inM.setElement(0, 0, new Complex(1.0));
			ComplexMatrix ouM = new ComplexMatrix(1, (int)Math.pow(2, qubits));
			ouM.setElement(0, i, new Complex(1.0));
			TraceRun t = new TraceRun(ouM);
			in.add(inM);
			ou.add(t);
		}
		SolutionGenerator sg = new SolutionGenerator();
		sg.genericIn = in;
		sg.genericOut = ou;
		sg.humanCompetitiveCircuit = 20;
		sg.humanCompetitiveGraph = 20;
		return sg;
	}
	
	public void buildMatrices(int qubits){

		for(int i = 0; i < (int)Math.pow(2, qubits); i++){
			ComplexMatrix uW = new ComplexMatrix((int)Math.pow(2, qubits), (int)Math.pow(2, qubits));
			for(int j = 0; j < (int)Math.pow(2, qubits); j++){
				if(i == j){
					uW.setElement(j, j, new Complex(-1.0));
				}
				else{
					uW.setElement(j, j, new Complex(1.0));
				}
			}
			matrices.add(uW);
		}
	}

}
