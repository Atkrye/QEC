package org.york.yqcgp.operator;

import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;

public class HadamardOperator extends Operator
{
	int maxArity;
	public HadamardOperator(int n){
		super("Green");
		this.maxArity = n;
	}
	
	public static ComplexMatrix generate(int in, int out, double phase){
		ComplexMatrix res = new ComplexMatrix((int)Math.pow(2, in), (int)Math.pow(2, out));
		if(out == 0){
			double val = 1 / Math.sqrt(Math.pow(2, in));
			for(int i = 0; i < Math.pow(2, in); i++){
				res.setElement(i, 0, new Complex(val));
			}
			return res;
		}
		if(in == 0){
			Complex second = Complex.ePow(phase);
			Complex first = new Complex(Math.sqrt(1.0 - second.mod() * second.mod()));
			res.setElement(0, 0, first);
			res.setElement(0, 1, second);
			return res;
		}
		res.setElement(0,0,new Complex(1.0));
		res.setElement((int)Math.pow(2, in) - 1, (int)Math.pow(2, out) - 1, Complex.ePow(phase));
		return res;
	}
	
	@Override
	public ComplexMatrix generateMatrix(int in, int out, double phase){
		return generate(in, out, phase);
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
