package dataProcessing.classifiersImprovement;

import engine.FileDataBase;
import engine.Object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Adam Piech on 2017-06-01.
 */

public class CrossValidation {

    private static final int SET_PART_NUMBER = 5;

    private Supplier<Stream<Object>> objectsStream;
    private int objectSetSize;

    public CrossValidation(FileDataBase fileDataBase) {
        this.objectsStream = getObjectStream(fileDataBase);
        this.objectSetSize = fileDataBase.getNoObjects();
    }

    public double execute(Callback callback) {

        double result = 0.0;
        for (int index = 0; index < SET_PART_NUMBER; index++) {

            int startRange = (int)((double)objectSetSize / (double) SET_PART_NUMBER * (double) index);
            int endRange = (int)((double)objectSetSize / (double) SET_PART_NUMBER * (double) (index + 1));

            List<Object> trainingSet = prepareTrainingSet(startRange, endRange);
            List<Object> testingSet = prepareTestingSet(startRange, endRange);
            result += callback.execute(trainingSet, testingSet);
        }

        return result / (double) SET_PART_NUMBER;
    }

    private List<Object> prepareTrainingSet(int startSkipRange, int endSkipRange) {
        List<Object> trainingSet = new ArrayList<>();
        trainingSet.addAll(prepareSetWithEndRange(startSkipRange));
        trainingSet.addAll(prepareSetWithStartRange(endSkipRange));
        return trainingSet;
    }

    private List<Object> prepareTestingSet(int start, int end) {
        return objectsStream
                .get()
                .skip(start)
                .limit(end)
                .collect(Collectors
                        .toList());
    }

    private List<Object> prepareSetWithStartRange(int skip) {
        return objectsStream
                .get()
                .skip(skip)
                .collect(Collectors
                        .toList());
    }

    private List<Object> prepareSetWithEndRange(int limit) {
        return objectsStream
                .get()
                .limit(limit)
                .collect(Collectors
                        .toList());
    }

    private Supplier<Stream<Object>> getObjectStream(FileDataBase fileDataBase) {
        List<Object> objects = fileDataBase.getObjects();
        Collections.shuffle(objects);
        return () -> objects.stream();
    }

}
