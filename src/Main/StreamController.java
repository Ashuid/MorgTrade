package Main;

import org.json.simple.JSONObject;

//TODO Make threads a thing
public class StreamController {

    //    public void HandleIncomingData(String data) {
//        uiController.AddItemToSearchListView("sager");
//    }


    public JSONObject Search(String type) {
        if (type.equals("Normal")) {
            return SearchWithStream(type);
        } else if (type.equals("Live")) {
            return SearchWithStream(type);
        }
        return null;
    }

//    private String SearchWithThreadedStream() {
//        return "threaded stream data";
//    }

    private JSONObject SearchWithStream(String type) {
        Stream stream = new Stream();
        return stream.Querry(type);
    }
}
