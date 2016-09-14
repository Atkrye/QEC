package org.york.zx;

import java.util.HashMap;

import org.york.builders.Builder;
import org.york.builders.MultiComponent;
import org.york.yqcgp.QuantumSystem;

public class SuperGraph implements MultiComponent{

	HashMap<String, ZXGraph> components = new HashMap<String, ZXGraph>();
	ZXCalcCGP zx;
	Builder build;
	
	public SuperGraph(ZXCalcCGP zx, Builder build){
		this.zx = zx;
		this.build = build;
	}
	
	public void init(){
		build.setup(this);
	}
	
	
	@Override
	public void addComponent(String name, Object[] args) {
		int layers = (Integer)args[0];
		int layerWidth = (Integer)args[1];
		int arity = (Integer)args[2];
		int inputs = (Integer)args[3];
		int workingBits = (Integer)args[4];
		int outputs = (Integer)args[5];
		int maxComplexity = (Integer)args[6];
		ZXGraph g = new ZXGraph(zx, layers, layerWidth, arity, inputs, workingBits, outputs, maxComplexity);
		g.setup();
		components.put(name,  g);
	}

	@Override
	public QuantumSystem buildComponent(String name) {
		return components.get(name).build();
	}
	
	public boolean systemChanged(){
		boolean change = false;
		for(ZXGraph c : components.values()){
			if(c.circuitChange()){
				change = true;
			}
		}
		return change;
	}
	
	public boolean isRunnable(){
		for(ZXGraph g : components.values()){
			if(!g.isRunnable()){
				return false;
			}
		}
		return true;
	}

	@Override
	public QuantumSystem buildComponentBest(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void copyComponents(SuperGraph sg) throws Exception{
		for(String label : sg.components.keySet()){
			this.components.put(label, sg.components.get(label).copy());
		}
	}
	
	public SuperGraph mutate(double mutationRate) throws Exception{
		SuperGraph newGraph = new SuperGraph(zx, build);
		
		newGraph.copyComponents(this);
		
		HashMap<String, ZXGraph> comp = newGraph.components;
		
		for(String label : comp.keySet()){
			comp.put(label, mutate(comp.get(label), mutationRate));
		}
		
		return newGraph;
	}
	

	public ZXGraph mutate(ZXGraph graph, double mutationRate){
		ZXGraph g = null;
		while(g == null || !g.isRunnable()){
			try {
				g = graph.copy();
			} catch (Exception e) {
				e.printStackTrace();
			}
			int nodes = g.layers * g.layerWidth;
			int arity = g.arity;
			int inputs = g.in;
			int workingBits = g.working;
			double nodeChance = (1 / (1 + (mutationRate * nodes)));
			while(zx.rand.nextDouble() >= nodeChance){
				g.mutateOp();
				g.mutatePhase();
			}
			double edgeChance = (1 / (1 + (mutationRate * nodes * arity)));
			while(zx.rand.nextDouble() >= edgeChance){
				g.mutateEdge();
			}
			double inChance = (1 / (1 + (mutationRate * inputs + workingBits)));
			while(zx.rand.nextDouble() >= inChance){
				g.mutateInput();
			}
		}
		return g;
	}
}
