package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private static Map<Integer, User> userMap;

    private int id;
    private String username;
    private String biography;
    private String phoneNumber;
    private int accountRank;
    private int acceptedContributions;

    public User(int id, String username, String biography, String phoneNumber, int accountRank, int acceptedContributions) {
        this.id = id;
        this.username = username;
        this.biography = biography;
        this.phoneNumber = phoneNumber;
        this.accountRank = accountRank;
        this.acceptedContributions = acceptedContributions;
        putUser(this);
    }

    private static void putUser(User user) {
        userMap.putIfAbsent(user.getId(), user);
    }

    public String getBiography() { return biography; }

    public static User getFromId(int id) { return userMap.get(id); }

    public static User fromJSONObject(JSONObject object) throws JSONException {
        int id = (int) object.get("id");
        String username = (String) object.get("username");
        String biography = (String) object.get("biography");
        String phoneNumber = (String) object.get("phone_number");
        int accountRank = (int) object.get("account_rank");
        int acceptedContributions = (int) object.get("accepted_contributions");
        return new User(id, username, biography, phoneNumber, accountRank, acceptedContributions);
    }

    public static User fromJSONArray(JSONArray data) throws JSONException {
        JSONObject object = (JSONObject) data.get(0);
        return fromJSONObject(object);
    }

    public static List<User> getTop() throws ServerResponseException {
        List<User> result = new ArrayList<>();

        try {
            ServerResponse response = WebClient.get("best_users.php");
            JSONArray array = response.getData();
            for (int i = 0; i < array.length(); i++) {
                result.add(User.fromJSONObject(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static List<User> searchBy(String username) throws ServerResponseException {
        List<User> result = new ArrayList<>();

        try {
            JSONObject postObject = new JSONObject().put("username", username);
            ServerResponse response = WebClient.postJSON("search_users_by_username.php", postObject);
            JSONArray array = response.getData();
            for (int i = 0; i < array.length(); i++) {
                result.add(User.fromJSONObject(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public int getRank() { return accountRank; }

    public String getUsername() { return username; }

    public int getId() { return id; }

    public String toString() {
        return String.format("id: %d, username: %s, biography: %s, phoneNumber: %s\n",
                id, username, biography, phoneNumber);
    }

    static {
        userMap = new HashMap<>();
    }
}
