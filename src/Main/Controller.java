package Main;

import org.json.simple.JSONObject;

import java.util.List;

//Acts as a router (mostly) for the program and helps prevent wait time in running processes
public class Controller {

    private UIHandler uiHandler;

    public Controller(UIHandler uiHandler) {
        this.uiHandler = uiHandler;
    }

    private final StreamController streamController = new StreamController(this);

    public void ParseIncomingDataFromStream(JSONObject data, List<String> parameters, String name) {
        new DataFilter(this, data, parameters, name).run();
    }

    public void AddItemToUISearchListView(JSONObject input) {
        uiHandler.AddItemToSearchListView(input);
    }

    public void PerformSearch(String type, List<String> parameters, String name) {
        ParseIncomingDataFromStream(streamController.Search(type, parameters, name), parameters, name);
    }

    public void KillSearchThread() {
        streamController.killThread();
    }
}
