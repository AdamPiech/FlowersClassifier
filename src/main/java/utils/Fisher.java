package utils;

import com.sun.org.apache.xalan.internal.utils.FeatureManager;
import engine.Object;
import org.la4j.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

import java.util.*;
import java.util.Map.Entry;

import static java.lang.Double.*;
import static java.lang.Math.*;
import static java.util.Map.Entry.*;
import static java.util.stream.Collectors.*;

/**
 * Created by Adam Piech on 2017-05-15.
 */

public class Fisher {

    private List<Object> objectsSet;
    private Map<Integer, Double> bestFeatures;
    private int requiredFeaturesQuantity;

    public Fisher(List<Object> trainingObjects, int requiredFeaturesQuantity) {
        this.objectsSet = trainingObjects;
        this.requiredFeaturesQuantity = requiredFeaturesQuantity;
    }

    public void execute() {
        Map<String, List<Object>> classesLists = separateClasses();
        Map<String, Matrix> classesMatrices = prepareClassesMatrices(classesLists);

        Matrix[] covarianceMatrices = new Matrix[2];
        List<Float>[] means = new List[2];

        int count = 0;
        for (String key : classesMatrices.keySet()) {
            means[count] = calculateMeans(classesLists.get(key));
            covarianceMatrices[count] =
                    calculateCovarianceMatrix(subtractMeansFromClass(classesMatrices.get(key), means[count++]));
        }

        bestFeatures = chooseBestFeatures(covarianceMatrices, means);
        createNewFeatureSet();
    }

    private Map<String, List<Object>> separateClasses() {
        return objectsSet
                .stream()
                .collect(groupingBy(Object::getClassName));
    }

    private Map<String, Matrix> prepareClassesMatrices(Map<String, List<Object>> classes) {
        Map<String, Matrix> classesMatrices = new HashMap<>();
        for (String key : classes.keySet()) {
            classesMatrices.put(key, prepareClassMatrix(classes.get(key)));
        }
        return classesMatrices;
    }

    private Matrix prepareClassMatrix(List<Object> singleClass) {

        int noClass = singleClass.size();
        int noFeatures = singleClass.get(0).getFeaturesNumber();
        Matrix classFeatures = new Basic2DMatrix(noClass, noFeatures);

        for (int row = 0; row < noClass; row++) {
            for (int col = 0; col < noFeatures; col++) {
                classFeatures.set(row, col, singleClass.get(row).getFeature().get(col));
            }
        }

        return classFeatures;
    }

    private void createNewFeatureSet() {
        for (Object object : objectsSet) {
            List<Float> newFeatures = new ArrayList<>();
            for (Integer featureNumber : bestFeatures.keySet()) {
                newFeatures.add(object.getFeature().get(featureNumber));
            }
            object.setFeature(newFeatures);
        }
    }

    private LinkedHashMap<Integer, Double> chooseBestFeatures(Matrix[] covarianceMatrices, List<Float>[] means) {
        return calculateFisher(covarianceMatrices, means)
                .entrySet()
                .stream()
                .sorted(comparingByValue((e1, e2) -> compare(e2, e1)))
                .limit(requiredFeaturesQuantity)
                .collect(toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap:: new));
    }

    private Map<Integer, Double> calculateFisher(Matrix[] covarianceMatrices, List<Float>[] means) {
        Map<Integer, Double> fisherParameters = new HashMap<>();
        for (int index = 0; index < means[0].size(); index++) {
            fisherParameters.put(index, calculateDistances(means[0].get(index), means[1].get(index)) /
                    (covarianceMatrices[0].get(index, index) + covarianceMatrices[1].get(index, index)));
        }
        return fisherParameters;
    }

    private List<Float> calculateMeans(List<Object> singleClass) {

        List<Float> means = new ArrayList<>();
        int noFeature = singleClass.get(0).getFeaturesNumber();

        for (int index = 0; index < noFeature; index++) {
            float sum = (float) 0;
            for (Object object : singleClass) {
                sum += object.getFeature().get(index);
            }
            means.add(sum / (float) noFeature);
        }

        return means;
    }

    private Matrix subtractMeansFromClass(Matrix singleClass, List<Float> means) {

        int rows = singleClass.rows();
        int cols = singleClass.columns();
        Matrix intraclassScattering = new Basic2DMatrix(rows, cols);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                intraclassScattering.set(row, col, singleClass.get(row, col) - means.get(col));
            }
        }

        return intraclassScattering;
    }

    private Matrix calculateCovarianceMatrix(Matrix singleClass) {
        return singleClass.transpose().multiplyByItsTranspose();
    }

    private double calculateDistances(Float f1, Float f2) {
        return abs(f1 + f2);
    }

}
