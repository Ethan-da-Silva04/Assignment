package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Date;

public class User {
    private int id;
    private String username;
    private String biography;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private int accountRank;
    private int acceptedDonations;

    public User(int id, String username, String biography, String phoneNumber, int accountRank, int acceptedDonations) {
        this.id = id;
        this.username = username;
        this.biography = biography;
        this.phoneNumber = phoneNumber;
        this.accountRank = accountRank;
        this.acceptedDonations = acceptedDonations;
    }

    public static User fromJSONArray(JSONArray data) throws JSONException {
        JSONObject object = (JSONObject) data.get(0);
        int id = (int) object.get("id");
        String username = (String) object.get("username");
        String biography = (String) object.get("biography");
        String phoneNumber = (String) object.get("phone_number");
        int accountRank = (int) object.get("account_rank");
        int acceptedDonations = (int) object.get("accepted_donations");
        return new User(id, username, biography, phoneNumber, accountRank, acceptedDonations);
    }

    public int getId() { return id; }

    public String toString() {
        return String.format("id: %d, username: %s, biography: %s, phoneNumber: %s\n",
                id, username, biography, phoneNumber);
    }
}
