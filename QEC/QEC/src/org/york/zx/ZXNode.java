package org.york.zx;

import org.york.yqcgp.maths.ComplexMatrix;
import org.york.yqcgp.operator.Operator;
import org.york.yqcgp.operator.Tag;

public class ZXNode {


	private Operator op;
	private double phase;
	private int x;
	private int y;
	ZXNodeVertex[] inputs;
	boolean active = false;
	
	public ZXNode(int x, int y, Operator op, double phase, int arity){
		this.x = x;
		this.y = y;
		this.setOp(op);
		this.setPhase(phase);
		this.inputs = new ZXNodeVertex[arity];
	}
	
	@Override
	public String toString(){
		return x + ":" + y + "-" + op.getName() + " with phase " + phase;
	}
	
	public ZXNode copy(){
		ZXNode n = new ZXNode(x, y, op, phase, inputs.length);
		for(int i = 0; i < inputs.length; i++){
			if(inputs[i] != null){
				n.setInput(i, inputs[i].copy());
			}
		}
		n.active = this.active;
		return n;
	}

	public Operator getOp() {
		return op;
	}
	
	public void setInput(int index, ZXNodeVertex input){
		this.inputs[index] = input;
	}
	
	public ZXNodeVertex getInput(int index){
		return this.inputs[index];
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
