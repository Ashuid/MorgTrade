package Main;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StreamThread extends TimerTask {

    private volatile boolean shouldRun = true;
    private String changeID = "";
    private String name;
    private List<String> parameters;
    private boolean priceRequirement;
    private StreamController streamController;
    private URL url = new URL("http://www.pathofexile.com/api/public-stash-tabs");

    public StreamThread(List<String> parameters, String name, boolean priceRequirement, StreamController streamController) throws MalformedURLException {
        this.name = name;
        this.parameters = parameters;
        this.priceRequirement = priceRequirement;
        this.streamController = streamController;
    }

    public void killThread() {
        shouldRun = false;
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() {
        while (shouldRun) {
            HttpURLConnection connection = null;
            try {
                if (changeID != null) {
                    url = new URL("http://www.pathofexile.com/api/public-stash-tabs?id=" + changeID);
                }
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                int responseCode = connection.getResponseCode();
                System.out.println("API query returned http code: " + responseCode);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                JSONParser jsonParser = new JSONParser();


                JSONObject data = (JSONObject) jsonParser.parse(reader.readLine());

                if (!changeID.equals(data.get("next_change_id").toString())) {
                    changeID = data.get("next_change_id").toString();
                    streamController.handleNextBatch(data, parameters, name, priceRequirement);
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                System.out.println("Error occurred in StreamThread");
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }
}
