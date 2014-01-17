package com.vista;

public class probability {
	TrainingDataManager tdm=new TrainingDataManager();
	public  static float arg=0;
	public  static float arg2=0;
	private static final float M = 0F;
	public probability(){
		
	}
	/**
	 * 一级分类计算
	 * @param x
	 * @param c
	 * @return
	 */
	public float calculatePxc(String x, String c) {
		float ret = 0F;
		float Nxc = tdm.getCountContainKeyOfClassification(c, x);
		float Nc = tdm.getTrainingFileCount2(c);
		float V = tdm.getTraningClassifications().length;
		if (Nxc==0){
			ret = 1 / (arg + M + V);	
		}else{
			ret = (Nxc + 1)/ (Nc + M + V);
		}
		
		ret=(float) Math.log(ret);
		return ret;
	}
	
	public void computArg(){
		//	float arg=0;
			String[] Classes = tdm.getTraningClassifications();
			for(int i=0;i<Classes.length;i++){
				arg += tdm.getTrainingFileCount2(Classes[i]);	
			}
			arg=arg/tdm.getTraningClassifications().length;
		}
		
	/**
	 * 二级分类计算
	 * @param x
	 * @param c
	 * @param c2
	 * @return
	 */
	public float calculatePxc2(String x, String c1,String c2) {
		float ret = 0F;
		float Nxc = tdm.getCountContainKeyOfClassification2(c1, c2,x);
		float Nc = tdm.getTrainingFileCountOfClassification2(c1,c2);
		float V = tdm.get2ClassificationsName(c1).length;
		if (Nxc>-1){
			ret = (Nxc+1) / (arg2 + M + V);	
		}else{
			ret = (Nxc + 1)/ (Nc + M + V);
		}
		
		ret=(float) Math.log(ret);
		return ret;
	}
	public void computArg2(String c1){
		//	float arg=0;
			String[] Classes = tdm.get2ClassificationsName(c1);
			for(int i=0;i<Classes.length;i++){
				arg2 += tdm.getTrainingFileCountOfClassification2(c1,Classes[i]);	
			}
			arg2=arg2/tdm.getTraningClassifications().length;
		}

}
