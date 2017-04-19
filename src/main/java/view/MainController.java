package view;

import engine.FileDataBase;
import engine.Object;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import utils.ClassifierTypes;
import utils.Classifiers.IClassifier;
import utils.Classifiers.kNMClassifier;
import utils.Classifiers.kNNClassifier;
import utils.Training;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Adam Piech on 2017-03-22.
 */
public class MainController {

    @FXML private AnchorPane anchorPane;
    @FXML private TextField trainingPartTextField;
    @FXML private TextArea textArea;
    @FXML private ChoiceBox classifierChoiceBox;
    @FXML private ChoiceBox noSamplesChoiceBox;

    private FileDataBase fileDataBase;
    private List<Object> trainingObjects;
    private List<Object> testingObjects;

    @FXML
    private void trainEvent() {
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
        showResult(percentOfProperlyClassified, percentOfMisclassified);
    }

    private IClassifier setClassifierProperties(IClassifier classifier) {
        if (classifier instanceof kNNClassifier) {
            ((kNNClassifier) classifier).setNumberOfSamples(Integer.valueOf((String) noSamplesChoiceBox.getValue()));
        }
        if (classifier instanceof kNMClassifier) {
//            ((kNMClassifier) classifier).setNumberOfSamples(Integer.valueOf((String) noSamplesChoiceBox.getValue()));
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
        fileDataBase.load(file.getAbsolutePath());
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
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("*.txt", "*.txt"));
        return fileChooser;
    }

    private void showResult(double percentOfProperlyClassified, double percentOfMisclassified) {
        DecimalFormat df = new DecimalFormat("#.00");
        textArea.setText("Poprawnie sklasyfikowano " + df.format(percentOfProperlyClassified) + "% obieków.\n"
                + "Błędnie sklasyfikowano " + df.format(percentOfMisclassified) + "% obieków.\n");
    }

}
