package com.vista;

import java.util.ArrayList;
import java.util.List;

public class mThread implements Runnable{
	private String name;
	static int id=0,count=0;
	static List<Buffer> Dbase = new ArrayList<Buffer>();
	Training t=new Training();
	public mThread(String name){
		this.name=name;
		
	}
	public void run(){
		try {
		if(name.equals("一级分类")){
			t.classify1(0,13);
		}
		/*if(name.equals("一级分类2")){
			t.classify1(11,23);
		}*/
		if(name.equals("二级分类")){
			t.classify2(0,13);
		}
		/*if(name.equals("二级分类2")){
			t.classify2(16,17);
		}*/
		if(name.equals("CHI")){
			t.Chi_square();
		}
		if(name.equals("TFIDF")){
			t.TFIDF();
		}
		if(name.equals("TFIDF2")){
			t.TFIDF2();
		}
		if(name.equals("CHI2")){
			t.CHI2();
		}
		
		}catch (Exception e) {
              //TODO 异常处理
        	System.out.println("出错");
        }
		finally{
			id++;
			System.out.println("线程"+id+"结束。。" + name);
		}
	}
}
