package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class ViewDonationPageActivity extends AppCompatActivity {
    private DonationPage page;

    public void showCreateBasket(View view) {
        Intent intent = new Intent(this, CreateBasketActivity.class);
        Intent previousIntent = getIntent();
        int mapId;
        if ((mapId = previousIntent.getIntExtra("ContributionMapId", -1)) != -1) {
            intent.putExtra("ContributionMapId", mapId);
            intent.putExtra("Mode", "ContributeWithExisting");
        } else {
            intent.putExtra("Mode", "ContributeWithNewBasket");
        }
        intent.putExtra("DonationPageName", page.getName());
        startActivity(intent);
    }

    public void showSearchPages(View view) {
        Intent intent = new Intent(this, SearchPagesActivity.class);
        startActivity(intent);
    }

    public void showAccountPage(View view) {
        int userId = page.getDonateeId();
        Intent intent = new Intent(this, AccountActivity.class)
                .putExtra("user_id", userId);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donation_page);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new ClickBackListener(this));

        Intent intent = getIntent();
        if (intent.getStringExtra("DonationPageName") != null) {
            String name = intent.getStringExtra("DonationPageName");
            page = DonationPage.getPage(name);

            // TODO: figure what is even happening with this.
            if (page == null) {
                return;
            }

            for (Map.Entry<String, DonationPage> entry : DonationPage.getEntries()) {
                System.out.println(entry.getValue());
            }

            try {
                page.fetchPageContent();
            } catch (ServerResponseException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        ScrollView scrollView = findViewById(R.id.scrollView);
        WebView webView = scrollView.findViewById(R.id.webView);

        webView.loadData(page.getContent(), "text/html", "UTF-8");

        TextView username = findViewById(R.id.username);
        username.setText(User.getFromId(page.getDonateeId()).getUsername());

        ListView listView = scrollView.findViewById(R.id.listView);
        PageProgressAdapter adapter = new PageProgressAdapter(getApplicationContext(), page);
        listView.setAdapter(adapter);
    }
}