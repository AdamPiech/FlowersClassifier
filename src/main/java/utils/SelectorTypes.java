package utils;

import utils.featuresExtractors.Fisher;
import utils.featuresExtractors.IFeaturesSelector;

import java.util.HashMap;
import java.util.Map;

import static utils.Constants.*;

/**
 * Created by Adam Piech on 2017-06-20.
 */
public class SelectorTypes {

    Map<String, IFeaturesSelector> selectors = new HashMap<>();

    public SelectorTypes() {
        selectors.put(FISHER, new Fisher());
//        selectors.put(SFS, new SFS());
    }

    public IFeaturesSelector getSelector(String key) {
        return selectors.get(key);
    }
}
