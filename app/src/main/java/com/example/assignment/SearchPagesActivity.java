package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchPagesActivity extends AppCompatActivity {
    public enum Mode {
        NAME_SEARCH,
        BASKET_SEARCH;

        void putInto(Intent intent) {
            intent.putExtra(Constants.KEY_PAGE_MODE, this.name());
        }

        static Mode retrieveFrom(Intent intent) {
            return Mode.valueOf(intent.getStringExtra(Constants.KEY_PAGE_MODE));
        }
    }

    private ListView listView;
    private PageViewAdapter pageAdapter;
    private List<DonationPage> pages;

    private Basket basket;
    private int contributionMapId;
    private Mode mode;

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
                pages = (mode == Mode.BASKET_SEARCH && text.isEmpty()) ? DonationPage.searchBy(basket) : DonationPage.searchBy(text);
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pages);

        mode = Mode.retrieveFrom(getIntent());

        pages = new ArrayList<>();
        EditText searchBar = findViewById(R.id.editTextPageSearch);
        Contribution contribution;

        switch (mode) {
            case BASKET_SEARCH: {
                contributionMapId = getIntent().getIntExtra(Constants.KEY_CONTRIBUTION_MAP_ID, -1);
                contribution = Contribution.getContribution(contributionMapId);
                basket = contribution.getBasket();
                searchBar.setOnEditorActionListener(getBasketSearcher());
                break;
            }
            case NAME_SEARCH: {
                searchBar.setOnEditorActionListener(getNameSearcher());
                break;
            }
            default: throw new RuntimeException("Failed determining mode");
        }

        pageAdapter = new PageViewAdapter(getApplicationContext(), pages);
        listView = findViewById(R.id.list_view);
        listView.setAdapter(pageAdapter);
        SearchPagesActivity activity = this;

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(activity, ViewDonationPageActivity.class);
            intent.putExtra(Constants.KEY_DONATION_PAGE_NAME, pages.get(position).getName());
            if (mode == Mode.BASKET_SEARCH) {
                intent.putExtra(Constants.KEY_CONTRIBUTION_MAP_ID, contributionMapId);
            }
            startActivity(intent);
        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new ClickBackListener(this));
    }
}