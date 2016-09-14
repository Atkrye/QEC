package org.york.yqcgp.operator;

import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;

public class ControlledOperator extends Operator{

	Operator superOp;
	public ControlledOperator(Operator superOp) {
		super("C-" + superOp.getName());
		this.superOp = superOp;
	}

	@Override
	public ComplexMatrix generateMatrix(int in, int out, double phase) {
		if(in > superOp.getMaxInputs()){
			ComplexMatrix m = superOp.generateMatrix(in - 1, out - 1, phase);
			ComplexMatrix m2 = new ComplexMatrix(m.getHeight() * 2, m.getHeight() * 2);
			for(int i = 0; i < m.getHeight(); i++){
				m2.setElement(i, i , new Complex(1.0));
			}
			for(int i = m.getHeight(); i < m.getHeight() * 2; i++ ){
				for(int j = m.getHeight(); j < m.getHeight() * 2; j++ ){
					m2.setElement(i, j, m.getElement(i - m.getHeight(), j - m.getHeight()));
				}
			}
			return m2;
		}
		else{
			return superOp.generateMatrix(in, out, phase);
		}
	}

	@Override
	public int getMinInputs() {
		return superOp.getMinInputs();
	}

	@Override
	public int getMaxInputs() {
		return superOp.getMaxInputs() + 1;
	}

	@Override
	public int getMinOutputs() {
		return superOp.getMinOutputs();
	}

	@Override
	public int getMaxOutputs() {
		return superOp.getMaxOutputs() + 1;
	}
	
	

}
