package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ContributeActivity extends AppCompatActivity {
    public void showCreateBasket(View view) {
        System.out.println("Where is my super suit? :3");
        Intent intent = new Intent(this, CreateBasketActivity.class);
        intent.putExtra("Mode", "PrepareContribution");
        startActivity(intent);
    }

    public void showSearchPages(View view) {
        Intent intent = new Intent(this, SearchPagesActivity.class);
        intent.putExtra("Mode", "NameSearch");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new ClickBackListener(this));
    }
}