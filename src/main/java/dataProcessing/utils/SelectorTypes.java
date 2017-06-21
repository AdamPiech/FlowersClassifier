package dataProcessing.utils;

import dataProcessing.featuresExtractors.Fisher;
import dataProcessing.featuresExtractors.IFeaturesSelector;
import dataProcessing.featuresExtractors.SFS;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam Piech on 2017-06-20.
 */
public class SelectorTypes {

    Map<String, IFeaturesSelector> selectors = new HashMap<>();

    public SelectorTypes() {
        selectors.put(Constants.FISHER, new Fisher());
        selectors.put(Constants.SFS, new SFS());
    }

    public IFeaturesSelector getSelector(String key) {
        return selectors.get(key);
    }
}
