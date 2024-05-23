package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.function.*;
import java.util.List;

public class Search<T> {
    public List<T> run(SearchQuery query, Function<JSONObject, T> deserializer, T object, String... fields) throws ServerResponseException, JSONException, NoSuchFieldException {
        List<T> result = new ArrayList<>();
        JSONObject jsonObject = prepareFields(object, fields);

        for (String location : query.getSearchLocations()) {
            JSONArray array = WebClient.postJSON(location, jsonObject).getData();
            for (int i = 0; i < array.length(); i++) {
                result.add(deserializer.apply(array.getJSONObject(i)));
            }
        }

        return result;
    }

    private JSONObject prepareFields(T object, String[] fields) throws NoSuchFieldException, JSONException {
        JSONObject jsonObject = new JSONObject();
        for (String field : fields) {
            jsonObject.put(Search.camelToSnake(field), object.getClass().getDeclaredField(field));
        }

        return jsonObject;
    }

    public static String camelToSnake(String name) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isUpperCase(name.charAt(i))) {
                result.append(name.charAt(i));
                continue;
            }
            result.append('_');
            result.append(Character.toLowerCase(name.charAt(i)));
        }
        return result.toString();
    }
}
