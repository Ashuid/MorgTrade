package Main;

public class StreamController {

    private UIController uiController;

    public void HandleIncomingData(String data) {
        uiController.AddItemToSearchListView("sager");
    }


    public String Search(String type) {
        if (type.equals("Normal")) {
            return SearchWithThreadedStream();
        } else if (type.equals("Live")) {
            return SearchWithStream();
        }
        return "Failed to perform search - Consult streamController for issue";
    }

    private String SearchWithThreadedStream() {
        return "threaded stream";
    }

    private String SearchWithStream() {
        return "basic stream";
    }
}
