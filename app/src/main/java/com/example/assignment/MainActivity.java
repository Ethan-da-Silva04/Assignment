package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;

import org.json.JSONException;

// BCrypt library, OkHttp library
public class MainActivity extends AppCompatActivity {
    // Method to handle button click
    public void foobar(View view) {
        // Action to be performed when the button is clicked
        //Toast.makeText(this, "Button Clicked!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        //testUser();
    }

    public void testUser() {
        try {
            UserSession session = UserSession.login("xyz", "xyze");
        } catch (ServerResponseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void testBasket() {
        ClientBasket basket = new ClientBasket();
        try {
            basket.add(1, 1);
            basket.add(1, 50);

           // ServerResponse response = basket.post();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "Sent basket to server!", Toast.LENGTH_SHORT).show();
    }

    public void share(View view) {
        String content = "Check out this awesome app!";
        String mimeType = "text/plain";
        ShareCompat.IntentBuilder
                .from(this)
                .setType(mimeType)
                .setChooserTitle("Share via")
                .setText(content)
                .startChooser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //System.out.println("Hello world!");
        setContentView(R.layout.activity_main);
    }
}