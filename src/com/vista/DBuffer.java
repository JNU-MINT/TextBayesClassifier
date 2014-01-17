package com.vista;

public class DBuffer {
	public double pr[];
	public String word;
	public DBuffer(int wide){
		pr=new double[wide];
		
		this.word=null;
		for(int i=0;i<pr.length;i++){
			this.pr[i]=0;
		}
	}
}
