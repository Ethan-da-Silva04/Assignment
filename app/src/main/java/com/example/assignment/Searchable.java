package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public interface Searchable {
    public List<Searchable> search();
}
