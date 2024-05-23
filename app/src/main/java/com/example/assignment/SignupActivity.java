package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

public class SignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void showMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void performSignup(View view) {
        if (UserSession.exists()) {
            Toast.makeText(this, "Already logged inz!", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText usernameEditText = findViewById(R.id.editTextUsername);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        EditText phoneNumberEditText = findViewById(R.id.editTextPageSearch);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String confirmedPassword = ((EditText) findViewById(R.id.editTextRetypePassword)).getText().toString();
        if (!confirmedPassword.equals(password)) {
            Toast.makeText(this, "Passwords must match.", Toast.LENGTH_SHORT).show();
        }
        
        try {
            UserSession session = UserSession.signup(username, password, "Hello, I'm a new user of ShareCycle!", phoneNumber);
            Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
        } catch (ServerResponseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
