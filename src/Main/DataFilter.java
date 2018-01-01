package Main;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataFilter implements Runnable {

    private final Controller controller;
    private JSONObject data;
    private List<String> parameters;
    private String itemName;
    private boolean priceRequirement;
    private volatile boolean shouldRun = true;

    public DataFilter(Controller controller, JSONObject data, List<String> parameters, String name, boolean priceRequirement) {
        this.controller = controller;
        this.data = data;
        this.parameters = parameters;
        this.itemName = name;
        this.priceRequirement = priceRequirement;
    }

    public void stopRunning() {
        shouldRun = false;
    }

    @Override
    public void run() {
        while (shouldRun) {
            if (data != null) {
                if (data.get("stashes") != null) {
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
                                if (!itemsInStash.isEmpty()) {

                                    //Loop over all the existing items
                                    for (Object item : (JSONArray) stashData.get("items")) {
                                        JSONObject currentItem = (JSONObject) item;

                                        //Filter out all the empty items and add the rest
                                        if (currentItem != null) {

                                            if (priceRequirement) {

                                                if (!itemName.isEmpty()) {

                                                    if (currentItem.get("note") != null) {
                                                        if (!currentItem.get("note").toString().isEmpty() &&
                                                                hasPrice(currentItem.get("note").toString())) {

                                                            CreateItemIfMatchesSearch(stashData, currentItem);
                                                        }
                                                    } else if (stashData.get("stash") != null) {
                                                        if (!stashData.get("stash").toString().isEmpty() &&
                                                                hasPrice(stashData.get("stash").toString())) {

                                                            CreateItemIfMatchesSearch(stashData, currentItem);
                                                        }
                                                    }
                                                }
                                            } else {

                                                if (!itemName.isEmpty()) {
                                                    CreateItemIfMatchesSearch(stashData, currentItem);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    stopRunning();
                }
            }
        }
    }

    private void CreateItemIfMatchesSearch(JSONObject stashData, JSONObject currentItem) {
        if (currentItem.get("name").toString().contains(itemName) ||
                currentItem.get("typeLine").toString().contains(itemName)) {
            CreateObjectForUI(stashData, currentItem);

        } else if (!parameters.isEmpty() && MatchesMods(currentItem)) {
            CreateObjectForUI(stashData, currentItem);
        }
    }

    private void CreateObjectForUI(JSONObject stashData, JSONObject currentItem) {
        JSONObject filteredObject = new JSONObject();

        //Add Name of Item
        addItemNameToObject(currentItem, filteredObject);

        //Add Price of Item
        addPriceToObject(stashData, currentItem, filteredObject);

        //Add Mods to item
        filteredObject.put("Mods", ExtractMods(currentItem));
        controller.AddItemToUISearchListView(filteredObject);
    }

    private ArrayList<String> ExtractMods(JSONObject item) {

        JSONArray mods = new JSONArray();
        if (item.get("explicitMods") != null) {
            mods.addAll((JSONArray) item.get("explicitMods"));
        }
        if (item.get("implicitMods") != null) {
            mods.addAll((JSONArray) item.get("implicitMods"));
        }
        if (item.get("enchantMods") != null) {
            mods.addAll((JSONArray) item.get("enchantMods"));
        }
        if (item.get("craftedMods") != null) {
            mods.addAll((JSONArray) item.get("craftedMods"));
        }
        return mods;
    }

    //TODO Maybe fix to take what matches and above #
    private Boolean MatchesMods(JSONObject item) {
        if (ExtractMods(item).containsAll(parameters)) {
            return true;
        }
        return false;
    }

    private void addPriceToObject(JSONObject stashData, JSONObject currentItem, JSONObject filteredObject) {
        if (hasPrice(stashData.get("stash").toString())) {
            String[] price = stashData.get("stash").toString().split("\\s");

            filteredObject.put("price", price[1] + " " + price[2]);
        }
        //Make sure the "note" attribute exists before we ask for it
        else if (currentItem.get("note") != null) {

            //Get price from note
            if (hasPrice(currentItem.get("note").toString())) {
                String[] price = currentItem.get("note").toString().split("\\s");

                filteredObject.put("price", price[1] + " " + price[2]);
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
        if (input.contains("~price") || input.contains("~b/o")) {
            return true;
        }
        return false;
    }
}
