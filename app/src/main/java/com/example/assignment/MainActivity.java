package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import android.content.DialogInterface;
import android.app.AlertDialog;

import org.json.JSONException;
import java.util.List;

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

    public void showLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void showInformation(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome to ShareCycle!")
                .setMessage("We're here to connect those who want to give to others with those in need.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Action to perform when OK button is clicked
                    }
                })
                .show(); // Show the AlertDialog
    }

    public void showSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
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

    public void testFeature(View view) {
        // Intent intent = new Intent(this, ViewDonationPageActivity.class);
        //startActivity(intent);
        showSearchPages(view);
    }

    public void showCreatePage(View view) {
        Intent intent = new Intent(this, CreatePageActivity.class);
        startActivity(intent);
    }

    public void showSearchPages(View view) {
        Intent intent = new Intent(this, SearchPagesActivity.class);
        startActivity(intent);
    }

    public void testBasket() {
        try {
            DonationPage templateSearch = new DonationPage("Some name");
            List<DonationPage> pages = new Search<DonationPage>().run(
                    new SearchQuery("search_pages_by_name.php"),
                    DonationPage::fromJSONObject,
                    templateSearch,
                    "name"
            );
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