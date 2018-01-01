package Main;

import org.json.simple.JSONObject;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Timer;

public class StreamController {

    private Controller controller;
    private StreamThread streamThread;

    public StreamController(Controller controller) {
        this.controller = controller;
    }

    public void Search(List<String> parameters, String name, boolean priceRequirement) {
        SearchWithStream(parameters, name, priceRequirement);
    }

    private void SearchWithStream(List<String> parameters, String name, boolean priceRequirement) {
        try {
            Timer time = new Timer();
            StreamThread thread = new StreamThread(parameters, name, priceRequirement, this);

            streamThread = thread;
            time.schedule(thread, 0, 5000);

        } catch (MalformedURLException e) {
            System.out.println("Error in stream creation");
        }
    }

    public void handleNextBatch(JSONObject data, List<String> parameters, String name, boolean priceRequirement) {
        controller.ParseIncomingDataFromStream(data, parameters, name, priceRequirement);
    }

    public void killThread() {
        try {
            streamThread.killThread();
        } catch (Exception e) {
            System.out.println("Could not kill StreamThread");
        }
    }
}
