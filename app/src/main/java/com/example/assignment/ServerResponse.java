package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class ServerResponse {
    private JSONArray data;
    private int statusCode;

    public ServerResponse(Response response) throws ServerResponseException {
        this.statusCode = response.code();

        try {
            String body = response.body().string();
            if (isUserError() || isServerError()) {
                throw new ServerResponseException(body, statusCode);
            }
            data = new JSONArray(body);
        } catch (IOException e) {
            System.out.println(e);
        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    public int getResponseCode() {
        return statusCode;
    }

    public JSONArray getData() { return data; }

    public boolean isSuccess() {
        return 200 <= statusCode && statusCode < 300;
    }
    public boolean isUserError() {
        return 400 <= statusCode && statusCode < 500;
    }

    public boolean isServerError() { return 500 <= statusCode && statusCode < 600; }
}
