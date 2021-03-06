package engine;

import java.util.List;

/**
 * Created by Adam Piech on 2017-03-22.
 */

public class Object {

    public int classID = -1;
    private String className;

    private List<Float> feature;

    public Object(String className, List<Float> feature) {
        this.className = className;
        this.feature = feature;
    }

    public String getClassName() {
        return className;
    }

    public int getFeaturesNumber() {
        return feature.size();
    }

    public List<Float> getFeatures() {
        return feature;
    }

    public void setFeature(List<Float> feature) {
        this.feature = feature;
    }

}
