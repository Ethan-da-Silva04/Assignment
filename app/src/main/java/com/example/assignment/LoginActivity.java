package com.example.assignment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //System.out.println("Hello world!");
        setContentView(R.layout.activity_login);
    }

    public void helloWorld(View view) {
        //EditText txt = (EditText) findViewById(R.id.editTextTextEmailAddress);
        //EditText password = findViewById(R.id.editTextTextPassword);
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", "henry");
            obj.put("password", "password1@");
            ServerResponse response = WebClient.postJSON("example.php", obj);
            System.out.println(response.getData().toString());
        } catch (JSONException e) {
            System.out.println(e);
        }
    }
}
