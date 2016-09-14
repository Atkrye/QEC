package org.york.yqcgp.operator;

import org.york.yqcgp.maths.ComplexMatrix;

public abstract class Operator
{
	private String id;
	Tag tag = null;
	public Operator(String name){
		this.id = name;
	}	
	
	public String getName(){
		return this.id;
	}
	
	public abstract ComplexMatrix generateMatrix(int in, int out, double phase);
	public abstract int getMinInputs();
	public abstract int getMaxInputs();
	public abstract int getMinOutputs();
	public abstract int getMaxOutputs();
	
	public boolean isFixedInputs(){
		return getMinInputs() == getMaxInputs();
	}
	
	public void setTag(Tag tag){
		this.tag = tag;
	}
	
	public Tag getTag(){
		return tag;
	}
	
	public boolean isFixedOutputs(){
		return getMinOutputs() == getMaxOutputs();
	}

	public static Operator Wire() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
