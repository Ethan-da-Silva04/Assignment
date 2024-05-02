package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class ServerResponse {
    private JSONArray data;
    private int responseCode;

    public ServerResponse(Response response) {
        this.responseCode = response.code();

        try {
            String body = response.body().string();
            data = new JSONArray(body);
        } catch (IOException|JSONException e) {
            System.out.println(e);
        }
    }

    public int getResponseCode() {
        return responseCode;
    }

    public JSONArray getData() { return data; }

    public boolean isSuccess() {
        return 200 <= responseCode && responseCode < 300;
    }
    public boolean isError() {
        return 400 <= responseCode && responseCode < 500;
    }
}
