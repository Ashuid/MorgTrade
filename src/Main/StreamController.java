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

    //Used to start a thread which performs the API queries.
    //The thread is run with the functionality of Timer.schedule
    //This allows the thread to "restart" should it get stuck for any reason.
    private void SearchWithStream(List<String> parameters, String name, boolean priceRequirement) {
        try {
            Timer time = new Timer();
            StreamThread thread = new StreamThread(parameters, name, priceRequirement, this);

            streamThread = thread;
            time.schedule(thread, 0, 10000);
        } catch (MalformedURLException e) {
            controller.DisplayError("Error in URL creation - Service might be down");
        }
    }

    //Used by the stream thread to send information to a data filter thread
    public void handleNextBatch(JSONObject data, List<String> parameters, String name, boolean priceRequirement) {
        controller.ParseIncomingDataFromStream(data, parameters, name, priceRequirement);
    }

    //Used to route errors to the ui
    public void DisplayError(String str) {
        controller.DisplayError(str);
    }

    //Used to pause and resume the thread.
    //As the thread is running uninterruptable methods this might take a moment to take effect
    public void StopStartThread() {
        try {
            streamThread.StopStartThread();
        } catch (Exception e) {
            controller.DisplayError("Error in thread deconstruction");
        }
    }

    //Used to kill the thread.
    //As the thread is running uninterruptable methods this might take a moment to take effect
    public void KillThread() {
        streamThread.KillThread();
    }
}
