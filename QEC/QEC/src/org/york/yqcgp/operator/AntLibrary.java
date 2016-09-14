package org.york.yqcgp.operator;

import java.util.ArrayList;

import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;

public class AntLibrary {

	public static Operator[] library(){
		ArrayList<Operator> lib = new ArrayList<Operator>();
		lib.add(new ControlledOperator(Swap()));
		lib.add(new ControlledOperator(PauliX()));
		lib.add(Hadamard());
		lib.add(new ControlledOperator(Rk(2)));
		lib.add(new ControlledOperator(Rk(3)));
		Operator[] ret = new Operator[lib.size()];
		return lib.toArray(ret);
	}
	
	public static Operator CNOT(){
		ComplexMatrix cm = new ComplexMatrix(4, 4);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, new Complex(1.0));
		cm.setElement(2,3, new Complex(1.0));
		cm.setElement(3,2, new Complex(1.0));
		return new AntOperator("CNOT", 2, 2, cm);
	}
	
	public static Operator CNOTUP(){
		ComplexMatrix cm = new ComplexMatrix(4, 4);
		cm.setElement(0,1, new Complex(1.0));
		cm.setElement(1,3, new Complex(1.0));
		cm.setElement(2,2, new Complex(1.0));
		cm.setElement(3,1, new Complex(1.0));
		return new AntOperator("CNOT", 2, 2, cm);
	}
	
	public static Operator CP(double phase){
		ComplexMatrix cm = new ComplexMatrix(4, 4);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, new Complex(1.0));
		cm.setElement(2,2, new Complex(1.0));
		cm.setElement(3,3, Complex.ePow(phase * 2.0));
		return new AntOperator("CP-" + phase, 2, 2, cm);
	}
	
	public static Operator CRk(int k){
		ComplexMatrix cm = new ComplexMatrix(4, 4);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, new Complex(1.0));
		cm.setElement(2,2, new Complex(1.0));
		cm.setElement(3,3, Complex.ePow(2.0 * Math.PI / Math.pow(2, k)));
		return new AntOperator("R" + k, 2, 2, cm);
	}
	
	public static Operator Rk(int k){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, Complex.ePow(2.0 * Math.PI / Math.pow(2, k)));
		return new AntOperator("R" + k, 1, 1, cm);
	}
	
	public static Operator CPUP(double phase){
		ComplexMatrix cm = new ComplexMatrix(4, 4);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, Complex.ePow(phase));
		cm.setElement(2,2, new Complex(1.0));
		cm.setElement(3,3, new Complex(1.0));
		return new AntOperator("CPUP-" + phase, 2, 2, cm);
	}
	
	public static Operator P(double phase){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, Complex.ePow(phase));
		return new AntOperator("P-" + phase, 1, 1, cm);
	}
	
	public static Operator CY(){
		ComplexMatrix cm = new ComplexMatrix(4, 4);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, new Complex(1.0));
		cm.setElement(2,3, new Complex(0, 1.0));
		cm.setElement(3,2, new Complex(0, -1.0));
		return new AntOperator("CY", 2, 2, cm);
	}

	public static Operator Swap(){
		ComplexMatrix cm = new ComplexMatrix(4, 4);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,2, new Complex(1.0));
		cm.setElement(2,1, new Complex(1.0));
		cm.setElement(3,3, new Complex(1.0));
		return new AntOperator("Swap", 2, 2, cm);
	}
	
	public static Operator CZ(){
		ComplexMatrix cm = new ComplexMatrix(4, 4);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, new Complex(1.0));
		cm.setElement(2,2, new Complex(1.0));
		cm.setElement(3,3, new Complex(-1.0));
		return new AntOperator("CZ", 2, 2, cm);
	}
	
	public static Operator Wire(){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, new Complex(1.0));
		return new AntOperator("Wire", 1, 1, cm);
	}
	
	
	public static Operator Measurement(){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, new Complex(1.0));
		Operator o = new AntOperator("Measurement", 1, 1, cm);
		o.setTag(new Tag("Measurement", null));
		return o;
	}
	
	public static Operator Hadamard(){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(0,0,new Complex(1.0/Math.sqrt(2)));
		cm.setElement(1,0,new Complex(1.0/Math.sqrt(2)));
		cm.setElement(0,1,new Complex(1.0/Math.sqrt(2)));
		cm.setElement(1,1,new Complex(-1.0/Math.sqrt(2)));
		return new AntOperator("Hadamard", 1, 1, cm);
	}
	
	public static Operator PauliX(){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(1,0,new Complex(1.0));
		cm.setElement(0,1,new Complex(1.0));
		return new AntOperator("X", 1, 1, cm);
	}
	
	public static Operator PauliY(){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(1,0,new Complex(0.0, -1.0));
		cm.setElement(0,1,new Complex(0.0, 1.0));
		return new AntOperator("Y", 1, 1, cm);
	}
	
	public static Operator PauliZ(){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(1,0,new Complex(1.0));
		cm.setElement(0,1,new Complex(-1.0));
		return new AntOperator("Z", 1, 1, cm);
	}
	
	public static Operator Toffoli(){
		ComplexMatrix cm = new ComplexMatrix(8, 8);
		for(int i = 0; i < 6; i++){
			cm.setElement(i, i, new Complex(1.0));
		}
		cm.setElement(6, 7, new Complex(1.0));
		cm.setElement(7, 6, new Complex(1.0));
		return new AntOperator("Toffoli", 3, 3, cm);
	}
}
