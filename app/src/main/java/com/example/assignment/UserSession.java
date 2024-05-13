package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;

public class UserSession {
    private static UserSession session;

    private User data;

    private UserSession(User data) {
        this.data = data;
    }

    public static UserSession get() { return session; }

    public static User getData() { return session.data; }

    public static UserSession login(String username, String password) throws ServerResponseException, JSONException {
        if (session != null) {
            return session;
        }
        return WebClient.login(username, password);
    }

    public static UserSession signup(String username, String password, String biography, String phoneNumber) throws ServerResponseException, JSONException {
        if (session != null) {
            return session;
        }

        return WebClient.signup(username, password, biography, phoneNumber);
    }

    public static UserSession initialize(JSONArray data) throws JSONException {
        session = new UserSession(User.fromJSONArray(data));
        return session;
    }

    public String toString() {
        return data.toString();
    }
}
