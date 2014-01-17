package com.vista;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
* 停用词处理器
* @author phinecos 
* 
*/
public class StopWordsHandler 
{	
	ArrayList<String> lss=new ArrayList<String>();
//	private static String stopWordsList[];
//	private static String stopWordsList[] ={"的", "我们","要","自己","之","将","“","”","，","（","）","后","应","到","某","后","个","是","位","新","一","两","在","中","或","有","更","好",""};//常用停用词
	public  boolean IsStopWord(String word)
	{/*	StopWordsHandler s=new StopWordsHandler();
		for(int i=0;i<s.stopWordsList.length;++i)
		{
			if(word.equalsIgnoreCase(s.stopWordsList[i]))
				return true;
		}
		return false;*/
		for(int i=0;i<lss.size();++i)
		{
			if(word.equalsIgnoreCase(lss.get(i)))
				return true;
		}
		return false;
	}
	public StopWordsHandler(String path){
		try{
		FileInputStream fi=new FileInputStream(path);
		InputStreamReader isReader=new InputStreamReader(fi,"GBK");
	//	FileReader isReader =new FileReader("E:\\workspace\\beyesi\\停用词表.txt");
	//	System.out.println("失败！");
		BufferedReader reader = new BufferedReader(isReader);
		int i=0;
		String aline;
		//		for(int i=0;reader.readLine() != null;i++){
		while ((aline = reader.readLine()) != null){
			lss.add(aline);
	//		stopWordsList[i]=aline;
	//		System.out.println(aline);
			i++;
		}

		}catch(Exception ex){
			System.out.println("读取停用词表文本时失败！");
		}
	//	String stopWordsList[] = null;
	//	for(int j=0;j<lss.size();j++){
	//		System.out.println(lss.get(j));
	//		stopWordsList[j]=lss.get(j);
	//	}
	}

	
}
