package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/*
    Donation basket to send to server
 */
public class Basket implements JSONSerializable {
    private static final int MAX_RESOURCE = 10000;
    private static final int MIN_RESOURCE = 0;

    protected List<DonationItem> items;

    public Basket() {
        items = new ArrayList<>();
    }

    public List<DonationItem> getItems() { return items; }

    public void add(int resourceId, int quantity) throws Exception {
        if (!contains(resourceId)) {
            items.add(new DonationItem(Resource.getFromId(resourceId), quantity));
            return;
        }

        modifyResource(resourceId, quantity);
    }

    public void removeResource(int resourceId, int quantity) throws Exception {
        if (!contains(resourceId)) {
            throw new Exception("Cannot remove a resource that is not contained within this basket");
        }
        modifyResource(resourceId, -quantity);
    }

    public boolean contains(Resource resource) {
        return contains(resource.getId());
    }

    public boolean contains(int resourceId) {
        return items.stream().anyMatch(x -> x.getId() == resourceId);
    }

    public DonationItem get(int resourceId) {
        for (DonationItem item : items) {
            if (item.getId() == resourceId) {
                return item;
            }
        }

        return null;
    }

    private void modifyResource(int resourceId, int amount) throws Exception {
        DonationItem item;
        if ((item = get(resourceId)) == null) {
            throw new Exception("Resource does not exist");
        }

        if (item.getQuantity() + amount < 0) {
            throw  new Exception("Cannot remove more quantity of item");
        }

        if (item.getQuantity() + amount > MAX_RESOURCE) {
            throw new Exception("Quantity cannot exceed max amount");
        }

        item.setQuantity(Math.min(MAX_RESOURCE, Math.max(0, item.getQuantity() + amount)));
    }

    @Override
    public JSONObject serialize() throws JSONException {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();

        for (DonationItem item : items) {
            array.put(item.serialize());
        }

        result.put("content", array);

        return result;
    }
}
