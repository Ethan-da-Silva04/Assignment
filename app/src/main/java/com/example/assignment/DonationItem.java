package com.example.assignment;

import org.json.JSONException;
import org.json.JSONObject;

public class DonationItem implements JSONSerializable {
    private int id;
    private Resource resource;
    private int quantity;

    // avoiding inheritance as it will make everything more complicated with refactoring, instead opting for a variable that is sometimes unused
    private int quantityReceived;

    public DonationItem(int id, Resource resource, int quantity) {
        this.id = id;
        this.resource = resource;
        this.quantity = quantity;
    }

    public DonationItem(Resource resource, int quantity) {
        this.resource = resource;
        this.quantity = quantity;
    }

    public DonationItem(int id, Resource resource, int quantityAsked, int quantityRecieved) {
        this.id = id;
        this.resource = resource;
        this.quantity = quantityAsked;
        this.quantityReceived = quantityRecieved;
    }

    public int getResourceId() { return resource.getId(); }

    public int getId() { return id; }

    public Resource getResource() { return resource; }
    public String getName() {
        return resource.getName();
    }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getQuantityAsked() { return quantity; }
    public int getQuantityReceived() { return quantityReceived; }

    public void setQuantityAsked(int quantityAsked) { this.quantity = quantityAsked; }

    public void setQuantityReceived(int quantityReceived) { this.quantityReceived = quantityReceived; }

    @Override
    public JSONObject serialize() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("resource_id", resource.getId());
        result.put("quantity", quantity);
        return result;
    }

}
