package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.function.*;
import java.util.List;

public class Search<T> {
    public List<T> run(SearchQuery query, JSONObject data, Function<JSONObject, T> deserializer) throws ServerResponseException, JSONException {
        List<T> result = new ArrayList<>();

        for (String location : query.getSearchLocations()) {
            JSONArray array = WebClient.postJSON(location, data).getData();
            for (int i = 0; i < array.length(); i++) {
                result.add(deserializer.apply(array.getJSONObject(i)));
            }
        }

        return result;
    }
}
