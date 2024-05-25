package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchPagesActivity extends AppCompatActivity {
    private ListView listView;
    private PageViewAdapter pageAdapter;
    private List<DonationPage> pages;

    private Basket basket;
    private int contributionMapId;

    public void showHome(View view) {
        startActivity(new Intent(this, HomepageActivity.class));
    }

    public TextView.OnEditorActionListener getNameSearcher() {
        return (v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_NULL) {
                return false;
            }

            try {
                pages = DonationPage.searchBy(v.getText().toString());
                pageAdapter = new PageViewAdapter(getApplicationContext(), pages);
                listView.setAdapter(pageAdapter);
            } catch (ServerResponseException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return true;
        };
    }

    public TextView.OnEditorActionListener getBasketSearcher() {
        return (v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_NULL) {
                return false;
            }

            String text = v.getText().toString();
            try {
                pages = (basket != null && text.isEmpty()) ? DonationPage.searchBy(basket) : DonationPage.searchBy(text);
                pageAdapter = new PageViewAdapter(getApplicationContext(), pages);
                listView.setAdapter(pageAdapter);
            } catch (ServerResponseException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return true;
        };
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: The different modes of activities should be handled better than this.
        // TODO: This should have multiple modes in it to handle the different cases of either from the basket, or by text, currently just by text is handled only, which is wrong
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pages);

        String mode = getIntent().getStringExtra("Mode");
        pages = new ArrayList<>();
        EditText searchBar = findViewById(R.id.editTextPageSearch);

        if (mode.equals("BasketSearch")) {
            contributionMapId = getIntent().getIntExtra("ContributionMapId", -1);
            Contribution contribution = Contribution.getContribution(contributionMapId);
            basket = contribution.getBasket();
            searchBar.setOnEditorActionListener(getBasketSearcher());
            try {
                pages = DonationPage.searchBy(basket);
            } catch (ServerResponseException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (mode.equals("NameSearch")) {
            searchBar.setOnEditorActionListener(getNameSearcher());
        } else {
            throw new RuntimeException("Big failure");
        }

        pageAdapter = new PageViewAdapter(getApplicationContext(), pages);
        listView = findViewById(R.id.list_view);
        listView.setAdapter(pageAdapter);
        SearchPagesActivity activity = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: This should send extra stuff if in basket search mode.
                Intent intent = new Intent(activity, ViewDonationPageActivity.class);
                intent.putExtra("DonationPageName", pages.get(position).getName());
                if (mode.equals("BasketSearch")) {
                    Contribution contribution = Contribution.getContribution(getIntent().getIntExtra("ContributionMapId", -1));
                    intent.putExtra("ContributionMapId", contributionMapId);
                }
                startActivity(intent);
            }
        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new ClickBackListener(this));
    }
}