package dataProcessing.featuresExtractors;

import com.xiantrimble.combinatorics.Combinatoric;
import com.xiantrimble.combinatorics.CombinatoricFactory;
import com.xiantrimble.combinatorics.CombinatoricFactoryImpl;
import engine.Object;
import org.la4j.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.*;
import static java.util.stream.Collectors.*;

/**
 * Created by Adam Piech on 2017-05-15.
 */

public class Fisher implements IFeaturesSelector {

    private List<Object> objectsSet;
    private int requiredFeaturesQuantity;

    public List<Integer> select(List<Object> trainingObjects, int requiredFeaturesQuantity) {

        this.objectsSet = trainingObjects;
        this.requiredFeaturesQuantity = requiredFeaturesQuantity;

        Map<String, List<Object>> classesLists = separateClasses();
        Map<String, Matrix> classesMatrices = prepareClassesMatrices(classesLists);

        Map<String, Matrix> covarianceMatrices = new HashMap<>();
        Map<String, List<Double>> means = new HashMap<>();

        for (String key : classesMatrices.keySet()) {
            means.put(key, calculateMeans(classesLists.get(key)));
            covarianceMatrices.put(key,
                    calculateCovarianceMatrix(subtractMeansFromClass(classesMatrices.get(key), means.get(key))));
        }

        List<Integer> bestFeatures = calculateFisherForAllCombinations(covarianceMatrices, means);
        createNewFeatureSet(bestFeatures);

        return bestFeatures;
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

        int noSamples = singleClass.size();
        int noFeatures = singleClass.get(0).getFeaturesNumber();
        Matrix classFeatures = new Basic2DMatrix(noSamples, noFeatures);

        for (int row = 0; row < noSamples; row++) {
            for (int col = 0; col < noFeatures; col++) {
                classFeatures.set(row, col, singleClass.get(row).getFeatures().get(col));
            }
        }

        return classFeatures;
    }

    private List<Integer> calculateFisherForAllCombinations(Map<String, Matrix> covarianceMatrices, Map<String, List<Double>> means) {

        Map<Double, Integer[]> fishers = new HashMap<>();
        for (Integer[] combination : calculateCombinationsOfFeatures(objectsSet.get(0).getFeaturesNumber(), requiredFeaturesQuantity)) {

            Matrix[] setOfMatrix = new Matrix[covarianceMatrices.keySet().size()];
            List<Double>[] setOfMeans = new List[means.keySet().size()];
            int count = 0;
            for (String key : covarianceMatrices.keySet()) {

                Matrix matrix = new Basic2DMatrix(combination.length, combination.length);
                List<Double> mean = new ArrayList<>(combination.length);
                for (int rowIndex = 0; rowIndex < combination.length; rowIndex++) {
                    mean.add(means.get(key).get(combination[rowIndex]));
                    for (int colIndex = 0; colIndex < combination.length; colIndex++) {
                        matrix.set(rowIndex, colIndex, covarianceMatrices.get(key).get(combination[rowIndex], combination[colIndex]));
                    }
                }

                setOfMatrix[count] = matrix;
                setOfMeans[count] = mean;
                count++;
            }

            fishers.put(calculateFisher(setOfMatrix, setOfMeans), combination);
        }

        return selectBestFeaturesSet(fishers);
    }

    private double calculateFisher(Matrix[] setOfMatrix, List<Double>[] setOfFeatures) {
        double distance = calculateDistances(setOfFeatures);
        double sumOfDet = setOfMatrix[0].determinant() + setOfMatrix[1].determinant();
//        double sumOfDet = setOfMatrix[0].add(setOfMatrix[1]).determinant();
        return distance / sumOfDet;
    }

    private List<Integer> selectBestFeaturesSet(Map<Double, Integer[]> fishers) {
        return Arrays
                .stream(fishers
                    .entrySet()
                    .stream()
                    .max((e1, e2) -> e1.getKey() > e2.getKey() ? 1 : -1)
                    .get()
                    .getValue())
                .collect(Collectors.toList());
    }

    private void createNewFeatureSet(List<Integer> bestFeatures) {
        for (Object object : objectsSet) {
            List<Float> newFeatures = new ArrayList<>();
            for (Integer featureNumber : bestFeatures) {
                newFeatures.add(object.getFeatures().get(featureNumber));
            }
            object.setFeature(newFeatures);
        }
    }

    private List<Double> calculateMeans(List<Object> singleClass) {

        List<Double> means = new ArrayList<>();
        int noFeature = singleClass.get(0).getFeaturesNumber();

        for (int index = 0; index < noFeature; index++) {
            double sum = 0.0;
            for (Object object : singleClass) {
                sum += object.getFeatures().get(index);
            }
            means.add(sum / (double) singleClass.size());
        }

        return means;
    }

    private Matrix subtractMeansFromClass(Matrix singleClass, List<Double> means) {

        int rows = singleClass.rows();
        int cols = singleClass.columns();
        Matrix intraClassScattering = new Basic2DMatrix(rows, cols);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                intraClassScattering.set(row, col, singleClass.get(row, col) - means.get(col));
            }
        }

        return intraClassScattering;
    }

    private Matrix calculateCovarianceMatrix(Matrix singleClass) {
        return singleClass
                .transpose()
                .multiplyByItsTranspose()
                .divide(singleClass.rows());
    }

    private Combinatoric<Integer> calculateCombinationsOfFeatures(int featuresQuantity, int requiredFeaturesQuantity) {
        Integer[] allFeature = getIntegerRange(featuresQuantity);
        CombinatoricFactory factory = new CombinatoricFactoryImpl();
        return factory.createCombinations(requiredFeaturesQuantity, allFeature);
    }

    private Integer[] getIntegerRange(int featuresQuantity) {
        return IntStream
                .rangeClosed(0, featuresQuantity - 1)
                .boxed()
                .toArray(Integer[]::new);
    }

    private double calculateDistances(List<Double>[] setOfFeatures) {
        double sum = 0.0;
        for (int index = 0; index < setOfFeatures[0].size(); index++) {
            sum += calculateDistances(setOfFeatures[0].get(index), setOfFeatures[1].get(index));
        }
        return sqrt(sum);
//        return sum / (double) setOfFeatures[0].size();
    }

    private double calculateDistances(double d1, double d2) {
        return pow(d1 - d2, 2);
    }

}
