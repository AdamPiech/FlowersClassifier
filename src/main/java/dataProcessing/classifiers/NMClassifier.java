package dataProcessing.classifiers;

import engine.Object;

import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.stream.Collectors.groupingBy;

/**
 * Created by Adam Piech on 2017-04-17.
 */

public class NMClassifier implements IClassifier {

    @Override
    public double execute(List<Object> trainingSet, List<Object> testingSet) {

        int properlyClassified = 0;
        int misclassified = 0;

        for (Object testingObject : testingSet) {
            if (classify(countClassesMeans(trainingSet), testingObject).equals(testingObject.getClassName())) {
                properlyClassified++;
            } else {
                misclassified++;
            }
        }

        return ((double) properlyClassified) / ((double) (properlyClassified + misclassified));
    }

    private List<Object> countClassesMeans(List<Object> trainingSet) {
        Map<String, List<Object>> classes = trainingSet
                .stream()
                .collect(groupingBy(Object::getClassName));

        List<Object> classesMeans = new ArrayList<>();
        for (String key : classes.keySet()) {
            List<Float> featuresMeans = countNewFeatures(classes.get(key));
            classesMeans.add(new Object(key, featuresMeans));
        }
        return classesMeans;
    }

    private List<Float> countNewFeatures(List<Object> objects) {
        List<Float> featuresMeans = new ArrayList<>();
        for (int featureIndex = 0; featureIndex < objects.get(0).getFeaturesNumber(); featureIndex++) {
            featuresMeans.add(countFeatureMean(objects, featureIndex));
        }
        return featuresMeans;
    }

    private float countFeatureMean(List<Object> objects, int featureIndex) {
        float feature = 0;
        for (Object object : objects) {
            feature += object.getFeatures().get(featureIndex);
        }
        return feature / (float) objects.size();
    }

    private String classify(List<Object> trainingSet, Object testingObject) {
        Map<Double, String> distanceResults = new HashMap<>();
        for (Object trainingObject : trainingSet) {
            distanceResults.put(countEuclideanDistance(testingObject, trainingObject), trainingObject.getClassName());
        }
        return distanceResults.get(getMinValue(distanceResults));
    }

    private double countEuclideanDistance(Object object, Object target) {
        double sum = 0.0;
        for (int index = 0; index < object.getFeaturesNumber(); index++ ) {
            sum += countDistance(object.getFeatures().get(index), target.getFeatures().get(index));
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

    private double countDistance(float object, float target) {
        return pow(object - target, 2);
    }

}
