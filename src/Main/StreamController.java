package Main;

import org.json.simple.JSONObject;

import java.util.List;
import java.util.Timer;

public class StreamController {

    private Controller controller;
    private StreamThread streamThread;

    public StreamController(Controller controller) {
        this.controller = controller;
    }

    public JSONObject Search(String type, List<String> parameters, String name) {
        if (type.equals("Normal")) {
            //TODO delete this maybe
            return Search();
        } else if (type.equals("Live")) {
            SearchWithStream(parameters, name);
        }
        return null;
    }

    private void SearchWithStream(List<String> parameters, String name) {
        Timer time = new Timer();
        StreamThread thread = new StreamThread(parameters, name, this);
        streamThread = thread;
        time.schedule(thread, 0, 1000);
    }

    public void handleNextBatch(JSONObject data, List<String> parameters, String name) {
        controller.ParseIncomingDataFromStream(data, parameters, name);
    }

    public void killThread() {
        try {
            streamThread.killThread();
        } catch (Exception e){
            System.out.println("Could not kill StreamThread");
        }
    }

    private JSONObject Search() {
        Stream stream = new Stream();
        return stream.Querry();
    }
}
