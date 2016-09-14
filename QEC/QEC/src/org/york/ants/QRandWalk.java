package org.york.ants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.york.builders.Builder;
import org.york.builders.MultiComponent;
import org.york.builders.SingleCircuit;
import org.york.yqcgp.QuantumSystem;
import org.york.yqcgp.YabukiEvaluator;
import org.york.yqcgp.QCGPElement;
import org.york.yqcgp.operator.ConnectionMap;
import org.york.yqcgp.operator.Operator;
import org.york.yqcgp.operator.OperatorLibrary;

public class QRandWalk extends QCGPElement implements MultiComponent{
	Random rand = new Random();
	Operator[] functionSet;
	public static final double MIN = 0.01;
	public static final double MAX = 1.0;
	int searchSpaceWidth;
	ArrayList<AntNode> nodes = new ArrayList<AntNode>();
	ArrayList<Double[][]> edges;
	int qubits;
	private AntNode[] inputs;
	private AntNode[] outputs;
	int evalAnt = 0;
	int functionNodes = 0;
	ArrayList<ArrayList<ArrayList<AntNode>>> paths;
	public Map<String, RandWalkComp> components = new HashMap<String, RandWalkComp>();
	Builder builder;
	
	public QRandWalk(int searchSpaceWidth, Operator[] functionSet, int qubits){
		this(functionSet, new SingleCircuit(searchSpaceWidth, 0, 2, qubits, qubits));
		this.searchSpaceWidth = searchSpaceWidth;
		this.qubits = qubits;
	}
	
	public QRandWalk(Operator[] functionSet, Builder builder){
		this.functionSet = functionSet;
		this.builder = builder;
	}
	
	
	
	public void calculateFunctionNodes(){
		functionNodes = 0;
		for(Operator op : functionSet){
			System.out.println(op.getName());
			System.out.println(op.getMaxInputs());
			System.out.println("Produces : " + (int)Math.floor((double)qubits / (double)op.getMaxInputs()));
			int num = (int)Math.floor((double)qubits / (double)op.getMaxInputs());
			functionNodes += num;
		}
		System.out.println("Function nodes per layer: " + functionNodes);
	}
	
	public void prepare(){

		builder.setup(this);
		this.calculateFunctionNodes();
		
		inputs = new AntNode[qubits];
		nodes = new ArrayList<AntNode>();
		int id = 0;
		Operator w = OperatorLibrary.Wire();
		for(int i = 0; i < qubits; i++){
			inputs[i] = new AntNode(0, i, w, 0.0, id);
			nodes.add(inputs[i]);
			id++;
		}
		for(int i = 1; i < searchSpaceWidth + 1; i++){
			int counter = 0;
			for(Operator op : functionSet){
				int num = (int)Math.floor((double)qubits / (double)op.getMaxInputs());
				for(int j = 0; j < num; j++){
					nodes.add(new AntNode(i, counter, op, 0.0, id));
					id++;
					counter++;
				}
			}
		}

		outputs = new AntNode[qubits];
		for(int i = 0; i < qubits; i++){
			outputs[i] = new AntNode(searchSpaceWidth + 1, i, w, 0.0, id);
			nodes.add(outputs[i]);
			id++;
		}
		edges = new ArrayList<Double[][]>();
		for(int q = 0; q < qubits; q++){
			Double[][] qEdge = new Double[nodes.size()][nodes.size()];
			for(AntNode n : nodes){
				for(AntNode n2 : nodes){
					if(n2.getX() > n.getX()){
						qEdge[n.index][n2.index] = 1.0;
					}
					else{
						qEdge[n.index][n2.index] = 0.0;
					}
				}
			}
			edges.add(qEdge);
		}
		resetCounters();
	}
	
	public void resetCounters(){
		for(AntNode node : nodes){
			node.counter = 0;
		}
	}
	

	@Override
	public QuantumSystem build() {
		ArrayList<ArrayList<AntNode>> currentPath = paths.get(evalAnt);
		int size = 0;

		QuantumSystem q = new QuantumSystem();
		
		ArrayList<AntNode> pipeIn = new ArrayList<AntNode>();
		ArrayList<AntNode> pipeOut = new ArrayList<AntNode>();
		ArrayList<AntNode> newPipe = new ArrayList<AntNode>();
		ArrayList<Integer> currentIndexes = new ArrayList<Integer>();
		for(int qu = 0; qu < qubits; qu++){
			pipeIn.add(currentPath.get(qu).get(1));
			currentIndexes.add(1);
			newPipe.add(null);
		}

		ConnectionMap cm = new ConnectionMap();
		q.prepareLayer();
		for(int i = 0; i < searchSpaceWidth; i++){
			boolean layerActive = false;
			for(int j = 0; j < functionNodes; j++){
				AntNode node = nodes.get(qubits + (i * functionNodes) + j);
				if(pipeIn.contains(node)){
					int inputs = 0;
					int outputs = 0;
					int path = 0;
					for(AntNode v : pipeIn){
						if(v.equals(node)){
							inputs++;
							outputs++;
							currentIndexes.set(path, currentIndexes.get(path) + 1);
							newPipe.set(path, currentPath.get(path).get(currentIndexes.get(path)));
							pipeOut.add(v);
						}
						path++;
					}
					if(inputs > 0){
						layerActive = true;
						q.nodeAdded();
						size++;
						q.addOperator(node.getMatrix(inputs, outputs), node.getTag());
					}
				}
			}
			if(layerActive){
				int path = 0;
				for(AntNode v : pipeIn){
					if(!pipeOut.contains(v)){
						pipeOut.add(v);
						newPipe.set(path, v);
						q.addOperator(OperatorLibrary.Wire().generateMatrix(1, 1, 0.0), null);
					}
					path++;
				}
				cm.buildAntMap(pipeIn, pipeOut);
				q.setConnectionMap(cm);
				q.buildLayer();
				pipeIn.clear();
				for(AntNode v : newPipe){
					pipeIn.add(v);
				}
				pipeOut.clear();
				q.prepareLayer();
			}
		}

		//Outputs layer merges connections to outputs
		boolean layerActive = false;
		int disconnects = 0;
		for(int j = 0; j < qubits; j++){
			int inputs = 0;
			for(AntNode v : pipeIn){
				if(v.equals(this.outputs[j])){
					inputs++;
					pipeOut.add(v);
				}
			}
			layerActive = true;
			if(inputs == 0){
				disconnects++;
			}
			q.addOperator(this.outputs[j].getMatrix(inputs, 1), this.outputs[j].getTag());
		}
		q.disconnectedOutputs = disconnects;
		if(layerActive){
			int path = 0;
			for(AntNode v : pipeIn){
				if(!pipeOut.contains(v)){
					pipeOut.add(v);
					newPipe.set(path, v);
					q.addOperator(OperatorLibrary.Wire().generateMatrix(1, 1, 0.0), null);
					path++;
				}
			}

			cm.buildAntMap(pipeIn, pipeOut);
			q.setConnectionMap(cm);
			q.buildLayer();
		}
		q.size = size;
		return q;
	}

	@Override
	public QCGPElement copy() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mutateOp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mutatePhase() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mutateEdge() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mutateInput() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addComponent(String name, Object[] args) {
		int searchSpaceWidth = (Integer)args[0];
		int qubits = (Integer)args[1];
		RandWalkComp comp = new RandWalkComp(name, this, searchSpaceWidth, qubits);
		comp.prepare();
		components.put(name, comp);
		System.out.println("Added " + name);
	}

	@Override
	public QuantumSystem buildComponent(String name) {
		return components.get(name).buildCircuitNu(evalAnt);
	}
	
	@Override
	public QuantumSystem buildComponentBest(String name) {
		return components.get(name).buildCircuit(-1);
	}
	
}
