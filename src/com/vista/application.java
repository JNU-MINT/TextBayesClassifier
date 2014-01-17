package com.vista;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
public class application {
	TrainingDataManager tdm=new TrainingDataManager();
	DataBase d=new DataBase();
	List<Buffer> db1,dbCHI1,TFIDF;
	static PrintWriter pw; // 日志记录器
	String[] Classes =tdm.getTraningClassifications();//分类
	StopWordsHandler s=new StopWordsHandler(".\\停用词表.txt");
	List<Buffer> db2[]=new List[Classes.length];
	List<Buffer> dbCHI2[]=new List[Classes.length];
	List<Buffer> TFIDFList[]=new List[Classes.length];
	public String Result="";
	int ct1=0,ct2=0,ct3=0,ct4=0;
	
	public application(){
		System.out.println("导入一级数据库...");
		String[] rowName=new String[Classes.length] ;//分类
		for (int k = 0; k <Classes.length; k++) {
			rowName[k]="子类"+Classes[k];
		}
		db1=d.getData("样本", rowName);
	/*	
		Buffer s=dbase.get(0);
		System.out.println(""+dbase.get(0).pr[3]);
		s.pr[3]=3;
		dbase.set(0, s);
		System.out.println(""+dbase.get(0).pr[3]);
		*/
		
		
		System.out.println("导入一级数据库CHI...");
		String[] rowName3=new String[Classes.length] ;//分类
		for (int k = 0; k <Classes.length; k++) {
			rowName3[k]="CHI"+Classes[k];
		}
		dbCHI1=d.getData("样本CHI", rowName3);
	/*	
		System.out.println("导入一级数据库TFIDF...");
		String[] rowName4=new String[Classes.length] ;//分类
		for (int k = 0; k <Classes.length; k++) {
			rowName4[k]="TFIDF"+Classes[k];
		}
		TFIDF=d.getData("样本TFIDF", rowName4);
		*/
		
		System.out.println("导入二级数据库...");

		for(int i=0;i<Classes.length;i++){
			String[] Classes2 = tdm.get2ClassificationsName(Classes[i]);
			String tableName="二"+Classes[i];
			String[] rowName2=new String[Classes2.length] ;//分类
			for (int k = 0; k <Classes2.length; k++) {
				rowName2[k]="子类"+Classes2[k];
			}
			db2[i]=d.getData(tableName, rowName2);
			
		}
		
		System.out.println("导入二级数据库CHI...");

		for(int i=0;i<Classes.length;i++){
			String[] Classes2 = tdm.get2ClassificationsName(Classes[i]);
			String tableName="二CHI"+Classes[i];
			String[] rowName2=new String[Classes2.length] ;//分类
			for (int k = 0; k <Classes2.length; k++) {
				rowName2[k]="CHI"+Classes2[k];
			}
			dbCHI2[i]=d.getData(tableName, rowName2);
			
		}
	/*	
		System.out.println("导入二级数据库TFIDF...");

		for(int i=0;i<Classes.length;i++){
			String[] Classes2 = tdm.get2ClassificationsName(Classes[i]);
			String tableName="二TFIDF"+Classes[i];
			String[] rowName2=new String[Classes2.length] ;//分类
			for (int k = 0; k <Classes2.length; k++) {
				rowName2[k]="TFIDF"+Classes2[k];
			}
			TFIDFList[i]=d.getData(tableName, rowName2);
			
		}
		
	/*	try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(".//Log//"
					+ getTime() + ".txt", true)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
/**
 * 
 * @param path
 * @param c1
 * @param c2
 */
	public void classify(String path,String c1,String c2){
		
		float T[]=new float[Classes.length];
/*	double tz[]=new double[CHI.size()];
		for (int i=0;i<CHI.size();i++){
			for (int j=0;j<Classes.length;j++){
				tz[i]+=CHI.get(i).pr[j];
			}
			Arrays.sort(tz);
		}

/*		
		for (int j=0;j<Classes.length;j++){
			double temp[]=new double[CHI.size()];
			for (int i=0;i<CHI.size();i++){
				temp[i]=CHI.get(i).pr[j];
			}
			Arrays.sort(temp);
			
			//****提取30%***********************************************************************************/
	//		T[j]=(float) temp[(int) (0.50*CHI.size())];
	//	}
		//*******************Variance***********************************************************/
		double CHI_var[]=new double[dbCHI1.size()];
	//	double sum=0;
		for (int i=0;i<dbCHI1.size();i++){
			CHI_var[i]=Variance(dbCHI1.get(i).pr);
	//		CHI_var[i]=SUM(CHI.get(i).pr);
	//		sum+=CHI_var[i];
		}
		double var[]=CHI_var;
		Arrays.sort(var);
	//	double AVG=0.1*sum/(CHI.size()+1);
		double AVG=var[(int) (var.length*0.12)];
		

	//	Arrays.sort(CHI.get(index));
		for (int j=0;j<Classes.length;j++){
	//		T[j]=(float) (temp[j]/CHI.size());
			System.out.print(Classes[j]+":"+T[j]+"-->");
		}
		
		System.out.println("开始一级分类...");
		String time=getTime();
		List<String> textlist=readText(path);
		
		
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(".//Log//"
					+ c1+"_"+c2 + ".txt", true)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int n1=0,n2=0;
		for(int i=0;i<textlist.size();i++){
			log(textlist.get(i));
			this.Result+=textlist.get(i)+"\n";
			boolean flag=false;
			List<ClassifyResult> crs = new ArrayList<ClassifyResult>();//分类结果
			String[] terms=ChineseSpliter.split(cutNumber(textlist.get(i)), " ").split(" ");
			terms = DropStopWords(terms);
			//*********************************CHI特征提取*******************************************************/
			//这里去除掉文本中相同的词
			for (int m = 0; m <dbCHI1.size(); m++) {
				for (int j = 0; j <terms.length; j++) {		
					if(dbCHI1.get(m).word.equals(terms[j])&& CHI_var[m]<AVG){
				//	if(CHI.get(m).word.equals(terms[j])&& CHI_var[m]<tz[(int) (CHI.size()*0.5)]){
						terms[j]=" ";
					}
				}
			}
			//******************************************************************************************/
			
			float r[][] = new float[terms.length][Classes.length];
			for (int m = 0; m <db1.size(); m++) {
				for (int j = 0; j <terms.length; j++) {
					if(db1.get(m).word.equals(terms[j])){
					//	System.out.println(terms[j]);
						for (int k = 0; k <Classes.length; k++) {
							//=改成+=
							r[j][k] += db1.get(m).pr[k];
						}
						int mForStop = m;
						String termForStop = terms[j]; 
						Buffer rr = db1.get(m);
						int forStop = 0;
					}
				}
			}
			//CHI
		/*	
			for (int m = 0; m <CHI.size(); m++) {
				for (int j = 0; j <terms.length; j++) {					
					if(CHI.get(m).word.equals(terms[j])){
					//	System.out.println(terms[j]);
						for (int k = 0; k <Classes.length; k++) {
						//	r[j][k]=dbase.get(m).pr[k];
			//				if(CHI.get(m).pr[k]<T[k]){
						//	if(CHI.get(m).pr[k]<200){	
								r[j][k]=r[j][k]+(float)Math.log(CHI.get(m).pr[k]+1);
			//					r[j][k]=r[j][k]-2;
						//		r[j][k]=r[j][k]-(Math.log(CHI.get(m).pr[k])+10);
			//				}
							
						}
					}
				}
			}
			//***************************************TDIDF*/
	/*	
			for (int m = 0; m <TFIDF.size(); m++) {
				for (int j = 0; j <terms.length; j++) {
					
					if(TFIDF.get(m).word.equals(terms[j])){
					//	System.out.println(terms[j]);
						for (int k = 0; k <Classes.length; k++) {
						//	r[j][k]=dbase.get(m).pr[k];
						//	if(TFIDF.get(m).pr[k]<150){
						//		r[j][k]=r[j][k]*2;
						//	}
						//	if(r[j][k]>-5){
				//			if(TFIDF.get(m).pr[k]>0.05){
							//	r[j][k]+=Math.log(TFIDF.get(m).pr[k]*100+0.001);
				//				r[j][k]+=TFIDF.get(m).pr[k]*50;
				//			}
				//			else
				/*			if(TFIDF.get(m).pr[k]>0.005){
								r[j][k]+=TFIDF.get(m).pr[k]*10;
							}
								else
								{
								//	if (TFIDF.get(m).pr[k]<0.01){
									r[j][k]=r[j][k]-3;
								}
							r[j][k]=r[j][k]+TFIDF.get(m).pr[k]*100;
						}
					}
				}
			}
			
			//****************************************/
			
			for (int j = 0; j <Classes.length; j++){
				ClassifyResult cr = new ClassifyResult();
				cr.classification = Classes[j];//分类
	//			float probility=(float) Math.log(PriorProbability.calculatePc(Classes[j]));
				float probility=getPriProbility(Classes[j]);
				for (int n = 0; n <terms.length; n++){
					probility+=r[n][j];
				}
				cr.probility = probility;//关键字在分类的条件概率
				crs.add(cr);
			}

			////////////////////////////////////////////////////////////////////////////////////////////////////
			crs=compare(crs);
			double cd=Confidence(crs);
			System.out.println(textlist.get(i));
			System.out.println(cd);
			log(""+cd);
			for(int w=0;w<crs.size();w++){
				System.out.println(crs.get(w).classification+":"+crs.get(w).probility);
				log(crs.get(w).classification+":"+crs.get(w).probility);
			}
			
			
			/*if(cd<crs.get(0).probility){
				this.ct4++;
				Increment(terms,crs.get(0).classification);
				log("***********************************************************");
			}*/
			if(c1.equals(crs.get(0).classification)){
				n1++;
				
				flag=true;
			}else{
				if (cd<crs.get(0).probility){
					this.ct3++;
					log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}
			}

			log("一级正确率"+n1+"/"+(i+1));
			this.Result+="本文属于："+crs.get(0).classification+"\n\n";

			//一级分类结束
			System.out.println();
			System.out.println();
			
			if(flag){
			int t=0;
			for(int n=0;n<Classes.length;n++){
				if(Classes[n].equals(crs.get(0).classification)){

					t=n;
					System.out.println(Classes[n]+":::"+t);
					break;
				}
			}
			
			
			String[] Classes2 = tdm.get2ClassificationsName(Classes[t]);
			List<ClassifyResult> crs2 = new ArrayList<ClassifyResult>();//分类结果
			
			
			float TT[]=new float[Classes2.length];
			for (int j=0;j<Classes2.length;j++){
				double temp[]=new double[dbCHI2[t].size()];
				for (int l=0;l<dbCHI2[t].size();l++){
					temp[l]=dbCHI2[t].get(l).pr[j];
				}
				Arrays.sort(temp);
				//****提取30%******************************************************************************************/
				TT[j]=(float) temp[(int) (0.5*dbCHI2[t].size())];
			}
			
			
			
			
			
			float rr[][] = new float[terms.length][Classes2.length];
			
			for (int m = 0; m <db2[t].size(); m++) {
				for (int j = 0; j <terms.length; j++) {
					
					if(db2[t].get(m).word.equals(terms[j])){
						for (int k = 0; k <Classes2.length; k++) {
						
							rr[j][k]=db2[t].get(m).pr[k];
							
						}
					}
				}
			}
			//**********************************CHI
			
			for (int m = 0; m <dbCHI2[t].size(); m++) {
				for (int j = 0; j <terms.length; j++) {
					
					if(dbCHI2[t].get(m).word.equals(terms[j])){
						for (int k = 0; k <Classes2.length; k++) {
							if(dbCHI2[t].get(m).pr[k]<TT[k]){
								rr[j][k]=rr[j][k]-3;
							//	rr[j][k]-=(Math.log(CHIList[t].get(m).pr[k])+10);
							//	rr[j][k]= rr[j][k]-((float)Math.log(CHIList[t].get(m).pr[k])+1)-10;
							}							
						}
					}
				}
			}
			
			
			//***********************************TFIDF*/
			/*
			for (int m = 0; m <TFIDFList[t].size(); m++) {
				for (int j = 0; j <terms.length; j++) {
					
					if(TFIDFList[t].get(m).word.equals(terms[j])){
						for (int k = 0; k <Classes2.length; k++) {
							if(TFIDFList[t].get(m).pr[k]>0.08){
							//	rr[j][k]+=Math.log(TFIDFList[t].get(m).pr[k]+0.001);
								rr[j][k]+=TFIDFList[t].get(m).pr[k]*50;
							}
							else
							if(TFIDFList[t].get(m).pr[k]>0.01){
								rr[j][k]+=TFIDFList[t].get(m).pr[k]*30;
							}
							else
								if (TFIDFList[t].get(m).pr[k]<0.01){
								r[j][k]=r[j][k]-3;
							}
						}
					}
				}
			}
			
			//************************************/
			
			for (int j = 0; j <Classes2.length; j++){
				ClassifyResult cr = new ClassifyResult();
				cr.classification = Classes2[j];//分类
				
				float probility=(float) Math.log(PriorProbability.calculatePc2(Classes[t],Classes2[j]));
				
				for (int n = 0; n <terms.length; n++){
					probility+=rr[n][j];
					
				}
				cr.probility = probility;//关键字在分类的条件概率
				crs2.add(cr);
				
			}
			crs2=compare(crs2);
			for(int w=0;w<crs2.size();w++){
				System.out.println(crs2.get(w).classification+":"+crs2.get(w).probility);
				log(crs2.get(w).classification+":"+crs2.get(w).probility);
			}
			if(c2.equals(crs2.get(0).classification)){
				n2++;
				
			}
			
			//二级级分类结束
			System.out.println();
			System.out.println();
			}
			log("二级正确率"+n2+"/"+(i+1));
		}
		this.ct1+=n1;
		this.ct2+=textlist.size();
		log("begin:"+time+"--end:"+getTime()+"-->"+"p="+this.ct1+"/"+this.ct2);
		log("p:"+this.ct4+"-->N:"+this.ct3);
	//	System.out.println("end......"+"p="+this.ct1+"/"+this.ct2);
	}
	public String getResult(){
		return this.Result;
	}
	/**
	 * 读取测试文本
	 * @param path
	 * @return
	 */
	public List<String> readText(String path){
		List<String> textlist = new ArrayList<String>();
		File traningTextDir;//训练语料存放目录
		traningTextDir = new File(path);
		File classDir = new File(traningTextDir.getPath());
		String[] ret = classDir.list();
		for (int i = 0; i < ret.length; i++) {
			InputStreamReader isReader;
			try {
				isReader = new InputStreamReader(new FileInputStream(traningTextDir+"\\"+ret[i]),"utf-8");
				BufferedReader reader = new BufferedReader(isReader);
				String aline;
				StringBuilder sb = new StringBuilder();
				try {
					while ((aline = reader.readLine()) != null){
						sb.append(aline + " ");
					}
					String aritcle = sb.toString();	
					textlist.add(aritcle);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return textlist;
	}
	/**
	 * 排序
	 * @param crs
	 * @return
	 */
	public List<ClassifyResult> compare(List<ClassifyResult> crs){
		java.util.Collections.sort(crs,new Comparator() 
		{
			public int compare(final Object o1,final Object o2) 
			{
				final ClassifyResult m1 = (ClassifyResult) o1;
				final ClassifyResult m2 = (ClassifyResult) o2;
				final double ret = m1.probility - m2.probility;
				if (ret < 0) 
				{
					return 1;
				} 
				else 
				{
					return -1;
				}
			}
		});
		return crs;
	}
	/**
	 * 置信度计算
	 * @param crs
	 * @return
	 */
	public double Confidence(List<ClassifyResult> crs){
		double sum1=0,sum2=0;
		for ( int i = 0;i<crs.size();i++){
			sum1+=crs.get(i).probility;
		}
		double AVG=sum1/(crs.size()+1);
		for ( int i = 0;i<crs.size();i++){
			sum2+=Math.pow(crs.get(i).probility-AVG, 2);
		}
		double pS=Math.sqrt(sum2/(crs.size()+1));
		//t(22)0.25=0.686,t(22)0.20=0.858,t(22)0.15=1.061
		//0.686  0.858  1.061  1.321  1.717  2.074  2.508  2.819  3.119  3.505
		double UP=AVG+2.508*pS;
		return UP;
	}
	/**
	 * 方差计算
	 * @param D
	 * @return
	 */
	public double Variance(float D[]){
		double avg=0;
		for(int i=0;i<D.length;i++){
			avg+=D[i];
		}
		avg=avg/D.length;
		double Var=0;
		for(int i=0;i<D.length;i++){
			Var+=((D[i]-avg)*(D[i]-avg));
		}
		Var=Var/D.length;
		return Var;
	}
	public double SUM(float D[]){
		double sum=0;
		for(int i=0;i<D.length;i++){
			sum+=D[i];
		}
		return sum;
	}
	/**
	 * 增量学习分类器修正
	 * 
	 * @param s
	 */
	public void Increment(String [] terms,String c){
		for(int i=0;i<terms.length ;i++){
			for(int j=i+1;j<terms.length ;j++){
				if(terms[i].equals(terms[j])){
					terms[i]=" ";
					break;
				}
			}
		}
		
		
		int t = 0;
		for (int i=0;i<Classes.length;i++){
			if(Classes[i].equals(c)){
				t=i;
				break;
			}
		}
		float Nc = 0;
		Buffer b = null,bb = null;
		for(int i=0;i<db1.size();i++){
			if(db1.get(i).word.equals("Volume")){
				Nc=db1.get(i).pr[t];
				b=db1.get(i);
				b.pr[t]=Nc+1f;
				db1.set(i, b);
			}
			
		}
		//**********************************/

		double N=0;
		for (int j=0;j<Classes.length;j++){
			try
			{
				N+=b.pr[j];
			}
			catch(Exception e)
			{
				System.out.println("ex1");
			}
		}
		int tt=0;
		for(int i=0;i<db1.size();i++){
			if(db1.get(i).word.equals("prioriprobability")){
				bb=db1.get(i);
				tt=i;
				break;
			}
		}
		
		for (int j=0;j<Classes.length;j++){
			if(j!=t){
				try 
				{
					bb.pr[j]=(float) (((N-1)/N)*bb.pr[j]);
				}
				catch(Exception e)
				{
					System.out.println("ex2");
				}
				
				
			}
			else{
				bb.pr[j]=(float) (((N-1)/N)*bb.pr[j]+1/N);
			}
			
		
		}
		db1.set(tt, bb);

		//***********************************************/
		System.out.println("NC:"+Nc);
		
		boolean f[]=new boolean[terms.length];
		for(int i=0;i<f.length;i++){
			f[i]=true;
		}
		
		for(int i=0;i<db1.size();i++){
			boolean flag=true;
			for(int j=0;j<terms.length ;j++){
				if(terms[j].equals(" ")==false&&db1.get(i).word.equals(terms[j])){
					f[j]=false;
					
					b=db1.get(i);
		//			b.pr[t]=(Nc/(Nc+1))*b.pr[t]+(1/(Nc+1));
		//			System.out.println("PO:"+b.pr[t]);
					b.pr[t]=(float) Math.log((Nc/(Nc+1))*Math.pow(Math.E, b.pr[t])+(1/(Nc+1)));
		//			System.out.println("NO:"+b.pr[t]);
					
					db1.set(i, b);
					flag=false;
					break;

				}
		/*		if(j==terms.length -1){
					b=dbase.get(i);
		//			b.pr[t]=(Nc/(Nc+1))*b.pr[t];
					b.pr[t]=(float) (Math.log(Nc/(Nc+1))+b.pr[t]);
					dbase.set(i, b);
				}*/

			}
			if (flag && db1.get(i).word.equals("Volume")==false&& db1.get(i).word.equals("prioriprobability")==false) {
				b=db1.get(i);

//				System.out.println("P:"+b.pr[t]);
//				b.pr[t]=(float) Math.log(Nc/(Nc+1))+ b.pr[t];
				b.pr[t]=(float) Math.log((Nc/(Nc+1))*Math.pow(Math.E, b.pr[t]));
//				System.out.println("N:"+b.pr[t]);
				db1.set(i, b);
			}
		}
		for(int i=0;i<f.length;i++){

			if (f[i]&&terms[i].length()>1){
				Buffer nb =new Buffer(Classes.length);
				nb.word=terms[i];
				for (int j=0;j<Classes.length;j++){
					if(j!=t){
						nb.pr[j]=1/b.pr[j];
					}
					else{
						nb.pr[j]=2/b.pr[j];
					}
				}
				db1.add(nb);
				System.out.println(terms[i]);
			}
		}
	}
	public float getPriProbility(String c){
		int t = 0;
		float p=1;
		for (int i=0;i<Classes.length;i++){
			if(Classes[i].equals(c)){
				t=i;
				break;
			}
		}
		for(int i=0;i<db1.size();i++){
			if(db1.get(i).word.equals("prioriprobability")){
				p=db1.get(i).pr[t];
				break;
			}
		}
		return (float) Math.log(p);
	}
	
	
	
	public static void log(String s) {
		//System.out.println(s);
		if (pw != null) {
			pw.println(" " + s);
			pw.flush();
		}
	}

	// 取得当前时间
	public static String getTime() {
		Date now = new Date();// 当前时间
		String dateTime = new SimpleDateFormat("yyMMddHHmmss").format(now);
		return dateTime;
	}
	/**
	 * 停用词处理
	 * @param oldWords
	 * @return
	 */
	public String[] DropStopWords(String[] oldWords)
	{
		Vector<String> v1 = new Vector<String>();
		for(int i=0;i<oldWords.length;++i)
		{
		//	if(StopWordsHandler.IsStopWord(oldWords[i])==false)
			if(s.IsStopWord(oldWords[i])==false&& oldWords[i].length()>1)
			{//不是停用词
				v1.add(oldWords[i]);
			}
		}
		String[] newWords = new String[v1.size()];
		v1.toArray(newWords);
		return newWords;
	}
	public static String cutNumber(String text){

		String reg="[\\d]";
		Pattern p=Pattern.compile(reg);
		Matcher m=p.matcher(text);
	//	String sr=null;
		while (m.find()){    
		    text = m.replaceAll(" "); 
		    }    
		  //  if (sr == null) {    
		   //   System.out.println("NO MATCHES: "); 
		  //  }

		return text;
	}
	
}
