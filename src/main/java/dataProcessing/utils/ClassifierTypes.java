package dataProcessing.utils;

import dataProcessing.classifiers.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam Piech on 2017-04-05.
 */
public class ClassifierTypes {

    Map<String, IClassifier> classifiers = new HashMap<>();

    public ClassifierTypes() {
        classifiers.put(Constants.NN, new NNClassifier());
        classifiers.put(Constants.kNN, new kNNClassifier());
        classifiers.put(Constants.NM, new NMClassifier());
        classifiers.put(Constants.kNM, new kNMClassifier());
    }

    public IClassifier getClassifier(String key) {
        return classifiers.get(key);
    }

}
