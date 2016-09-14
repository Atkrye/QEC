package org.york.ants;

import org.york.yqcgp.maths.ComplexMatrix;
import org.york.yqcgp.operator.Operator;
import org.york.yqcgp.operator.Tag;

public class AntNode {

	private Operator op;
	private double phase;
	private int x;
	private int y;
	int counter = 0;
	public int index;
	
	public AntNode(int x, int y, Operator op, double phase, int index){
		this.x = x;
		this.y = y;
		this.index = index;
		this.setOp(op);
		this.setPhase(phase);
	}
	
	@Override
	public String toString(){
		return x + ":" + y + "-" + op.getName() + " with phase " + phase;
	}

	public Operator getOp() {
		return op;
	}

	public void setOp(Operator op) {
		this.op = op;
	}

	public double getPhase() {
		return phase;
	}

	public void setPhase(double phase) {
		this.phase = phase;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}

	public ComplexMatrix getMatrix(int inputs, int outputs) {
		return op.generateMatrix(inputs, outputs, this.phase);
	}
	
	public Tag getTag(){
		return op.getTag();
	}
}
