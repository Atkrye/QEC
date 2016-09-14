package org.york.yqcgp.operator;

import org.york.yqcgp.maths.ComplexMatrix;

public class FixedMatrixOperator extends Operator
{
	private ComplexMatrix matrix;
	private int inputs;
	private int outputs;
	
	public FixedMatrixOperator(String name, int in, int out, ComplexMatrix cm){
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
			if(in == getMaxInputs()){
				ComplexMatrix outM = GreenOperator.generate(outputs, out, 0.0);
				return outM.times(this.matrix);
			}
			else if(out == getMaxOutputs()){
				ComplexMatrix inM = GreenOperator.generate(in, inputs, 0.0);
				return this.matrix.times(inM);
			}
			else{
				ComplexMatrix inM = GreenOperator.generate(in, inputs, 0.0);
				ComplexMatrix outM = GreenOperator.generate(outputs, out, 0.0);
				return outM.times(this.matrix).times(inM);
			}
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
