package org.york.yqcgp.operator;

import java.util.ArrayList;

import org.york.ants.AntNode;
import org.york.ants.AntNodeHolder;
import org.york.yqcgp.maths.Complex;
import org.york.yqcgp.maths.ComplexMatrix;
import org.york.zx.ZXNodeVertex;

public class ConnectionMap extends Operator{
	
	ComplexMatrix map;
	public ConnectionMap() {
		super("Connection");
	}
	
	public static boolean[] increment(boolean[] register){
		int i = register.length - 1;
		if(register[i] == false){
			register[i] = true;
		}
		else{
			while(i >= 0 && register[i] == true){
				register[i] = false;
				i--;
			}
			if(i >= 0){
				register[i] = true;				
			}
		}
		return register;
	}
	
	public static void printRegister(boolean[] register){
		String ret = "";
		for(int i = 0; i < register.length; i++){
			if(register[i]){
				ret = ret + "1";
			}
			else{
				ret = ret + "0";
			}
		}
		System.out.println(ret);
	}
	
	public void prepareConnectionMap(){
		
	}

	@Override
	public ComplexMatrix generateMatrix(int in, int out, double phase) {
		return map;
	}

	@Override
	public int getMinInputs() {
		return 0;
	}

	@Override
	public int getMaxInputs() {
		return 0;
	}

	@Override
	public int getMinOutputs() {
		return 0;
	}

	@Override
	public int getMaxOutputs() {
		return 0;
	}

	public void buildAntMap(ArrayList<AntNode> pipeIn, ArrayList<AntNode> pipeOut) {
		map = new ComplexMatrix((int)Math.pow(2, pipeIn.size()), (int)Math.pow(2, pipeIn.size()));
		if(pipeIn.size() == 0){
			map.setElement(0, 0, new Complex(1.0));
		}
		else{
			int[] mapping = new int[pipeIn.size()];
			Boolean[] used = new Boolean[pipeIn.size()];
			int index = 0;
			for(AntNode input : pipeIn){
				boolean found = false;
				for(int i = 0; i < pipeOut.size() && !found; i++){
					if(pipeOut.get(i).equals(input) && used[i] == null){
						mapping[index] = i;
						used[i] = true;
						found = true;
					}
				}
				index++;
			}
			boolean[] register = new boolean[pipeIn.size()];
			for(int i = 0; i < pipeIn.size(); i++){
				register[i] = false;
			}
			
			for(int i = 0; i < (int)Math.pow(2, pipeIn.size()); i++){
				int target = 0;
				for(int j = 0; j < pipeIn.size(); j++){
					if(register[j] == true){
						target = target + (int)Math.pow(2, (pipeIn.size() - 1) - mapping[j]);
					}
				}
				map.setElement(i, target, new Complex(1.0));
				register = increment(register);
			}
			
		}
	}
	


	public void buildNuAntMap(ArrayList<AntNodeHolder> pipeIn, ArrayList<AntNodeHolder> pipeOut) {
		map = new ComplexMatrix((int)Math.pow(2, pipeIn.size()), (int)Math.pow(2, pipeIn.size()));
		if(pipeIn.size() == 0){
			map.setElement(0, 0, new Complex(1.0));
		}
		else{
			int[] mapping = new int[pipeIn.size()];
			Boolean[] used = new Boolean[pipeIn.size()];
			int index = 0;
			for(AntNodeHolder input : pipeIn){
				boolean found = false;
				for(int i = 0; i < pipeOut.size() && !found; i++){
					if(pipeOut.get(i).equals(input) && used[i] == null){
						mapping[index] = i;
						used[i] = true;
						found = true;
					}
				}
				index++;
			}
			boolean[] register = new boolean[pipeIn.size()];
			for(int i = 0; i < pipeIn.size(); i++){
				register[i] = false;
			}
			
			for(int i = 0; i < (int)Math.pow(2, pipeIn.size()); i++){
				int target = 0;
				for(int j = 0; j < pipeIn.size(); j++){
					if(register[j] == true){
						target = target + (int)Math.pow(2, (pipeIn.size() - 1) - mapping[j]);
					}
				}
				map.setElement(i, target, new Complex(1.0));
				register = increment(register);
			}
			
		}
	}

	public void buildZXMap(ArrayList<ZXNodeVertex> out,
			ArrayList<ZXNodeVertex> in) {

		map = new ComplexMatrix((int)Math.pow(2, in.size()), (int)Math.pow(2, in.size()));
		if(in.size() == 0){
			map.setElement(0, 0, new Complex(1.0));
		}
			else{
			int[] mapping = new int[in.size()];
			Boolean[] used = new Boolean[in.size()];
			int index = 0;
			for(ZXNodeVertex input : in){
				boolean found = false;
				for(int i = 0; i < out.size() && !found; i++){
					if(out.get(i).equals(input) && used[i] == null){
						mapping[index] = i;
						used[i] = true;
						found = true;
					}
				}
				index++;
			}
			boolean[] register = new boolean[in.size()];
			for(int i = 0; i < in.size(); i++){
				register[i] = false;
			}
			for(int i = 0; i < (int)Math.pow(2, in.size()); i++){
				int target = 0;
				for(int j = 0; j < in.size(); j++){
					if(register[j] == true){
						target = target + (int)Math.pow(2, (in.size() - 1) - mapping[j]);
					}
				}

				map.setElement(i, target, new Complex(1.0));
				register = increment(register);
			}
		}
	}

}
