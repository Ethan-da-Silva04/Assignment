package com.example.assignment;

import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public abstract class DonationBasket implements JSONSerializable {
    protected List<DonationItem> items;

    public List<DonationItem> getItems() { return items; }
}
