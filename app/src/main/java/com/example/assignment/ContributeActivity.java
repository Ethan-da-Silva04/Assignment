package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ContributeActivity extends AppCompatActivity {
    public void showCreateBasket(View view) {
        Intent intent = new Intent(this, CreateBasketActivity.class);
        CreateBasketActivity.Mode.PREPARE_CONTRIBUTION.putInto(intent);
        startActivity(intent);
    }

    public void showSearchPages(View view) {
        Intent intent = new Intent(this, SearchPagesActivity.class);
        SearchPagesActivity.Mode.NAME_SEARCH.putInto(intent);
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