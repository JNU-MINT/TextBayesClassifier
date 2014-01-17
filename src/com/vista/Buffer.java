package com.vista;

public class Buffer {
	public float pr[];
	public String word;
	public Buffer(int wide){
		pr=new float[wide];
		
		this.word=null;
		for(int i=0;i<pr.length;i++){
			this.pr[i]=0;
		}
	}
}
