package com.vista;

import java.util.ArrayList;
import java.util.List;

public class confidence {
	private static TrainingDataManager tdm = new TrainingDataManager();
	public confidence(){
		
	}
	public List<String> areaConfidence(List<ClassifyResult> crs){
		List<String> name = new ArrayList<String>();
		double sum1=0;
		double sum2=0;
		double v=tdm.getTraningClassifications().length;
		for ( int i = 0;i<((v+1)/2);i++){
			sum1+=crs.get(i).probility;
		}
		double avg=(2*sum1)/(v+1);
		for ( int i = 0;i<((v+1)/2);i++){
			sum2 += Math.pow((crs.get(i).probility-avg), 2);
	//		sum2+=Math.pow((crs.get(i).probility-crs.get((int) ((v-1)/2)).probility), 2);
		}
		System.out.println(sum2+"+-*/");
		double powS = sum2/((v+1)/2-1);
		//t(22)0.25=0.686,t(22)0.20=0.858,t(22)0.15=1.061
		//0.686  0.858  1.061  1.321  1.717  2.074  2.508  2.819  3.119  3.505
//		double down = (avg+3.505*Math.sqrt(powS/v));
		double down = (crs.get((int) ((v+1)/4)).probility+3.505*Math.sqrt(powS/((v+1)/2)));
		
		
//		double up = avg+0.686*Math.sqrt(powS/v);
//		System.out.println("avg="+avg);
//		System.out.println("-------="+Math.sqrt(powS/((v+1)/2)));
//		System.out.println("*****"+crs.get((int) ((v+1)/4)).probility);
	//	System.out.println("Xarg="+Math.sqrt(powS/v));
		name.add(crs.get(0).classification);
	//	System.out.println("可能是："+crs.get(0).classification);
		for ( int i = 1;i<v;i++){
			if (down<=crs.get(i).probility){
	//			System.out.println("可能是："+crs.get(i).classification);
				name.add(crs.get(i).classification);
				
			}else{
				break;
			}
		}
		name.add("置信下限"+down);
		System.out.println("置信下限"+down);
		return name;
	}
}
