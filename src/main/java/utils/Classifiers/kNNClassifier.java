package utils.Classifiers;

import engine.Object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by Adam Piech on 2017-04-17.
 */

public class kNNClassifier implements IClassifier {

    private int numberOfSamples = 3;

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
        return takeNearestSample(distanceResults);
    }

    private double countEuclideanDistance(Object object, Object target) {
        double sum = 0.0;
        for (int index = 0; index < object.getFeaturesNumber(); index++ ) {
            sum += countDistance(object, target, index);
        }
        return sqrt(sum);
    }

    private String takeNearestSample(Map<Double, String> distanceResults) {
        Supplier<Stream<Double>> lines = () -> distanceResults
                .keySet()
                .stream()
                .sorted(Double::compareTo);

        Map<Double, String> winSamples = new HashMap<>();
        for (int index = 0; index < numberOfSamples; index++) {
            double key = lines.get().skip(index).min(Double::compareTo).get();
            winSamples.put(key, distanceResults.get(key));
        }

        return getTheMostCommonElementInMap(winSamples);
    }

    private String getTheMostCommonElementInMap(Map<Double, String> winSamples) {
        return winSamples
                .values()
                .stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                .entrySet()
                .stream()
                .max((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1)
                .get()
                .getKey();
    }

    private double countDistance(Object object, Object target, int noFeatures) {
        return pow(object.getFeature().get(noFeatures) - target.getFeature().get(noFeatures), 2);
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    public void setNumberOfSamples(int numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

}
