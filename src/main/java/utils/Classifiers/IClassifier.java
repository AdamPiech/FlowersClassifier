package utils.classifiers;

import engine.Object;

import java.util.List;

/**
 * Created by Adam Piech on 2017-04-05.
 */
public interface IClassifier {

    public double execute(List<Object> trainingSet, List<Object> testingSet);

}
