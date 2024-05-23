package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

public class ViewDonationPageActivity extends AppCompatActivity {
    private DonationPage page;

    public void showCreateBasket(View view) {
        Intent intent = new Intent(this, CreateBasketActivity.class);
        intent.putExtra("Mode", "Contribute");
        intent.putExtra("DonationPageName", page.getName());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donation_page);

        Intent intent = getIntent();
        if (intent.getStringExtra("DonationPageName") != null) {
            String name = intent.getStringExtra("DonationPageName");
            page = DonationPage.getPage(name);
            try {
                page.fetchPageContent();
            } catch (ServerResponseException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        ScrollView scrollView = findViewById(R.id.scrollView);
        WebView webView = scrollView.findViewById(R.id.webView);

        webView.loadData(page.getContent(), "text/html", "UTF-8");

        ListView listView = scrollView.findViewById(R.id.listView);
        PageProgressAdapter adapter = new PageProgressAdapter(getApplicationContext(), page);
        listView.setAdapter(adapter);
    }
}