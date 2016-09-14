package org.york.ants;

import java.util.ArrayList;

import javax.swing.JFrame;

import org.york.yqcgp.QuantumSystem;
import org.york.yqcgp.operator.ConnectionMap;
import org.york.yqcgp.operator.Operator;
import org.york.yqcgp.operator.OperatorLibrary;

/** The AntComponent class is a self-contained quantum domain which a set of ants/qubits can explore. This can
 * be the only component in a QAnt problem (e.g. for learning a Bell-Pair generator) or it can be one of many 
 * (e.g. exploring the "Alice" component of a teleportation circuit).
 * @see Usage:
 * An AntComponent should be prepared using the prepare method before each run of a Quantum Ant algorithm. This is to reset
 * the domain + pheromone levels and set up the domain (if it is the first run). 
 * @author Tim Atkinson - tja511@york.ac.uk - 12/09/2016
 *
 */
public class AntComponent {

	int searchSpaceWidth;
	ArrayList<AntNode> nodes = new ArrayList<AntNode>();
	ArrayList<Double[][]> edges;
	int qubits;
	AntNode[] inputs;
	private AntNode[] outputs;
	int functionNodes = 0;
	QAntOpt parent;
	double bestScore = 0.0;
	double bestSizeScore = 0.0;
	ArrayList<ArrayList<AntNode>> bestPath;
	ArrayList<ArrayList<ArrayList<AntNode>>> paths = new ArrayList<ArrayList<ArrayList<AntNode>>>();
	AntDisplay applet;
	public String name;
	int displayCounter = 0;
	
	public boolean display = true;
	
	/** Constructor simply stores params
	 * @param name The name of the component, e.g. "Alice" for the "Alice" component of a teleportation circuit
	 * @param parent The parent QuantumAnt algorithm that spawned this component
	 * @param searchSpaceWidth The horizontal width (number of layers) that the component explores
	 * @param qubits The number of qubits ("Ants") that the component uses
	 */
	public AntComponent(String name, QAntOpt parent, int searchSpaceWidth, int qubits){
		this.name = name;
		this.parent = parent;
		this.searchSpaceWidth = searchSpaceWidth;
		this.qubits = qubits;
		
	}
	
	public void display(){
		if(display){
	        applet = new AntDisplay(this);
	        applet.init();
	
	        JFrame frame = new JFrame();
	        frame.getContentPane().add(applet);
	        frame.setTitle(name + " display");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.pack();
	        frame.setSize(1000, 1000);
	        frame.setVisible(true);
		}
	}
	
	public void openDisplay(){
		display = true;
		display();
	}
	

	public void calculateFunctionNodes(){
		functionNodes = 0;
		for(Operator op : parent.functionSet){
			int num = (int)Math.floor((double)qubits / (double)op.getMaxInputs());
			functionNodes += num;
		}
	}
	
	public void prepare(){
		calculateFunctionNodes();
		inputs = new AntNode[qubits];
		int id = 0;
		Operator w = OperatorLibrary.Wire();
		for(int i = 0; i < qubits; i++){
			inputs[i] = new AntNode(0, i, w, 0.0, id);
			nodes.add(inputs[i]);
			id++;
		}
		for(int i = 1; i < searchSpaceWidth + 1; i++){
			int counter = 0;
			for(Operator op : parent.functionSet){
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
	
	public void buildPaths(int ants){

		paths.clear();
		for(int ant = 0; ant < ants; ant++){
			/** Generate Solutions **/
			resetCounters();
			ArrayList<ArrayList<AntNode>> antPath = new ArrayList<ArrayList<AntNode>>();
			for(int q = 0; q < qubits; q++){
				ArrayList<AntNode> qPath = new ArrayList<AntNode>();
				AntNode currentNode = inputs[q];
				Double[][] qEdge = edges.get(q);
				while(currentNode.getX() != searchSpaceWidth + 1){
					qPath.add(currentNode);
					double totalPheromone = 0.0;
					ArrayList<AntNode> targets = new ArrayList<AntNode>();
					ArrayList<Double> pheromoneLevels = new ArrayList<Double>();
					for(int target = 0; target < nodes.size(); target++){
						AntNode targetNode = nodes.get(target);
						Operator o = targetNode.getOp();
						if(targetNode.getX() > currentNode.getX() && targetNode.counter < o.getMaxInputs() && !qPath.get(qPath.size() - 1).equals(targetNode)){
							targets.add(nodes.get(target));
							if(targetNode.getX() == searchSpaceWidth + 1){
								pheromoneLevels.add(qEdge[currentNode.index][targetNode.index] * currentNode.getX());
								totalPheromone += qEdge[currentNode.index][targetNode.index] * currentNode.getX();
							}
							else{
								pheromoneLevels.add(qEdge[currentNode.index][targetNode.index] * (1 + targetNode.counter) * (1 + targetNode.counter) * o.getMaxInputs());
								totalPheromone += qEdge[currentNode.index][targetNode.index] * (1 + targetNode.counter) * (1 + targetNode.counter) * o.getMaxInputs();
							}
						}
					}
					double prob = parent.rand.nextDouble();
					double pheromoneSum = 0.0;
					boolean found = false;
						for(int potentialTarget = 0; potentialTarget < targets.size() && !found; potentialTarget++){
							pheromoneSum += pheromoneLevels.get(potentialTarget);							if(!found && prob <= pheromoneSum / totalPheromone){
							found = true;
							currentNode = targets.get(potentialTarget);
							currentNode.counter++;
						}
					}
				}
				qPath.add(currentNode);
				antPath.add(qPath);
			}
			paths.add(antPath);
			//Flatten paths
			if(bestScore > 0.9999){
				for(ArrayList<AntNode> qPath : antPath){
					ArrayList<AntNode> toRemove = new ArrayList<AntNode>();
					for(AntNode node : qPath){
						if(node.counter < node.getOp().getMaxInputs() && node.getX() != 0 && node.getX() != searchSpaceWidth + 1){
							toRemove.add(node);
						}
					}
					for(AntNode node : toRemove){
						qPath.remove(node);
					}
				}
			}
		}
	}
	
	public QuantumSystem buildCircuit(int ant){

		ArrayList<ArrayList<AntNode>> currentPath = null;
		if(ant == -1){
			currentPath = bestPath;
		}
		else{
			currentPath = paths.get(ant);
		}
		int size = 0;
		
		QuantumSystem q = new QuantumSystem();
		
		ArrayList<AntNode> pipeIn = new ArrayList<AntNode>();
		ArrayList<AntNode> pipeOut = new ArrayList<AntNode>();
		ArrayList<AntNode> newPipe = new ArrayList<AntNode>();
		ArrayList<Integer> currentIndexes = new ArrayList<Integer>();
		for(int qu = 0; qu < qubits; qu++){
			pipeIn.add(currentPath.get(qu).get(0));
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
							if(currentPath.get(path).get(currentIndexes.get(path)) == null){
								System.exit(0);
							}
							newPipe.set(path, currentPath.get(path).get(currentIndexes.get(path)));
							pipeOut.add(node);
						}
						path++;
					}
					if(inputs > 0){
						layerActive = true;
						q.nodeAdded();
						size++;
						q.addOperator(node.getMatrix(inputs, outputs), node.getTag());
					}
					else{
					}
				}
			}
			
			if(layerActive){
				for(AntNode v : pipeIn){
					if(v.equals(null)){
						System.exit(0);
					}
					if(!pipeOut.contains(v)){
						int pathIndex = 0;
						for(AntNode v2 : pipeIn){
							if(v2.equals(v)){
								pipeOut.add(v);
								newPipe.set(pathIndex, v);
								q.addOperator(OperatorLibrary.Wire().generateMatrix(1, 1, 0.0), null);
							}
							pathIndex++;
						}
					}
				}
				
				cm.buildAntMap(pipeIn, pipeOut);
				q.setConnectionMap(cm);
				q.buildLayer();
				pipeIn.clear();
				for(AntNode v : newPipe){
					if(v.equals(null)){
						System.exit(0);
					}
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
					int count = 0;
					for(AntNode v2 : pipeIn){
						if(v2.equals(v)){
							count++;
						}
					}
					for(int c = 0; c < count; c++){
						pipeOut.add(v);
						newPipe.set(path, v);
						q.addOperator(OperatorLibrary.Wire().generateMatrix(1, 1, 0.0), null);
					}
				}
				path++;
			}

			cm.buildAntMap(pipeIn, pipeOut);
			q.setConnectionMap(cm);
			q.buildLayer();
		}
		q.size = size;
		return q;
	}
	

	public QuantumSystem buildCircuitNu(int ant){

		ArrayList<ArrayList<AntNode>> currentPath = null;
		if(ant == -1){
			currentPath = bestPath;
		}
		else{
			currentPath = paths.get(ant);
		}
		int size = 0;
		
		QuantumSystem q = new QuantumSystem();
		
		ArrayList<AntNodeHolder> pipeIn = new ArrayList<AntNodeHolder>();
		ArrayList<AntNodeHolder> pipeOut = new ArrayList<AntNodeHolder>();
		ArrayList<AntNodeHolder> newPipe = new ArrayList<AntNodeHolder>();
		ArrayList<Integer> currentIndexes = new ArrayList<Integer>();
		for(int qu = 0; qu < qubits; qu++){
			pipeIn.add(new AntNodeHolder(qu, currentPath.get(qu).get(1)));
			currentIndexes.add(1);
		}

		ConnectionMap cm = new ConnectionMap();
		q.prepareLayer();
		for(int i = 0; i < searchSpaceWidth; i++){
			boolean layerActive = false;
			
				for(int j = 0; j < functionNodes; j++){
					AntNode node = nodes.get(qubits + (i * functionNodes) + j);
					int inputs = 0;
					int outputs = 0;
					for(AntNodeHolder v : pipeIn){
						if(v.node.equals(node)){
							inputs++;
							outputs++;
							currentIndexes.set(v.path, currentIndexes.get(v.path) + 1);
							if(currentPath.get(v.path).get(currentIndexes.get(v.path)) == null){
								System.exit(0);
							}
							newPipe.add(new AntNodeHolder(v.path, currentPath.get(v.path).get(currentIndexes.get(v.path))));
							pipeOut.add(v);
						}
					}
					if(inputs > 0){
						layerActive = true;
						q.nodeAdded();
						size++;
						q.addOperator(node.getMatrix(inputs, outputs), node.getTag());
					}
				}
			
			if(layerActive){
				for(AntNodeHolder v : pipeIn){
					if(v.equals(null)){
						System.exit(0);
					}
					if(!pipeOut.contains(v)){
						for(AntNodeHolder v2 : pipeIn){
							if(v2.equals(v)){
								pipeOut.add(v);
								newPipe.add(v);
								q.addOperator(OperatorLibrary.Wire().generateMatrix(1, 1, 0.0), null);
							}
						}
					}
				}
				
				cm.buildNuAntMap(pipeIn, pipeOut);
				q.setConnectionMap(cm);
				q.buildLayer();
				pipeIn.clear();
				for(AntNodeHolder v : newPipe){
					if(v.equals(null)){
						System.exit(0);
					}
					pipeIn.add(v);
				}
				newPipe.clear();
				pipeOut.clear();
				q.prepareLayer();
			}
		}

		//Outputs layer merges connections to outputs
		boolean layerActive = false;
		int disconnects = 0;
		for(int j = 0; j < qubits; j++){
			int inputs = 0;
			for(AntNodeHolder v : pipeIn){
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
			for(AntNodeHolder v : pipeIn){
				if(!pipeOut.contains(v)){
					int count = 0;
					for(AntNodeHolder v2 : pipeIn){
						if(v2.equals(v)){
							count++;
						}
					}
					for(int c = 0; c < count; c++){
						pipeOut.add(v);
						q.addOperator(OperatorLibrary.Wire().generateMatrix(1, 1, 0.0), null);
					}
				}
			}

			cm.buildNuAntMap(pipeIn, pipeOut);
			q.setConnectionMap(cm);
			q.buildLayer();
		}
		q.size = size;
		return q;
	}

	public QuantumSystem printCircuitNu(int ant){

		ArrayList<ArrayList<AntNode>> currentPath = null;
		if(ant == -1){
			currentPath = bestPath;
		}
		else{
			currentPath = paths.get(ant);
		}
		int size = 0;
		
		QuantumSystem q = new QuantumSystem();
		
		ArrayList<AntNodeHolder> pipeIn = new ArrayList<AntNodeHolder>();
		ArrayList<AntNodeHolder> pipeOut = new ArrayList<AntNodeHolder>();
		ArrayList<AntNodeHolder> newPipe = new ArrayList<AntNodeHolder>();
		ArrayList<Integer> currentIndexes = new ArrayList<Integer>();
		for(int qu = 0; qu < qubits; qu++){
			pipeIn.add(new AntNodeHolder(qu, currentPath.get(qu).get(1)));
			currentIndexes.add(1);
			newPipe.add(null);
			System.out.println("Init: " + pipeIn.get(qu));
		}


		System.out.println("Paths: ");
		int qubit = 0;
		for(ArrayList<AntNode> qPath : currentPath){
			qubit++;
			System.out.println("Qubit: " + qubit);
			for(AntNode node : qPath){
				System.out.println(qubit + ": " + node);
			}
		}
		return q;
	}
	

	public QuantumSystem printBuild(int ant){

		ArrayList<ArrayList<AntNode>> currentPath = null;
		if(ant == -1){
			currentPath = bestPath;
		}
		else{
			currentPath = paths.get(ant);
		}
		int size = 0;
		
		System.out.println("Paths: ");
		int qubit = 0;
		for(ArrayList<AntNode> qPath : currentPath){
			qubit++;
			System.out.println("Qubit: " + qubit);
			for(AntNode node : qPath){
				System.out.println(qubit + ": " + node);
			}
		}
		
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
							if(currentPath.get(path).get(currentIndexes.get(path)) == null){
								System.exit(0);
							}
							newPipe.set(path, currentPath.get(path).get(currentIndexes.get(path)));
							pipeOut.add(node);
						}
						path++;
					}
					if(inputs > 0){
						layerActive = true;
						q.nodeAdded();
						size++;
						q.addOperator(node.getMatrix(inputs, outputs), node.getTag());
					}
					else{
					}
				}
			}
			
			if(layerActive){
				for(AntNode v : pipeIn){
					if(v.equals(null)){
						System.exit(0);
					}
					if(!pipeOut.contains(v)){
						int pathIndex = 0;
						for(AntNode v2 : pipeIn){
							if(v2.equals(v)){
								pipeOut.add(v);
								newPipe.set(pathIndex, v);
								q.addOperator(OperatorLibrary.Wire().generateMatrix(1, 1, 0.0), null);
							}
							pathIndex++;
						}
					}
				}

				System.out.println("Pipe in");
				for(AntNode v : pipeIn){
					System.out.println(v);
				}
				System.out.println("Pipe out");
				for(AntNode v : pipeOut){
					System.out.println(v);
				}
				System.out.println("New pipe");
				for(AntNode v : newPipe){
					System.out.println(v);
				}
				
				System.out.println("Connection map");
				cm.buildAntMap(pipeIn, pipeOut);
				q.setConnectionMap(cm);
				System.out.println(cm.generateMatrix(0, 0, 0.0));
				q.buildLayer();
				System.out.println("Matrix");
				System.out.println(q.layers.get(q.layers.size() - 1));
				pipeIn.clear();
				for(AntNode v : newPipe){
					if(v.equals(null)){
						System.exit(0);
					}
					pipeIn.add(v);
				}
				pipeOut.clear();
				q.prepareLayer();
			}
		}
		
		System.out.println("Outputs");

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
					int count = 0;
					for(AntNode v2 : pipeIn){
						if(v2.equals(v)){
							count++;
						}
					}
					for(int c = 0; c < count; c++){
						pipeOut.add(v);
						newPipe.set(path, v);
						q.addOperator(OperatorLibrary.Wire().generateMatrix(1, 1, 0.0), null);
					}
				}
				path++;
			}

			System.out.println("Pipe in");
			for(AntNode v : pipeIn){
				System.out.println(v);
			}
			System.out.println("Pipe out");
			for(AntNode v : pipeOut){
				System.out.println(v);
			}
			System.out.println("New pipe");
			for(AntNode v : newPipe){
				System.out.println(v);
			}
			
			System.out.println("Connection map");
			
			cm.buildAntMap(pipeIn, pipeOut);
			q.setConnectionMap(cm);
			System.out.println(cm.generateMatrix(0, 0, 0.0));
			q.buildLayer();
			
			System.out.println("Matrix");
			System.out.println(q.layers.get(q.layers.size() - 1));
		}
		q.size = size;
		return q;
	}
	
	public QuantumSystem buildBest(){
		return buildCircuit(-1);
	}
	
	public void updatePheromones(ArrayList<Double> scores, ArrayList<Double> sizeScores, int ants, double evap){
		
		for(int ant = 0; ant < ants; ant++){
			if(scores.get(ant) >= bestScore || bestScore == 0.0){
				bestScore = scores.get(ant);
				if(scores.get(ant) <= 0.9999){
					bestPath = paths.get(ant);
				}
				
			}
			if(scores.get(ant) > 0.9999 && sizeScores.get(ant) >= bestSizeScore){
				bestSizeScore = sizeScores.get(ant);
				bestPath = paths.get(ant);
			}
		}
		
		/** Compute new pheromone levels **/
		for(int q = 0; q < qubits; q++){
			/**Apply evaporation**/
			for(int i = 0; i < nodes.size(); i++){
				for(int j = 0; j < nodes.size(); j++){
					if(edges.get(q)[i][j] == null){
						edges.get(q)[i][j] = QAntOpt.MIN * ants;
					}
					edges.get(q)[i][j] = (1 - evap) * edges.get(q)[i][j];
					if(edges.get(q)[i][j] < QAntOpt.MIN * ants){
						edges.get(q)[i][j] = QAntOpt.MIN * ants;
					}
				}
			}
		}
		
		for(int a = 0; a < ants; a++){
			ArrayList<ArrayList<AntNode>> aPath = paths.get(a);
			for(int q = 0; q < qubits; q++){
				ArrayList<AntNode> qPath = aPath.get(q);
				for(int index = 0; index < qPath.size() - 1; index++){
					edges.get(q)[qPath.get(index).index][qPath.get(index + 1).index] = edges.get(q)[qPath.get(index).index][qPath.get(index + 1).index] + scores.get(a) + (sizeScores.get(a));

					if(edges.get(q)[qPath.get(index).index][qPath.get(index + 1).index] > QAntOpt.MAX * ants){
						edges.get(q)[qPath.get(index).index][qPath.get(index + 1).index] = QAntOpt.MAX * ants;
					}
				}
			}
		}
		
		//Elitism
		ArrayList<ArrayList<AntNode>> aPath = bestPath;
		for(int q = 0; q < qubits; q++){
			ArrayList<AntNode> qPath = aPath.get(q);
			for(int index = 0; index < qPath.size() - 1; index++){
				edges.get(q)[qPath.get(index).index][qPath.get(index + 1).index] = edges.get(q)[qPath.get(index).index][qPath.get(index + 1).index] + bestScore + bestSizeScore;

				if(edges.get(q)[qPath.get(index).index][qPath.get(index + 1).index] > QAntOpt.MAX * ants){
					edges.get(q)[qPath.get(index).index][qPath.get(index + 1).index] = QAntOpt.MAX * ants;
				}
			}
		}
		displayCounter++;
		if(displayCounter % 100 == 0 && display){
			this.applet.update();
		}
	}
}
