package org.york.yqcgp.maths;

import java.util.*;

import org.york.yqcgp.Utils;
public class ComplexMatrix
{
	private Complex[][] vals;
	
	public ComplexMatrix(int width, int height){
		vals = new Complex[width][height];
	}

	@Override
	public String toString()
	{
		String ret = "\n";
		for(int row = 0; row < this.getHeight(); row++){
			for(int column = 0; column < this.getWidth(); column ++){
				if(getElement(column, row) == null){
					ret = ret + "0,";
				}
				else{
					ret = ret + getElement(column, row) + ",";
				}
			}
			ret = ret + "\n";
		}
		return ret;
	}

	
	public ComplexMatrix times(ComplexMatrix cm){
		ComplexMatrix res = new ComplexMatrix(cm.getWidth(), this.getHeight());
		for(int column = 0; column < this.getWidth(); column ++){
			for(int row = 0; row < this.getHeight(); row++){
				Complex el = getElement(column, row);
				if(el != null){
					for(int targetCol = 0; targetCol < cm.getWidth(); targetCol++){
						Complex targEl = cm.getElement(targetCol, column);
						if(targEl != null){
								if(res.getElement(targetCol, row) == null){
									res.setElement(targetCol, row, el.times(targEl));
								}
								else{
									res.setElement(targetCol, row, res.getElement(targetCol, row).plus(el.times(targEl)));
								}
						}
					}
				}
			}
		}
		return res;
	}
	

	public LinearAlgebra[] times(LinearAlgebra[] a){
		LinearAlgebra[] res = new LinearAlgebra[this.getHeight()];
		for(int row = 0; row < this.getHeight(); row ++){
			for(int column = 0; column < this.getWidth(); column++){
				Complex el = getElement(column, row);
				if(el != null){
					if(res[row] == null){
						System.out.println(a[column] + " * " + el + " = " + a[column].times(el));
						res[row] = a[column].times(el);
					}
					else{
						res[row] = res[row].plus(a[column].times(el));
					}
				}
			}
			if(res[row] == null){
				res[row] = new LinearAlgebra();
			}
		}
		return res;
	}
	
	
	public ComplexMatrix tensor(ComplexMatrix cm){
		ComplexMatrix res = new ComplexMatrix(this.getWidth() * cm.getWidth(), this.getHeight() * cm.getHeight());
		ArrayList<Integer> cmX = new ArrayList<Integer>();
		ArrayList<Integer> cmY = new ArrayList<Integer>();
		int cmCount = 0;
		
		for(int column = 0; column < cm.getWidth(); column ++){
			for(int row = 0; row < cm.getHeight(); row++){
				Complex el = cm.getElement(column, row);
				if(el != null){
					cmX.add(column);
					cmY.add(row);
					cmCount++;
				}
			}
		}
		
		for(int column = 0; column < this.getWidth(); column ++){
			for(int row = 0; row < this.getHeight(); row++){
				Complex el = getElement(column, row);
				if(el != null){
					int colStart = column * cm.getWidth();
					int rowStart = row * cm.getHeight();
					for(int i = 0; i < cmCount; i++){
						int x = cmX.get(i);
						int y = cmY.get(i);
						Complex targEl = cm.getElement(x, y);
						res.setElement(colStart + x, rowStart + y, el.times(targEl));
					}
				}
			}
		}
		return res;
	}
	
	public int getWidth(){
		return vals.length;
	}
	
	public int getHeight(){
		return vals[0].length;
	}
	
	public Complex getElement(int col, int row){
		return vals[col][row];
	}
	
	public void setElement(int col, int row, Complex c){
		vals[col][row] = c;
	}
	
	public void resize(){
		double prob = 0.0;
		for(int i = 0; i < getHeight(); i++){
			if(getElement(0, i) != null){
				prob += getElement(0, i).mod() * getElement(0, i).mod();
			}
		}
		double scale = 1.0 / Math.sqrt(prob);

		for(int i = 0; i < getHeight(); i++){
			if(getElement(0, i) != null){
				setElement(0, i, getElement(0, i).times(scale));
			}
		}
		prob = 0.0;
		for(int i = 0; i < getHeight(); i++){
			if(getElement(0, i) != null){
				prob += getElement(0, i).mod() * getElement(0, i).mod();
			}
		}
	}
	
	public static ComplexMatrix I(int n){
		ComplexMatrix i = new ComplexMatrix((int)Math.pow(2, n), (int)Math.pow(2, n));
		for(int j = 0; j < (int)Math.pow(2, n); j++){
			i.setElement(j,j,new Complex(1.0));
		}
		return i;
	}
	
	public ComplexMatrix plus(ComplexMatrix m){
		ComplexMatrix res = new ComplexMatrix(this.getWidth(), this.getHeight());
		for(int column = 0; column < this.getWidth(); column ++){
			for(int row = 0; row < this.getHeight(); row++){
				if(this.getElement(column, row) != null || m.getElement(column, row) != null){
					Complex val1 = Complex.Zero();
					if(this.getElement(column, row) != null){
						val1 = this.getElement(column, row);
					}
					Complex val2 = Complex.Zero();
					if(m.getElement(column, row) != null){
						val2 = m.getElement(column, row);
					}
					res.setElement(column, row, val1.plus(val2));
				}
			}
		}
		return res;
	}
	
	public ComplexMatrix minus(ComplexMatrix m){
		return this.plus(m.negative());
	}
	
	public ComplexMatrix negative(){
		ComplexMatrix res = new ComplexMatrix(this.getWidth(), this.getHeight());
		for(int column = 0; column < this.getWidth(); column ++){
			for(int row = 0; row < this.getHeight(); row++){
				if(this.getElement(column, row) != null){
					res.setElement(column, row, this.getElement(column, row).negative());
				}
			}
		}
		return res;
	}
	
	public double size(){
		double s = 0;
		for(int column = 0; column < this.getWidth(); column ++){
			for(int row = 0; row < this.getHeight(); row++){
				if(this.getElement(column, row) != null){
					s += this.getElement(column, row).mod();
				}
			}
		}
		return Math.sqrt(s);
	}
	
	public ComplexMatrix normalize(){
		double sqN = 0.0;
		for(int column = 0; column < this.getWidth(); column ++){
			for(int row = 0; row < this.getHeight(); row++){
				if(getElement(column, row) != null){
					Complex el = getElement(column, row);
					sqN += el.getReal() * el.getReal();
					sqN += el.getIm() * el.getIm();
				}
			}
		}
		double N = Math.sqrt(sqN);
		ComplexMatrix nu = new ComplexMatrix(this.getWidth(), this.getHeight());
		if(N != 0.0){
			for(int column = 0; column < this.getWidth(); column ++){
				for(int row = 0; row < this.getHeight(); row++){
					if(getElement(column, row) != null){
						nu.setElement(column, row, getElement(column, row).divide(N));
					}
				}
			}
			return nu;
		}
		return nu;
		
	}
}
	
