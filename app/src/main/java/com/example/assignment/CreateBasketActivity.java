package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateBasketActivity extends AppCompatActivity {
    private String[] dropdownItems;
    private ArrayAdapter<String> adapterItems;

    ListView listView;
    BasketListViewAdapter basketAdapter;

    private AutoCompleteTextView autoCompleteText;
    private Basket basket;

    private DonationPage page;

    private Contribution contribution;

    public void postPage(View view) {
        page.setBasket(basket);

        try {
            JSONObject serialized = page.serialize();
            ServerResponse response = WebClient.postJSON("post_donation_page.php", serialized);
            Toast.makeText(this, "Page successfully posted!", Toast.LENGTH_SHORT).show();
            page.setId((Integer) response.getData().getJSONObject(0).get("id"));
        } catch (ServerResponseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Intent intent = new Intent(CreateBasketActivity.this, HomepageActivity.class);
        startActivity(intent);
    }

    public void postContribution(View view) {
        Contribution contribution = (this.contribution != null) ? this.contribution : Contribution.fromSession(page.getId(), basket);
        contribution.setRecipientPageId(page.getId());
        JSONObject serialized = null;
        try {
            serialized = contribution.serialize();
            ServerResponse response = WebClient.postJSON("post_contribution.php", serialized);
            Toast.makeText(this, "Contribution successfully posted!", Toast.LENGTH_SHORT).show();
            contribution.setId((Integer) response.getData().getJSONObject(0).get("id"));
            if (this.contribution != null) {
                this.contribution.getBasket().subtract(basket);
            }
            startActivity(new Intent(CreateBasketActivity.this, HomepageActivity.class));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ServerResponseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void prepareContribution(View view) {
        Contribution contribution = new Contribution(basket);
        Intent intent = new Intent(this, SearchPagesActivity.class)
                .putExtra("Mode", "BasketSearch")
                .putExtra("ContributionMapId", contribution.getMapId());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_basket);

        Intent intent = getIntent();
        if (intent.getStringExtra("DonationPageName") != null) {
            page = DonationPage.getPage(intent.getStringExtra("DonationPageName"));
        }

        basket = new Basket();
        String mode = intent.getStringExtra("Mode");
        Button next = findViewById(R.id.username);
        Button back = findViewById(R.id.back_button);
        final CreateBasketActivity activity = this;
        back.setOnClickListener(new ClickBackListener(this));

        dropdownItems = Resource.getNames();
        autoCompleteText = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<>(this, R.layout.resource_select_item, dropdownItems);
        autoCompleteText.setAdapter(adapterItems);

        if (mode.equals("ContributeWithNewBasket")) {
            next.setText("Post Contribution");
            next.setOnClickListener(v -> postContribution(v));
        }  else if (mode.equals("ContributeWithExisting")) {
            next.setText("Post Contribution");
            next.setOnClickListener(v -> postContribution(v));
            contribution = Contribution.getContribution(intent.getIntExtra("ContributionMapId", -1));
            basket.getItems().addAll(contribution.getBasket().getItems());
            autoCompleteText.setVisibility(View.INVISIBLE);
        } else if (mode.equals("PrepareContribution")) {
            next.setText("Search Pages");
            next.setOnClickListener(v -> prepareContribution(v));
        } else if (mode.equals("CreatePage")) {
            next.setText("Post Page");
            next.setOnClickListener(v -> postPage(v));
        } else {
            Toast.makeText(getApplicationContext(), "Failed determining mode.", Toast.LENGTH_SHORT).show();
        }

        listView = findViewById(R.id.list_view);
        basketAdapter = new BasketListViewAdapter(getApplicationContext(), basket);
        listView.setAdapter(basketAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                basket.add(position, 1);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                basket.removeAll(position);
                return true;
            }

        });

        basket.setListView(listView);
        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String resourceName = parent.getItemAtPosition(position).toString();
                basket.add(Resource.getFromName(resourceName), 1);
                basket.setAdapter(basketAdapter);
            }
        });
    }
}