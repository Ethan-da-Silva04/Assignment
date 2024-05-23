package com.example.assignment;

import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
    Donation basket to send to server
 */
public class Basket implements JSONSerializable {
    private static final int MAX_RESOURCE = 10000;


    private int id = 0;

    protected List<DonationItem> items;
    private ListView view = null;

    private static Map<Integer, Basket> createdBaskets;
    private static int nextId = 0;

    public Basket() {
        id = nextId++;
        items = new ArrayList<>();
        createdBaskets.put(id, this);
    }

    public static Basket getBasket(int id) {
        return createdBaskets.get(id);
    }

    public int getId() { return id; }

    public void setListView(ListView view) {
        this.view = view;
    }

    public ListView getListView() {
        return view;
    }

    public void setAdapter(BasketListViewAdapter adapter) {
        view.setAdapter(adapter);
    }

    public List<DonationItem> getItems() { return items; }

    public boolean add(int index, int quantity) {
        if (quantity <= 0) {
            return false;
        }
        if (index < 0 || index >= items.size()) {
            return false;
        }

        return modifyResource(index, quantity);
    }

    public boolean add(Resource resource, int quantity) {
        if (!contains(resource)) {
            items.add(new DonationItem(resource, quantity));
            return true;
        }

        if (quantity <= 0) {
            return false;
        }

        return modifyResource(resource, quantity);
    }

    public boolean add(DonationItem item) {
        if (!contains(item.getResource())) {
            items.add(item);
            return true;
        }

        return false;
    }

    public boolean setQuantity(int index, int quantity) {
        if (index < 0 || index >= items.size()) {
            return false;
        }

        return modifyResource(index, quantity - items.get(index).getQuantity());
    }

    public static Basket fromJSONArray(JSONArray array) {
        return null;
    }

    public boolean remove(Resource resource, int quantity) {
        return modifyResource(resource, -quantity);
    }

    public boolean remove(int index, int quantity) {
        if (index < 0 || index >= items.size()) {
            return false;
        }

        return modifyResource(index, -quantity);
    }

    public boolean removeAll(int index) {
        if (index < 0 || index >= items.size()) {
            return false;
        }

        items.remove(index);
        return true;
    }

    public boolean removeAll(Resource resource) {
        if (!contains(resource)) {
            return false;
        }

        items.removeIf(x -> x.getResourceId() == resource.getId());
        return true;
    }

    public DonationItem get(int index) {
        return items.get(index);
    }

    public boolean contains(Resource resource) {
        return items.stream().anyMatch(x -> x.getResourceId() == resource.getId());
    }

    public DonationItem get(Resource resource) {
        for (DonationItem item : items) {
            if (item.getId() == resource.getId()) {
                return item;
            }
        }

        return null;
    }

    private boolean modifyResource(DonationItem item, int amount) {
        if (item == null) {
            return false;
        }

        if (item.getQuantity() + amount > MAX_RESOURCE) {
            return false;
        }

        if (item.getQuantity() + amount <= 0) {
            items.removeIf(x -> x.getResourceId() == item.getResourceId());
            return true;
        }

        item.setQuantity(item.getQuantity() + amount);
        return true;
    }

    private boolean modifyResource(Resource resource, int amount) {
        return modifyResource(get(resource), amount);
    }

    private boolean modifyResource(int index, int amount) {
        return modifyResource(items.get(index), amount);
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

    static {
        createdBaskets = new HashMap<>();
    }
}
