package org.york.yqcgp.operator;

import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;

public class AntOperator extends Operator
{
	private ComplexMatrix matrix;
	private int inputs;
	private int outputs;
	
	public AntOperator(String name, int in, int out, ComplexMatrix cm){
		super(name);
		this.inputs = in;
		this.outputs = out;
		this.matrix = cm;
	}
	
	@Override
	public ComplexMatrix generateMatrix(int in, int out, double phase){
		if(in == getMaxInputs() && out == getMaxOutputs()){
			return this.matrix;
		}
		else{
			ComplexMatrix cm = new ComplexMatrix((int)Math.pow(2, in), (int)Math.pow(2, in));
			for(int i = 0; i < (int)Math.pow(2, in); i++){
				cm.setElement(i, i, new Complex(1.0));
			}
			return cm;
		}
	}
	
	@Override
	public int getMinInputs(){
		return 0;
	}
	
	@Override
	public int getMaxInputs(){
		return inputs;
	}
	
	@Override
	public int getMinOutputs(){
		return outputs;
	}

	@Override
	public int getMaxOutputs()
	{
		return outputs;
	}
}
