package org.york.yqcgp;

import java.util.Random;

import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;
import org.york.yqcgp.maths.LinearAlgebra;

public class Utils {
	
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
