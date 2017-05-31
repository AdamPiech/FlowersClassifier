package view;

import engine.FileDataBase;
import engine.Object;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import utils.ClassifierTypes;
import utils.Classifiers.IClassifier;
import utils.Classifiers.kNMClassifier;
import utils.Classifiers.kNNClassifier;
import utils.Fisher;
import utils.NoSampleTypes;
import utils.Training;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static utils.Constants.RESOURCE_DIRECTORY;

/**
 * Created by Adam Piech on 2017-03-22.
 */

public class MainController implements Initializable {

    @FXML private AnchorPane anchorPane;
    @FXML private TextField trainingPartTextField;
    @FXML private TextArea textArea;
    @FXML private ChoiceBox classifierChoiceBox;
    @FXML private ChoiceBox noSamplesChoiceBox;
    @FXML private ChoiceBox noFeaturesChoiceBox;

    private FileDataBase fileDataBase;
    private List<Object> trainingObjects;
    private List<Object> testingObjects;

    private String filePath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        NoSampleTypes noSampleTypes = new NoSampleTypes();
        classifierChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, java.lang.Object oldValue, java.lang.Object newValue) {
                noSamplesChoiceBox.setItems(noSampleTypes.getChoiceBoxItems((String) newValue));
                noSamplesChoiceBox.setValue(noSampleTypes.getChoiceBoxValue((String) newValue));
            }
        });
    }

    @FXML
    private void trainEvent() {
        //(Fisher) Za każdym razem trzeba ładować na nowo z pliku - poprawić!!!
        fileDataBase.load(filePath);
        Fisher fisher = new Fisher(fileDataBase.getObjects(), Integer.parseInt(noFeaturesChoiceBox.getValue().toString()));
        fisher.execute();

        int trainingPart = (int) Double.parseDouble(trainingPartTextField.getText());
        Training training = new Training(fileDataBase);
        training.train(trainingPart);
        trainingObjects = training.getTrainingObjects();
        testingObjects = training.getTestingObjects();
    }

    @FXML
    private void executeEvent() {
        ClassifierTypes classifierTypes = new ClassifierTypes();
        IClassifier classifier = setClassifierProperties(classifierTypes.getClassifier((String) classifierChoiceBox.getValue()));
        double percentOfProperlyClassified = classifier.execute(trainingObjects, testingObjects) * 100.0;
        double percentOfMisclassified = 100.0 - percentOfProperlyClassified;
        showResultOfClassification(classifierChoiceBox.getValue() + ":   k = " + noSamplesChoiceBox.getValue(),
                percentOfProperlyClassified, percentOfMisclassified);
    }

    private IClassifier setClassifierProperties(IClassifier classifier) {
        if (classifier instanceof kNNClassifier) {
            ((kNNClassifier) classifier).setNumberOfSamples(Integer.valueOf((String) noSamplesChoiceBox.getValue()));
        }
        if (classifier instanceof kNMClassifier) {
            ((kNMClassifier) classifier).setNumberOfSamples(Integer.valueOf((String) noSamplesChoiceBox.getValue()));
        }
        return classifier;
    }

    @FXML
    private FileDataBase openFile() {
        FileChooser fileChooser = createFileChooser("Open file");
        File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());

        if (file == null) {
            return null;
        }

        FileDataBase fileDataBase = new FileDataBase();
        fileDataBase.load(filePath = file.getAbsolutePath());
        prepareFeaturesChoiceBox(fileDataBase.getNoFeatures());
        return this.fileDataBase = fileDataBase;
    }

    @FXML
    private void saveFile() {
        FileChooser fileChooser = createFileChooser("Save file");
        fileChooser.showSaveDialog(anchorPane.getScene().getWindow());
    }

    private FileChooser createFileChooser(String windowName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(windowName);
        fileChooser.setInitialDirectory(new File(RESOURCE_DIRECTORY));
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("*.txt", "*.txt"));
        return fileChooser;
    }

    private void showResultOfClassification(String instanceName, double percentOfProperlyClassified, double percentOfMisclassified) {
        DecimalFormat df = new DecimalFormat("#.00");
        textArea.setText(textArea.getText() + instanceName + ".\n"
                + "Poprawnie sklasyfikowano " + df.format(percentOfProperlyClassified) + "% obiektów.\n"
                + "Błędnie sklasyfikowano " + df.format(percentOfMisclassified) + "% obiektów.\n\n");
    }

    private void prepareFeaturesChoiceBox(int noFeatures) {
        List<Integer> features = IntStream
                .range(1, noFeatures + 1)
                .mapToObj(i -> i)
                .collect(Collectors.toList());

        noFeaturesChoiceBox.setItems(FXCollections.observableArrayList(features));
        noFeaturesChoiceBox.setValue(noFeatures);
    }

}
