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

                        if (ContainsViableData(stashData)) {

                            //Loop over all the found stash items
                            for (Object item : (JSONArray) stashData.get("items")) {
                                JSONObject currentItem = (JSONObject) item;

                                //Filter out empty items
                                if (currentItem != null) {
                                    //Start checking against user parameters
                                    if (priceRequirement) {

                                        if (!itemName.isEmpty()) {

                                            if (currentItem.get("note") != null) {
                                                if (!currentItem.get("note").toString().isEmpty() &&
                                                        HasPrice(currentItem.get("note").toString())) {

                                                    CreateItemIfMatchesSearch(stashData, currentItem);
                                                }
                                            } else if (stashData.get("stash") != null) {
                                                if (!stashData.get("stash").toString().isEmpty() &&
                                                        HasPrice(stashData.get("stash").toString())) {

                                                    CreateItemIfMatchesSearch(stashData, currentItem);
                                                }
                                            }
                                        } else {
                                            CreateItemIfMatchesSearch(stashData, currentItem);
                                        }
                                    } else {
                                        CreateItemIfMatchesSearch(stashData, currentItem);
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

    //Match items with user parameters and call to send the item to the ui
    private void CreateItemIfMatchesSearch(JSONObject stashData, JSONObject currentItem) {
        switch (0) {
            case 0:
                if (itemName != null && !itemName.isEmpty()) {

                    if (parameters != null && !parameters.isEmpty()) {

                        if (ExtractTypeLine(currentItem).contains(itemName) || ExtractItemName(currentItem).contains(itemName)) {
                            if (MatchesMods(currentItem)) {
                                CreateObjectForUI(stashData, currentItem);
                                break;
                            }
                            break;
                        }
                    }
                    if (ExtractItemName(currentItem).contains(itemName) ||
                            currentItem.get("typeLine").toString().contains(itemName)) {
                        CreateObjectForUI(stashData, currentItem);
                        break;
                    } else {
                        break;
                    }

                } else if (parameters != null && !parameters.isEmpty() && MatchesMods(currentItem)) {
                    CreateObjectForUI(stashData, currentItem);
                    break;
                }
        }
    }

    //Creates a new JSONObject to send to the ui for easy data parsing without unnecessary data
    private void CreateObjectForUI(JSONObject stashData, JSONObject currentItem) {
        JSONObject filteredObject = new JSONObject();

        //Add Name of item
        AddItemNameToObject(currentItem, filteredObject);

        //Add typeLine to item
        AddItemTypeLineToObject(currentItem, filteredObject);

        //Add Price of item
        AddPriceToObject(stashData, currentItem, filteredObject);

        //Add sockets to item
        if (currentItem.get("sockets") != null && !currentItem.get("sockets").toString().isEmpty()) {
            AddSocketsToObject(currentItem, filteredObject);
        }

        //Add item level to item
        addItemLevelToItem(currentItem, filteredObject);

        //Add Mods to item
        filteredObject.put("mods", ExtractMods(currentItem));

        //Add the current items location to the ui item
        filteredObject.put("x", currentItem.get("x"));
        filteredObject.put("y", currentItem.get("y"));
        filteredObject.put("stashName", stashData.get("stash"));
        filteredObject.put("seller", stashData.get("lastCharacterName"));
        controller.AddItemToUISearchListView(filteredObject);
    }

    //Cut out unwanted information from the name field
    private String ExtractItemName(JSONObject item) {
        try {
            String itemName = item.get("name").toString();

            if (itemName != null) {
                if (itemName.contains(">>")) {
                    String[] cleanItemName = item.get("name").toString().split(">>");
                    itemName = cleanItemName[3];

                    return itemName;
                }
                return itemName;
            }
        } catch (NullPointerException e) {
            controller.DisplayError("Error while parsing data - the search continues");
        }
        return null;
    }

    //Cut out unwanted information from the typeLine field
    private String ExtractTypeLine(JSONObject item) {
        try {
            String typeLine = item.get("typeLine").toString();

            if (typeLine != null) {
                if (typeLine.contains(">>")) {
                    String[] cleanTypeLine = item.get("typeLine").toString().split(">>");
                    typeLine = cleanTypeLine[3];

                    return typeLine;
                }
                return typeLine;
            }
        } catch (NullPointerException e) {
            controller.DisplayError("Error while parsing data - the search continues");
        }
        return null;
    }

    //Get all the various types of mods from the item
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

    //Check if the item matches the users parameters
    private Boolean MatchesMods(JSONObject item) {
        int hitCount = 0;
        for (String mod : ExtractMods(item)) {
            for (String param : parameters) {
                if (mod.equals(param)) {
                    hitCount++;
                }
            }
        }
        return hitCount == parameters.size();
    }

    //Extracts the items price to add to the created object for the ui
    private void AddPriceToObject(JSONObject stashData, JSONObject currentItem, JSONObject filteredObject) {
        if (HasPrice(stashData.get("stash").toString())) {
            String[] price = stashData.get("stash").toString().split("\\s");

            if (price.length == 3 && price[1] != null && price[2] != null) {
                filteredObject.put("price", price[1] + " " + price[2]);
            }
        }
        //Make sure the "note" attribute exists before we ask for it
        else if (currentItem.get("note") != null) {

            //Get price from note
            if (HasPrice(currentItem.get("note").toString())) {
                String[] price = currentItem.get("note").toString().split("\\s");

                filteredObject.put("price", price[1] + " " + price[2]);
            }
        }
        //If price does not exist on item then add "Make Offer" as price since item has no set price but has been put for sale
        filteredObject.putIfAbsent("price", "Make Offer");
    }

    //Extracts and adds name to the object for the ui
    private void AddItemNameToObject(JSONObject currentItem, JSONObject filteredObject) {
        if (ExtractItemName(currentItem) != null && !ExtractItemName(currentItem).isEmpty()) {
            filteredObject.put("itemName", ExtractItemName(currentItem));
        }
    }

    //Extracts and adds typeLine to the object for the ui
    private void AddItemTypeLineToObject(JSONObject currentItem, JSONObject filteredObject) {
        if (ExtractTypeLine(currentItem) != null && !ExtractTypeLine(currentItem).isEmpty()) {
            filteredObject.put("itemTypeLine", ExtractTypeLine(currentItem));
        }
    }

    //Extracts and adds item level to the object for the ui
    private void addItemLevelToItem(JSONObject currentItem, JSONObject filteredObject) {
        if (currentItem.get("ilvl") != null && !currentItem.get("ilvl").toString().isEmpty()) {
            filteredObject.put("ilvl", currentItem.get("ilvl"));
        }
    }

    //Extracts and adds the sockets to the object for the ui
    private void AddSocketsToObject(JSONObject currentItem, JSONObject filteredObject) {
        JSONArray sockets = (JSONArray) currentItem.get("sockets");
        List<String> filteredSockets = new ArrayList<>();

        for (Object object : sockets) {
            JSONObject socketObject = (JSONObject) object;
            filteredSockets.add(socketObject.get("sColour").toString());
        }
        filteredObject.put("sockets", filteredSockets);
    }

    private boolean ContainsViableData(JSONObject stashData) {
        //Filter out all stashes from accounts that no longer exist
        if (stashData.get("accountName") != null) {

            //Filter out all the stashes that does not contain items for sale
            if (stashData.get("public") != null) {

                //Filter out all stashes that does not contain items
                JSONArray itemsInStash = (JSONArray) stashData.get("items");
                if (!itemsInStash.isEmpty()) {

                    //Filter out the empty stashes
                    if (stashData.get("stash") != null && !stashData.get("stash").toString().isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //Used to check if an item contains data determining price
    private boolean HasPrice(String input) {
        return input.contains("~price") || input.contains("~b/o");
    }
}
