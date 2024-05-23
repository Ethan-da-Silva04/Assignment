package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Contribution implements JSONSerializable {
    private int id;
    private int posterId;
    private int recipientPageId;
    private Basket basket;

    private Contribution() {
        this.id = 0;
    }

    private Contribution(int id, int posterId, int recipientPageId, Basket basket) {
        this.id = id;
        this.posterId = posterId;
        this.recipientPageId = recipientPageId;
        this.basket = basket;
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

    @Override
    public JSONObject serialize() throws JSONException {
        return new JSONObject()
            .put("basket", basket.serialize())
            .put("poster_id", posterId)
            .put("recipient_page_id", recipientPageId);
    }
}
