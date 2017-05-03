package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

import static utils.Constants.*;

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
        choiceBoxItems.put(NN, NN_CHOICEBOX_ITEMS);
        choiceBoxItems.put(kNN, kNN_CHOICEBOX_ITEMS);
        choiceBoxItems.put(NM, NM_CHOICEBOX_ITEMS);
        choiceBoxItems.put(kNM, kNM_CHOICEBOX_ITEMS);
    }

    private void initializeChoiceBoxValueMap() {
        choiceBoxValue.put(NN, NN_CHOICEBOX_VALUE);
        choiceBoxValue.put(kNN, kNN_CHOICEBOX_VALUE);
        choiceBoxValue.put(NM, NM_CHOICEBOX_VALUE);
        choiceBoxValue.put(kNM, kNM_CHOICEBOX_VALUE);
    }

    public ObservableList<String> getChoiceBoxItems(String key) {
        return choiceBoxItems.get(key);
    }

    public String getChoiceBoxValue(String key) {
        return choiceBoxValue.get(key);
    }


}
