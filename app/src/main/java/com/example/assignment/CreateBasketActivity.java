package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateBasketActivity extends AppCompatActivity {
    public enum Mode {
        CONTRIBUTE_WITH_NEW_BASKET,
        CONTRIBUTE_WITH_EXISTING,
        PREPARE_CONTRIBUTION,
        PENDING_CONTRIBUTION,
        CREATE_PAGE;

        void putInto(Intent intent) {
            intent.putExtra(Constants.KEY_PAGE_MODE, this.name());
        }

        static Mode retrieveFrom(Intent intent) {
            return Mode.valueOf(intent.getStringExtra(Constants.KEY_PAGE_MODE));
        }
    }

    private String[] dropdownResources;
    private ArrayAdapter<String> resourcesAdapter;

    ListView listView;
    BasketListViewAdapter basketAdapter;

    private AutoCompleteTextView autoCompleteText;
    private Basket basket;

    private DonationPage page;

    private Contribution contribution;

    private Mode mode;

    public void postPage(View view) {
        page.setBasket(basket);

        try {
            JSONObject serialized = page.serialize();
            ServerResponse response = WebClient.postJSON("post_donation_page.php", serialized);
            Toast.makeText(this, "Page successfully posted!", Toast.LENGTH_SHORT).show();
            page.setId((Integer) response.getData().getJSONObject(0).get("id"));
        } catch (ServerResponseException e) {
            ServerExceptionHandler.handle(getApplicationContext(), e);
            return;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Intent intent = new Intent(CreateBasketActivity.this, HomepageActivity.class);
        startActivity(intent);
    }

    public void postContribution(View view) {
        Contribution contribution = (mode == Mode.CONTRIBUTE_WITH_EXISTING) ? this.contribution : Contribution.fromSession(page.getId(), basket);
        contribution.setRecipientPageId(page.getId());
        try {
            JSONObject serialized = contribution.serialize();
            ServerResponse response = WebClient.postJSON("post_contribution.php", serialized);

            Toast.makeText(this, "Contribution successfully posted!", Toast.LENGTH_SHORT).show();

            contribution.setId((Integer) response.getData().getJSONObject(0).get("id"));

            if (mode != Mode.CONTRIBUTE_WITH_EXISTING) {
                startActivity(new Intent(CreateBasketActivity.this, HomepageActivity.class));
                return;
            }

            this.contribution.getBasket().subtract(basket);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ServerResponseException e) {
            ServerExceptionHandler.handle(getApplicationContext(), e);
            return;
        }

        Intent intent = new Intent(CreateBasketActivity.this, SearchPagesActivity.class)
                .putExtra(Constants.KEY_CONTRIBUTION_MAP_ID, contribution.getMapId())
                .putExtra(Constants.KEY_DONATION_PAGE_NAME, page.getName());
        if (mode == Mode.CONTRIBUTE_WITH_EXISTING) {
            SearchPagesActivity.Mode.BASKET_SEARCH.putInto(intent);
        } else {
            SearchPagesActivity.Mode.NAME_SEARCH.putInto(intent);
        }
        startActivity(intent);
    }

    public void prepareContribution(View view) {
        Contribution contribution = new Contribution(basket);
        Intent intent = new Intent(this, SearchPagesActivity.class)
                .putExtra(Constants.KEY_CONTRIBUTION_MAP_ID, contribution.getMapId());
        SearchPagesActivity.Mode.BASKET_SEARCH.putInto(intent);
        startActivity(intent);
    }

    public void acceptContribution(View view) {
        // TODO: test me
        try {
            Contribution modified = new Contribution(contribution.getId(), basket, contribution.getRecipientPageId(), contribution.getPosterId());
            ServerResponse response = WebClient.postJSON("accept_contribution.php", modified.serialize());
            Toast.makeText(this, "Contribution successfully accepted!", Toast.LENGTH_SHORT).show();
            System.out.println(response.getData());
        } catch (ServerResponseException e) {
            ServerExceptionHandler.handle(getApplicationContext(), e);
            return;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void setListViewForPending() {
        Map<Integer, Integer> referenceMaxima = new HashMap<>();

        for (DonationItem item : basket.getItems()) {
            referenceMaxima.put(item.getResourceId(), item.getQuantity());
        }

        basketAdapter.setReferenceMaxima(referenceMaxima);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            int itemId = basket.get(position).getResourceId();
            if (basket.get(position).getQuantity() + 1 > referenceMaxima.getOrDefault(itemId, 0)) {
                Toast.makeText(getApplicationContext(), "Cannot exceed promised quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            basket.add(position, 1);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            basket.removeAll(position);
            return true;
        });

        basket.setListView(listView);

        autoCompleteText.setOnItemClickListener((parent, view, position, id) -> {
            String resourceName = parent.getItemAtPosition(position).toString();
            Resource resource = Resource.getFromName(resourceName);
            int itemId = resource.getId();
            if (!referenceMaxima.containsKey(itemId)) {
                Toast.makeText(getApplicationContext(), "Cannot add an item not within the original basket.", Toast.LENGTH_SHORT).show();
                return;
            }
            basket.add(resource, 1);
            basket.setAdapter(basketAdapter);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_basket);

        Intent intent = getIntent();
        mode = Mode.retrieveFrom(intent);
        if (intent.getStringExtra(Constants.KEY_DONATION_PAGE_NAME) != null) {
            page = DonationPage.getPage(intent.getStringExtra(Constants.KEY_DONATION_PAGE_NAME));
        }


        basket = new Basket();
        Button next = findViewById(R.id.username);
        Button back = findViewById(R.id.back_button);
        back.setOnClickListener(new ClickBackListener(this));

        dropdownResources = Resource.getNames();
        autoCompleteText = findViewById(R.id.auto_complete_txt);
        resourcesAdapter = new ArrayAdapter<>(this, R.layout.resource_select_item, dropdownResources);
        autoCompleteText.setAdapter(resourcesAdapter);

        switch (mode) {
            case CONTRIBUTE_WITH_NEW_BASKET: {
                next.setText("Post Contribution");
                next.setOnClickListener(v -> postContribution(v));
                break;
            }
            case CONTRIBUTE_WITH_EXISTING: {
                next.setText("Post Contribution");
                next.setOnClickListener(v -> postContribution(v));
                contribution = Contribution.getContribution(intent.getIntExtra(Constants.KEY_CONTRIBUTION_MAP_ID, -1));
                basket.getItems().addAll(contribution.getBasket().getItems());
                autoCompleteText.setVisibility(View.INVISIBLE);
                findViewById(R.id.textInputLayout).setVisibility(View.INVISIBLE);
                break;
            }
            case PREPARE_CONTRIBUTION: {
                next.setText("Search Pages");
                next.setOnClickListener(v -> prepareContribution(v));
                break;
            }
            case CREATE_PAGE: {
                next.setText("Post Page");
                next.setOnClickListener(v -> postPage(v));
                break;
            }

            case PENDING_CONTRIBUTION: {
                next.setText("Accept Contribution");
                contribution = Contribution.getContribution(intent.getIntExtra(Constants.KEY_CONTRIBUTION_MAP_ID, -1));
                basket.getItems().addAll(contribution.getBasket().getItems());
                next.setOnClickListener(v -> acceptContribution(v));
                break;
            }
            default: throw new RuntimeException("Failed determining page mode: " + mode);
        }

        listView = findViewById(R.id.list_view);

        basketAdapter = new BasketListViewAdapter(getApplicationContext(), basket);
        listView.setAdapter(basketAdapter);

        if (mode == Mode.PENDING_CONTRIBUTION) {
            setListViewForPending();
            return;
        }

        listView.setOnItemClickListener((parent, view, position, id) -> basket.add(position, 1));

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            basket.removeAll(position);
            return true;
        });

        basket.setListView(listView);

        autoCompleteText.setOnItemClickListener((parent, view, position, id) -> {
            String resourceName = parent.getItemAtPosition(position).toString();
            basket.add(Resource.getFromName(resourceName), 1);
            basket.setAdapter(basketAdapter);
        });
    }
}