package utils;

import utils.Classifiers.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam Piech on 2017-04-05.
 */
public class ClassifierTypes {

    Map<String, IClassifier> classifiers = new HashMap<>();

    public ClassifierTypes() {
        classifiers.put("NN", new NNClassifier());
        classifiers.put("k-NN", new kNNClassifier());
        classifiers.put("NM", new NMClassifier());
        classifiers.put("k-NM", new kNMClassifier());
    }

    public IClassifier getClassifier(String key) {
        return classifiers.get(key);
    }

}
