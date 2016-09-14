package org.york.builders;

import org.york.ants.QAntOpt;
import org.york.ants.QRandWalk;
import org.york.yqcgp.QuantumSystem;
import org.york.yqcgp.maths.ComplexMatrix;
import org.york.yqcgp.operator.AntLibrary;
import org.york.yqcgp.operator.Operator;
import org.york.yqcgp.operator.Tag;
import org.york.zx.SuperGraph;

public class TeleportationBuilder extends Builder{

	@Override
	public void setup(MultiComponent comp) {
		Object[] args = null;
		if(comp.getClass().equals(QAntOpt.class) ||comp.getClass().equals(QRandWalk.class)){
			args = new Object[2];
			args[0] = 4;
			args[1] = 2;
		}
		else if(comp.getClass().equals(SuperGraph.class)){
			args = new Object[7];
			//Layers
			args[0] = 5;
			//Layer Width
			args[1] = 5;
			//Arity
			args[2] = 2;
			//Inputs
			args[3] = 2;
			//Working
			args[4] = 0;
			//Outputs
			args[5] = 2;
			//Max complexity
			args[6] = 3;
		}
		comp.addComponent("EPR", args);
		comp.addComponent("Alice", args);
		if(comp.getClass().equals(QAntOpt.class) ||comp.getClass().equals(QRandWalk.class)){
			args[1] = 3;
		}
		if(comp.getClass().equals(SuperGraph.class)){
			args[3] = 3;
			args[5] = 3;
			args[6] = 4;
		}
		comp.addComponent("Bob", args);
	}

	@Override
	public QuantumSystem build(MultiComponent comp) {
		Operator wire = AntLibrary.Wire();
		ComplexMatrix wireMatrix = wire.generateMatrix(1, 1, 0.0);
		QuantumSystem epr = comp.buildComponent("EPR");
		QuantumSystem alice = comp.buildComponent("Alice");
		QuantumSystem bob = comp.buildComponent("Bob");
		
		QuantumSystem meas = new QuantumSystem();
		meas.prepareLayer();
		meas.addOperator(wireMatrix, new Tag("Measurement", null));
		meas.addOperator(wireMatrix, new Tag("Measurement", null));
		meas.addOperator(wireMatrix, null);
		meas.buildLayer();
		QuantumSystem q = combine(combine(tensor(wireMatrix, epr), tensor(alice, wireMatrix)), combine(meas, bob));
		return q;
	}
	
	

}
