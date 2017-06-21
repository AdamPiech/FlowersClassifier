package dataProcessing.featuresExtractors;

import engine.Object;

import java.util.List;

/**
 * Created by Adam Piech on 2017-05-31.
 */

public interface IFeaturesSelector {

    public List<Integer> select(List<Object> trainingObjects, int requiredFeaturesQuantity);

}
