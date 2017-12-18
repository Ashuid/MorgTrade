package Main;

import org.json.simple.JSONObject;

//TODO Make threads a thing
public class StreamController {

    public JSONObject Search(String type) {
        //TODO Fix live mode
        if (type.equals("Normal")) {
            return SearchWithStream();
        } else if (type.equals("Live")) {
            return SearchWithStream();
        }
        return null;
    }

    private JSONObject SearchWithStream() {
        Stream stream = new Stream();
        return stream.Querry();
    }
}
