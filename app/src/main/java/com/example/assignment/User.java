package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class User implements Searchable {
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

    public static User fromJSONObject(JSONObject object) throws JSONException {
        int id = (int) object.get("id");
        String username = (String) object.get("username");
        String biography = (String) object.get("biography");
        String phoneNumber = (String) object.get("phone_number");
        int accountRank = (int) object.get("account_rank");
        int acceptedDonations = (int) object.get("accepted_donations");
        return new User(id, username, biography, phoneNumber, accountRank, acceptedDonations);
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

    @Override
    public List<Searchable> search() {
        try {
            JSONObject object = new JSONObject();
            object.put("id", id);
            return Searchable.fromQuery(
                    new SearchQuery("request_contributions"),
                    object
            );
        } catch (ServerResponseException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Searchable deserialize(JSONArray array) {
        try {
            return User.fromJSONArray(array);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Searchable deserialize(JSONObject object) {
        try {
            return User.fromJSONObject(object);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
