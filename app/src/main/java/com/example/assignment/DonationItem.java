package com.example.assignment;

import org.json.JSONException;
import org.json.JSONObject;

public class DonationItem implements JSONSerializable {
    private Resource resource;
    private int quantity;

    public DonationItem(Resource resource, int quantity) {
        this.resource = resource;
        this.quantity = quantity;
    }

    public int getId() {
        return resource.getId();
    }

    public String getName() {
        return resource.getName();
    }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public JSONObject serialize() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("resource", resource.serialize());
        result.put("quantity", quantity);
        return result;
    }

}
