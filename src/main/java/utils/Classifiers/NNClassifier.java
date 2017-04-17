package utils.Classifiers;

import engine.Object;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static java.lang.Math.*;

/**
 * Created by Adam Piech on 2017-04-05.
 */

public class NNClassifier implements IClassifier {

    @Override
    public double execute(List<Object> trainingSet, List<Object> testingSet) {

        int properlyClassified = 0;
        int misclassified = 0;

        for (Object testingObject : testingSet) {
            if (classify(trainingSet, testingObject).equals(testingObject.getClassName())) {
                properlyClassified++;
            } else {
                misclassified++;
            }
        }

        return ((double) properlyClassified) / ((double) (properlyClassified + misclassified));
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

    private double countDistance(Object object, Object target, int noFeatures) {
        return pow(object.getFeature().get(noFeatures) - target.getFeature().get(noFeatures), 2);
    }

}
