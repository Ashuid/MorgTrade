package Main;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.json.simple.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

//TODO Lav Keygen class
//TODO genundersøg om der er nødvendighed for en keygen class hvis vi ikke hånterere mutations

//TODO lav Parser class

//TODO Leg med idéen om currency conversion table

//TODO Spørg og få quotes fra yuki og morten om hvad programmet skal kunne, usecase like.

//TODO fix alle try-catches så det bliver vist i UI

//TODO add en league picker

//Controller for the JavaFX UI element. Handles all user interactions.
public class UIController {
    public ListView<String> searchListView;
    public TabPane tabPane;
    public Button demoButton;
    public Button demoButton2;
    public TextField modTextField;
    public ListView<String> modListView;

    private final Controller controller = new Controller(this);

    public void DemoUI() {
        //TODO Remove when no longer needing demo ware
        searchListView.getItems().add("1");
        searchListView.getItems().add("two");
        searchListView.getItems().add("3");
        modListView.getItems().add("%# added to test");
        modListView.getItems().add("+# to maximum test");
    }

    public void DemoListPlusOne(MouseEvent mouseEvent) {
        //TODO Remove when no longer need demo ware
        AddItemToSearchListView(String.valueOf(ThreadLocalRandom.current().nextInt(0, 100)));
    }

    public void Whisper(ContextMenuEvent event) {
        try {
            String pickedItem = event.getPickResult().getIntersectedNode().idProperty().getBean().toString().split("'")[1];
            if (pickedItem != null) {
                //TODO oneline this shit
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
        } catch (Exception e){
            System.out.println("Exception occurred on item: " + input.get("itemName") + " - " + input.get("price"));
            System.out.println("Full item is: " + input.get("demoItem").toString());
        }
    }

    public void PerformSearch(ActionEvent actionEvent) {
        controller.PerformSearch("Normal");
    }
}
