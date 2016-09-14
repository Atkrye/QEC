package org.york.yqcgp.maths;

public class Complex
{
	
	public static Complex Zero(){
		return new Complex(0.0, 0.0);
	}
	
	public static Complex i(){
		return new Complex(0.0, 1.0);
	}
	
	private double real;
	private double imaginary;
	
	public Complex(double real, double imaginary){
		this.real = real;
		this.imaginary = imaginary;
	}
	
	public Complex(double real){
		this(real, 0.0);
	}
	
	@Override
	public String toString(){
		return real + "+" + imaginary + "i";
	}
	
	public double getReal(){
		return real;
	}
	
	public double getIm(){
		return imaginary;
	}
	
	public void setReal(double newReal){
		this.real = newReal;
	}
	
	public void setIm(double newIm){
		this.imaginary = newIm;
	}
	
	public Complex negative(){
		return new Complex(-real,-imaginary);
	}
	
	public Complex conjugate(){
		return new Complex(real, -imaginary);
	}
	
	public double mod(){
		return Math.sqrt((real * real) + (imaginary * imaginary));
	}
	
	public Complex times(Complex c){
		return new Complex((this.real * c.getReal()) - (this.imaginary * c.getIm()), (real * c.getIm()) + (imaginary * c.getReal()));
	}
	
	public Complex divide(Complex c){
		return times(c.conjugate()).divide(c.mod());
	}
	
	public Complex times(double val){
		return new Complex(real * val, imaginary * val);
	}
	
	public Complex divide(double val){
		return times(1/val);
	}
	
	public Complex plus(Complex c){
		if(c == null){
			return new Complex(this.real, this.imaginary);
		}
		return new Complex(this.real + c.getReal(), this.imaginary + c.getIm());
	}
	
	public Complex minus(Complex c){
		if(c == null){
			return new Complex(this.real, this.imaginary);
		}
		return plus(c.negative());
	}
	
	public static Complex ePow(double i){
		double re = Math.cos(i);
		double im = Math.sin(i);
		if(re < 0.000001 && re > -0.000001){
			re = 0.0;
		}
		if(im < 0.000001 && im > -0.000001){
			im = 0.0;
		}
		return new Complex(re, im);
	}
}
