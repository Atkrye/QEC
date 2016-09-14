package org.york.yqcgp;


public abstract class QCGPElement {

	public void setup(){
		
	}
	
	public void reset(){
		
	}
	
	public abstract QuantumSystem build(); 
	
	public abstract QCGPElement copy() throws Exception;

	public abstract void mutateOp();

	public abstract void mutatePhase();

	public abstract void mutateEdge();

	public abstract void mutateInput();

	public void printBuild() {
		
	}
}
