package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;

// BCrypt library, OkHttp library
public class MainActivity extends AppCompatActivity {
    // Method to handle button click
    public void foobar(View view) {
        // Action to be performed when the button is clicked
        Toast.makeText(this, "Button Clicked!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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