package org.york.builders;

import org.york.ants.QAntOpt;
import org.york.ants.QRandWalk;
import org.york.yqcgp.QuantumSystem;
import org.york.zx.SuperGraph;

public class SingleCircuit extends Builder{

	private int layers;
	private int layerWidth;
	private int qubits;
	private int arity;
	private int maxComplexity;
	public SingleCircuit(int layers, int layerWidth, int arity, int qubits, int maxComplexity){
		this.layers = layers;
		this.layerWidth = layerWidth;
		this.qubits = qubits;
		this.arity = arity;
		this.maxComplexity = maxComplexity;
	}
	
	@Override
	public void setup(MultiComponent comp) {
		Object[] args = null;
		if(comp.getClass().equals(QAntOpt.class) || comp.getClass().equals(QRandWalk.class)){
			args = new Object[2];
			args[0] = layers;
			args[1] = qubits;
		}
		if(comp.getClass().equals(SuperGraph.class)){
			args = new Object[7];
			args[0] = layers;
			args[1] = layerWidth;
			args[2] = arity;
			args[3] = qubits;
			args[4] = 0;
			args[5] = qubits;
			args[6] = maxComplexity;
		}
		comp.addComponent("Main", args);
	}

	@Override
	public QuantumSystem build(MultiComponent comp) {
		return comp.buildComponent("Main");
	}

}
