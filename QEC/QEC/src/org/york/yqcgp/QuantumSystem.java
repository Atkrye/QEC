package org.york.yqcgp;

import java.util.ArrayList;
import java.util.Random;

import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;
import org.york.yqcgp.maths.TraceRun;
import org.york.yqcgp.operator.ConnectionMap;
import org.york.yqcgp.operator.Tag;
public class QuantumSystem
{
	Random rand = new Random();
	public ArrayList<ComplexMatrix> layers = new ArrayList<ComplexMatrix>();
	public ArrayList<Tag[]> tags = new ArrayList<Tag[]>();
	ArrayList<ComplexMatrix> currentLayer;
	ConnectionMap cm;
	ArrayList<Tag> currentTags;
	int layerSize = 0;
	int workingQubits = 0;
	Complex workingState0;
	Complex workingState1;
	public int size = 0;
	int currentLayerSize = 0;

	public int disconnectedOutputs = 0;
	public int disconnectedInputs = 0;
	
	public void setWorkingQubits(int num, Complex defaultZeroVal, Complex defaultOneVal){
		workingQubits = num;
		workingState0 = defaultZeroVal;
		workingState1 = defaultOneVal;
	}
	
	public void prepareLayer(){
		currentLayer = new ArrayList<ComplexMatrix>();
		currentTags = new ArrayList<Tag>();
		layerSize = 0;
		currentLayerSize = 0;
		this.cm = null;
	}
	
	public void addOperator(ComplexMatrix cm, Tag t){
		currentLayer.add(cm);
		layerSize += (int)(Math.log(cm.getHeight()) / Math.log(2));
		if(t == null){
			currentTags.add(new Tag("None", null));
		}
		else{
			currentTags.add(t);
		}
	}
	
	public void nodeAdded(){
		currentLayerSize++;
	}
	
	public void buildLayer(){
		Tag[] layerTags = new Tag[layerSize];
		int qubit = 0;
		boolean newLayer = false;
		ComplexMatrix cm = new ComplexMatrix(1,1);
		cm.setElement(0,0,new Complex(1.0));
		for(int i = 0; i < currentLayer.size(); i++){
			if(!currentTags.get(i).label.equals("None")){
				newLayer = true;
				layerTags[qubit] = currentTags.get(i);
			}
			qubit += (int)(Math.log(currentLayer.get(i).getHeight()) / Math.log(2));
			cm = cm.tensor(currentLayer.get(i));
		}
		if(this.cm != null){
			cm = cm.times(this.cm.generateMatrix(0, 0, 0.0));
			this.cm = null;
		}
		if(newLayer || layers.size() == 0){
			layers.add(cm);
			tags.add(layerTags);
		}
		else{
			layers.set(layers.size() -1, cm.times(layers.get(layers.size() - 1)));
		}
		size+= currentLayerSize;
		currentLayerSize = 0;
	}
	

	public TraceRun runTrace(ComplexMatrix inputs){
		for(int i = 0; i < workingQubits; i++){
			ComplexMatrix w = new ComplexMatrix(1, 2);
			w.setElement(0, 0, workingState0);
			w.setElement(0, 1, workingState1);
			inputs = inputs.tensor(w);
		}
		TraceRun trace = new TraceRun(inputs);
		for(int i = 0; i < layers.size(); i++){
			for(int qubit = 0; qubit < tags.get(i).length; qubit++){
				if(tags.get(i)[qubit] != null && tags.get(i)[qubit].label.equals("Measurement")){
					trace = trace.measure(qubit);
				}
			}
			trace = trace.apply(layers.get(i));
		}
		return trace;
	}
	
	public ComplexMatrix applyTag(ComplexMatrix c, Tag t, int qubits, int qubit){
		if(t.label.equals("Measurement")){
			boolean zero = true;
			double zeroProb = 0.0;
			double totalProb = 0.0;
			int counter = 0;
			int resetCount = (int)Math.pow(2, (qubits - 1) - qubit);
			System.out.println("Qubit " + qubit + ". Reset count " + resetCount);
			System.out.println(c);
			for(int q = 0; q < c.getHeight(); q++){
				if(c.getElement(0, q) != null){
					if(zero){
						zeroProb += c.getElement(0, q).mod();
					}
					totalProb += c.getElement(0, q).mod();
				}
				System.out.println(counter + " - " + zero);
				counter++;
				if(counter == resetCount){
					counter = 0;
					zero = ! zero;
				}
			}
			counter = 0;
			boolean measureZero = false;
			double adjustProb = Math.sqrt((totalProb - zeroProb) / totalProb);
			System.out.println(zeroProb + " vs. " + totalProb);
			if(rand.nextDouble() <= zeroProb / totalProb){
				measureZero = true;
				adjustProb = Math.sqrt(zeroProb / totalProb);
			}
			zero = true;
			for(int q = 0; q < c.getHeight(); q++){
				if(zero == measureZero){
					if(c.getElement(0, q) != null){
						c.setElement(0, q, c.getElement(0, q).divide(adjustProb));
					}
				}
				else{
					c.setElement(0, q, Complex.Zero());
				}
				counter++;

				System.out.println(counter + " - " + zero + " - " + measureZero);
				if(counter == resetCount){
					counter = 0;
					zero = ! zero;
				}
			}
			return c;
		}
		return c;
	}

	public void setConnectionMap(ConnectionMap cm) {
		this.cm = cm;
	}

	//ZX Graphs are built output -> Input
	public void buildZXLayer() {
		Tag[] layerTags = new Tag[layerSize];
		int qubit = 0;
		boolean newLayer = false;
		ComplexMatrix cm = new ComplexMatrix(1,1);
		cm.setElement(0,0,new Complex(1.0));
		for(int i = 0; i < currentLayer.size(); i++){
			if(!currentTags.get(i).label.equals("None")){
				newLayer = true;
				layerTags[qubit] = currentTags.get(i);
			}
			qubit += (int)(Math.log(currentLayer.get(i).getHeight()) / Math.log(2));
			cm = cm.tensor(currentLayer.get(i));
		}
		if(this.cm != null){
			cm = this.cm.generateMatrix(0, 0, 0.0).times(cm);
			this.cm = null;
		}
		if(newLayer || layers.size() == 0){
			layers.add(0, cm);
			tags.add(0, layerTags);
		}
		else{
			layers.set(0, layers.get(0).times(cm));
		}
		size+= currentLayerSize;
		currentLayerSize = 0;
	}
}
