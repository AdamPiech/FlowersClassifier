package utils;

import javafx.collections.ObservableList;

import static javafx.collections.FXCollections.*;

/**
 * Created by Adam Piech on 2017-05-03.
 */

public class Constants {

    public static String NN = "NN";
    public static String kNN = "k-NN";
    public static String NM = "NM";
    public static String kNM = "k-NM";

    public static ObservableList<String> NN_CHOICEBOX_ITEMS = observableArrayList("1");
    public static ObservableList<String> kNN_CHOICEBOX_ITEMS = observableArrayList("1", "3", "5", "7", "9");
    public static ObservableList<String> NM_CHOICEBOX_ITEMS = observableArrayList("1");
    public static ObservableList<String> kNM_CHOICEBOX_ITEMS = observableArrayList("1", "2", "3", "4", "5");

    public static String NN_CHOICEBOX_VALUE = "1";
    public static String kNN_CHOICEBOX_VALUE = "3";
    public static String NM_CHOICEBOX_VALUE = "1";
    public static String kNM_CHOICEBOX_VALUE = "2";

}
