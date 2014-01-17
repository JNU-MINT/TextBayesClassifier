package com.vista;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Training {
	TrainingDataManager tdm=new TrainingDataManager();
	StopWordsHandler s=new StopWordsHandler(".\\停用词表.txt");
	String[] Classes = tdm.getTraningClassifications();//分类
	
	
	/**
	 * 一级分类训练调度
	 */
//	List<Buffer> Dbase = new ArrayList<Buffer>();
	public void classify1(int start,int end){
		DataBase db=new DataBase();
		probability pro=new probability();
		pro.computArg();
		System.out.println("开始一级分类训练...");
	//	for(int a=0;a<Classes.length;a++){
		for(int a=start;a<end;a++){
			System.out.println("导入一级文本文件..."+Classes[a]);
			String text=getText1(Classes[a]);
			System.out.println("开始一级分词...");
			text=cutNumber(text);
			String[] terms = ChineseSpliter.split(text, " ").split(" ");
			terms = DropStopWords(terms);
			System.out.println("开始一级词训练...");
			for(int i=0;i<terms.length;i++){
				boolean flag=true;
				for(int j=0;j<mThread.Dbase.size();j++){
					if(mThread.Dbase.get(j).word.equals(terms[i])){
				//		System.out.println(Dbase.get(j).word+":"+Dbase.get(j).pr[a]);
						if(mThread.Dbase.get(j).pr[a]>-1E-8){
							mThread.Dbase.get(j).pr[a]=(float) (0.95*mThread.Dbase.get(j).pr[a]);
						}
				//		System.out.println(Dbase.get(j).word+":"+Dbase.get(j).pr[a]);
						flag=false;
						break;
					}
				}
				if(flag){
					Buffer b=new Buffer(Classes.length);
					for(int l=0;l<Classes.length;l++){
						b.pr[l]=pro.calculatePxc(terms[i],Classes[l]);
					}
					b.word=terms[i];
					mThread.Dbase.add(b);
					mThread.count++;
					if(mThread.count%50==0){
						System.out.println("一级训练词："+mThread.count);
					}
				}
			}
		}
		/*if(mThread.id==1){*/
			System.out.println("一级训练结束...\n");
			System.out.println("写入数据库...");
			db.creatTable("样本");
			for(int i=0;i<Classes.length;i++){
				db.addRow("样本", "子类"+Classes[i]);
			}
			db.insertData(mThread.Dbase, Classes.length, "样本");
//			Dbase.clear();
			System.out.println("一级分类训练结束...\n");
		/*}*/
	}
	
	
	
	/**
	 * 二级分类训练调度
	 */
	public void classify2(int t1,int t2){
		DataBase db=new DataBase();
	//	for(int i=0;i<Classes.length;i++){
		for(int i=t1;i<t2;i++){
			List<Buffer> dbase = new ArrayList<Buffer>();
			probability pro=new probability();
			pro.computArg2(Classes[i]);
			String[] Classes2 = tdm.get2ClassificationsName(Classes[i]);
			int count=0;
		
			for(int aa=0;aa<Classes2.length;aa++){
				System.out.println("开始二级"+Classes[i]+"__"+Classes2[aa]+"分类训练...");
				String text=getText2(Classes[i],Classes2[aa]);
				System.out.println("开始分词二...");
				text=cutNumber(text);
				String[] terms = ChineseSpliter.split(text, " ").split(" ");
				terms = DropStopWords(terms);
				System.out.println("开始词训练二...");
				
				for(int k=0;k<terms.length;k++){
					boolean flag=true;
					for(int j=0;j<dbase.size();j++){
						if(dbase.get(j).word.equals(terms[k])){
							if(dbase.get(j).pr[aa]>-1E-8){
								dbase.get(j).pr[aa]=(float) (0.9*dbase.get(j).pr[aa]);
							}
							flag=false;
							break;
						}
					}
					if(flag){
						Buffer b=new Buffer(Classes2.length);
						for(int l=0;l<Classes2.length;l++){
							b.pr[l]=pro.calculatePxc2(terms[k],Classes[i],Classes2[l]);
						}
						b.word=terms[k];
						dbase.add(b);
						count++;
						if(count%100==0){
							System.out.println("二级训练词："+count);
						}
					}
				}
			}
			System.out.println("训练结束...\n");
			System.out.println("写入数据库...");
			String tableName="二"+Classes[i];
			db.creatTable(tableName);
			for(int m=0;m<Classes2.length;m++){
				db.addRow(tableName, "子类"+Classes2[m]);
			}
			db.insertData(dbase, Classes2.length, tableName);
			dbase.clear();
			System.out.println("二级分类"+Classes[i]+"训练结束...\n");
		}
		System.out.println("二级分类训练结束...\n");
	}
	
	
	
	/**
	 * 一级分类CHI训练调度
	 */
	public void Chi_square(){
		List<Buffer> Dbase = new ArrayList<Buffer>();
		DataBase db=new DataBase();
		int count=0;
		double N = tdm.getTrainingFileCount();
		double Nc[] =new double[Classes.length];
		double Nxc[] =new double[Classes.length];
		for(int a=0;a<Classes.length;a++){
			System.out.println("导入一级文本文件CHI..."+Classes[a]);
			String text=getText1(Classes[a]);
			System.out.println("开始分词一CHI...");
			text=cutNumber(text);
			String[] terms = ChineseSpliter.split(text, " ").split(" ");
			terms = DropStopWords(terms);
			System.out.println("开始训练CHI...");
			for(int w=0;w<terms.length;w++){
				boolean flag=true;
				for(int k=0;k<Dbase.size();k++){
					if(Dbase.get(k).word.equals(terms[w])){
						flag=false;
						break;
					}
				}
				if(flag){
					double sumNxc=0;
					for(int i=0;i<Classes.length;i++){
						Nxc[i] = tdm.getCountContainKeyOfClassification(Classes[i], terms[w]);
						sumNxc+=Nxc[i];
						Nc[i] = tdm.getTrainingFileCountOfClassification(Classes[i]);
					}
					double px;
					double pxc[] =new double[Classes.length];
					double pnxnc[] =new double[Classes.length];
					double pnxc[] =new double[Classes.length];
					double pxnc[] =new double[Classes.length];
					double pc[] =new double[Classes.length];
					for(int i=0;i<Classes.length;i++){
						pc[i]=(Nc[i]+1)/(N+1);
						pxc[i]=(Nxc[i]+1)/(N+1);
						pnxc[i]=(Nc[i]-Nxc[i]+1)/(N+1);
						pxnc[i]=(sumNxc-Nxc[i])/(N+1);
						pnxnc[i]=(N-Nc[i]-sumNxc+Nxc[i]+1)/(N+1);
					}
					px=(sumNxc+1)/(N+1);
					double chixc[] =new double[Classes.length];
					Buffer b=new Buffer(Classes.length);
					b.word=terms[w];
					for(int i=0;i<Classes.length;i++){
			//			chixc[i]=N*(pxc[i]*pnxnc[i]-pxnc[i]*pnxc[i])/(px*pc[i]*(1-px)*(1-pc[i]));
						chixc[i]=(pxc[i]*pnxnc[i]-pxnc[i]*pnxc[i])/(px*pc[i]*(1-px)*(1-pc[i]));
						if(chixc[i]>0){
							b.pr[i]=(float) chixc[i];
						}
						else{
							b.pr[i]=0;
						}
					}
					Dbase.add(b);
					count++;
					if(count%100==0){
						System.out.println("一级训练词CHI："+count);
					}
				}
			}
			
		}
		//写入数据库
		System.out.println("CHI训练结束CHI...\n");
		System.out.println("CHI训练写入数据库CHI...");
		String tableName="样本CHI";
		db.creatTable(tableName);
		for(int i=0;i<Classes.length;i++){
			db.addRow(tableName, "CHI"+Classes[i]);
		}
		db.insertData(Dbase, Classes.length, tableName);
//		Dbase.clear();
		System.out.println("一级分类训练结束CHI...\n");
		
		
		
	}
		
	
	/**
	 * 一级TF-IDF训练
	 */
	@SuppressWarnings("unchecked")
	public void TFIDF(){
		double N = tdm.getTrainingFileCount();
		double Nc[] =new double[Classes.length];
		double Nxc[] =new double[Classes.length];
		double index[] =new double[Classes.length];
		List<DBuffer> Lw=new ArrayList<DBuffer>();
		DataBase db=new DataBase();
		List<Buffer> Dbase=new ArrayList<Buffer>();
	
		int count=0;

		List<String> sList=new ArrayList();

		
		for(int a=0;a<Classes.length;a++){
			System.out.println("导入一级文本文件TF..."+Classes[a]);
			String text=getText1(Classes[a]);
			System.out.println("开始分词一TF...");
			text=cutNumber(text);
			String[] termss = ChineseSpliter.split(text, " ").split(" ");
			String[] terms = DropStopWords(termss);
			
			for(int w=0;w<terms.length;w++){
				if(terms[w].length()>1&&terms[w].equals("null")==false){

					sList.add(terms[w]);
					index[a]++;
				}
			}
		}
	/*		System.out.println("导入一级文本文件TFIDF..."+Classes[a]);
			String text=getText1(Classes[a]);
			System.out.println("开始分词一TFIDF...");
			text=cutNumber(text);
			String[] termss = ChineseSpliter.split(text, " ").split(" ");
			String[] terms = DropStopWords(termss);
			System.out.println("开始训练TFIDF...");*/
			for(int w=0;w<sList.size();w++){
				boolean flag=true;
				for(int k=0;k<Lw.size();k++){
					if(Lw.get(k).word.equals(sList.get(w))){
						flag=false;
						break;
					}
				}
				if(flag){
					double TF[] =new double[Classes.length];
					double IDF=0;
					double Na=0;
					int temp2=0;
					for(int i=0;i<Classes.length;i++){
						Nxc[i] = tdm.getCountContainKeyOfClassification(Classes[i], sList.get(w));
						Na+=Nxc[i];
						Nc[i] = tdm.getTrainingFileCountOfClassification(Classes[i]);
				//		TF[i]=(Nxc[i]+1)/(Nc[i]+1);
				//		System.out.println("TF"+TF);
						int temp=0;
						
						for(int k=temp2;k<temp2+index[i];k++){
							if(sList.get(w).equals(sList.get(k))){
								temp++;
							}
						}
						temp2+=index[i];
						TF[i]=(temp*Nc[i])/index[i];
			//			System.out.println("TFi:"+TF[i]+"-->index[i]:"+index[i]+"-->temp:"+temp);
					}

				/*	
				/*	
					if(Na==0.0){
						Na=0.5;
					}else{
						IDF=Math.log((N+1)/(Na+1));
					}
					*/
					IDF=Math.log((N+1)/(Na+1));
			//		double Var=Variance(TF);
					double W[] =new double[Classes.length];
			//		String s=terms[w];
					DBuffer b=new DBuffer(Classes.length);
					b.word=sList.get(w);
					for(int i=0;i<Classes.length;i++){
						
			//			W[i]=(TF[i]*IDF*Nxc[i]*Nxc[i])/(Na*Nc[i]);
						W[i]=TF[i]*IDF;
				//		W[i]=(TF[i])*IDF;
				//		System.out.println("W[i]"+W[i]+"IDF"+IDF);
						b.pr[i]= W[i];
					//	s+="-->"+Classes[i]+":"+W[i];
					}
			//		b.Var=Variance(W);
					Lw.add(b);
					count++;
					if(count%100==0){
						System.out.println("TFIDF训练词："+count);
				}
				//	System.out.println(s);
				}
			}
			//归一化
			double sum=1.0;
			int lenght=Lw.size();
		/*	
			if (lenght>100){
			//	double t= (5*Math.sqrt(lenght-100)+100);
				lenght=(lenght-100)/2+100;
			}
			*/
	//		lenght=lenght*4/5;
			double q[][]=new double[lenght][Classes.length];
	/*		for(int i=0;i<Classes.length;i++){
				for(int j=0;j<(lenght);j++){
					sum+=(Lw.get(j).pr[i]*Lw.get(j).pr[i]);
				}
				sum=Math.sqrt(sum);
				for(int j=0;j<(lenght);j++){
					q[j][i]=Lw.get(j).pr[i]/sum;
					
				}
			}*/
			for(int j=0;j<(lenght);j++){
				for(int i=0;i<Classes.length;i++){
					sum+=(Lw.get(j).pr[i]*Lw.get(j).pr[i]);
				}
				sum=Math.sqrt(sum);
				for(int i=0;i<Classes.length;i++){
					q[j][i]=Lw.get(j).pr[i]/sum;
				}
			}
			for(int j=0;j<(lenght);j++){
				Buffer b=new Buffer(Classes.length);
				b.word=Lw.get(j).word;
		//		b.Var=Lw.get(j).Var;
				for(int i=0;i<Classes.length;i++){
					b.pr[i]= (float) q[j][i];
			//		if(b.pr[i]<1E-8){
			//			b.pr[i]=0;
			//		}
				}
				Dbase.add(b);
		}
		//写入数据库
		System.out.println("训练结束TFIDF...\n");
		System.out.println("写入数据库TFIDF...");
		String tableName="样本TFIDF";
		db.creatTable(tableName);
		for(int i=0;i<Classes.length;i++){
			db.addRow(tableName, "TFIDF"+Classes[i]);
		}
		db.insertData(Dbase, Classes.length, tableName);
//		Dbase.clear();
		System.out.println("一级分类训练结束TFIDF...\n");
	}

	
	
	/**
	 * 二级chi
	 *
	 */
//	public void CHI2(int t1,int t2){
	public void CHI2(){
		DataBase db=new DataBase();
			for(int i=0;i<Classes.length;i++){
	//	for(int i=t1;i<t2;i++){
			List<Buffer> Dbase = new ArrayList<Buffer>();
			String[] Classes2 = tdm.get2ClassificationsName(Classes[i]);
			int count=0;
			double N = tdm.getTrainingFileCount2(Classes[i]);
			double Nc[] =new double[Classes2.length];
			double Nxc[] =new double[Classes2.length];
			for(int aa=0;aa<Classes2.length;aa++){
				System.out.println("开始二级CHI"+Classes[i]+"__"+Classes2[aa]+"分类训练...");
				String text=getText2(Classes[i],Classes2[aa]);
				System.out.println("开始分词二CHI...");
				text=cutNumber(text);
				String[] terms = ChineseSpliter.split(text, " ").split(" ");
				terms = DropStopWords(terms);
				System.out.println("开始词训练二CHI...");
				
				for(int w=0;w<terms.length;w++){
					boolean flag=true;
					for(int k=0;k<Dbase.size();k++){
						if(Dbase.get(k).word.equals(terms[w])){
							flag=false;
							break;
						}
					}
					if(flag){
						double sumNxc=0;
						for(int ii=0;ii<Classes2.length;ii++){
							Nxc[ii] = tdm.getCountContainKeyOfClassification2(Classes[i], Classes2[ii],terms[w]);
							sumNxc+=Nxc[ii];
							Nc[ii] = tdm.getTrainingFileCountOfClassification(Classes[i]);
						}
						double px;
						double pxc[] =new double[Classes2.length];
						double pnxnc[] =new double[Classes2.length];
						double pnxc[] =new double[Classes2.length];
						double pxnc[] =new double[Classes2.length];
						double pc[] =new double[Classes2.length];

						
						for(int ii=0;ii<Classes2.length;ii++){
							pc[ii]=(Nc[ii]+1)/(N+1);
							pxc[ii]=(Nxc[ii]+1)/(N+1);
							pnxc[ii]=(Nc[ii]-Nxc[ii]+1)/(N+1);
							pxnc[ii]=(sumNxc-Nxc[ii])/(N+1);
							pnxnc[ii]=(N-Nc[ii]-sumNxc+Nxc[ii]+1)/(N+1);
						}
						px=(sumNxc+1)/(N+1);
						double chixc[] =new double[Classes2.length];
						Buffer b=new Buffer(Classes2.length);
						b.word=terms[w];
						for(int ii=0;ii<Classes2.length;ii++){
				//			chixc[i]=N*(pxc[i]*pnxnc[i]-pxnc[i]*pnxc[i])/(px*pc[i]*(1-px)*(1-pc[i]));
							chixc[ii]=(pxc[ii]*pnxnc[ii]-pxnc[ii]*pnxc[ii])/(px*pc[ii]*(1-px)*(1-pc[ii]));
							if(chixc[ii]>0){
								b.pr[ii]=(float) chixc[ii];
							}
							else{
								b.pr[ii]=0;
							}
						}
						Dbase.add(b);
						count++;
						if(count%100==0){
							System.out.println("二级训练词CHI："+count);
						}
					}
				}
			}
			System.out.println("CHI二训练结束...\n");
			System.out.println("CHI二训练写入数据库...");
			String tableName="二CHI"+Classes[i];
			db.creatTable(tableName);
			for(int m=0;m<Classes2.length;m++){
				db.addRow(tableName, "CHI"+Classes2[m]);
			}
			db.insertData(Dbase, Classes2.length, tableName);
			Dbase.clear();
			System.out.println("CHI二级分类"+Classes[i]+"训练结束...\n");
		}
		System.out.println("CHI二训练结束CHI...\n");
	}
	
	
	/**
	 * 二级TFIDF
	 */
//	public void TFIDF2(int t1,int t2){
	public void TFIDF2(){
		DataBase db=new DataBase();
			for(int v=0;v<Classes.length;v++){
	//	for(int v=t1;v<t2;v++){
			List<Buffer> Dbase = new ArrayList<Buffer>();
			String[] Classes2 = tdm.get2ClassificationsName(Classes[v]);
			int count=0;
			double N = tdm.getTrainingFileCount2(Classes[v]);
			double Nc[] =new double[Classes2.length];
			double Nxc[] =new double[Classes2.length];
			double index[] =new double[Classes2.length];
			List<Buffer> Lw=new ArrayList<Buffer>();
			List<String> sList=new ArrayList<String>();
			for(int a=0;a<Classes2.length;a++){
				System.out.println("导入一级文本文件IDFTF..."+Classes2[a]);
				String text=getText2(Classes[v],Classes2[a]);
				System.out.println("开始分词一TFIDF...");
				text=cutNumber(text);
				String[] termss = ChineseSpliter.split(text, " ").split(" ");
				String[] terms = DropStopWords(termss);
				
				for(int w=0;w<terms.length;w++){
					if(terms[w].length()>1&&terms[w].equals("null")==false){
						
						sList.add(terms[w]);
						index[a]++;
					}
				}
			}
			for(int w=0;w<sList.size();w++){
				boolean flag=true;
				for(int k=0;k<Lw.size();k++){
					if(Lw.get(k).word.equals(sList.get(w))){
						flag=false;
						break;
					}
				}
				if(flag){
					
					double TF[] =new double[Classes2.length];
					double IDF=0;
					double Na=0;
					int temp2=0;
					for(int i=0;i<Classes2.length;i++){
						Nxc[i] = tdm.getCountContainKeyOfClassification2(Classes[v],Classes2[i], sList.get(w));
						Na+=Nxc[i];
						Nc[i] = tdm.getTrainingFileCountOfClassification2(Classes[v],Classes2[i]);
				//		TF[i]=(Nxc[i]+1)/(Nc[i]+1);
				//		System.out.println("TF"+TF);
						int temp=0;
						for(int k=temp2;k<temp2+index[i];k++){
							if(sList.get(w).equals(sList.get(k))){
								temp++;
							}
						}
						temp2+=index[i];
						TF[i]=(temp*Nc[i])/index[i];
			//			System.out.println("TFi:"+TF[i]+"-->index[i]:"+index[i]+"-->temp:"+temp);
					}
					IDF=Math.log((N+1)/(Na+1));
			//		double Var=Variance(TF);
					double W[] =new double[Classes2.length];
			//		String s=terms[w];
					Buffer b=new Buffer(Classes2.length);
					b.word=sList.get(w);
					for(int i=0;i<Classes2.length;i++){
						
			//			W[i]=(TF[i]*IDF*Nxc[i]*Nxc[i])/(Na*Nc[i]);
						W[i]=TF[i]*IDF;
				//		W[i]=(TF[i])*IDF;
				//		System.out.println("W[i]"+W[i]+"IDF"+IDF);
						b.pr[i]= (float) W[i];
					//	s+="-->"+Classes[i]+":"+W[i];
					}
			//		b.Var=Variance(W);
					Lw.add(b);
					count++;
					if(count%100==0){
						System.out.println("TFIDF训练词："+count);
				}
				//	System.out.println(s);
				}
			}
			//归一化
			double sum=1.0;
			int lenght=Lw.size();
		/*	
			if (lenght>100){
			//	double t= (5*Math.sqrt(lenght-100)+100);
				lenght=(lenght-100)/2+100;
			}
			*/
	//		lenght=lenght*4/5;
			double q[][]=new double[lenght][Classes2.length];
			for(int i=0;i<Classes2.length;i++){
				for(int j=0;j<(lenght);j++){
					sum+=(Lw.get(j).pr[i]*Lw.get(j).pr[i]);
				}
				sum=Math.sqrt(sum);
				for(int j=0;j<(lenght);j++){
					q[j][i]=Lw.get(j).pr[i]/sum;
				}
			}
			/*
			for(int j=0;j<(lenght);j++){
				for(int i=0;i<Classes.length;i++){
					sum+=(Lw.get(j).pr[i]*Lw.get(j).pr[i]);
				}
				sum=Math.sqrt(sum);
				for(int i=0;i<Classes.length;i++){
					q[j][i]=Lw.get(j).pr[i]/sum;
				}
				
			}
			*/
			for(int j=0;j<(lenght);j++){
				Buffer b=new Buffer(Classes2.length);
				b.word=Lw.get(j).word;
		//		b.Var=Lw.get(j).Var;
				for(int i=0;i<Classes2.length;i++){
					b.pr[i]= (float) q[j][i];
			//		if(b.pr[i]<1E-8){
			//			b.pr[i]=0;
			//		}
				}
				Dbase.add(b);

			}
			System.out.println("训练结束...\n");
			System.out.println("写入数据库...");
			String tableName="二TFIDF"+Classes[v];
			db.creatTable(tableName);
			for(int m=0;m<Classes2.length;m++){
				db.addRow(tableName, "TFIDF"+Classes2[m]);
			}
			db.insertData(Dbase, Classes2.length, tableName);
			Dbase.clear();
			System.out.println("二级分类"+Classes[v]+"训练结束...\n");
			
			
		}
		System.out.println("训练结束TFIDF...\n");
	}
	
	
	
	
	
	/**
	 * 读取一级分类所有文本
	 * @return
	 */
	@SuppressWarnings("static-access")
	public String getText1(String Classes){
		
		String text=null;
	//	for(int i=0;i<Classes.length;i++){
			String Classes2[]=tdm.get2ClassificationsName(Classes);
			for(int j=0;j<tdm.get2ClassificationsName(Classes).length;j++){
				String path[]=tdm.get2FilesPath(Classes, Classes2[j]);
				for(int k=0;k<path.length;k++){
					try {
						text+=tdm.getText(path[k])+"\n";
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
			}
	//	}
		System.out.println("导入一级文本结束...");
		return text;
	}
	
	
	/**
	 * 读取二级分类样本
	 * @param Classification
	 * @return
	 */
	@SuppressWarnings("static-access")
	public String getText2(String Classification,String Classes2){
		System.out.println("导入二级文本文件...");
		String text=null;
	//	String Classes2[]=tdm.get2ClassificationsName(Classification);
	//	for(int i=0;i<Classes2.length;i++){
			String path[]=tdm.get2FilesPath(Classification, Classes2);
			for(int k=0;k<path.length;k++){
				try {
					text+=tdm.getText(path[k])+"\n";
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
	//	}
		System.out.println("导入二级文本结束...");
		return text;
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
	
	
	/**
	 * 去除数字
	 * @param text
	 * @return
	 */
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
