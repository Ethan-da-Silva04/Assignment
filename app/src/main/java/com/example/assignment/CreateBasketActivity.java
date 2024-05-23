package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.view.View;
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

    private String donationPageName = null;

    public void postPage(View view) {
        DonationPage page = DonationPage.getPage(donationPageName);
        page.setBasket(basket);

        try {
            JSONObject serialized = page.serialize();
            System.out.println("THIS ONE IS IMPORTANT " + serialized);
            ServerResponse response = WebClient.postJSON("post_donation_page.php", serialized);
            System.out.println(response.getData());
            Toast.makeText(this, "Page successfully posted!", Toast.LENGTH_SHORT).show();
            page.setId((Integer) response.getData().getJSONObject(0).get("id"));
        } catch (ServerResponseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_basket);

        Intent intent = getIntent();
        if (intent.getStringExtra("DonationPageName") != null) {
            donationPageName = intent.getStringExtra("DonationPageName");
        }




        basket = new Basket();

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

        dropdownItems = Resource.getNames();

        autoCompleteText = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<>(this, R.layout.resource_select_item, dropdownItems);
        autoCompleteText.setAdapter(adapterItems);
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