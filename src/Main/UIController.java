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

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

//TODO Lav Keygen class
//TODO genundersøg om der er nødvendighed for en keygen class hvis vi ikke hånterere mutations

//TODO lav Parser class

//TODO Kun en af hver search ad gangen, en normal search og max en live search.

//TODO Leg med idéen om currency conversion table
//TODO Spørg og få quotes fra yuki og morten om hvad programmet skal kunne, usecase like.

//Controller for the JavaFX UI element. Handles all user interactions, both in and out.
public class UIController {
    public ListView<String> searchListView;
    public TabPane tabPane;
    public Button demoButton;
    public Button demoButton2;
    public TextField modTextField;
    public ListView<String> modListView;

    private Controller controller;

    public void DemoUI() {
        searchListView.getItems().add("1");
        searchListView.getItems().add("two");
        searchListView.getItems().add("3");
        modListView.getItems().add("%# added to test");
        modListView.getItems().add("+# to maximum test");
    }

    public void Whisper(ContextMenuEvent event) {
        try {
            String pickedItem = event.getPickResult().getIntersectedNode().idProperty().getBean().toString().split("'")[1];
            if (pickedItem != null) {
                StringSelection selection = new StringSelection(pickedItem);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
                System.out.println("Text copied to clipboard: " + pickedItem);
            }
        } catch (Exception ignored) {
        }
    }

    public void DemoListPlusOne(MouseEvent mouseEvent) {
       AddItemToSearchListView(String.valueOf(ThreadLocalRandom.current().nextInt(0, 100)));
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

    public void PerformSearch(ActionEvent actionEvent) {
        controller.PerformSearch("Normal");
    }
}
