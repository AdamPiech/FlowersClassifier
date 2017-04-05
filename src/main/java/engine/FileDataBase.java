package engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Adam Piech on 2017-03-22.
 */

public class FileDataBase {

    private int noClass = 0;
    private int noObjects = 0;
    private int noFeatures = 0;

    private List<String> classNamesVector = new ArrayList<>();
    private List<Object> objects = new ArrayList<>();
    private List<Integer> featuresIDs = new ArrayList<>();
    private Map<String, Integer> classCounters = new HashMap<>();

    public boolean addObject(Object object) {

        if (noFeatures == 0) {
            noFeatures = object.getFeaturesNumber();
        } else if (noFeatures != object.getFeaturesNumber()) {
            return false;
        }

        objects.add(object);
        ++noObjects;

        if (classCounters.computeIfPresent(object.getClassName(), (k, v) -> v + 1) == null) {
            classCounters.computeIfAbsent(object.getClassName(), v -> 1);
            classNamesVector.add(object.getClassName());
            ++noClass;
        }

        return true;
    }

    public void clear() {

        noClass = 0;
        noObjects = 0;
        noFeatures = 0;

        classNamesVector.clear();
        objects.clear();
        featuresIDs.clear();
        classCounters.clear();
    }

    public boolean load(String fileName) {

        clear();

        Supplier<Stream<String>> lines = () -> {
            try {
                return Files.lines(Paths.get(fileName));
            } catch (IOException e) {
                return null;
            }
        };

        try {
            getFeaturesInfoFromFileLines(lines);
            for (java.lang.Object streamLine : lines.get().skip(1).toArray()) {
                List<String> lineData = convertFileLineToList((String) streamLine);
                addObject(new Object(getObjectNameFromFileLine(lineData), getObjectFeaturesFromFileLine(lineData)));
            }
        } catch (Exception e) {
            System.out.println("It is'n database structure!");
            return false;
        }

        return true;
    }

    private void getFeaturesInfoFromFileLines(Supplier<Stream<String>> lines) {
        featuresIDs = Arrays.stream(lines
                .get()
                .findFirst()
                .get()
                .split(", "))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
        noFeatures = featuresIDs.remove(0);
    }

    private List<String> convertFileLineToList(String streamLine) {
        return Arrays
                .stream(streamLine
                        .split(","))
                .collect(Collectors
                        .toList());
    }

    private List<Float> getObjectFeaturesFromFileLine(List<String> lineData) {
        return lineData
                .stream()
                .map(Float::valueOf)
                .collect(Collectors.toList());
    }

    private String getObjectNameFromFileLine(List<String> lineData) {
        return lineData
                .remove(0)
                .split(" ")[0];
    }

    public void save(String fileName) {
    }

    public int getNoClass() {
        return noClass;
    }

    public int getNoObjects() {
        return noObjects;
    }

    public int getNoFeatures() {
        return noFeatures;
    }

    public List<String> getClassNames() {
        return classNamesVector;
    }

    public List<Object> getObjects() {
        return objects;
    }

    public Map<String, Integer> getClassCounters() {
        return classCounters;
    }

}
