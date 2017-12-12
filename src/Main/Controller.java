package Main;

import org.json.simple.JSONObject;

//Acts as a router for the program and helps prevent wait time in running processes
public class Controller {

    private UIController uiController;

    public Controller (UIController uiController){
        this.uiController = uiController;
    }

    private final StreamController streamController = new StreamController();

    public void ParseIncomingDataFromStream(JSONObject data) {
        System.out.println(data.toString());

        //TODO Split JSON into all the seperate objects in the new parser class
        //TODO Move this to another class as this place only routes
        for (Object key : data.keySet()){
            String keyString = (String) key;
            Object value = data.get(keyString);
            AddItemToUISearchListView(value.toString());
        }
    }

    public void AddItemToUISearchListView(String input){
        uiController.AddItemToSearchListView(input);
    }

    public void PerformSearch(String type) {
        ParseIncomingDataFromStream(streamController.Search(type));
    }
}
