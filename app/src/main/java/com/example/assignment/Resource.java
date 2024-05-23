package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Resource {
    private int id;
    private String name;
    private String description;

    private static Map<Integer, Resource> resources = null;

    private Resource(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static String[] getNames() {
        Map<Integer, Resource> resources = getResources();
        String[] result = new String[resources.size()];
        int i = 0;

        for (Map.Entry<Integer, Resource> entries : resources.entrySet()) {
            result[i++] = entries.getValue().getName();
        }

        return result;
    }

    public static Resource getFromName(String name) {
        Map<Integer, Resource> resources = getResources();

        for (Map.Entry<Integer, Resource> entries : resources.entrySet()) {
            if (entries.getValue().name.equals(name)) {
                return entries.getValue();
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static Resource getFromId(int id) {
        return getResources().get(id);
    }

    public static Map<Integer, Resource> getResources() {
        if (resources != null) {
            return resources;
        }
        try {
            ServerResponse response = WebClient.get("resources.php");
            JSONArray array = response.getData();
            resources = new HashMap<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject resourceAsJSON = (JSONObject) array.get(i);
                int id = (int) resourceAsJSON.get("id");
                String name = (String) resourceAsJSON.get("name");
                String description = (String) resourceAsJSON.get("description");
                resources.put(id, new Resource(id, name, description));
            }

        } catch (ServerResponseException e) {
            System.out.println(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return resources;
    }
}
