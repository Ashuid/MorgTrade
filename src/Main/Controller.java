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

    public void ParseIncomingDataFromStream(JSONObject data, List<String> parameters, String name, boolean priceRequirement) {
        new DataFilter(this, data, parameters, name, priceRequirement).run();
    }

    public void AddItemToUISearchListView(JSONObject input) {
        uiHandler.AddItemToSearchListView(input);
    }

    public void PerformSearch(List<String> parameters, String name, boolean priceRequirement) {
        streamController.Search(parameters, name, priceRequirement);
    }

    public void KillSearchThread() {
        streamController.killThread();
    }
}
