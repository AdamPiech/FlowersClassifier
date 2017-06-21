package dataProcessing.classifiersImprovement;

import engine.Object;

import java.util.List;

/**
 * Created by Adam Piech on 2017-06-21.
 */
public interface Callback {

    double execute(List<Object> trainingSet, List<Object> testingSet);

}
