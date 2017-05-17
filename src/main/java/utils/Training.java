package utils;

import engine.FileDataBase;
import engine.Object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Adam Piech on 2017-04-05.
 */

public class Training {

    private FileDataBase fileDataBase;
    private List<Object> trainingObjects;
    private List<Object> testingObjects;

    private int noAllObjects = 0;
    private int noTrainingObjects = 0;
    private int noTestingObjects = 0;

    public Training(FileDataBase fileDataBase) {
        this.fileDataBase = fileDataBase;
        this.trainingObjects = new ArrayList<>();
        this.testingObjects = new ArrayList<>();
    }

    public void train(int trainingPart) {
        Supplier<Stream<Object>> objectsStream = getObjectStream();
        computeSetsSize(trainingPart);
        prepareTrainingSet(objectsStream);
        prepareTestingSet(objectsStream);
    }

    private void prepareTrainingSet(Supplier<Stream<Object>> objectsStream) {
        trainingObjects = objectsStream
                .get()
                .limit(noTrainingObjects)
                .collect(Collectors
                        .toList());
    }

    private void prepareTestingSet(Supplier<Stream<Object>> objectsStream) {
        testingObjects = objectsStream
                .get()
                .skip(noTrainingObjects)
                .collect(Collectors
                        .toList());
    }

    private Supplier<Stream<Object>> getObjectStream() {
        List<Object> objects = fileDataBase.getObjects();
        Collections.shuffle(objects);
        return () -> objects.stream();
    }

    private void computeSetsSize(int trainingPart) {
        noAllObjects = fileDataBase.getNoObjects();
        noTrainingObjects = (int) (((double) noAllObjects) * ((double) trainingPart) / 100.0);
        noTestingObjects = noAllObjects - noTrainingObjects;
    }

    public List<Object> getTrainingObjects() {
        return trainingObjects;
    }

    public List<Object> getTestingObjects() {
        return testingObjects;
    }
}
