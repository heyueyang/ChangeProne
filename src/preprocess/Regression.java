package preprocess;

import predict.ClassifyWithIsolation;
import predict.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.Saver;

public class Regression{
	private weka.classifiers.functions.LinearRegression LG = new weka.classifiers.functions.LinearRegression();
	private weka.classifiers.functions.SimpleLinearRegression simpleLG = new weka.classifiers.functions.SimpleLinearRegression();
	private Instances data = null;
	private String dataPath = null;
	private static String disPath = "E:\\dadaset\\new data\\statis.csv";//"E:\\dadaset\\nasa-data\\statis.csv";
	private static String outPath = Config.result_folder+"regression.csv";
	private static String outArff = Config.result_folder+"regression.arff";
	
	public static void main(String[] args) throws Exception{
		
		SimpleDateFormat dateFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long start = System.currentTimeMillis();
		String date =   dateFor.format(new Date(start));
		Calendar calendar = Calendar.getInstance();
    	System.out.println("============Start Time:"+date+"============");
    	if(!new File(outPath).exists()){
			
			
			//File ff = new File(Config.data_folder);
    		File ff = new File(Config.select_folder);
			File fff = new File(Config.result_folder);
			if(!fff.exists()) fff.mkdirs();
	    	String[] files = ff.list();
	    	Instances dataIns = null;
	    	//String[] files = {"liferay1.arff"};

	
	    	File out = new File(outPath); // CSV数据文件 
	    	System.out.println("===Output Path==="+outPath);
	    	
			BufferedWriter bw = new BufferedWriter(new FileWriter(out, true)); // 附加 
			String[] temp = new String[2];
			
			String[][] minorityRatio = getDistribution(files.length);
	    	double[] thres = new double[files.length];
	    	
	    	
	    	for(int i = 0;i < files.length; i++){
	    		temp = minorityRatio[i];
	    		//System.out.println(minorityRatio[i].toString());
				//File f = new File(Config.data_folder + files[i]);
	    		File f = new File(Config.select_folder + files[i]);
				ArffLoader loader = new ArffLoader();
				loader.setFile(f);
				dataIns = loader.getDataSet();
				thres[i] = ClassifyWithIsolation.findThreshold(dataIns, 1);
				System.out.println(temp[0] + "," + temp[1] + "," + thres[i]);
				bw.write(temp[0] + "," + temp[1] + "," + thres[i]);  
			    bw.newLine();  
	    	}
	    	bw.close(); 
    	}
    	
    	//FileReader fr = new FileReader(outPath);
		//Instances regIns = new Instances(fr);
		//ArffLoader loader = new ArffLoader();
		//loader.setFile(new File(outPath));
		//Instances data = loader.getDataSet();
		

		//ArffSaver saver = new ArffSaver();
		//saver.setInstances(instances);
        //saver.setFile(new File(outArff));  
        //saver.writeBatch();
		
		//simpleLG.buildClassifier(regIns);

		long end = System.currentTimeMillis();
		calendar.setTimeInMillis(end - start);
		System.out.println("============End Time:" + dateFor.format(new Date(end)) + "============");
		System.out.println("============Time Used:"+calendar.get(Calendar.MINUTE)+"min"+calendar.get(Calendar.MILLISECOND)+"s==========");
	}
	
	public static String[][] getDistribution(int m ) throws IOException{	
		
		BufferedReader reader = new BufferedReader(new FileReader(disPath));
		String line = null;
		ArrayList<String[]> dis = new ArrayList<String[]>(0);
		String[][] temp = new String[m][2];
		reader.readLine();
		reader.readLine();
		int j = 0;
		while((line=reader.readLine())!=null){  
            String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分 
            
            if(Double.parseDouble(item[item.length-1]) > Double.parseDouble(item[item.length-2])){
            	temp[j][0] = item[0].substring(6,item[0].lastIndexOf("."));
            	temp[j][1] = item[item.length-2];
            }else{
            	temp[j][0] = item[0].substring(6,item[0].lastIndexOf("."));
            	temp[j][1] = item[item.length-1];
            }
            j++;

        }  
		return temp;
		
	}
}