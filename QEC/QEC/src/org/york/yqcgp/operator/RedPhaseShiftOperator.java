package org.york.yqcgp.operator;

import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;

public class RedPhaseShiftOperator extends Operator
{
	int maxArity;
	public RedPhaseShiftOperator(int n){
		super("Red");
		this.maxArity = n;
	}

	@Override
	public ComplexMatrix generateMatrix(int in, int out, double phase){
		ComplexMatrix hadamard = new ComplexMatrix(2,2);
		hadamard.setElement(0,0,new Complex(1.0/Math.sqrt(2)));
		hadamard.setElement(1,0,new Complex(1.0/Math.sqrt(2)));
		hadamard.setElement(0,1,new Complex(1.0/Math.sqrt(2)));
		hadamard.setElement(1,1,new Complex(-1.0/Math.sqrt(2)));
		ComplexMatrix inM = new ComplexMatrix(1,1);
		inM.setElement(0,0,new Complex(1.0));
		ComplexMatrix oM = new ComplexMatrix(1,1);
		oM.setElement(0,0,new Complex(1.0));
		for(int input = 0; input < in; input++){
			inM = inM.tensor(hadamard);
		}
		for(int output = 0; output < out; output++){
			oM = oM.tensor(hadamard);
		}
		ComplexMatrix green = GreenOperator.generate(in, out, phase);
		
		return oM.times(green).times(inM);
	}

	@Override
	public int getMinInputs(){
		return 0;
	}

	@Override
	public int getMaxInputs(){
		return maxArity;
	}

	@Override
	public int getMinOutputs(){
		return maxArity;
	}

	@Override
	public int getMaxOutputs()
	{
		return maxArity;
	}

}
