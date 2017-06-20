package utils.classifiers;

import engine.Object;

import java.util.*;

import static java.lang.Math.pow;
import static java.lang.Math.scalb;
import static java.lang.Math.sqrt;

/**
 * Created by Adam Piech on 2017-04-17.
 */

public class kNMClassifier implements IClassifier {

    private static final String NAME_SEPARATOR = "_";

    private int numberOfSamples = 2;

    @Override
    public double execute(List<Object> trainingSet, List<Object> testingSet) {

        int properlyClassified = 0;
        int misclassified = 0;

        Map<String, List<Float>> classes = prepareSubclasses(trainingSet);
        for (Object testingObject : testingSet) {
            if (classify(classes, testingObject).equals(testingObject.getClassName())) {
                properlyClassified++;
            } else {
                misclassified++;
            }
        }

        return ((double) properlyClassified) / ((double) (properlyClassified + misclassified));
    }

    public Map<String, List<Float>> prepareSubclasses(List<Object> trainingSet) {

        Map<String, List<Object>> separatedClasses = separateClass(trainingSet);
        Map<String, List<Float>> finalSubclasses = new HashMap<>();

        for (String classname : separatedClasses.keySet()) {
            finalSubclasses.putAll(prepareSubclassesForSingleClass(separatedClasses.get(classname), classname));
        }

        return finalSubclasses;
    }

    public Map<String, List<Float>> prepareSubclassesForSingleClass(List<Object> classes, String className) {

        Map<String, List<Object>> separatedSubclasses = randomSeparateSubclass(classes, className);
        Map<String, List<Float>> subclassesMeans = countSubclassesMeans(separatedSubclasses);

        boolean classesHaveBeenChanged = true;
        while (classesHaveBeenChanged) {
            classesHaveBeenChanged = false;

            Map<String, List<Object>> newSeparatedSubclasses = new HashMap<>();
            for (String subclassName : separatedSubclasses.keySet()) {
                newSeparatedSubclasses.put(subclassName, new ArrayList<>());
            }

            for (String subclassName : separatedSubclasses.keySet()) {
                for (Object object : separatedSubclasses.get(subclassName)) {
                    String correctSubclass = classifyInSubclass(subclassesMeans, object);
                    if (!subclassName.equals(correctSubclass)) {
                        newSeparatedSubclasses.get(correctSubclass).add(object);
                        classesHaveBeenChanged = true;
                    } else {
                        newSeparatedSubclasses.get(subclassName).add(object);
                    }
                }
            }

            separatedSubclasses = newSeparatedSubclasses;
            subclassesMeans = countSubclassesMeans(separatedSubclasses);
        }
        return subclassesMeans;
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

    private Map<String, List<Object>> randomSeparateSubclass(List<Object> classes, String className) {
        Map<String, List<Object>> subclasses = new HashMap<>();
        for (int noSubclass = 0; noSubclass < numberOfSamples; noSubclass++) {
            subclasses.put(className + NAME_SEPARATOR + noSubclass, new ArrayList<>());
        }
        for (Object object : classes) {
            subclasses.get(className + NAME_SEPARATOR + new Random().nextInt(numberOfSamples)).add(object);
        }
        return subclasses;
    }

    private Map<String, List<Float>> countSubclassesMeans(Map<String, List<Object>> subclasses) {
        Map<String, List<Float>> subclassesMeans = new HashMap<>();
        for (String subclassName : subclasses.keySet()) {
            if (subclasses.get(subclassName).size() > 0) {
                for (int noSubclass = 0; noSubclass < numberOfSamples; noSubclass++) {
                    subclassesMeans.put(subclassName, countNewFeatures(subclasses.get(subclassName)));
                }
            }
        }
        return subclassesMeans;
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

    private String classify(Map<String, List<Float>> trainingSet, Object testingObject) {
        return classifyInSubclass(trainingSet, testingObject).split(NAME_SEPARATOR)[0];
    }

    private String classifyInSubclass(Map<String, List<Float>> trainingSet, Object testingObject) {
        Map<Double, String> distanceResults = new HashMap<>();
        for (String key : trainingSet.keySet()) {
            distanceResults.put(countEuclideanDistance(testingObject, trainingSet.get(key)), key);
        }
        return distanceResults.get(getMinValue(distanceResults));
    }

    private double countEuclideanDistance(Object object, List<Float> targetFeatures) {
        double sum = 0.0;
        for (int index = 0; index < targetFeatures.size(); index++ ) {
            sum += countDistance(object.getFeatures().get(index), targetFeatures.get(index));
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

    public void setNumberOfSamples(int numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

}
