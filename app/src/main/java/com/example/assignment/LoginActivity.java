package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void showMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void showSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void performLogin(View view) {
        if (UserSession.exists()) {
            System.out.println("Existenced already!!!");
            Toast.makeText(this, "Already logged in!", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText usernameEditText = findViewById(R.id.editTextUsername);
        EditText passwordEditText = findViewById(R.id.editTextRetypePassword);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        try {
            UserSession session = UserSession.login(username, password);
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            System.out.println(session.get());
            return;
        } catch (ServerResponseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
