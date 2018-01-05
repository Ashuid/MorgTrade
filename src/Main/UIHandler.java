package Main;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

//TODO Spørg og få quotes fra yuki og morten om hvad programmet skal kunne, usecase like.

//Controller for the JavaFX UI element. Handles all user interactions.
public class UIHandler {
    public ListView<String> searchListView;
    public TabPane tabPane;
    public Button demoButton;
    public TextField modTextField;
    public TextField nameTextField;
    public ListView<String> modListView;
    public CheckBox priceCheckBox;
    public Button startSearchButton;
    public Label errorLabel;
    public Button stopSearchButton;

    private final Controller controller = new Controller(this);

    private ArrayList<JSONObject> itemList = new ArrayList<>();

    //Method to populate name field and add a parameter to the list
    //with some basic inputs.
    //Used for users with no knowledge of the possible inputs to still get a taste of the functionality
    public void DemoUI() {
        modListView.getItems().clear();
        modListView.getItems().add("+40 to maximum Life");
        nameTextField.setText("Leather");
    }

    //Used to format items into strings the user can use to buy the items
    private String FormatWhisper(JSONObject item) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add("@" + item.get("seller"))
                .add("Hi, I'd like to buy your " + CombineItemNameAndTypeIfPossible(item))
                .add("for my " + item.get("price"))
                .add("( Located in " + item.get("stashName"))
                .add("; Position: Left " + item.get("x"))
                .add(", Top " + item.get("y"))
                .add(")");

        return joiner.toString();
    }

    //Used to create a full identification of items
    private String CombineItemNameAndTypeIfPossible(JSONObject obj) {
        if (obj.get("itemName") != null && !obj.get("itemName").toString().isEmpty()) {
            return obj.get("itemName").toString() + ", " + obj.get("itemTypeLine").toString();
        } else {
            return obj.get("itemTypeLine").toString();
        }
    }

    //Handler for right clicking items to get buy information to clipboard
    public void Whisper(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            int index = searchListView.getSelectionModel().getSelectedIndex();
            if (index > -1) {
                JSONObject pickedItem = itemList.get(index);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(FormatWhisper(pickedItem));
                clipboard.setContents(selection, null);
            }
        }
    }

    //Used to remove mods from the list of mods to search for
    public void DeleteModFromList(ContextMenuEvent event) {
        try {
            modListView.getItems().remove(modListView.getSelectionModel().getSelectedIndex());
            modTextField.requestFocus();
        } catch (ArrayIndexOutOfBoundsException exception) {
            //Ignored as this is caused by right clicking empty space in the mod list
        }
    }

    //Adds mods to the mod search list when user presses the enter key.
    public void ModTextFieldHandleEnterKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER) &&
                !Objects.equals(modTextField.getText().replaceAll("\\s", ""), "")) {
            modListView.getItems().add(0, modTextField.getText());
            modTextField.clear();
        }
    }

    //Adds items to the list for the user to browse
    public void AddItemToSearchListView(JSONObject input) {
        try {
            //Limit amount of items displayed in the UI to limit memory usage
            if (searchListView.getItems().size() > 100) {
                searchListView.getItems().remove(searchListView.getItems().size() - 1);
                itemList.remove(searchListView.getItems().size() - 1);
            }

            //Make sure the same seller isn't performing market manipulation
            int manipulationCount = 0;
            for (JSONObject object : itemList) {
                if (object.get("seller").toString().equals(input.get("seller"))) {
                    manipulationCount++;
                }
            }

            //If there is no sign of market manipulation on the item then we add it
            if (manipulationCount < 2) {
                StringJoiner joiner = new StringJoiner(" ");

                joiner.add(CombineItemNameAndTypeIfPossible(input));

                joiner.add("| Price: " + input.get("price").toString());

                joiner.add("Seller: " + input.get("seller").toString());

                if (input.get("sockets") != null && !input.get("sockets").toString().isEmpty()) {
                    joiner.add("Sockets:");
                    for (String str : (List<String>) input.get("sockets")) {
                        joiner.add(str);
                    }
                }

                if (input.get("ilvl") != null && !input.get("ilvl").toString().isEmpty() && !input.get("ilvl").toString().equals("0")) {
                    joiner.add("Item level: " + input.get("ilvl").toString());
                }

                if (input.get("mods") != null && !input.get("mods").toString().isEmpty() && !input.get("mods").toString().contains("<currencyitem>")) {
                    JSONArray mods = (JSONArray) input.get("mods");

                    joiner.add("Mods:");
                    for (Object obj : mods) {
                        joiner.add(obj.toString());
                    }
                }

                itemList.add(0, input);
                Platform.runLater(() -> searchListView.getItems().add(0, joiner.toString()));
            }
        } catch (Exception e) {
            DisplayError("Error occurred while adding an item to the list");
        }
    }

    //Used to display any meaningful errors or general info to the user
    public void DisplayError(String str) {
        Platform.runLater(() -> errorLabel.setText(str));
    }

    //Starts the search with the users parameters and calls for the existing search thread to stop
    public void PerformLiveSearch(MouseEvent mouseEvent) {
        if (!modListView.getItems().isEmpty() || !nameTextField.getText().isEmpty()) {
            try {
                controller.KillThread();
            }catch (NullPointerException e){
                //Ignored as this is caused by an attempt to kill a non existing thread
            }

            searchListView.getItems().clear();
            List<String> parameters = modListView.getItems();
            controller.PerformSearch(parameters, nameTextField.getText(), priceCheckBox.isSelected());
            DisplayError("Searching");
        }
    }

    public void StopSearch(MouseEvent mouseEvent) {
        controller.StopStartSearchThread();
    }
}
