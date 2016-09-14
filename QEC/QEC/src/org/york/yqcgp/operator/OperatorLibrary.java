package org.york.yqcgp.operator;

import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;

public class OperatorLibrary
{
	
	public static Operator CNOT(){
		ComplexMatrix cm = new ComplexMatrix(4, 4);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, new Complex(1.0));
		cm.setElement(2,3, new Complex(1.0));
		cm.setElement(3,2, new Complex(1.0));
		return new FixedMatrixOperator("CNOT", 2, 2, cm);
	}

	public static Operator Swap(){
		ComplexMatrix cm = new ComplexMatrix(4, 4);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,2, new Complex(1.0));
		cm.setElement(2,1, new Complex(1.0));
		cm.setElement(3,3, new Complex(1.0));
		return new FixedMatrixOperator("Swap", 2, 2, cm);
	}
	
	public static Operator CZ(){
		ComplexMatrix cm = new ComplexMatrix(4, 4);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, new Complex(1.0));
		cm.setElement(2,2, new Complex(1.0));
		cm.setElement(3,3, new Complex(-1.0));
		return new FixedMatrixOperator("CZ", 2, 2, cm);
	}
	
	public static Operator Wire(){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, new Complex(1.0));
		return new FixedMatrixOperator("Wire", 1, 1, cm);
	}
	
	
	public static Operator Measurement(){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(0,0, new Complex(1.0));
		cm.setElement(1,1, new Complex(1.0));
		Operator o = new FixedMatrixOperator("Measurement", 1, 1, cm);
		o.setTag(new Tag("Measurement", null));
		return o;
	}
	
	public static Operator Hadamard(){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(0,0,new Complex(1.0/Math.sqrt(2)));
		cm.setElement(1,0,new Complex(1.0/Math.sqrt(2)));
		cm.setElement(0,1,new Complex(1.0/Math.sqrt(2)));
		cm.setElement(1,1,new Complex(-1.0/Math.sqrt(2)));
		return new FixedMatrixOperator("Hadamard", 1, 1, cm);
	}
	
	public static Operator PauliX(){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(1,0,new Complex(1.0));
		cm.setElement(0,1,new Complex(1.0));
		return new FixedMatrixOperator("X", 1, 1, cm);
	}
	
	public static Operator PauliY(){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(1,0,new Complex(0.0, -1.0));
		cm.setElement(0,1,new Complex(0.0, 1.0));
		return new FixedMatrixOperator("Y", 1, 1, cm);
	}
	
	public static Operator PauliZ(){
		ComplexMatrix cm = new ComplexMatrix(2, 2);
		cm.setElement(1,0,new Complex(1.0));
		cm.setElement(0,1,new Complex(-1.0));
		return new FixedMatrixOperator("Z", 1, 1, cm);
	}
	
	public static Operator Toffoli(){
		ComplexMatrix cm = new ComplexMatrix(8, 8);
		for(int i = 0; i < 6; i++){
			cm.setElement(i, i, new Complex(1.0));
		}
		cm.setElement(6, 7, new Complex(1.0));
		cm.setElement(7, 6, new Complex(1.0));
		return new FixedMatrixOperator("Toffoli", 3, 3, cm);
	}
}
