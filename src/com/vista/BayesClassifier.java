package com.vista;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.*;


/**
* 朴素贝叶斯分类器
*/
public class BayesClassifier extends JFrame implements ActionListener
{
	static PrintWriter pw; // 日志记录器
	ArrayList ls=new ArrayList();
	ArrayList conclusion=new ArrayList();
//	ArrayList pWord=new ArrayList();

	List<String> pResult;
	private static TrainingDataManager tdm;//训练集管理器
	private String trainnigDataPath;//训练集路径

	private JMenuBar menuBar = new JMenuBar();
	private JMenuItem openSourceFile, openDicItem, closeItem;
	private JRadioButtonMenuItem fmmItem, bmmItem;
	private JMenuItem openTrainFileItem, saveDicItem, aboutItem;
	private JButton btSeg1, btSeg2, btSeg3,btSeg4;
	private JTextArea taOutput;
	private JTextArea taInput;
//	private JTextField taInputFile;
	private JPanel panel;
//	JLabel infoDic, infoAlgo;
	public String FilePath;

	private void initFrame()
	{
		setTitle("南方日报新闻分类系统");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("文件");
		JMenu algorithmMenu =  new JMenu("训练");
		JMenu trainMenu =  new JMenu("设置");
		JMenu helpMenu =  new JMenu("帮助");
		 
		openSourceFile = fileMenu.add("截入待分词文件");
		openDicItem = fileMenu.add("载入词典");
		fileMenu.addSeparator();
		closeItem = fileMenu.add("退出");
		
		algorithmMenu.add(fmmItem = new JRadioButtonMenuItem("正向最大匹配", true));
		algorithmMenu.add(bmmItem = new JRadioButtonMenuItem("逆向最大匹配", false));
		ButtonGroup algorithms = new ButtonGroup();
		algorithms.add(fmmItem);
		algorithms.add(bmmItem);
		
		openTrainFileItem = trainMenu.add("载入并训练语料");
		saveDicItem = trainMenu.add("保存词典");
		
		aboutItem = helpMenu.add("关于Word Segment Demo");		
		
		menuBar.add(fileMenu);
		menuBar.add(algorithmMenu);
		menuBar.add(trainMenu);
		menuBar.add(helpMenu);
		openDicItem.addActionListener(this);
		openSourceFile.addActionListener(this);
		closeItem.addActionListener(this);
		openTrainFileItem.addActionListener(this);
		saveDicItem.addActionListener(this);
		aboutItem.addActionListener(this);	
		fmmItem.addActionListener(this);
		bmmItem.addActionListener(this);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
	//	topPanel.setLayout(new GridLayout(2,3,10,10));
	//	JPanel centerPanel = new JPanel();
	//	centerPanel.setLayout(new GridLayout(2,1));//GridLayout
		JPanel bottomPanel = new JPanel();
		this.getContentPane().add(topPanel, BorderLayout.NORTH);
	//    this.getContentPane().add(centerPanel, BorderLayout.CENTER);
	    this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

	    
	    btSeg1 = new JButton("文本分类");
	    
	    btSeg2 = new JButton("新闻集分类");
	    btSeg3 = new JButton("训练样本");
	    btSeg4 = new JButton("加载目录");
	    //btSeg1 = new JButton("正向最大匹配分词");
	    //btSeg2 = new JButton("逆向最大匹配分词");
	    //btSeg3 = new JButton("JE-analyzer分词(Lucene)");
		//tfInput = new JTextField("", 30);
		taInput = new JTextArea();   taInput.setLineWrap( true );
		taOutput = new JTextArea();  taOutput.setLineWrap( true );
		
	    this.getContentPane().add(new JScrollPane(taInput), BorderLayout.CENTER); 
	    this.getContentPane().add(new JScrollPane(taOutput), BorderLayout.CENTER); 
//		taInputFile=new JTextField(20);
		//topPanel.add(tfInput);
		topPanel.add(btSeg4);
	//	topPanel.add(btSeg1);
		topPanel.add(btSeg2);
		topPanel.add(btSeg3);


		//topPanel.add(btSeg3);
//		taInputFile.setText("加载目录");
//		centerPanel.add(taInput );  //taInput.setText( "aaa");
//		centerPanel.add(taOutput);  //taOutput.setText( "bbb");
		
//		infoDic = new JLabel();
//		infoAlgo = new JLabel();
//		bottomPanel.add(taInputFile);
//		bottomPanel.add(infoAlgo);
		saveDicItem.setEnabled(false);
		btSeg1.addActionListener(this);
		btSeg2.addActionListener(this);
		btSeg3.addActionListener(this);
		btSeg4.addActionListener(this);
		//btSeg3.addActionListener(this);

	}
	
	public void actionPerformed(ActionEvent e) {
		//文本分类
		if(e.getSource() == btSeg1)
		{/*
			String[] Classes =tdm.getTraningClassifications();//分类
			List<Buffer> dbase;
			DataBase d=new DataBase();
			
			d.creatTable("一级样本");
			for(int i=0;i<Classes.length;i++){
				d.addRow("一级样本", "子类"+Classes[i]);
			}
			
			String[] rowName=new String[Classes.length] ;//分类
			for (int k = 0; k <Classes.length; k++) {
				rowName[k]="子类_"+Classes[k];
			}
			dbase=d.getData("一级分类样本", rowName);
			for(int i=0;i<dbase.size();i++){
				dbase.get(i).word=dbase.get(i).word.replaceAll("\\s", "");
			}
			d.insertData(dbase, Classes.length, "一级样本");
			String s="yayn      m";
			System.out.println(s.replaceAll("\\s", ""));
			for(int i=0;i<Classes.length;i++){
				String[] Classes2 = tdm.get2ClassificationsName(Classes[i]);
				String tableName="二"+Classes[i];
				d.creatTable(tableName);
				for(int m=0;m<Classes2.length;m++){
					d.addRow(tableName, "子类"+Classes2[m]);
				}
				String[] rowName=new String[Classes2.length] ;//分类
				for (int k = 0; k <Classes2.length; k++) {
					rowName[k]="子类"+Classes2[k];
				}
				dbase=d.getData("二级"+Classes[i], rowName);
				for(int k=0;k<dbase.size();k++){
					dbase.get(k).word=dbase.get(k).word.replaceAll("\\s", "");
				}
				d.insertData(dbase, Classes2.length, tableName);
				System.out.println(i);
			}*/
		//	application app=new application();
		//	app.classify("..\\trs2txt\\nf1012_test\\2\\sample\\05_社会劳动灾难事故\\03","05_社会劳动灾难事故","03");
			TrainingDataManager tdm=new TrainingDataManager();
			float Nxc = tdm.getCountContainKeyOfClassification("01_政治", "贪污");
		//	float Nc = tdm.getTrainingFileCount2("01_政治");
		//	float V = tdm.getTraningClassifications().length;
			System.out.println(Nxc);
		}
		
		//新闻集分类
		if(e.getSource() == btSeg2)
		{	
		//	test();
			
			
			String classes="1娱乐";
			String[] Classes2 = get2ClassificationsName(classes);
			application app=new application();
			
			app.classify(this.FilePath,"1娱乐","01");
			
			/*
			for(int i=0;i<Classes2.length;i++){//***********
				//String path=".\\sample\\"+classes+"\\"+Classes2[i];////*****
				File traningTextDir = new File(this.FilePath);
				//File traningTextDir = new File(path);
				if (traningTextDir.isDirectory()) 
				{
				//	app.classify(this.FilePath,classes,Classes2[0]);
					app.classify(this.FilePath,classes,Classes2[i]);
				}
			}//**
			*/
	//		String s=taOutput.getText();
	//		taOutput.setText(s+"\n"+app.getResult());
			taOutput.setText(app.getResult());
		}
		
		//训练样本
		if(e.getSource() == btSeg3)
		{
		//	Training t=new Training();
		//	t.classify1();
		//	t.classify2();
			
			mThread m1=new mThread("一级分类");
			Thread t1=new Thread(m1);
			t1.start();
			
			
			mThread m2=new mThread("二级分类");
			Thread t2=new Thread(m2);
			t2.start();
			
			
			mThread m6=new mThread("CHI");
			Thread t6=new Thread(m6);
			t6.start();
			
			
			
			mThread m4=new mThread("CHI2");
			Thread t4=new Thread(m4);
			t4.start();
			
			/*
			mThread m7=new mThread("TFIDF");
			Thread t7=new Thread(m7);
			t7.start();
			
			mThread m5=new mThread("TFIDF2");
			Thread t5=new Thread(m5);
			t5.start();*/
			
			
		}
		
		//加载目录
		if(e.getSource() == btSeg4){
	//		System.out.println("test");
	//		read();
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//        JFileChooser.FILES_ONLY
//        JFileChooser.DIRECTORIES_ONLY
			int returnVal = fc.showOpenDialog(this);
			File file_choosed = fc.getSelectedFile();
			this.FilePath=file_choosed.getPath();
			System.out.println(this.FilePath);
			taOutput.setText("新闻目录："+this.FilePath);
		}
	}
		
	public void test(){
		TrainingDataManager tdm=new TrainingDataManager();
		String[] Classes =tdm.getTraningClassifications();//分类
		for (int j=0;j<Classes.length;j++){
			String classes=Classes[j];
			String[] Classes2 = get2ClassificationsName(classes);
			application app=new application();
			
			for(int i=0;i<Classes2.length;i++){
				String path="..\\trs2txt\\nf1012_test\\Old\\sample\\"+classes+"\\"+Classes2[i];
				File traningTextDir = new File(path);
				if (traningTextDir.isDirectory()) 
				{
					app.classify(path,classes,Classes2[i]);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void read(){
		String[] Classes =tdm.getTraningClassifications();//分类
		DataBase d=new DataBase();
		List<Buffer> dbase;
		System.out.println("导入一级数据库...");
		String[] rowName=new String[Classes.length] ;//分类
		for (int k = 0; k <Classes.length; k++) {
			rowName[k]="TFIDF"+Classes[k];
		}
		dbase=d.getData("样本TFIDF", rowName);
		
	/*	
		java.util.Collections.sort(dbase,new Comparator() 
		{
			public int compare(final Object o1,final Object o2) 
			{
				final Buffer m1 = (Buffer) o1;
				final Buffer m2 = (Buffer) o2;
				final double ret = m1.pr[21] - m2.pr[21];
		//		final double ret = m1.TFIDF[21] - m2.TFIDF[21];
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
		*/
	//	for(int i=0;i<dbase.size();i++){
		for(int i=0;i<100;i++){	
	//		String s=dbase.get(i).word+": "+dbase.get(i).pr[21];
			String s=dbase.get(i).word;
			for (int k = 0; k <Classes.length; k++) {
		//		s+=Classes[k]+":"+dbase.get(i).pr[k];
			}
			System.out.println(s);
		}
		
	}
	
	/**
	* 默认的构造器，初始化训练集
	*/
	public BayesClassifier() 
	{
		tdm =new TrainingDataManager();
	}

	/**
	* 计算给定的文本属性向量X在给定的分类Cj中的类条件概率
	* <code>ClassConditionalProbability</code>连乘值
	* @param X 给定的文本属性向量
	* @param Cj 给定的类别
	* @return 分类条件概率连乘值，即<br>
	*/
	
	
	public static void main(String[] args) throws IOException
	{
		BayesClassifier classifier = new BayesClassifier();//构造Bayes分类器
		classifier.initFrame();
		Toolkit theKit = classifier.getToolkit();
		Dimension wndSize = theKit.getScreenSize();
		
		classifier.setBounds(wndSize.width/4, wndSize.height/4,
						wndSize.width/2, wndSize.height/2);
		classifier.setVisible(true);
		/*
		String s[]=tdm.get2FilesPath("01_政治","03");
		for(int i=0;i<s.length;i++){
			System.out.println(s[i]);
		}*/
	//	DataBase d=new DataBase();
	//	d.insertData("tcxp", 5,"sogou");
	//	System.out.println(tdm.getTrainingFileCount2("01_政治"));

	}
	
	public String[] get2ClassificationsName(String classification){
		String Path=".\\sample";
		File traningTextDir= new File(Path);
		File classDir = new File(traningTextDir.getPath() +File.separator +classification);
		String[] ret = classDir.list();
		return ret;
	}
	
}