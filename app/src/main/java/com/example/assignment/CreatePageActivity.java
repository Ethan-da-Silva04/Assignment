package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreatePageActivity extends AppCompatActivity {
    // TODO: this doesn't work, it's far too closed down to be fun to use or expressive, I need to expand this so that it works for a larger subset of HTML
    private static final String formatString = "<!doctype html>" +
            "<html>" +
            "<head>" +
            "<title>%s</title>" +
            "</head>" +
            "<body>" +
            "<h1>%s</h1>" +
            "<p>%s</p>" +
            "</body>" +
            "</html>";


    public void partialConstructPage(View view) {
        StringBuilder builder = new StringBuilder();
        EditText editName = findViewById(R.id.editTextPageSearch);
        EditText editWhyDonate = findViewById(R.id.editTextWhyDonate);

        String name = editName.getText().toString();
        String whyDonate = editWhyDonate.getText().toString();
        builder.append(String.format(formatString, name, name, whyDonate));
        String pageContent = builder.toString();

        DonationPage page = new DonationPage(name, pageContent);
        showCreateBasket(view, page);
    }


    public void showHome(View view) {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }


    public void showMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void showCreateBasket(View view, DonationPage page) {
        EditText editName = findViewById(R.id.editTextPageSearch);
        EditText editWhyDonate = findViewById(R.id.editTextWhyDonate);

        String name = editName.getText().toString();
        String whyDonate = editWhyDonate.getText().toString();

        if (name == null || name.equals("")) {
            Toast.makeText(getApplicationContext(), "Cannot have an empty name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (whyDonate == null || whyDonate.equals("")) {
            Toast.makeText(getApplicationContext(), "Cannot have an empty description.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CreateBasketActivity.class);

        intent.putExtra(Constants.KEY_DONATION_PAGE_NAME, page.getName());
        CreateBasketActivity.Mode.CREATE_PAGE.putInto(intent);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_page);
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new ClickBackListener(this));
    }
}