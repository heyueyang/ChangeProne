package preprocess;

import predict.Config;
import predict.FileUtil;

import java.io.File;
import java.sql.Date;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Calendar;
import java.util.Vector;
import java.util.jar.Attributes;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;

public class LogPre{
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

    	String result_folder = Config.sample_folder;
    	String data_folder = Config.data_folder;
    	File f = new File(result_folder);
    	if(!f.exists()) f.mkdirs();
  
    	File ff = new File(data_folder);//select_folder
    	File[] files = ff.listFiles();
    	for(int i = 0;i < files.length; i++){

    		System.out.println("=========File:"+files[i]+"=======");
        	try {
        		
        		Instances inputIns = FileUtil.ReadData(files[i].getAbsolutePath());
        		Instances resIns = Log(inputIns);
        		String path = result_folder + FileUtil.getFileName(files[i].getAbsolutePath()) + "Log" + ".arff";
        		FileUtil.WriteData(resIns, path);
        		/*Instances inputIns = FileUtil.ReadData(files[i].getAbsolutePath());
        		Instances resIns = Sample.AntiOverSample(inputIns, 0.05);
        		String path = result_folder + FileUtil.getFileName(files[i].getAbsolutePath()) + "AntiOver" + ".arff";
        		FileUtil.WriteData(resIns, path);
        		
        		inputIns = FileUtil.ReadData(files[i].getAbsolutePath());
        		resIns = Sample.AntiUnderSample(inputIns, 0.05);
        		path = result_folder + FileUtil.getFileName(files[i].getAbsolutePath()) + "AntiUnder" + ".arff";
        		FileUtil.WriteData(resIns, path);*/
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    	}
	//System.out.println("============Total Time Used:"+calendar.get(Calendar.MINUTE)+"min"+calendar.get(Calendar.SECOND)+"s==========");
	}
	
	public static Instances Log(Instances ins) throws Exception{
		
		FastVector attInfo = new FastVector();
		for(int i = 0; i < ins.numAttributes(); i++){
			weka.core.Attribute temp = ins.attribute(i);
			attInfo.addElement(temp);
		}
		//String path = Config.select_folder + FileUtil.getFileName(files[i].getAbsolutePath()) + "simple_LogFilted" + ".arff";
	
		Instances res = new Instances(ins);
		res.delete();
		//Instances res = new Instances(attInfo, ins.numInstances());
		
		//res.setClassIndex(res.numAttributes() - 1); //􁦡􁗝􀚓􁔄􀪂􀯔
		int numIn = ins.numInstances(); //􀷄􀴝􁵞 instance 􁌱􁫫􀕯􀽜􀣘􀒁􀨫􀷄
		System.out.println("===num Attr:" + ins.numAttributes() + "===");
		System.out.println("===num Ins:" + ins.numInstances() + "===");
		
		for (int i = 0;i < numIn; i++){
			Instance temp = ins.instance(i);
			for (int j=0;j < temp.numAttributes()-1;j++){
				double v1 = temp.value(j);
				double v2 = Math.log(v1+0.0001);
				temp.setValue(j, v2);
				//System.out.println(i + " " + j + "  " + v1 + "   " +v2);
			}
			res.add(temp); 
		}
		return res;
	}
	
}