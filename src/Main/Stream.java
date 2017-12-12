package Main;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Stream implements Runnable{

    //TODO grab key from jsonobject

    private volatile boolean shouldRun = true;
    private String key = null;

    public Stream() {}

    public JSONObject Querry() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://www.pathofexile.com/api/public-stash-tabs");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();
            System.out.println("API query returned http code: " + responseCode);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            JSONParser jsonParser = new JSONParser();

            return (JSONObject) jsonParser.parse(reader.readLine());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.out.println("Error occurred in Stream");
        }

        return null;
    }

    @Override
    public void run() {

    }
}
