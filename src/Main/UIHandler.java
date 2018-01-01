package Main;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.json.simple.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

//TODO Leg med idéen om currency conversion table / Forklar hvorfor det ikke blev til : lightweight

//TODO Spørg og få quotes fra yuki og morten om hvad programmet skal kunne, usecase like.

//TODO fix alle try-catches så det bliver vist i UI

//Controller for the JavaFX UI element. Handles all user interactions.
public class UIHandler {
    public ListView<String> searchListView;
    public TabPane tabPane;
    public Button demoButton;
    public TextField modTextField;
    public TextField nameTextField;
    public ListView<String> modListView;
    public CheckBox priceCheckBox;

    private final Controller controller = new Controller(this);

    //Method to populate name field and add a parameter to the list
    //with some basic inputs.
    //Used for users with no knowledge of the possible inputs to still get a taste of the functionality
    public void DemoUI() {
        modListView.getItems().clear();
        modListView.getItems().add("+40 to maximum Life");
        nameTextField.setText("Chaos");
    }

    public void Whisper(ContextMenuEvent event) {
        try {
            String pickedItem = event.getPickResult().getIntersectedNode().idProperty().getBean().toString().split("'")[1];
            if (pickedItem != null) {
                //TODO oneline this shit and make better
                StringSelection selection = new StringSelection(pickedItem);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
                System.out.println("Text copied to clipboard: " + pickedItem);
            }
        } catch (Exception ignored) {
        }
    }

    public void DeleteModFromList(ContextMenuEvent event) {
        try {
            modListView.getItems().remove(modListView.getSelectionModel().getSelectedIndex());
            modTextField.requestFocus();
        } catch (Exception exception) {
            System.out.println("An error occurred in the controller @ DeleteModFromListMethod");
        }
    }

    public void ModTextFieldHandleEnterKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER) && !Objects.equals(modTextField.getText().replaceAll("\\s", ""), "")) {
            modListView.getItems().add(0, modTextField.getText());
            modTextField.clear();
        }
    }

    public void AddItemToSearchListView(String input) {
        searchListView.getItems().add(0, input);
    }

    public void AddItemToSearchListView(JSONObject input) {
        //TODO add proper display of items
        try {
            searchListView.getItems().add(0, "Item Name: " + input.get("itemName") + " - Price: " + input.get("price").toString());
        } catch (IllegalStateException e) {}
    }

    public void PerformLiveSearch(ActionEvent actionEvent) {
        if (!modListView.getItems().isEmpty() || !nameTextField.getText().isEmpty()) {
            controller.KillSearchThread();
            searchListView.getItems().clear();
            List<String> parameters = modListView.getItems();
            controller.PerformSearch(parameters, nameTextField.getText(), priceCheckBox.isSelected());
        }
    }
}
