package com.vista;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* 训练集管理器
*/

public class TrainingDataManager 
{
	private String[] traningFileClassifications;//训练语料分类集合
	private File traningTextDir;//训练语料存放目录
	//private static String defaultPath = "G:\\WorkSpace\\beyesi\\yuliao\\SogouC\\Sample";
	//  private static String defaultPath = "D:\\workspace\\beyesi\\yuliao\\SogouC";
	//private static String defaultPath = "C:\\beiyesi\\Sample";
	//private static String defaultPath = "..\\trs2txt\\nf1012_test\\1\\sample";
	private static String defaultPath = ".\\sample";
	
	public TrainingDataManager() 
	{
		traningTextDir = new File(defaultPath);
		if (!traningTextDir.isDirectory()) 
		{
			throw new IllegalArgumentException("训练语料库搜索失败！ [" +defaultPath + "]");
		}
		this.traningFileClassifications = traningTextDir.list();
	}
	/**
	* 返回训练文本类别，这个类别就是目录名
	* @return 训练文本类别
	*/
	public String[] getTraningClassifications() 
	{
		return this.traningFileClassifications;
	}
	/**
	* 根据训练文本类别返回这个类别下的所有训练文本路径（full path）
	* @param classification 给定的分类
	* @return 给定分类下所有文件的路径（full path）
	*/
	public String[] getFilesPath(String classification) 
	{
		File classDir = new File(traningTextDir.getPath() +File.separator +classification);
		String[] ret = classDir.list();
		for (int i = 0; i < ret.length; i++) 
		{
			ret[i] = traningTextDir.getPath() +File.separator +classification +File.separator +ret[i];
		}
		return ret;
	}
	/**
	 * 返回二级分类的类名名
	 */
	public String[] get2ClassificationsName(String classification){
		File classDir = new File(traningTextDir.getPath() +File.separator +classification);
		String[] ret = classDir.list();
		return ret;
	}
	
	/**
	 * 返回二级分类文本路径
	 */
	public String[] get2FilesPath(String classification,String classification2) 
	{
		File classDir = new File(traningTextDir.getPath() +File.separator +classification+File.separator +classification2);
		String[] ret = classDir.list();
		for (int i = 0; i < ret.length; i++) 
		{
			ret[i] = traningTextDir.getPath() +File.separator +classification +File.separator +classification2+File.separator +ret[i];
		}
		return ret;
	}
	

	/**
	* 返回给定路径的文本文件内容
	* @param filePath 给定的文本文件路径
	* @return 文本内容
	* @throws java.io.FileNotFoundException
	* @throws java.io.IOException
	*/
	public static String getText(String filePath) throws FileNotFoundException,IOException 
	{
	
		InputStreamReader isReader =new InputStreamReader(new FileInputStream(filePath),"utf-8");
		BufferedReader reader = new BufferedReader(isReader);
		String aline;
		StringBuilder sb = new StringBuilder();
		int t=0;
		while ((aline = reader.readLine()) != null)
		{   
			t++;
			//if (t>1){
			sb.append(aline + " ");
			//}
		}
		isReader.close();
		reader.close();
		return sb.toString();
	}

	/**
	* 返回训练文本集中所有的文本数目
	* @return 训练文本集中所有的文本数目
	*/
	public int getTrainingFileCount()
	{
		int ret = 0;
		for (int i = 0; i < traningFileClassifications.length; i++)
		{
		//	ret +=getTrainingFileCountOfClassification(traningFileClassifications[i]);
			ret+=getTrainingFileCount2(traningFileClassifications[i]);
		}
		return ret;
	}
	/**
	 * 返回二级训练文本集中所有的文本数目
	 * @param classification
	 * @return
	 */
	public int getTrainingFileCount2(String classification)
	{
		int ret = 0;
		for (int i = 0; i < get2ClassificationsName(classification).length; i++)
		{
			ret +=getTrainingFileCountOfClassification2(classification,get2ClassificationsName(classification)[i]);
		}
		return ret;
	}

	/**
	* 返回训练文本集中在给定分类下的训练文本数目
	* @param classification 给定的分类
	* @return 训练文本集中在给定分类下的训练文本数目
	*/
	public int getTrainingFileCountOfClassification(String classification)
	{
		File classDir = new File(traningTextDir.getPath() +File.separator +classification);
		return classDir.list().length;
	}
	/**
	 * 返回二级训练文本集中在给定分类下的训练文本数目
	 * @param classification
	 * @param classification2
	 * @return
	 */
	public int getTrainingFileCountOfClassification2(String classification,String classification2)
	{
		//File classDir = new File(traningTextDir.getPath() +File.separator +classification+File.separator +classification2);
		File classDir = new File(traningTextDir.getPath() +File.separator +classification+File.separator +classification2);
		return classDir.list().length;
	}

	/**
	* 返回给定分类中包含关键字／词的训练文本的数目
	* @param classification 给定的分类
	* @param key 给定的关键字／词
	* @return 给定分类中包含关键字／词的训练文本的数目
	*/
	public int getCountContainKeyOfClassification(String classification,String key) 
	{
		int ret = 0;
		/*
		try 
		{
			String[] filePath = getFilesPath(classification);
			for (int j = 0; j < filePath.length; j++) 
			{
				String text = getText(filePath[j]);
				if (text.contains(key)) 
				{
					ret++;
				}
			}
		}
		catch (FileNotFoundException ex) 
		{
			Logger.getLogger(TrainingDataManager.class.getName()).log(Level.SEVERE, null,ex);
	
		} 
		catch (IOException ex)
		{
			Logger.getLogger(TrainingDataManager.class.getName()).log(Level.SEVERE, null,ex);
	
		}*/
		for(int i=0;i<get2ClassificationsName(classification).length;i++){
			ret+=getCountContainKeyOfClassification2(classification,get2ClassificationsName(classification)[i],key);
		}
		return ret;
	}
	/**
	 * 返回二级分类给定分类中包含关键字／词的训练文本的数目
	 * @param classification1
	 * @param classification2
	 * @param key
	 * @return
	 */
	public int getCountContainKeyOfClassification2(String classification1,String classification2,String key) 
	{
		int ret = 0;
		try 
		{
			String[] filePath = get2FilesPath(classification1,classification2);
			for (int j = 0; j < filePath.length; j++) 
			{
				String text = getText(filePath[j]);
				if (text.contains(key)) 
				{
					ret++;
				}
			}
		}
		catch (FileNotFoundException ex) 
		{
			Logger.getLogger(TrainingDataManager.class.getName()).log(Level.SEVERE, null,ex);
	
		} 
		catch (IOException ex)
		{
			Logger.getLogger(TrainingDataManager.class.getName()).log(Level.SEVERE, null,ex);
	
		}
		return ret;
	}
}