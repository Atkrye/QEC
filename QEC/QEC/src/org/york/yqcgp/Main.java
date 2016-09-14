package org.york.yqcgp;

import org.york.builders.SingleCircuit;
import org.york.builders.TeleportationBuilder;
import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;
import org.york.yqcgp.operator.AntLibrary;
import org.york.yqcgp.operator.GreenOperator;
import org.york.yqcgp.operator.GreenPhaseShiftOperator;
import org.york.yqcgp.operator.Operator;
import org.york.yqcgp.operator.OperatorLibrary;
import org.york.yqcgp.operator.RedOperator;
import org.york.yqcgp.operator.RedPhaseShiftOperator;
import org.york.yqcgp.operator.Tag;

public class Main
{
	
	public static void main(String[] args)
	{
		QFTExp(3);
	}
	
	public static void QFT3Test(){
		QuantumSystem q = new QuantumSystem();
		Operator H = AntLibrary.Hadamard();
		Operator CNOT = AntLibrary.CNOT();
		Operator CNOTU = AntLibrary.CNOTUP();
		Operator CP2 = AntLibrary.CP(Math.PI / 2);
		Operator CP4 = AntLibrary.CP(Math.PI / 4);
		Operator CP8 = AntLibrary.CP(Math.PI / 8);
		Operator CP16 = AntLibrary.CP(Math.PI / 16);
		Operator Swap = AntLibrary.Swap();
		Operator Wire = AntLibrary.Wire();
		Operator r2 = AntLibrary.Rk(2);
		
		ComplexMatrix r3 = new ComplexMatrix(8, 8);
		for(int i = 0; i < 8; i++){
			if(i == 5 || i == 7){
				r3.setElement(i, i, Complex.ePow(2.0 * Math.PI / Math.pow(2.0, 3.0)));
			}
			else{
				r3.setElement(i, i, new Complex(1.0));
			}
		}
		
		q.prepareLayer();
		q.addOperator(H.generateMatrix(1,1,0.0), null);
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.buildLayer();
		
		q.prepareLayer();
		q.addOperator(r2.generateMatrix(2,2,0.0), null);
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.buildLayer();
		
		q.prepareLayer();
		q.addOperator(r3, null);
		q.buildLayer();
		
		q.prepareLayer();
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.addOperator(H.generateMatrix(1,1,0.0), null);
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.buildLayer();
		q.prepareLayer();
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.addOperator(r2.generateMatrix(2,2,0.0), null);
		q.buildLayer();
		q.prepareLayer();
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.addOperator(H.generateMatrix(1,1,0.0), null);
		q.buildLayer();
		
		System.out.println(q.layers.get(0));
	}

	public static Operator[] zxLib(){
		Operator[] lib = new Operator[3];
		lib[0] = OperatorLibrary.Hadamard();
		lib[1] = new GreenPhaseShiftOperator(5);
		lib[2] = new RedPhaseShiftOperator(5);
		return lib;
	}
	
	public static void experimentTest(){
		Experimenter exp = new Experimenter();
		String[] names = {"one", "two", "three"};
		int[] runs = {100, 100, 100};
		String[] modes = {"ACO", "ACO", "ACO"};
		int[] populationSizes = {5, 5, 5};
		int[] maxRuns = {2000000, 2000000, 2000000};
		double[] rates = {0.05, 0.1, 0.15};
		exp.run(names, runs, modes, rates, populationSizes, maxRuns, new SingleCircuit(5, 5, 2, 2, 2), new YabukiEvaluator(SolutionGenerator.BellPair(3)));
	}
	
	public static void QFTExp(int qubits){
		System.out.println("QFT exp");
		Experimenter exp = new Experimenter();
		String[] names = {"a"};
		int[] runs = {1};
		String[] modes = {"ZX"};
		int[] populationSizes = {5};
		int[] maxRuns = {10000000};
		double[] rates = {0.15};
		exp.run(names, runs, modes, rates, populationSizes, maxRuns, new SingleCircuit(14, 14, 2, qubits, qubits + 1), new YabukiEvaluator(SolutionGenerator.QFT(3, 3, 50000000 * (qubits))));
	}
	
	public static void GroverExp(int qubits){
		System.out.println("QFT exp");
		Experimenter exp = new Experimenter();
		String[] names = {"a"};
		int[] runs = {1};
		String[] modes = {"ACO"};
		int[] populationSizes = {1000};
		int[] maxRuns = {10000000};
		double[] rates = {0.2};
		exp.run(names, runs, modes, rates, populationSizes, maxRuns, new SingleCircuit(14, 14, 3, qubits, qubits + 4), new GroverEvaluator(qubits));
	}
	
	public static void TPExp(){
		System.out.println("QFT exp");
		Experimenter exp = new Experimenter();
		String[] names = {"a"};
		int[] runs = {1};
		String[] modes = {"ZX"};
		int[] populationSizes = {5};
		int[] maxRuns = {10000000};
		double[] rates = {0.1};
		exp.run(names, runs, modes, rates, populationSizes, maxRuns, new TeleportationBuilder(), new YabukiEvaluator(SolutionGenerator.TP(5, 10000)));
	}
	
	public static void QFTCheck(){
		QuantumSystem q = new QuantumSystem();
		Operator H = AntLibrary.Hadamard();
		Operator CNOT = AntLibrary.CNOT();
		Operator CNOTU = AntLibrary.CNOTUP();
		Operator CP2 = AntLibrary.CP(Math.PI / 2);
		Operator CP4 = AntLibrary.CP(Math.PI / 4);
		Operator CP8 = AntLibrary.CP(Math.PI / 8);
		Operator CP16 = AntLibrary.CP(Math.PI / 16);
		Operator Swap = AntLibrary.Swap();
		Operator Wire = AntLibrary.Wire();

		ComplexMatrix pi8Big = new ComplexMatrix(8, 8);
		for(int i = 0; i < 8; i++){
			if(i != 5 && i != 7){
				pi8Big.setElement(i, i, new Complex(1.0));
			}
			else{
				pi8Big.setElement(i, i, Complex.ePow(Math.PI / 4));
			}
		}
		System.out.println(CP2.generateMatrix(2, 2, 0.0));
		
		q.prepareLayer();
		q.addOperator(H.generateMatrix(1,1,0.0), null);
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.buildLayer();
		q.prepareLayer();
		q.addOperator(CP4.generateMatrix(2,2,0.0), null);
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.buildLayer();
		q.prepareLayer();
		q.addOperator(pi8Big, null);
		q.buildLayer();
		q.prepareLayer();
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.addOperator(H.generateMatrix(1,1,0.0), null);
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.buildLayer();
		q.prepareLayer();
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.addOperator(CP4.generateMatrix(2,2,0.0), null);
		q.buildLayer();
		q.prepareLayer();
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.addOperator(H.generateMatrix(1,1,0.0), null);
		q.buildLayer();
		q.prepareLayer();
		q.addOperator(Wire.generateMatrix(1,1,0.0), null);
		q.addOperator(Swap.generateMatrix(2,2,0.0), null);
		q.buildLayer();
		

		ComplexMatrix wire = AntLibrary.Wire().generateMatrix(1, 1, 0.0);
		ComplexMatrix pi2 = AntLibrary.CP(Math.PI / 2.0).generateMatrix(2, 2, 0.0);
		ComplexMatrix pi4 = AntLibrary.CP(Math.PI / 4.0).generateMatrix(2, 2, 0.0);
		ComplexMatrix l1 = H.generateMatrix(1, 1, 0.0).tensor(wire).tensor(wire);
		System.out.println("Layer 1: " + l1);
		
		ComplexMatrix l2 = pi4.tensor(wire);
		System.out.println("Layer 2: " + l2);
		
		ComplexMatrix l3 = pi8Big;
		System.out.println("Layer 3: " + l3);
		
		ComplexMatrix l4 = wire.tensor(H.generateMatrix(1, 1, 0.0)).tensor(wire);
		System.out.println("Layer 4: " + l4);
		
		ComplexMatrix l5 = wire.tensor(pi4);
		System.out.println("Layer 5: " + l5);
		
		ComplexMatrix l6 = wire.tensor(wire).tensor(H.generateMatrix(1, 1, 0.0));
		System.out.println("Layer 6: " + l6);
		
		ComplexMatrix l7 = wire.tensor(AntLibrary.Swap().generateMatrix(2, 2, 0.0));
		System.out.println("Layer 7: " + l7);
		
		System.out.println("Final matrix: ");
		System.out.println(l1.times(l2).times(l3).times(l4).times(l5).times(l6).times(l7));
		
		System.out.println("Q");
		System.out.println(q.layers.get(0));

		int qubits = 3;
		double N = Math.pow(2, qubits);
		ComplexMatrix Fn = new ComplexMatrix((int)Math.pow(2, qubits), (int)Math.pow(2, qubits));
		for(int i = 0; i < (int)Math.pow(2, qubits); i++){
			for(int j = 0; j < (int)Math.pow(2, qubits); j++){
				Fn.setElement(i, j, Complex.ePow((2.0 * Math.PI * i * j) / N).times(1.0 / Math.sqrt(N)));
			}
		}
		System.out.println("FN");
		System.out.println(Fn);
		
		QuantumSystem qs = new QuantumSystem();
		qs.prepareLayer();
		qs.addOperator(Fn, null);
		qs.buildLayer();
		
		QFTEvaluator qf = new QFTEvaluator(3);
		qf.startSession(0);
		System.out.println(qf.evaluateSystem(0, qs));
		
		YabukiEvaluator eval = new QFTEvaluator(3);
		eval.startSession(0);
		System.out.println(eval.evaluateSystem(0, q));
	}

	public static void tpAntPop(){
		Experimenter exp = new Experimenter();
		String[] names = {"a", "b", "c", "d", "e", "f"};
		int[] runs = {20, 20, 20, 20, 20, 20};
		String[] modes = {"ZX", "ZX", "ZX", "ZX", "ZX", "ZX"};
		int[] populationSizes = {8, 8, 8, 8, 8, 8};
		int[] maxRuns = {500000 / 8, 500000 / 8, 500000 / 8, 500000 / 8, 500000 / 8, 500000 / 8};
		double[] rates = {0.02, 0.05, 0.10, 0.15, 0.20, 0.30};
		exp.run(names, runs, modes, rates, populationSizes, maxRuns, new TeleportationBuilder(), new YabukiEvaluator(SolutionGenerator.TP(10, 100000000)));
	}
	

	public static void groverExp(){
		System.out.println("Grovers exp");
		Experimenter exp = new Experimenter();
		String[] names = {"a"};
		int[] runs = {1};
		String[] modes = {"ZX"};
		int[] populationSizes = {5};
		int[] maxRuns = {1000000};
		double[] rates = {0.1};
		exp.run(names, runs, modes, rates, populationSizes, maxRuns, new SingleCircuit(10, 10, 3, 3, 5), new GroverEvaluator(3));
	}
}
