package dataProcessing.utils;

import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam Piech on 2017-05-03.
 */

public class NoSampleTypes {

    Map<String, ObservableList<String>> choiceBoxItems = new HashMap<>();
    Map<String, String> choiceBoxValue = new HashMap<>();

    public NoSampleTypes() {
        initializeChoiceBoxItemsMap();
        initializeChoiceBoxValueMap();
    }

    private void initializeChoiceBoxItemsMap() {
        choiceBoxItems.put(Constants.NN, Constants.NN_CHOICEBOX_ITEMS);
        choiceBoxItems.put(Constants.kNN, Constants.kNN_CHOICEBOX_ITEMS);
        choiceBoxItems.put(Constants.NM, Constants.NM_CHOICEBOX_ITEMS);
        choiceBoxItems.put(Constants.kNM, Constants.kNM_CHOICEBOX_ITEMS);
    }

    private void initializeChoiceBoxValueMap() {
        choiceBoxValue.put(Constants.NN, Constants.NN_CHOICEBOX_VALUE);
        choiceBoxValue.put(Constants.kNN, Constants.kNN_CHOICEBOX_VALUE);
        choiceBoxValue.put(Constants.NM, Constants.NM_CHOICEBOX_VALUE);
        choiceBoxValue.put(Constants.kNM, Constants.kNM_CHOICEBOX_VALUE);
    }

    public ObservableList<String> getChoiceBoxItems(String key) {
        return choiceBoxItems.get(key);
    }

    public String getChoiceBoxValue(String key) {
        return choiceBoxValue.get(key);
    }


}
