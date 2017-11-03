package compare;

import java.io.File;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class Test {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Instances ins = null;

	       Classifier cfs = null;

	      

	       try {

	           //读入训练测试样本

	           File file = new File("C:\\Program Files\\Weka-3-7\\data\\contact-lenses.arff");

	           ArffLoader loader = new ArffLoader();

	           loader.setFile(file);

	           ins = loader.getDataSet();

	           ins.setClassIndex(ins.numAttributes()-1);

	           //初始化分类器

	           cfs = (Classifier)Class.forName("weka.classifiers.bayes.NaiveBayes").newInstance();

	           //使用训练样本进行分类

	           cfs.buildClassifier(ins);

	           //使用测试样本测试分类器的学习效果

	           Instance testInst;

	           Evaluation testingEvaluation = new Evaluation(ins);

	           int length = ins.numInstances();

	           for(int i = 0; i < length ; i++){

	              testInst = ins.instance(i);

	              testingEvaluation.evaluateModelOnceAndRecordPrediction(cfs, testInst);

	           }

	           //打印分类结果

	           System.out.println("分类的正确率"+(1-testingEvaluation.errorRate()));

	       } catch (Exception e) {

	           e.printStackTrace();

	       }
	}

}
