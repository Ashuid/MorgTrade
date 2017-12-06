package Main;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Stream {

    private String key = null;

    public Stream() {
    }

    public JSONObject Querry(String type) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://www.pathofexile.com/api/public-stash-tabs");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            //Add: if key != null append to url
//            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
//            writer.writeBytes("&"+key);
//            writer.flush();
//            writer.close();

            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader.readLine());


//
//            while ((input = reader.readLine()) != null){
//                response.append(input);
//                System.out.println(input);
//            }
//
//            JSONParser parser = new JSONParser();
//
//            Object object = parser.parse("{}");
//            JSONArray jsonArray = (JSONArray) object;
//
//            reader.close();

            return jsonObject;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
