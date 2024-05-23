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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pages);

        EditText searchBar = findViewById(R.id.editTextPageSearch);
        pages = new ArrayList<>();
        pageAdapter = new PageViewAdapter(getApplicationContext(), pages);


        listView = findViewById(R.id.list_view);
        listView.setAdapter(pageAdapter);
        SearchPagesActivity activity = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, ViewDonationPageActivity.class);
                intent.putExtra("DonationPageName", pages.get(position).getName());
                startActivity(intent);
            }
        });


        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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
            }
        });
    }
}