package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreatePageActivity extends AppCompatActivity {
    public void partialConstructPage(View view) {
        StringBuilder builder = new StringBuilder();
        EditText editName = findViewById(R.id.editTextPageSearch);
        EditText editWhyDonate = findViewById(R.id.editTextWhyDonate);

        String name = editName.getText().toString();
        String whyDonate = editWhyDonate.getText().toString();
        builder.append(String.format(
                "<!doctype html>" +
                "<html>" +
                "<head>" +
                        "<title>%s</title>" +
                "</head>" +
                "<body>" +
                    "<h1>%s</h1>" +
                    "<p> %s </p>" +
                "</body>" +
                "</html>"
        ,name, name, whyDonate));
        String pageContent = builder.toString();

        DonationPage page = new DonationPage(name, pageContent);
        showCreateBasket(view, page);
    }


    public void showMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void showCreateBasket(View view, DonationPage page) {
        Intent intent = new Intent(this, CreateBasketActivity.class);

        intent.putExtra("DonationPageName", page.getName());

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_page);
    }
}