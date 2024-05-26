package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    // specifically to use for account history
    private LocalDateTime createdAt;

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

    public Contribution(int id, Basket basket, int recipientPageId, int posterId) {
        this.id = id;
        this.basket = basket;
        this.recipientPageId =recipientPageId;
        this.posterId = posterId;
        putContribution(this);
    }

    private static void putContribution(Contribution contribution) {
        contribution.mapId = nextMapId;
        contributionMap.put(nextMapId++, contribution);
    }

    public int getId() { return id; }

    public int getPosterId() { return  posterId; }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static Contribution getContribution(int mapId) {
        return contributionMap.get(mapId);
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Contribution fromSession(int recipientPageId, Basket basket) {
        Contribution contribution = new Contribution();
        contribution.posterId = UserSession.getData().getId();
        contribution.recipientPageId = recipientPageId;
        contribution.basket = basket;
        return contribution;
    }

    public int getMapId() { return mapId; }

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

        if (object.has("page_name")) {
            // will be added to a map internal to the DonationPage class
            new DonationPage(recipientPageId, (int) object.get("donatee_id"), (String) object.get("page_name"));
        }

        contributions.putIfAbsent(contributionId, new Contribution(contributionId, posterId, recipientPageId));
        Contribution contribution = contributions.get(contributionId);
        if (object.has("created_at")) {
            contribution.setCreatedAt(LocalDateTime.parse((CharSequence) object.get("created_at"), Constants.fromFormatter));
        }

        DonationItem item = new DonationItem(itemId, Resource.getFromId(resourceId), quantity);
        contributions.get(contributionId).getBasket().add(item);
    }

    private static void populateWithResult(Map<Integer, Contribution> contributions, JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            addSearchQueryResult(array.getJSONObject(i), contributions);
        }
    }

    public static List<Contribution> listFromPageMap(Map<Integer, Contribution> contributions) {
        List<Contribution> result = new ArrayList<>();
        result.addAll(contributions.values());
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
            .put("id", id)
            .put("recipient_page_id", recipientPageId);
    }

    public int getRecipientPageId() { return recipientPageId; }

    static {
        contributionMap = new HashMap<>();
    }
}
