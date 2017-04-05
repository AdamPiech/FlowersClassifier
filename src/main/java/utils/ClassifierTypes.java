package utils;

import utils.Classifiers.IClassifier;
import utils.Classifiers.NNClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam Piech on 2017-04-05.
 */
public class ClassifierTypes {

    Map<String, IClassifier> classifiers = new HashMap<>();

    public ClassifierTypes() {
        classifiers.put("NN", new NNClassifier());
        classifiers.put("k-NN", null);
        classifiers.put("NM", null);
        classifiers.put("k-NM", null);
    }

    public IClassifier getClassifier(String key) {
        return classifiers.get(key);
    }

}
