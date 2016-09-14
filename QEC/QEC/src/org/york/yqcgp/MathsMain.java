package org.york.yqcgp;

import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;
import org.york.yqcgp.operator.AntLibrary;

public class MathsMain {

	public static void main(String[] args) {
		ComplexMatrix H = AntLibrary.Hadamard().generateMatrix(1, 1, 0.0);
		ComplexMatrix wire = AntLibrary.Wire().generateMatrix(1, 1, 0.0);
		ComplexMatrix pi2 = AntLibrary.Rk(2).generateMatrix(2, 2, 0.0);
		ComplexMatrix pi3 = AntLibrary.Rk(3).generateMatrix(2, 2, 0.0);
		ComplexMatrix swap1 = wire.tensor(AntLibrary.Swap().generateMatrix(2,2, 0.0));
		ComplexMatrix swap2 = AntLibrary.Swap().generateMatrix(2,2, 0.0).tensor(wire);
		ComplexMatrix swap = swap1.times(swap2).times(swap1);
		System.out.println(swap);
		ComplexMatrix r3 = new ComplexMatrix(8, 8);
		for(int i = 0; i < 8; i++){
			if(i == 5 || i == 7){
				r3.setElement(i, i, Complex.ePow(2.0 * Math.PI / Math.pow(2.0, 3.0)));
			}
			else{
				r3.setElement(i, i, new Complex(1.0));
			}
		}
		System.out.println(pi3);
		
		ComplexMatrix l1 = H.tensor(wire).tensor(wire);
		System.out.println("Layer 1: " + l1);
		
		ComplexMatrix l2 = pi2.tensor(wire);
		System.out.println("Layer 2: " + l2);
		
		System.out.println("L2,1: " + l2.times(l1));
		
		ComplexMatrix l3 = r3;
		System.out.println("Layer 3: " + l3);

		System.out.println("L3,2,1: " + l3.times(l2.times(l1)));
		
		ComplexMatrix l4 = wire.tensor(H).tensor(wire);
		System.out.println("Layer 4: " + l4);
		
		ComplexMatrix l5 = wire.tensor(pi2);
		System.out.println("Layer 5: " + l5);
		
		ComplexMatrix l6 = wire.tensor(wire).tensor(H);
		System.out.println("Layer 6: " + l6);
		
		System.out.println("Layer 7: " + swap);
		
		System.out.println("Final matrix: ");
		ComplexMatrix M = swap.times(l6).times(l5).times(l4).times(l3).times(l2).times(l1);
		
		int qubits = 3;
		double N = Math.pow(2, qubits);
		ComplexMatrix Fn = new ComplexMatrix((int)Math.pow(2, qubits), (int)Math.pow(2, qubits));
		for(int i = 0; i < (int)Math.pow(2, qubits); i++){
			for(int j = 0; j < (int)Math.pow(2, qubits); j++){
				Fn.setElement(i, j, Complex.ePow((2.0 * Math.PI * i * j) / N).times(1.0 / Math.sqrt(N)));
			}
		}
		
		System.out.println("FN: " + Fn);
		System.out.println(Complex.ePow(16.493361431346415));
		
		ComplexMatrix in = Utils.generateRandomState(3);
		System.out.println("Circuit out " + M.times(in));
		System.out.println("Fn out " + Fn.times(in));
		
		System.out.println("Circuit difference: " + Fn.minus(M));
	}

}
