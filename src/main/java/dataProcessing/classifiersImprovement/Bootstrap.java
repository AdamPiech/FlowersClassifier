package dataProcessing.classifiersImprovement;

import engine.FileDataBase;
import engine.Object;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Adam Piech on 2017-06-01.
 */

public class Bootstrap {

    private static final int BOOTSTRAP_ITERATION = 100;

    private List<Object> objects;

    private int noAllObjects = 0;
    private int noTrainingObjects = 0;
    private int noTestingObjects = 0;

    public Bootstrap(FileDataBase fileDataBase) {
        this.objects = fileDataBase.getObjects();
        this.noAllObjects = fileDataBase.getNoObjects();
    }

    public double execute(Callback callback, int trainingPart) {
        computeSetsSize(trainingPart);
        double result = 0.0;
        for (int index = 0; index < BOOTSTRAP_ITERATION; index++) {
            List<Object> trainingSet = prepareTrainingSet();
            List<Object> testingSet = prepareTestingSet();
            result += callback.execute(trainingSet, testingSet);
        }
        return result / (double) BOOTSTRAP_ITERATION;
    }

    private List<Object> prepareTrainingSet() {
        return getObjectStream()
                .get()
                .limit(noTrainingObjects)
                .collect(Collectors
                        .toList());
    }

    private List<Object> prepareTestingSet() {
        return getObjectStream()
                .get()
                .skip(noTrainingObjects)
                .collect(Collectors
                        .toList());
    }

    private Supplier<Stream<Object>> getObjectStream() {
        Collections.shuffle(objects);
        return () -> objects.stream();
    }

    private void computeSetsSize(int trainingPart) {
        noTrainingObjects = (int) (((double) noAllObjects) * ((double) trainingPart) / 100.0);
        noTestingObjects = noAllObjects - noTrainingObjects;
    }

}
