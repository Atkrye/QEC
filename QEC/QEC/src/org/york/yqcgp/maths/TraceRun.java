package org.york.yqcgp.maths;

import java.util.ArrayList;

public class TraceRun {

	public ArrayList<ComplexMatrix> results = new ArrayList<ComplexMatrix>();
	public ArrayList<Double> probabilities = new ArrayList<Double>();
	public ArrayList<String> labels = new ArrayList<String>();
	
	public TraceRun(ComplexMatrix init){
		results.add(init);
		probabilities.add(1.0);
		labels.add("");
	}
	
	@Override
	public String toString(){
		String ret = "Trace run with " + results.size() + " results.";
		for(int i = 0; i < results.size(); i++){
			ret = ret + "\nResult " + i + " with label " + labels.get(i) + ", probability " + probabilities.get(i);
			ret = ret + "\n" + results.get(i);
		}
		return ret;
	}
	
	public TraceRun(ArrayList<ComplexMatrix> res, ArrayList<Double> prob, ArrayList<String> lab){
		for(int i = 0; i < res.size(); i++){
			results.add(res.get(i));
			probabilities.add(prob.get(i));
			labels.add(lab.get(i));
		}
	}
	
	public TraceRun copy(){
		return new TraceRun(results, probabilities, labels);
	}
	
	public TraceRun apply(ComplexMatrix c){
		TraceRun res = copy();
		for(int i = 0; i < results.size(); i++){
			ComplexMatrix m = c.times(results.get(i));
			m.resize();
			res.results.set(i, m);
		}
		return res;
	}
	
	public TraceRun measure(int qubit){
		ArrayList<ComplexMatrix> newResults = new ArrayList<ComplexMatrix>();
		ArrayList<Double> newProbs = new ArrayList<Double>();
		ArrayList<String> newLabels = new ArrayList<String>();
		for(int i = 0; i < results.size(); i++){
			double zeroProb = 0.0;
			double oneProb = 0.0;

			boolean zeroVal = true;
			int counter = 0;
			int qubits = (int)(Math.log(results.get(i).getHeight()) / Math.log(2));
			int resetCounter = (int)Math.pow(2, qubits - (qubit + 1));
			
			for(int j = 0; j < results.get(i).getHeight(); j++){
				if(zeroVal){
					zeroProb += toProb(results.get(i).getElement(0, j));
				}
				else{
					oneProb += toProb(results.get(i).getElement(0, j));
				}
				counter++;
				if(counter == resetCounter){
					zeroVal = !zeroVal;
					counter = 0;
				}
			}
			
			double totalProb = zeroProb + oneProb;
			zeroProb = zeroProb / totalProb;
			oneProb = oneProb /totalProb;
			if(true){
	
				ComplexMatrix zeroM = new ComplexMatrix(results.get(i).getHeight(), results.get(i).getHeight());
				ComplexMatrix oneM = new ComplexMatrix(results.get(i).getHeight(), results.get(i).getHeight());
				counter = 0;
				zeroVal = true;
				for(int j = 0; j < results.get(i).getHeight(); j++){
					if(zeroVal){
						zeroM.setElement(j, j, new Complex(1.0 / Math.sqrt(zeroProb)));
					}
					else{
						oneM.setElement(j, j, new Complex(1.0 / Math.sqrt(oneProb)));
					}
					counter++;
					if(counter == resetCounter){
						zeroVal = !zeroVal;
						counter = 0;
					}
				}
				
				
				ComplexMatrix zeroRes = zeroM.times(results.get(i));
				ComplexMatrix oneRes = oneM.times(results.get(i));
				
				newResults.add(zeroRes);
				newProbs.add(probabilities.get(i) * zeroProb);
				newLabels.add("0" + labels.get(i));
				newResults.add(oneRes);
				newProbs.add(probabilities.get(i) * oneProb);
				newLabels.add("1" + labels.get(i));
			}
			else{
				System.out.println("Zero prob");
				newResults.add(results.get(i));
				newResults.add(results.get(i));
				newProbs.add(probabilities.get(i));
				newProbs.add(probabilities.get(i));
				newLabels.add("0" + labels.get(i));
				newLabels.add("1" + labels.get(i));
			}
		}
		return new TraceRun(newResults, newProbs, newLabels);
	}
	
	public double getProbability(String label){
		for(int i = 0; i < results.size(); i++){
			if(labels.get(i).equals(label)){
				return probabilities.get(i);
			}
		}
		return 0.0;
	}

	public ComplexMatrix getOutcome(String label){
		for(int i = 0; i < results.size(); i++){
			if(labels.get(i).equals(label)){
				return results.get(i);
			}
		}
		return null;
	}
	
	public static double toProb(Complex c){
		if(c == null){
			return 0.0;
		}
		return c.mod() * c.mod();
	}
	
}
