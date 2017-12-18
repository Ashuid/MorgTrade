package Main;

import org.json.simple.JSONObject;

import java.util.List;

//Acts as a router (mostly) for the program and helps prevent wait time in running processes
public class Controller {

    private UIHandler uiHandler;

    public Controller(UIHandler uiHandler) {
        this.uiHandler = uiHandler;
    }

    private final StreamController streamController = new StreamController();

    public void ParseIncomingDataFromStream(JSONObject data, List<String> parameters) {
        new DataFilter(this, data, parameters).run();
    }

    public void AddItemToUISearchListView(JSONObject input) {
        uiHandler.AddItemToSearchListView(input);
    }

    public void AddItemToUISearchListView(String input) {
        uiHandler.AddItemToSearchListView(input);
    }

    public void PerformSearch(String type, List<String> parameters) {
        ParseIncomingDataFromStream(streamController.Search(type), parameters);
    }
}
