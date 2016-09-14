package org.york.zx;

import java.util.ArrayList;

import org.york.yqcgp.QuantumSystem;
import org.york.yqcgp.QCGPElement;
import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;
import org.york.yqcgp.operator.ConnectionMap;
import org.york.yqcgp.operator.Operator;
import org.york.yqcgp.operator.OperatorLibrary;

public class ZXGraph extends QCGPElement{


	ZXNode[][] nodes;
	int layers;
	int layerWidth;
	private ZXCalcCGP zxcgp;
	int arity;
	private ZXNode[] inputs;
	private ZXNode[] outputs;
	int in;
	int ou;
	int working;
	private boolean ready = false;
	private int maxComplexity;
	private boolean activeChanged = false;
	
	public ZXGraph(ZXCalcCGP zxcgp, int layers, int layerWidth, int arity, int inputs, int workingBits, int outputs, int maxComplexity){
		this.layers = layers;
		this.layerWidth = layerWidth;
		this.zxcgp = zxcgp;
		this.arity = arity;
		this.in = inputs;
		this.ou = outputs;
		this.working = workingBits;
		this.maxComplexity = maxComplexity;
	}
	
	public ZXGraph copy() throws Exception{
		if(ready){
			ZXGraph g = new ZXGraph(this.zxcgp, this.layers, this.layerWidth, this.arity, this.in, this.working, this.ou, this.maxComplexity);
			g.setup(nodes, inputs, outputs);
			return g;
		}
		else{
			throw new Exception("Graph not ready!");
		}
	}
	
	@Override
	public void setup(){
		ready = true;
		generateIO();
		populate();
		generateEdges();
	}
	
	public void setup(ZXNode[][] nodes, ZXNode[] inputs, ZXNode[] outputs){
		this.nodes = new ZXNode[layerWidth][layers];
		this.inputs = new ZXNode[in + working];
		this.outputs = new ZXNode[ou];
		for(int i = 0; i < layerWidth; i++){
				for(int j = 0; j < layers; j++){
					if(nodes[i][j] != null){
						this.nodes[i][j] = nodes[i][j].copy();
					}
				}
		}
		for(int i = 0; i < in + working; i++){
			if(inputs[i] != null){
				this.inputs[i] = inputs[i].copy();
			}
		}
		for(int i = 0; i < ou; i++){
			if(outputs[i] != null){
				this.outputs[i] = outputs[i].copy();
			}
		}
		ready = true;
	}
	
	@Override
	public void reset(){
		setup();
	}
	
	public void generateIO(){
		this.inputs = new ZXNode[in + working];
		for(int i = 0; i < (in + working); i++){
			this.inputs[i] = new ZXNode(i, -1, OperatorLibrary.Wire(), 0.0, 1);
		}
		this.outputs = new ZXNode[ou];
		for(int i = 0; i < ou; i++){
			this.outputs[i] = new ZXNode(i, layers, OperatorLibrary.Wire(), 0.0, 1);
		}
	}
	
	public void populate(){
		nodes = new ZXNode[layerWidth][layers];
		for(int i = 0; i < layerWidth; i++){
			for(int j = 0; j < layers; j++){
				nodes[i][j] = new ZXNode(i, j, zxcgp.randomOp(), zxcgp.randomPhase(), arity);
			}
		}
	}
	
	public void generateEdges(){
		for(int i = 0; i < layerWidth; i++){
				for(int j = 0; j < layers; j++){
					for(int k = 0; k < arity; k++){
						mutateEdge(i, j, k);
					}
				}
		}
		for(int i = 0; i < ou; i++){
			mutateOutput(i);
		}

		updateActiveNodes();
	}
	
	public ZXNode getNode(int x, int y){
		if(y == -1){
			return inputs[x];
		}
		if(y == layers){
			return outputs[x];
		}
		return nodes[x][y];
	}
	
	@Override
	public QuantumSystem build(){
		QuantumSystem q = new QuantumSystem();
		q.setWorkingQubits(working, new Complex(1.0), new Complex(0.0));
		
		ArrayList<ZXNodeVertex> pipeIn = new ArrayList<ZXNodeVertex>();
		ArrayList<ZXNodeVertex> pipeOut = new ArrayList<ZXNodeVertex>();
		ArrayList<ZXNodeVertex> newPipe = new ArrayList<ZXNodeVertex>();
		int size = 0;
		
		for(int i = 0; i < ou; i++){
			if(outputs[i].getInput(0) != null){
				pipeIn.add(outputs[i].getInput(0));
			}
		}

		ConnectionMap cm = new ConnectionMap();
		q.prepareLayer();
		for(int i = layers - 1; i > -1; i--){
			boolean layerActive = false;
			int layerComplexity = 0;
			for(int j = 0; j < layerWidth; j++){
				if(containsNode(pipeIn, getNode(j, i))){
					int inputs = 0;
					int outputs = 0;
					for(ZXNodeVertex v : pipeIn){
						if(v.equals(getNode(j, i).getX(), getNode(j, i).getY())){
							outputs++;
							for(int iIndex = 0; iIndex < arity; iIndex++){
								if(getNode(j, i).getInput(iIndex) != null){
									inputs++;
									newPipe.add(getNode(j, i).getInput(iIndex));
								}
							}
							pipeOut.add(v);
						}
					}
					if(inputs > 0 || outputs > 0){
						layerActive = true;
						q.nodeAdded();
						size++;
						q.addOperator(getNode(j, i).getMatrix(inputs, outputs), getNode(j, i).getTag());
						layerComplexity += inputs;
					}
				}
			}
			if(layerActive){
				ArrayList<ZXNodeVertex> toAdd = new ArrayList<ZXNodeVertex>();
				for(ZXNodeVertex v : pipeIn){
					if(!pipeOut.contains(v)){
						toAdd.add(v);
						q.addOperator(OperatorLibrary.Wire().generateMatrix(1, 1, 0.0), null);
						layerComplexity++;
					}
				}
				for(ZXNodeVertex v : toAdd){
					newPipe.add(v);
					pipeOut.add(v);
				}
				if(layerComplexity > maxComplexity){
					return deadCircuit(in + working, ou);
				}
				cm.buildZXMap(pipeIn, pipeOut);
				q.setConnectionMap(cm);
				q.buildZXLayer();
				pipeIn = newPipe;
				pipeOut.clear();
				newPipe = new ArrayList<ZXNodeVertex>();
				q.prepareLayer();
			}
		}

		//Input layer
		boolean layerActive = false;
		int disconnects = 0;
		int layerComplexity = 0;
		for(int j = 0; j < in; j++){
			int outputs = 0;
			for(ZXNodeVertex v : pipeIn){
				if(v.equals(this.inputs[j].getX(), this.inputs[j].getY())){
					outputs++;
					pipeOut.add(v);
				}
			}
			layerActive = true;
			if(outputs == 0){
				disconnects++;
			}
			layerComplexity += outputs;
			q.addOperator(this.inputs[j].getMatrix(1, outputs), this.inputs[j].getTag());
		}
		q.disconnectedInputs = disconnects;
		if(layerActive){
			ArrayList<ZXNodeVertex> toAdd = new ArrayList<ZXNodeVertex>();
			for(ZXNodeVertex v : pipeIn){
				if(!pipeOut.contains(v)){
					toAdd.add(v);
					q.addOperator(OperatorLibrary.Wire().generateMatrix(1, 1, 0.0), null);
					layerComplexity++;
				}
			}
			for(ZXNodeVertex v : toAdd){
				newPipe.add(v);
				pipeOut.add(v);
			}
			if(layerComplexity > maxComplexity){
				return deadCircuit(in + working, ou);
			}

			cm.buildZXMap(pipeIn, pipeOut);
			q.setConnectionMap(cm);
			q.buildZXLayer();
		}
		
		q.size = size;
		
		
		return q;
	}
	
	private boolean containsNode(ArrayList<ZXNodeVertex> pipeIn, ZXNode zxNode){
		for(ZXNodeVertex v : pipeIn){
			if(v != null && v.equals(zxNode.getX(), zxNode.getY())){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void mutatePhase(){
		double phase = zxcgp.randomPhase();
		ZXNode n = getNode(zxcgp.rand.nextInt(layerWidth), zxcgp.rand.nextInt(layers));
		if(n.active){
			this.activeChanged = true;
		}
		n.setPhase(phase);
	}
	
	@Override
	public void mutateOp(){
		Operator op = zxcgp.randomOp();
		ZXNode n = getNode(zxcgp.rand.nextInt(layerWidth), zxcgp.rand.nextInt(layers));
		if(n.active){
			this.activeChanged = true;
		}
		n.setOp(op);
	}
	
	@Override
	public void mutateEdge(){
		ZXNode n = getNode(zxcgp.rand.nextInt(layerWidth), zxcgp.rand.nextInt(layers));
		if(n.active){
			this.activeChanged = true;
		}
		mutateEdge(n.getX(), n.getY(), zxcgp.rand.nextInt(arity));
	}
	
	public void mutateEdge(int x, int y, int index){
		ZXNodeVertex v = zxcgp.randomVertex(-1, y, layerWidth, in, true);
		getNode(x, y).setInput(index, v);
	}
	
	@Override
	public void mutateInput(){
		mutateOutput(zxcgp.rand.nextInt(ou));
	}
	
	public void mutateOutput(int output){
		ZXNodeVertex v = zxcgp.randomVertex(-1, layers, layerWidth, in, false);
		ZXNode n = getOutput(output);
		if(n.active){
			this.activeChanged = true;
		}
		n.setInput(0, v);
	}

	public ZXNode getInput(int i) {
		return inputs[i];
	}

	public ZXNode getOutput(int i) {
		return outputs[i];
	}
	
	public QuantumSystem deadCircuit(int inputs, int outputs){
		System.out.println("DEAD");
		ComplexMatrix u = new ComplexMatrix((int)Math.pow(2, outputs), (int)Math.pow(2, inputs));
		QuantumSystem q = new QuantumSystem();
		q.prepareLayer();
		q.addOperator(u, null);
		q.buildLayer();
		return q;
	}
	
	public boolean circuitChange(){
		if(activeChanged){
			updateActiveNodes();
			activeChanged = false;
			return true;
		}
		return false;
	}
	
	public void updateActiveNodes(){
		
		for(ZXNode[] nodeList : nodes){
			for(ZXNode node : nodeList){
				node.active = false;
			}
		}
		ArrayList<ZXNode> nodeChecks = new ArrayList<ZXNode>();
		for(ZXNode o : outputs){
			nodeChecks.add(o);
		}
		while(!nodeChecks.isEmpty()){
			ArrayList<ZXNode> toDrop = new ArrayList<ZXNode>();
			ArrayList<ZXNode> toAdd = new ArrayList<ZXNode>();
			for(ZXNode node : nodeChecks){
				node.active = true;
				for(ZXNodeVertex v : node.inputs){
					if(v != null && v.getY() < node.getY()){
						ZXNode newNode = getNode(v.getX(), v.getY());
						toAdd.add(newNode);
					}
				}
				toDrop.add(node);
			}
			for(ZXNode d : toDrop){
				nodeChecks.remove(d);
			}
			for(ZXNode a : toAdd){
				nodeChecks.add(a);
			}
		}
	}
	
	public boolean isRunnable(){

		ArrayList<ZXNodeVertex> pipeIn = new ArrayList<ZXNodeVertex>();
		
		for(int i = 0; i < ou; i++){
			if(outputs[i].getInput(0) != null){
				pipeIn.add(outputs[i].getInput(0));
			}
		}
		int layer = layers - 1;
		while(pipeIn.size() <= maxComplexity){
			if(layer == -1){
				return true;
			}
			ArrayList<ZXNodeVertex> toAdd = new ArrayList<ZXNodeVertex>();
			ArrayList<ZXNodeVertex> toDel = new ArrayList<ZXNodeVertex>();
			for(int j = 0; j < layerWidth; j++){
				if(containsNode(pipeIn, getNode(j, layer))){
					for(ZXNodeVertex v : pipeIn){
						if(v.equals(getNode(j, layer).getX(), getNode(j, layer).getY())){
							for(int iIndex = 0; iIndex < arity; iIndex++){
								if(getNode(j, layer).getInput(iIndex) != null){
									toAdd.add(getNode(j, layer).getInput(iIndex));
								}
							}
							toDel.add(v);
						}
					}
				}
			}
			for(ZXNodeVertex v : toAdd){
				pipeIn.add(v);
			}
			for(ZXNodeVertex v : toDel){
				pipeIn.remove(v);
			}
			layer--;
		}
		return false;
	}

}