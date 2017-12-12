package Main;

import org.json.simple.JSONObject;

//Acts as a router (mostly) for the program and helps prevent wait time in running processes
public class Controller {

    private UIController uiController;

    public Controller (UIController uiController){
        this.uiController = uiController;
    }

    private final StreamController streamController = new StreamController();

    public void ParseIncomingDataFromStream(JSONObject data) {
        new DataFilter(this, data).run();
    }

    public void AddItemToUISearchListView(JSONObject input){
        uiController.AddItemToSearchListView(input);
    }

    public void AddItemToUISearchListView(String input){
        uiController.AddItemToSearchListView(input);
    }

    public void PerformSearch(String type) {
        ParseIncomingDataFromStream(streamController.Search(type));
    }
}
