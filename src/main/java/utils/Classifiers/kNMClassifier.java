package utils.Classifiers;

import engine.Object;

import java.util.List;

/**
 * Created by Adam Piech on 2017-04-17.
 */

public class kNMClassifier implements IClassifier {

    private int numberOfSamples = 2;

    @Override
    public double execute(List<Object> trainingSet, List<Object> testingSet) {
        return 0;
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    public void setNumberOfSamples(int numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

}
