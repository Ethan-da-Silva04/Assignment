package com.example.assignment;

import static java.util.Collections.sort;

import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
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

    /* Only works when the items are sorted by the resource id's. Do not call if this is not the case */
    private DonationItem searchByResource(Resource resource) {
        int left = 0;
        int right = items.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int resourceId = items.get(mid).getResourceId();
            if (resourceId == resource.getId()) {
                return items.get(mid);
            }

            if (resourceId < resource.getId()) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return null;
    }

    public Basket difference(Basket other) {
        Comparator<DonationItem> comparator = new Comparator<DonationItem>() {
            @Override
            public int compare(DonationItem fst, DonationItem snd) {
                return fst.getResourceId() - snd.getResourceId();
            }
        };

        items.sort(comparator);
        other.items.sort(comparator);
        Basket result = new Basket();
        result.items.addAll(this.items);

        for (int i = 0; i < result.items.size(); i++) {
            DonationItem item = result.items.get(i);
            DonationItem otherItem = other.searchByResource(result.items.get(i).getResource());

            if (otherItem.getQuantity() >= item.getQuantity()) {
                result.items.remove(i);
                i--;
                continue;
            }

            item.setQuantity(item.getQuantity() - otherItem.getQuantity());
        }

        return result;
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
