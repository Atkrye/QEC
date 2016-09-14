package org.york.builders;

import org.york.yqcgp.QuantumSystem;
import org.york.yqcgp.maths.ComplexMatrix;


public abstract class Builder {

	public abstract void setup(MultiComponent comp);
	
	public abstract QuantumSystem build(MultiComponent comp);
	

	public QuantumSystem tensor(QuantumSystem a, ComplexMatrix c){
		for(int i = 0; i < a.layers.size(); i++){
			a.layers.set(i, a.layers.get(i).tensor(c));
		}
		return a;
	}
	
	public QuantumSystem tensor(ComplexMatrix c, QuantumSystem a){
		for(int i = 0; i < a.layers.size(); i++){
			a.layers.set(i, c.tensor(a.layers.get(i)));
		}
		return a;
	}
	
	public QuantumSystem combine(QuantumSystem a, QuantumSystem b){
		for(int i = 0; i < b.layers.size(); i++){
			boolean noTags = true;
			for(int j = 0; j < b.tags.get(i).length; j++){
				if(b.tags.get(i)[j] != null){
					noTags = false;
				}
			}
			if(noTags){
				a.layers.set(a.layers.size() - 1, b.layers.get(i).times(a.layers.get(a.layers.size() - 1)));
			}
			else{
				a.layers.add(b.layers.get(i));
				a.tags.add(b.tags.get(i));
			}
		}
		a.size += b.size;
		a.disconnectedOutputs += b.disconnectedOutputs;
		return a;
	}
}
