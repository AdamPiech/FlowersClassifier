package utils.Classifiers;

import engine.Object;

import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by Adam Piech on 2017-04-17.
 */

public class kNMClassifier implements IClassifier {

    private int numberOfSamples = 2;

    @Override
    public double execute(List<Object> trainingSet, List<Object> testingSet) {
        int properlyClassified = 0;
        int misclassified = 0;

        Map<String, List<Double>> subclassesMeans =
                countSubclassesMeans(randomSeparateSubclass(separateClass(trainingSet)));

        //TODO
        /*
        * iteracja
        * porownac obiekty z listy
        * wrzucac je do nablizszej klasy
        */

        someMethod(subclassesMeans, trainingSet);

        /*
        *
        */

//        for (String key : subclassesMeans.keySet()) {
            for (Object testingObject : testingSet) {
                if (classify(subclassesMeans, testingObject).split("_")[0].equals(testingObject.getClassName())) {
                    properlyClassified++;
                } else {
                    misclassified++;
                }
//            }
        }

        return ((double) properlyClassified) / ((double) (properlyClassified + misclassified));
    }

    private Map<String, List<Object>> separateClass(List<Object> trainingSet) {
        Map<String, List<Object>> classes = new HashMap<>();
        for (Object object : trainingSet) {
            if (!classes.containsKey(object.getClassName())) {
                classes.put(object.getClassName(), new ArrayList<>());
            }
            classes.get(object.getClassName()).add(object);
        }
        return classes;
    }

    private Map<String, List<Object>> randomSeparateSubclass(Map<String, List<Object>> classes) {
        Map<String, List<Object>> subclasses = new HashMap<>();
        for (String className : classes.keySet()) {
            for (int noSubclass = 0; noSubclass < numberOfSamples; noSubclass++) {
                subclasses.put(className + "_" + noSubclass, new ArrayList<>());
            }
            for (Object object : classes.get(className)) {
                subclasses.get(className + "_" + new Random().nextInt(numberOfSamples)).add(object);
            }
        }
        return subclasses;
    }

    private Map<String, List<Double>> countSubclassesMeans(Map<String, List<Object>> subclasses) {
        Map<String, List<Double>> subclassesMeans = new HashMap<>();
        for (String subclassName : subclasses.keySet()) {
            for (int noSubclass = 0; noSubclass < numberOfSamples; noSubclass++) {
                subclassesMeans.put(subclassName, new ArrayList<>());
            }

            int noFeatures = subclasses.get(subclassName).get(0).getFeaturesNumber();
            for (int featureIndex = 0; featureIndex < noFeatures; featureIndex++) {
                double feature = 0.0;
                for (int objectIndex = 0; objectIndex < subclasses.get(subclassName).size(); objectIndex++) {
                    feature += subclasses.get(subclassName).get(objectIndex).getFeature().get(featureIndex);
                }
                feature /= subclasses.get(subclassName).size();
                subclassesMeans.get(subclassName).add(feature);
            }
        }
        return subclassesMeans;
    }

    private void someMethod(Map<String, List<Double>> subclassesMeans, List<Object> trainingSet) {





    }

    private String classify(Map<String, List<Double>> trainingSet, Object testingObject) {
        Map<Double, String> distanceResults = new HashMap<>();
        for (String key : trainingSet.keySet()) {
            distanceResults.put(countEuclideanDistance(testingObject, trainingSet.get(key)), key);
        }
        return distanceResults.get(getMinValue(distanceResults));
    }

    private double countEuclideanDistance(Object object, List<Double> target) {
        double sum = 0.0;
        for (int index = 0; index < object.getFeaturesNumber(); index++ ) {
            sum += countDistance(object, target, index);
        }
        return sqrt(sum);
    }

    private Double getMinValue(Map<Double, String> distanceResults) {
        return distanceResults
                .keySet()
                .stream()
                .min(Double::compare)
                .get();
    }

    private double countDistance(Object object, List<Double> target, int noFeatures) {
        return pow(object.getFeature().get(noFeatures) - target.get(noFeatures), 2);
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    public void setNumberOfSamples(int numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

}
