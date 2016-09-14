package org.york.yqcgp;

import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;

public class QFTEvaluator extends YabukiEvaluator{

	ComplexMatrix Fn;
	public QFTEvaluator(int qubits) {
		super(SolutionGenerator.QFT(0, qubits, 1000000000));
		// TODO Auto-generated constructor stub
		double N = Math.pow(2, qubits);
		ComplexMatrix Fn = new ComplexMatrix((int)Math.pow(2, qubits), (int)Math.pow(2, qubits));
		for(int i = 0; i < (int)Math.pow(2, qubits); i++){
			for(int j = 0; j < (int)Math.pow(2, qubits); j++){
				Fn.setElement(i, j, Complex.ePow((2.0 * Math.PI * i * j) / N).times(1.0 / Math.sqrt(N)));
			}
		}
		this.Fn = Fn;
	}

	@Override
	public double evaluateSystem(int sessionID, QuantumSystem q){
		counters.put(sessionID, counters.get(sessionID) + 1);
		ComplexMatrix Qm = q.layers.get(0);
		return (1.0 / (1.0 + Fn.normalize().minus(Qm.normalize()).size()));
	}
}
