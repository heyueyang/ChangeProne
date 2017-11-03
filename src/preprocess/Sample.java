package preprocess;
import predict.Config;

import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Random;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;

public class Sample{
	 private static String className = "bug_introducingCopy";//"change_prone";
	 public Sample(String claName){
		 className = claName;
	 }
	 public Sample(){
	 }
	 
	 public static Instances AntiUnderSample(Instances init, double samRatio) throws Exception{
		 double ratio = samRatio;
		 int numAttr = init.numAttributes();
			int numInstance = init.numInstances();

			FastVector attInfo = new FastVector();
			for (int i = 0; i < numAttr; i++) {
				weka.core.Attribute temp = init.attribute(i);
				attInfo.addElement(temp);
			}

			Instances NoInstances = new Instances("No", attInfo, numInstance);

			NoInstances.setClass(NoInstances.attribute(className));

			Instances YesInstances = new Instances("yes", attInfo, numInstance);
			YesInstances.setClass(YesInstances.attribute(className));

			init.setClass(init.attribute(className));
			int classIndex = init.classIndex();
			
			int numYes = 0;
			int numNo = 0;
			
			for (int i = 0; i < numInstance; i++) {
				Instance temp = init.instance(i);
				double Value = temp.value(classIndex);
				if (Value == 0) { // yes
					NoInstances.add(temp);
					numNo++;
				} else {
					YesInstances.add(temp);
					numYes++;
				}
			}
			
			Instances res;
			if (numYes > numNo) {
				if ((double)(numNo/numYes) <= ratio) {
					return init;
				}
				res = excuteSample(YesInstances, NoInstances, ratio);
				
			} else {
				if ((double)(numYes/numNo) <= ratio) {
					return init;
				}
				res = excuteSample(NoInstances, YesInstances, ratio);
			}
			return res;

		 /*
			Instances res = new Instances(ins);
			res.delete();
			int valueCnt[] = {0,0};
			int attNum = ins.numAttributes();
			int insNum = ins.numInstances();
			int[] label = new int[insNum];
			ins.setClassIndex(attNum-1);
			int temp = 0;
			for(int i =0; i < insNum; i++){		
				temp = (int) ins.instance(i).classValue();
				valueCnt[temp]++;
				label[i] = temp;
			}
			int anomCnt = (valueCnt[0] > valueCnt[1]) ? valueCnt[1] : valueCnt[0];
	    	int anomLab = (valueCnt[0] > valueCnt[1]) ? 1 : 0;
	    	
	    	java.util.Random r=new java.util.Random();
	    	int[] posInd = new int[anomCnt];
	    	int[] negInd = new int[insNum - anomCnt];
	    	int t = 0, p = 0;
	    	int samNum = 0;
	    	for(int i =0; i < insNum; i++){		
				temp = (int) ins.instance(i).classValue();
				if(label[i] == anomLab){
					posInd[t++] = i;
					
				}else{
					negInd[p++] = i;
				}	
			}
	    	//sampling in positive index
	    	if(((double)anomCnt/(double)insNum) <= samRatio){
	    		System.out.println(" (anomCnt"+anomCnt+")/insNum("+insNum+") "+((double)anomCnt/(double)insNum)+"<= samRatio "+samRatio);
	    		res = ins;
	    	}else{
		    	samNum = (int) ((insNum-anomCnt)*samRatio);
		    	if(samNum <= anomCnt){
			    	for(int i =0; i < samNum; i++){
			    		//System.out.println(posInd[r.nextInt(anomCnt)]);
			    		res.add(ins.instance(posInd[r.nextInt(anomCnt)]));
			    	}
			    	for(int i =0; i < negInd.length; i++){
			    		res.add(ins.instance(negInd[i]));
			    	} 
			    	
		    	}else{
		    		throw new Exception("samNum "+samNum+" must be smaller than number of positive instances "+anomCnt);
		    	}
		    	
	    	}
	    	String underPath = Config.select_folder + this.file.substring( 0,  file.lastIndexOf(".")) + "_undersample" + ".arff";
            System.out.println("===UnderSample Path==="+underPath);
            System.out.println("===minority num("+anomLab+"):"+anomCnt+" ===");
            System.out.println("===new minority num("+anomLab+"):"+samNum+" ===");
            ArffSaver saver = new ArffSaver(); 
    		saver.setInstances(res);  
    	    saver.setFile(new File(underPath));  
    	    saver.writeBatch(); 
	    	return res;*/
	    	
		 }
	 
	 public static Instances AntiOverSample(Instances init, double overTimes) throws Exception{
		 double ratio = overTimes;
		 FastVector attInfo = new FastVector();
			for (int i = 0; i < init.numAttributes(); i++) {
				weka.core.Attribute temp = init.attribute(i);
				attInfo.addElement(temp);
			}
			Instances YesInstances = new Instances("DefectSample1", attInfo,
					init.numInstances());// 杩欓噷鐨勫垵濮嬪閲忛渶瑕佹敞鎰忥紝涓嶈灏忎簡銆�
			YesInstances.setClass(YesInstances.attribute(className));

			// YesInstances.setClassIndex(init.numAttributes() - 1);
			// 鏈兘缁熶竴鐨勫皢绫绘爣绛句綔涓烘渶鍚庝竴涓睘鎬э紝鍙兘�?艰嚧璁＄畻涓婄殑澶嶆潅锛屾湁寰呮敼杩涖�
			Instances Noinstances = new Instances("DefectSample2", attInfo,
					init.numInstances());
			Noinstances.setClass(Noinstances.attribute(className));
			init.setClass(init.attribute(className));
			int classIndex = init.classIndex();
			int numInstance = init.numInstances();
			int numYes = 0;
			int numNo = 0;
			for (int i = 0; i < numInstance; i++) {
				Instance temp = init.instance(i);
				double Value = temp.value(classIndex);
				if (Value == 1) { // weka鐨勫唴閮ㄥ�骞朵笉涓庡睘鎬х殑鍊肩浉�?瑰簲锛屽弬鑰僿eka api銆�
					YesInstances.add(temp);
					numYes++;
				} else // clear change
				{
					Noinstances.add(temp);
					numNo++;
				}
			}
			// 濡傛灉鏁伴噺鐩哥瓑锛屽疄闄呬笂鏄病鏈夋墽琛岃繃閲囨牱鐨勩�
			Instances res;
			if (numYes > numNo) {
				if ((double)(numNo/numYes) <= ratio) {
					return init;
				}
				res = excuteSample(Noinstances, YesInstances, 1/ratio);
			} else {
				if ((double)(numYes/numNo) <= ratio) {
					return init;
				}
				res = excuteSample(YesInstances, Noinstances, 1/ratio);
			}
			return res;
		    /*
			Instances res = ins;
			int valueCnt[] = {0,0};
			int attNum = ins.numAttributes();
			int insNum = ins.numInstances();
			int[] label = new int[insNum];
			ins.setClassIndex(attNum-1);
			int temp = 0;
			for(int i =0; i < insNum; i++){		
				temp = (int) ins.instance(i).classValue();
				valueCnt[temp]++;
				label[i] = temp;
			}
			int majorityCnt = (valueCnt[0] > valueCnt[1]) ? valueCnt[0] : valueCnt[1];
	    	int majorityLab = (valueCnt[0] > valueCnt[1]) ? 0 : 1;
	    	
	    	java.util.Random r=new java.util.Random();
	    	int[] posInd = new int[insNum-majorityCnt];
	    	int[] negInd = new int[majorityCnt];
	    	int t = 0, p = 0;
	    	
	    	for(int i =0; i < insNum; i++){		
				temp = (int) ins.instance(i).classValue();
				if(label[i] == majorityLab){
					negInd[t++] = i;
					
				}else{
					posInd[p++] = i;
				}	
			}
	    	//sampling in positive index
	    	int samNum = (int) (majorityCnt*(overTimes-1));
		    for(int i =0; i < samNum; i++){
		    	//System.out.println(posInd[r.nextInt(anomCnt)]);
		    	res.add(ins.instance(negInd[r.nextInt(majorityCnt)]));
		    }
		    String overPath = Config.select_folder + this.file.substring( 0,  file.lastIndexOf(".")) + "_oversample" + ".arff";
            System.out.println("===OverSample Path==="+overPath);
            System.out.println("===majority num("+majorityLab+"):"+majorityCnt+" ===");
            System.out.println("===new majority num("+majorityLab+"):"+samNum+" ===");
            ArffSaver saver = new ArffSaver(); 
    		saver.setInstances(res);  
    	    saver.setFile(new File(overPath));  
    	    saver.writeBatch(); 
	    	
	    	return res;*/
		 }
	 
	 /**
		 * 杩囬噰鏍锋柟娉曘�?
		 * 
		 * @param init
		 * @return
		 * @throws IOException
		 */
		public static Instances OverSample(Instances init) throws IOException {
			FastVector attInfo = new FastVector();
			for (int i = 0; i < init.numAttributes(); i++) {
				weka.core.Attribute temp = init.attribute(i);
				attInfo.addElement(temp);
			}
			Instances YesInstances = new Instances("DefectSample1", attInfo,
					init.numInstances());// 杩欓噷鐨勫垵濮嬪閲忛渶瑕佹敞鎰忥紝涓嶈灏忎簡銆�
			YesInstances.setClass(YesInstances.attribute(className));

			// YesInstances.setClassIndex(init.numAttributes() - 1);
			// 鏈兘缁熶竴鐨勫皢绫绘爣绛句綔涓烘渶鍚庝竴涓睘鎬э紝鍙兘�?艰嚧璁＄畻涓婄殑澶嶆潅锛屾湁寰呮敼杩涖�
			Instances Noinstances = new Instances("DefectSample2", attInfo,
					init.numInstances());
			Noinstances.setClass(Noinstances.attribute(className));
			init.setClass(init.attribute(className));
			int classIndex = init.classIndex();
			int numInstance = init.numInstances();
			int numYes = 0;
			int numNo = 0;
			for (int i = 0; i < numInstance; i++) {
				Instance temp = init.instance(i);
				double Value = temp.value(classIndex);
				if (Value == 1) { // weka鐨勫唴閮ㄥ�骞朵笉涓庡睘鎬х殑鍊肩浉�?瑰簲锛屽弬鑰僿eka api銆�
					YesInstances.add(temp);
					numYes++;
				} else // clear change
				{
					Noinstances.add(temp);
					numNo++;
				}
			}
			// 濡傛灉鏁伴噺鐩哥瓑锛屽疄闄呬笂鏄病鏈夋墽琛岃繃閲囨牱鐨勩�
			if (numYes == numNo) {
				return init;
			}
			Instances res;
			if (numYes > numNo) {
				res = excuteSample(YesInstances, Noinstances, 1);
			} else {
				res = excuteSample(Noinstances, YesInstances, 1);
			}
			return res;
		}

		/**
		 * 鎸夌収缁欏畾鐨勬瘮渚嬭繘琛岃繃鎶芥牱銆�
		 * 
		 * @param instances1
		 *            涓诲疄渚嬮泦锛屽嵆渚濇嵁鐨勫疄渚嬮泦锛屼篃灏辨槸鍏ㄩ儴浣跨敤鐨勫疄渚嬮泦銆�
		 * @param instances2
		 *            鍓疄渚嬮泦锛屼篃灏辨槸鐪熸�?炶閲囨牱鐨勫疄渚嬮泦銆�?		 * @param i
		 *            鎶芥牱鍚庡緱鍒扮殑涓嶅悓鐨勭被鏍囩鐨勬瘮渚嬶紝鍗虫娊鏍峰悗num(yesInstances)/num(noinstances)鐨勬瘮渚嬶紝娉ㄦ剰锛�?		 *            鐢变簬涓轰簡 鍔犻�绋嬪簭杩愯閫熷害锛屾渶鍚庡疄楠岀粨鏋滄娊鏍锋椂璁剧疆涓�銆�
		 */
		private static Instances excuteSample(Instances instances1,
				Instances instances2, double ratio) {
			int numSample = (int) Math.ceil(instances1.numInstances() * ratio); // 浼氫笉浼氱敱浜庡疄渚嬫暟杩囧鑰屽穿婧冿�?
			int numNo = instances2.numInstances();
			Random rn = new Random();
			for (int i = 0; i < numSample; i++) {
				instances1.add(instances2.instance(rn.nextInt(numNo)));
			}
			return instances1;
		}

		/**
		 * 娆犻噰鏍锋柟娉�
		 * @param init 鐢ㄤ簬閲囨牱鐨勫疄渚嬮泦.
		 * @return
		 * @throws IOException
		 */
		public static Instances UnderSample(Instances init) throws IOException {
			int numAttr = init.numAttributes();
			int numInstance = init.numInstances();

			FastVector attInfo = new FastVector();
			for (int i = 0; i < numAttr; i++) {
				weka.core.Attribute temp = init.attribute(i);
				attInfo.addElement(temp);
			}

			Instances NoInstances = new Instances("No", attInfo, numInstance);

			NoInstances.setClass(NoInstances.attribute(className));

			Instances YesInstances = new Instances("yes", attInfo, numInstance);
			YesInstances.setClass(YesInstances.attribute(className));

			init.setClass(init.attribute(className));
			int classIndex = init.classIndex();
			
			int numYes = 0;
			int numNo = 0;
			
			for (int i = 0; i < numInstance; i++) {
				Instance temp = init.instance(i);
				double Value = temp.value(classIndex);
				if (Value == 0) { // yes
					NoInstances.add(temp);
					numNo++;
				} else {
					YesInstances.add(temp);
					numYes++;
				}
			}
			if (numYes == numNo) {
				return init;
			}
			Instances res;
			if (numYes > numNo) {
				res = excuteSample(NoInstances, YesInstances, 1);
			} else {
				res = excuteSample(YesInstances, NoInstances, 1);
			}
			return res;
		}

		
		public static Instances SmoteSample(Instances ins, double ratio) throws Exception
		{
			int rat = 0;
			int classIndex = ins.classIndex();
			int numInstance = ins.numInstances();
			int numYes = 0;
			int numNo = 0;
			
			for (int i = 0; i < numInstance; i++) {
				Instance temp = ins.instance(i);
				double Value = temp.value(classIndex);
				if (Value == 0) { // yes
					numNo++;
				} else {
					numYes++;
				}
			}
			if (numYes == numNo) {
				rat = 100;
			}
			if (numYes > numNo) {
				rat = numYes/numNo;
			} else {
				rat = numNo/numYes;
			}
			
			weka.filters.supervised.instance.SMOTE smote = new  SMOTE();
			//smote.setPercentage(ratio*100);
			//smote.setPercentage(rat*100);
			//System.out.println("somte percentage : " + smote.getPercentage());
			ins.setClassIndex(ins.numAttributes()-1);
			smote.setInputFormat(ins);
			Instances res = Filter.useFilter(ins, smote);
			return res;
			
		}
		
		public Instances RandomSample(Instances init, double ratio) {
			int numAttr = init.numAttributes();
			int numInstance = init.numInstances();
			int totalNum = (int) (numInstance * ratio);
			
			FastVector attInfo = new FastVector();
			for (int i = 0; i < numAttr; i++) {
				weka.core.Attribute temp = init.attribute(i);
				attInfo.addElement(temp);
			}
			Instances res = new Instances("Res", attInfo, totalNum);
			Random rn = new Random();
			for (int i = 0; i <totalNum; i++) {
					res.add(init.instance(rn.nextInt(numInstance)));
			}
			res.setClass(res.attribute(className));
			return res;
		}
	
}