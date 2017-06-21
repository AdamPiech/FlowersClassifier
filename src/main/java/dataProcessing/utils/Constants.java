package dataProcessing.utils;

import javafx.collections.ObservableList;

import static java.io.File.*;
import static javafx.collections.FXCollections.*;

/**
 * Created by Adam Piech on 2017-05-03.
 */

public class Constants {

    public final static String RESOURCE_DIRECTORY = "src" + separator + "main" + separator + "resources";

    public final static String NN = "NN";
    public final static String kNN = "k-NN";
    public final static String NM = "NM";
    public final static String kNM = "k-NM";
    public final static String FISHER = "Fisher";
    public final static String SFS = "SFS";

    public final static ObservableList<String> NN_CHOICEBOX_ITEMS = observableArrayList("1");
    public final static ObservableList<String> kNN_CHOICEBOX_ITEMS = observableArrayList("1", "3", "5", "7", "9");
    public final static ObservableList<String> NM_CHOICEBOX_ITEMS = observableArrayList("1");
    public final static ObservableList<String> kNM_CHOICEBOX_ITEMS = observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9");

    public final static String NN_CHOICEBOX_VALUE = "1";
    public final static String kNN_CHOICEBOX_VALUE = "3";
    public final static String NM_CHOICEBOX_VALUE = "1";
    public final static String kNM_CHOICEBOX_VALUE = "2";

}
