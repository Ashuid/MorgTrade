package Main;

//Acts as a router for the program and helps prevent wait time in running processes
public class Controller {

    private StreamController streamController;
    private UIController uiController;

    public String ParseIncomingDataFromStream(String data) {
        return "";
        //Consume in new threaded class
    }

    public void AddItemToUISearchListView(String input){
        uiController.AddItemToSearchListView(input);
    }

    public String PerformSearch(String type) {
        return ParseIncomingDataFromStream(streamController.Search(type));
    }
}
