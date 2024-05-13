package com.example.assignment;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONSerializable {
    public JSONObject serialize() throws JSONException;
}
