package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class User {
    private int id;
    private String username;
    private String biography;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private int accountRank;
    private int acceptedContributions;

    public User(int id, String username, String biography, String phoneNumber, int accountRank, int acceptedContributions) {
        this.id = id;
        this.username = username;
        this.biography = biography;
        this.phoneNumber = phoneNumber;
        this.accountRank = accountRank;
        this.acceptedContributions = acceptedContributions;
    }

    public static User fromJSONObject(JSONObject object) throws JSONException {
        System.out.println(object);
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

    public int getId() { return id; }

    public String toString() {
        return String.format("id: %d, username: %s, biography: %s, phoneNumber: %s\n",
                id, username, biography, phoneNumber);
    }
}
