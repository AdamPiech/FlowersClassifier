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

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.stream.Collectors.groupingBy;

/**
 * Created by Adam Piech on 2017-06-21.
 */
public class SFS implements IFeaturesSelector {

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

        List<Integer> bestFeatures = calculateSFS(covarianceMatrices, means);
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

    private List<Integer> calculateSFS(Map<String, Matrix> covarianceMatrices, Map<String, List<Double>> means) {

        Set<Integer> allFeatures = getIntegerRangeList(objectsSet.get(0).getFeaturesNumber());
        List<Integer> selectedFeatures = new ArrayList<>();
        for (int noFeatures = 1; noFeatures <= requiredFeaturesQuantity; noFeatures++) {

            Map<Double, List<Integer>> fishers = new HashMap<>();
            for (Integer proposedFeatures : allFeatures) {
                List<Integer> currentFeatures = new ArrayList<>(selectedFeatures);
                currentFeatures.add(proposedFeatures);
                fishers.put(prepareDataAndCalculateFisher(covarianceMatrices, means, currentFeatures), currentFeatures);
            }

            selectedFeatures = selectBestFeaturesSet(fishers);
            allFeatures.removeAll(selectedFeatures);
        }

        return selectedFeatures;
    }

    private double prepareDataAndCalculateFisher(Map<String, Matrix> covarianceMatrices, Map<String, List<Double>> means, List<Integer> currentFeatures) {

        Matrix[] setOfMatrix = new Matrix[covarianceMatrices.keySet().size()];
        List<Double>[] setOfMeans = new List[means.keySet().size()];
        int count = 0;
        for (String key : covarianceMatrices.keySet()) {

            Matrix matrix = new Basic2DMatrix(currentFeatures.size(), currentFeatures.size());
            List<Double> mean = new ArrayList<>(currentFeatures.size());
            for (int rowIndex = 0; rowIndex < currentFeatures.size(); rowIndex++) {
                mean.add(means.get(key).get(currentFeatures.get(rowIndex)));
                for (int colIndex = 0; colIndex < currentFeatures.size(); colIndex++) {
                    matrix.set(rowIndex, colIndex, covarianceMatrices.get(key).get(currentFeatures.get(rowIndex), currentFeatures.get(colIndex)));
                }
            }

            setOfMatrix[count] = matrix;
            setOfMeans[count] = mean;
            count++;
        }

        return calculateFisher(setOfMatrix, setOfMeans);
    }

    private double calculateFisher(Matrix[] setOfMatrix, List<Double>[] setOfFeatures) {
        double distance = calculateDistances(setOfFeatures);
        double sumOfDet = setOfMatrix[0].determinant() + setOfMatrix[1].determinant();
//        double sumOfDet = setOfMatrix[0].add(setOfMatrix[1]).determinant();
        return distance / sumOfDet;
    }

    private List<Integer> selectBestFeaturesSet(Map<Double, List<Integer>> fishers) {
        return fishers
                .entrySet()
                .stream()
                .max((e1, e2) -> e1.getKey() > e2.getKey() ? 1 : -1)
                .get()
                .getValue();
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
        Integer[] allFeature = getIntegerRangeArray(featuresQuantity);
        CombinatoricFactory factory = new CombinatoricFactoryImpl();
        return factory.createCombinations(requiredFeaturesQuantity, allFeature);
    }

    private Integer[] getIntegerRangeArray(int featuresQuantity) {
        return IntStream
                .rangeClosed(0, featuresQuantity - 1)
                .boxed()
                .toArray(Integer[] :: new);
    }

    private Set<Integer> getIntegerRangeList(int featuresQuantity) {
        return IntStream
                .rangeClosed(0, featuresQuantity - 1)
                .boxed()
                .collect(Collectors.toSet());
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
