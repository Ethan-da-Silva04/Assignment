package com.example.assignment;

import org.json.JSONException;
import org.json.JSONObject;

public class PostDonationBasket extends ClientDonationBasket {
    private int recipientId;

    @Override
    public JSONObject serialize() throws JSONException {
        JSONObject result = super.serialize();
        JSONObject basketDetails = new JSONObject();
        basketDetails.put("poster_id", UserSession.getData().getId());
        basketDetails.put("recipient_id", recipientId);
        return null;
    }

    @Override
    public ServerResponse post() throws ServerResponseException {
        return null;
    }
}
