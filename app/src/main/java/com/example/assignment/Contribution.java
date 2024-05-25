package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contribution implements JSONSerializable {
    private static Map<Integer, Contribution> contributionMap;
    private static int nextMapId = 0;

    // id to  be used with contributionMap. Do not use id.
    private int mapId;

    private int id;
    private int posterId;
    private int recipientPageId;
    private Basket basket;

    private Contribution() {
        this.id = 0;
        putContribution(this);
    }

    private Contribution(int id, int posterId, int recipientPageId) {
        this.id = id;
        this.posterId = posterId;
        this.recipientPageId = recipientPageId;
        basket = new Basket();
        putContribution(this);
    }

    private Contribution(int id, int posterId, int recipientPageId, Basket basket) {
        this.id = id;
        this.posterId = posterId;
        this.recipientPageId = recipientPageId;
        this.basket = basket;
        putContribution(this);
    }

    public Contribution(Basket basket) {
        this.id = 0;
        this.posterId = UserSession.getId();
        this.recipientPageId = 0;
        this.basket = basket;
        putContribution(this);
    }

    private static void putContribution(Contribution contribution) {
        contribution.mapId = nextMapId;
        contributionMap.put(nextMapId++, contribution);
    }

    public static Contribution getContribution(int mapId) {
        return contributionMap.get(mapId);
    }

    public static Contribution fromSession(int recipientPageId, Basket basket) {
        Contribution contribution = new Contribution();
        contribution.posterId = UserSession.getData().getId();
        contribution.recipientPageId = recipientPageId;
        contribution.basket = basket;
        return contribution;
    }

    public static Contribution fromAcceptAction(int id, int posterId, int recipientPageId, Basket basket) {
        Contribution contribution = new Contribution();
        contribution.id = id;
        contribution.posterId = posterId;
        contribution.recipientPageId = recipientPageId;
        contribution.basket = basket;
        return contribution;
    }

    public int getMapId() { return mapId; }

    public static Contribution fromJSONObject(JSONObject object) {

        try {
            int id = (int) object.get("id");
            int posterId = (int) object.get("poster_id");
            int recipientPageId = (int) object.get("recipient_page_id");
            Basket basket = Basket.fromJSONArray((JSONArray) object.get("content"));
            return new Contribution(id, posterId, recipientPageId, basket);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void setId(int id) { this.id = id; }

    public Basket getBasket() {
        return basket;
    }

    private static void addSearchQueryResult(JSONObject object, Map<Integer, Contribution> contributions) throws JSONException {
        int itemId = (int) object.get("id");
        int contributionId = (int) object.get("contribution_id");
        int resourceId = (int) object.get("resource_id");
        int posterId = (int) object.get("poster_id");
        int recipientPageId = (int) object.get("recipient_page_id");
        int quantity = (int) object.get("quantity");

        contributions.putIfAbsent(contributionId, new Contribution(contributionId, posterId, recipientPageId));
        Basket basket = contributions.get(contributionId).getBasket();
        DonationItem item = new DonationItem(itemId, Resource.getFromId(resourceId), quantity);
        basket.add(item);
    }

    private static void populateWithResult(Map<Integer, Contribution> contributions, JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            addSearchQueryResult(array.getJSONObject(i), contributions);
        }
    }

    public static List<Contribution> listFromPageMap(Map<Integer, Contribution> pages) {
        List<Contribution> result = new ArrayList<>();
        for (Map.Entry<Integer, Contribution> entry : pages.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    public void setRecipientPageId(int value) { recipientPageId = value; }

    public static List<Contribution> listFromJSONArray(JSONArray array) {
        Map<Integer, Contribution> contributions = new HashMap<>();
        try {
            populateWithResult(contributions, array);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return listFromPageMap(contributions);
    }

    @Override
    public JSONObject serialize() throws JSONException {
        return new JSONObject()
            .put("basket", basket.serialize())
            .put("poster_id", posterId)
            .put("recipient_page_id", recipientPageId);
    }

    static {
        contributionMap = new HashMap<>();
    }
}
