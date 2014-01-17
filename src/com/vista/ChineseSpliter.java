package com.vista;

import java.io.ByteArrayInputStream;
import java.io.IOException;  	
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import jeasy.analysis.MMAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.core.IKSegmenter;

/**
* 中文分词器
*/
public class ChineseSpliter 
{
	/**
	* 对给定的文本进行中文分词
	* @param text 给定的文本
	* @param splitToken 用于分割的标记,如"|"
	* @return 分词完毕的文本
	*/
	public static String split2(String text,String splitToken)
	{
		String result = null;
		MMAnalyzer analyzer = new MMAnalyzer();  
		try  	
        {		
			result = analyzer.segment(text, splitToken);	
		}  	
        catch (IOException e)  	
        { 	////
			System.out.println("02出错");
        	e.printStackTrace(); 	
        } 	
        return result;
	}
	
	public static String split(String text, String splitToken) 
	{
		StringBuffer sb = new StringBuffer();
		try
		{
			byte[] bt = text.getBytes();
			InputStream ip = new ByteArrayInputStream(bt);
			Reader read = new InputStreamReader(ip);
			IKSegmenter iks = new IKSegmenter(read, true);
			Lexeme t;
			while ((t = iks.next()) != null)
			{
				sb.append(t.getLexemeText() + " ");
			}
			sb.delete(sb.length() - 1, sb.length());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}
}
