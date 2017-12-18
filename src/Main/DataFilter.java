package Main;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataFilter implements Runnable {

    private final Controller controller;
    private JSONObject data;
    private List<String> parameters;
    private volatile boolean shouldRun = true;

    public DataFilter(Controller controller, JSONObject data, List<String> parameters) {
        this.controller = controller;
        this.data = data;
        this.parameters = parameters;
    }

    public void stopRunning() {
        shouldRun = false;
    }

    //TODO filter for league

    @Override
    public void run() {
        //TODO Make this shit smarter. idk make a an array with shit it should match and only if it matches it all should it pass. This many nested if's are bad
        while (shouldRun) {
            JSONArray array = (JSONArray) data.get("stashes");

            //Loop over each stash from search data
            for (Object value : array) {
                JSONObject stashData = (JSONObject) value;

                //Filter out all stashes from accounts that no longer exist
                if (stashData.get("accountName") != null) {

                    //Filter out all the stashes that does not contain items for sale
                    if (stashData.get("public") != null) {

                        //Filter out all stashes that does not contain items
                        JSONArray itemsInStash = (JSONArray) stashData.get("items");
                        if (itemsInStash.size() > 0) {

                            //Loop over all the existing items
                            for (Object item : (JSONArray) stashData.get("items")) {
                                JSONObject currentItem = (JSONObject) item;

                                //Filter out all the empty items and add the rest
                                if (currentItem != null) {

                                    if (MatchesParameters(currentItem)) {

                                        JSONObject filteredObject = new JSONObject();

                                        //Add Name of Item
                                        addItemNameToObject(currentItem, filteredObject);

                                        //Add Price of Item
                                        addPriceToObject(stashData, currentItem, filteredObject);

                                        //Add Mods to item
                                        filteredObject.put("Mods", ExtractMods(currentItem));
                                        System.out.println(ExtractMods(currentItem).toString());
                                        System.out.println("---------------------------");
                                        controller.AddItemToUISearchListView(filteredObject);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            stopRunning();
        }
        System.out.println("Data parser thread stopped running");
    }

    private ArrayList<String> ExtractMods(JSONObject item) {

        JSONArray mods = new JSONArray();
        if (item.get("explicitMods") != null) {
            mods.addAll((JSONArray) item.get("explicitMods"));
//            System.out.println(item.get("explicitMods").toString());
        }
        if (item.get("implicitMods") != null) {
            mods.addAll((JSONArray) item.get("implicitMods"));
//            System.out.println(item.get("implicitMods").toString());
        }
        if (item.get("enchantMods") != null) {
            mods.addAll((JSONArray) item.get("enchantMods"));
//            System.out.println(item.get("enchantMods").toString());
        }
        if (item.get("craftedMods") != null) {
            mods.addAll((JSONArray) item.get("craftedMods"));
//            System.out.println(item.get("craftedMods").toString());
        }
//        System.out.println("--------------------------------------");
        return mods;
    }

    //TODO Maybe fix to take what matches and above #
    private Boolean MatchesParameters(JSONObject item) {
        if (ExtractMods(item).containsAll(parameters)) {
            return true;
        }
        return false;
    }

    private void addPriceToObject(JSONObject stashData, JSONObject currentItem, JSONObject filteredObject) {
        if (hasPrice(stashData.get("stash").toString())) {
            String[] price = stashData.get("stash").toString().split("\\s");
            if (!price[1].equals("0")) {
                filteredObject.put("price", price[1] + " " + price[2]);
            }
        }
        //Make sure the "note" attribute exists before we ask for it
        else if (currentItem.get("note") != null) {

            //Get price from note
            if (hasPrice(currentItem.get("note").toString())) {
                String[] price = currentItem.get("note").toString().split("\\s");
                //Only take items with a price
                if (!price[1].equals("0")) {
                    filteredObject.put("price", price[1] + " " + price[2]);
                }
            }
        }
        //If price does not exist on item then add "Make Offer" as price since item has no set price but has been put for sale
        filteredObject.putIfAbsent("price", "Make Offer");
    }

    private void addItemNameToObject(JSONObject currentItem, JSONObject filteredObject) {
        String itemTypeLine = currentItem.get("typeLine").toString();
        //If item is of unique quality then add the uniques name
        if (!currentItem.get("name").toString().equals("")) {
            String[] splitItemName = currentItem.get("name").toString().split(">>");
            filteredObject.put("itemName", splitItemName[3]);
        } else if (!currentItem.get("name").toString().equals("")) {
            filteredObject.put("itemName", currentItem.get("name"));

            //If item is not of unique quality then add the items typeLine
        } else if (itemTypeLine.contains(">>")) {
            String[] splitItemName = itemTypeLine.split(">>");
            filteredObject.put("itemName", splitItemName[3]);
        } else {
            filteredObject.put("itemName", currentItem.get("typeLine"));
        }
    }

    private boolean hasPrice(String input) {
        return input.startsWith("~price") || input.startsWith("~b/o");
    }
}
