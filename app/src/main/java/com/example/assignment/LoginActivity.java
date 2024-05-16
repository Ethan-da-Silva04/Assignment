package com.example.assignment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void helloWorld(View view) {
        EditText emailEditText = findViewById(R.id.editTextTextEmailAddress);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword);
        String username = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        try {
            UserSession session = UserSession.login(username, password);
            System.out.println(session.get());
        } catch (ServerResponseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT);
    }
}
