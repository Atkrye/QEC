package org.york.yqcgp.maths;

import java.util.ArrayList;

public class LinearAlgebra {
	ArrayList<String> symbols = new ArrayList<String>();
	ArrayList<Complex> values = new ArrayList<Complex>();
		
	public void addValue(String symbol, Complex value){
		symbols.add(symbol);
		values.add(value);
	}
	
	public Complex getValue(String symbol){
		for(int i = 0; i < symbols.size(); i++){
			if(symbols.get(i).equals(symbol)){
				return values.get(i);
			}
		}
		return null;
	}
	
	@Override
	public String toString(){
		String ret = "";
		for(int i = 0; i < symbols.size(); i++){
			ret = ret +  values.get(i) + " " + symbols.get(i) + " + ";
		}
		if(ret == ""){
			ret = "0";
		}
		return ret;
	}
	
	public LinearAlgebra times(Complex c){
		LinearAlgebra l = new LinearAlgebra();
		for(int i = 0; i < symbols.size(); i++){
			l.addValue(symbols.get(i), values.get(i).times(c));
		}
		return l;
	}
	
	public ArrayList<String> getSymbols(){
		return symbols;
	}
	
	public ArrayList<Complex> getValues(){
		return values;
	}
	
	public LinearAlgebra plus(LinearAlgebra a){
		LinearAlgebra l = new LinearAlgebra();
		for(int i = 0; i < symbols.size(); i++){
			if(a.getValue(symbols.get(i)) != null){
				l.addValue(symbols.get(i), a.getValue(symbols.get(i)).plus(values.get(i)));
			}
			else{
				l.addValue(symbols.get(i), values.get(i));
			}
		}
		for(int i = 0; i < a.getSymbols().size(); i++){
			if(getValue(a.getSymbols().get(i)) == null){
				l.addValue(a.getSymbols().get(i), a.getValue(a.getSymbols().get(i)));
			}
		}
		return l;
	}
	
	public LinearAlgebra minus(LinearAlgebra a){
		return plus(a.negative());
	}
	
	public LinearAlgebra negative(){
		LinearAlgebra l = new LinearAlgebra();
		for(int i = 0; i < symbols.size(); i++){
			l.addValue(symbols.get(i), values.get(i).negative());
		}
		return l;
	}
	
	public double size(){
		double size = 0.0;
		for(Complex val : values){
			size += val.mod();
		}
		return size;
	}

	public double differentElements(LinearAlgebra a) {
		int diff = 0;
		for(int i = 0; i < symbols.size(); i++){
			if(isZero(a.getValue(symbols.get(i))) && getValue(symbols.get(i)).mod() > 0.0001){
				diff++;
			}
		}
		for(int i = 0; i < a.getSymbols().size(); i++){
			if(isZero(getValue(a.getSymbols().get(i))) && a.getValue(a.getSymbols().get(i)).mod() > 0.0001){
				diff++;
			}
		}
		return diff;
	}
	
	public boolean isZero(Complex c){
		if(c == null){
			return true;
		}
		else if(c.mod() < 0.0001){
			return true;
		}
		return false;
	}

	public LinearAlgebra copy() {
		LinearAlgebra la = new LinearAlgebra();
		for(String label : getSymbols()){
			la.addValue(label, getValue(label));
		}
		return la;
	}

}
