package org.york.yqcgp;

import java.util.ArrayList;
import java.util.HashMap;

import org.york.yqcgp.maths.ComplexMatrix;
import org.york.yqcgp.maths.TraceRun;

public class YabukiEvaluator {

	public SolutionGenerator sg;
	HashMap<Integer, Integer> counters = new HashMap<Integer, Integer>();
	
	public YabukiEvaluator(SolutionGenerator sg) {
		this.sg = sg;
	}
	
	public int startSession(int sessionID){
		sg.startSession(sessionID);
		counters.put(sessionID, 0);
		return sessionID;
	}
	
	public int getRuns(int sessionID){
		return counters.get(sessionID);
	}
	
	public void dropSession(int sessionID){
		counters.remove(sessionID);
		sg.dropSession(sessionID);
	}
	
	public double evaluateSystem(int sessionID, QuantumSystem q){
		counters.put(sessionID, counters.get(sessionID) + 1);
		ArrayList<ComplexMatrix> in = sg.getInputs(sessionID);
		ArrayList<TraceRun> ou = sg.getOutputs(sessionID);
		double errorSum = 0.0;
		for(int i = 0; i < in.size(); i++){
			ComplexMatrix input = in.get(i);
			TraceRun target = ou.get(i);
			TraceRun real = q.runTrace(input);
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
	
	public void setSolution(SolutionGenerator sg){
		this.sg = sg;
	}
	

	public double compareMatrices(ComplexMatrix real, ComplexMatrix target, String label){
		int targetA = 0;
		int targetB = 1;
		if(label.equals("00")){
			targetA = 0;
			targetB = 1;
		}
		else if(label.equals("01")){
			targetA = 2;
			targetB = 3;
		}
		else if(label.equals("10")){
			targetA = 4;
			targetB = 5;
		}
		else if(label.equals("11")){
			targetA = 6;
			targetB = 7;
		}
		double bonus = 0.0;
		double realA = Math.sqrt(TraceRun.toProb(real.getElement(0, targetA)));
		double realB = Math.sqrt(TraceRun.toProb(real.getElement(0, targetB)));
		double targA = Math.sqrt(TraceRun.toProb(target.getElement(0, targetA)));
		double targB = Math.sqrt(TraceRun.toProb(target.getElement(0, targetB)));
		double score = Math.pow((realA / realB) - (targA / targB), 2);
		if(score == Double.NaN){
			score = Math.pow((realB / realA) - (targB / targA), 2);
		}
		return bonus + Math.pow((realA / realB) - (targA / targB), 2);
	}
	
}
