package utils.Classifiers;

import com.sun.javafx.collections.MappingChange;
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

public class NMClassifier implements IClassifier {

    @Override
    public double execute(List<Object> trainingSet, List<Object> testingSet) {

        int properlyClassified = 0;
        int misclassified = 0;

        for (Object testingObject : countMeanSample(testingSet)) {
            if (classify(trainingSet, testingObject).equals(testingObject.getClassName())) {
                properlyClassified++;
            } else {
                misclassified++;
            }
        }

        return ((double) properlyClassified) / ((double) (properlyClassified + misclassified));
    }

    private List<Object> countMeanSample(List<Object> trainingSet) {

//        trainingSet
//                .stream()
//                .collect(Collectors.groupingBy(Object::getClassName));



        //TODO
//        Supplier<Stream<Object>> xxx = () -> trainingSet.stream();
//        xxx
//                .get()
//                .collect(Collectors.groupingBy(w -> w.getClassName(), Collectors.counting()))
//                .entrySet()
//                .stream();

        return null;
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
