package Main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Stream {

    private String parameters = null;
    private String key = null;

    public Stream(String parameters){
        this.parameters = parameters;
    }

    public String Querry(String parameters) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://www.pathofexile.com/api/public-stash-tabs");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            


        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

}
