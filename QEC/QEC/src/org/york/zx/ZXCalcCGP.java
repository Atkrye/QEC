package org.york.zx;

import java.util.ArrayList;
import java.util.Random;
import org.york.builders.Builder;
import org.york.yqcgp.QuantumSystem;
import org.york.yqcgp.Result;
import org.york.yqcgp.YabukiEvaluator;
import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;
import org.york.yqcgp.maths.LinearAlgebra;
import org.york.yqcgp.operator.Operator;

public class ZXCalcCGP {

	public Random rand;
	private Operator[] lib;
	private Builder builder;
	
	public ZXCalcCGP(Operator[] lib, Builder builder){
		this.lib = lib;
		this.rand = new Random();
		this.builder = builder;
	}
	
	public Result runExperiment(int populationSize, int maxRuns, double mutationRate, YabukiEvaluator eval){
		return runExperiment(populationSize, maxRuns, mutationRate, eval, 0);
	}

	public Result runExperiment(int populationSize, int maxRuns, double mutationRate, YabukiEvaluator eval, int sessionID){
		SuperGraph[] population = new SuperGraph[populationSize];
		for(int i = 0; i < populationSize; i++){
			SuperGraph sg = new SuperGraph(this, builder);
			sg.init();
			population[i] = sg;
		}
		
		double bestScore = 0.0;
		double bestSizeScore = 0.0;
		SuperGraph bestGraph = null;
		int winnerIndex = 0;
		ArrayList<Double> scores = new ArrayList<Double>();
		ArrayList<Double> sizeScores = new ArrayList<Double>();
		eval.startSession(sessionID);
		double roundBest = 0.0;
		double roundBestSize = 0.0;
		int evals = 0;
		for(int i = 0; i < populationSize; i++){
			QuantumSystem q = builder.build(population[i]);
			double score = eval.evaluateSystem(sessionID, q);
			evals++;
			scores.add(score);
			if(score > bestScore && bestSizeScore == 0.0){
				bestScore = score;
				bestGraph = population[i];
			}
			if(score > roundBest && bestSizeScore == 0.0){
				roundBest = score;
				winnerIndex = i;
			}
			if(score > 0.9999){
				double sizeScore = 1.0 / (1.0 + (double)q.size);
				sizeScores.add(sizeScore);
				if(sizeScore > bestSizeScore){
					bestSizeScore = sizeScore;
					bestGraph = population[i];
				}

				if(score > 0.9999 && sizeScore > roundBestSize){
					roundBest = sizeScore;
					winnerIndex = i;
				}
			}
			else{
				sizeScores.add(0.0);
			}
			eval.sg.notifyGeneration(sessionID);
		}
		int i;
		boolean perfect = false;
		for(i = 0; (i < maxRuns && !perfect); i++){
			roundBest = 0.0;
			roundBestSize = 0.0;
			SuperGraph winner = population[winnerIndex];
			for(int j = 0; j < populationSize; j++){
				if(j != winnerIndex){
					try {
						population[j] = winner.mutate(mutationRate);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			for(int j = 0; j < populationSize; j++){
				double score = 0.0;
				QuantumSystem q = null;
				boolean change = false;
				if(j != winnerIndex){
					if(population[j].systemChanged()){
						change = true;
						q = builder.build(population[j]);
						score = eval.evaluateSystem(sessionID, q);
						evals++;
						scores.set(j, score);
					}
					else{
						score = scores.get(winnerIndex);
					}
				}
				else{
					score = scores.get(j);
				}
				if(score > bestScore && bestSizeScore == 0.0){
					bestScore = score;
					bestGraph = population[j];
				}
				if(score > roundBest){
					roundBest = score;
				}
				if(score > 0.9999){
					double sizeScore = 0.0;
					if(change){
						sizeScore = 1.0 / (1.0 + (double)q.size);
						sizeScores.set(j, sizeScore);
					}
					else{
						sizeScore = sizeScores.get(winnerIndex);
					}
					if(sizeScore > bestSizeScore){
						bestSizeScore = sizeScore;
						bestGraph = population[j];
					}

					if(score > 0.9999 && sizeScore > roundBestSize){
						roundBestSize = sizeScore;
					}
				}
				else{
					sizeScores.set(j, 0.0);
				}
				
			}
			
			if(roundBest <= 0.9999){
				ArrayList<Integer> winners = new ArrayList<Integer>();
				for(int j = 0; j < populationSize; j++){
					if(roundBest - scores.get(j) < 0.00001){
						winners.add(j);
					}
				}
				winnerIndex = winners.get(rand.nextInt(winners.size()));
			}
			else{
				ArrayList<Integer> winners = new ArrayList<Integer>();
				for(int j = 0; j < populationSize; j++){
					if(sizeScores.get(j) == roundBestSize){
						winners.add(j);
					}
				}
				winnerIndex = winners.get(rand.nextInt(winners.size()));
			}
			if(bestScore > 0.9999 && bestSizeScore >= (1.0 / (1.0 + (double)eval.sg.humanCompetitiveGraph))){
				perfect = true;
			}
			if(eval.sg.notifyGeneration(sessionID)){
				QuantumSystem q = builder.build(population[winnerIndex]);
				double score = eval.evaluateSystem(sessionID, q);
				evals++;
				scores.set(winnerIndex, score);
				if(score > 0.9999){
					sizeScores.set(winnerIndex, (1.0 / (1.0 + q.size)));
					bestSizeScore = (1.0 / (1.0 + q.size));
				}
				bestScore = score;
			}
			System.out.println("Generation " + i + " best performance " + bestScore);
		}
		return new Result(bestScore > 0.9999, perfect, bestScore, bestSizeScore, i, evals, "ZX", population[winnerIndex]);
	}
	
	public Operator randomOp(){
		return lib[rand.nextInt(lib.length)];
	}
	
	public double randomPhase(){
		double prob = rand.nextDouble();
		if(prob <= 0.3){
			return 0.0;
		}
		else if (prob <= 0.6){
			return Math.PI;
		}
		else if (prob <= 0.8){
			return rand.nextDouble();
		}
		else{
			return ((double)rand.nextInt(14) + 1.0) * Math.PI / 8.0;
		}
	}
	
	public ZXNodeVertex randomVertex(int yMin, int yMax, int layerWidth, int inputs, boolean allowDisconnects){
		if(allowDisconnects && rand.nextDouble() > 0.5){
			return null;
		}
		int y = 0;
		if(yMax == yMin){
			y = yMax;
		}
		else{
			y = rand.nextInt(yMax - yMin) + yMin;
		}
		int x = 0;
		if(y > yMin){
			x = rand.nextInt(layerWidth);
		}
		else{
			x = rand.nextInt(inputs);
		}
		return new ZXNodeVertex(x, y);
	}
	
	public static ComplexMatrix generateRandomState(int qubits){
		Random rand = new Random();
		ComplexMatrix c = new ComplexMatrix(1, 1);
		c.setElement(0, 0, new Complex(1.0));
		for(int i = 0; i < qubits; i++){
			ComplexMatrix m = new ComplexMatrix(1, 2);
			double val = rand.nextDouble();
			m.setElement(0, 0, new Complex(Math.sqrt(val)));
			m.setElement(0, 1, new Complex(Math.sqrt(1.0 - val)));
			c = c.tensor(m);
		}
		return c;
	}
	
	public static void printLA(LinearAlgebra[] la){
		for(int i = 0; i < la.length; i++){
			if(la[i] == null){
				System.out.println("Empty");
			}
			else{
				System.out.println(la[i]);
			}
		}
	}
}
