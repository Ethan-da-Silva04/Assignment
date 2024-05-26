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

public class ViewDonationPageActivity extends AppCompatActivity {
    private DonationPage page;

    public void showCreateBasket(View view) {
        Intent intent = new Intent(this, CreateBasketActivity.class);
        Intent previousIntent = getIntent();
        int mapId;
        if ((mapId = previousIntent.getIntExtra(Constants.KEY_CONTRIBUTION_MAP_ID, -1)) != -1) {
            intent.putExtra(Constants.KEY_CONTRIBUTION_MAP_ID, mapId);
            CreateBasketActivity.Mode.CONTRIBUTE_WITH_EXISTING.putInto(intent);
        } else {
            CreateBasketActivity.Mode.CONTRIBUTE_WITH_NEW_BASKET.putInto(intent);
        }
        intent.putExtra(Constants.KEY_DONATION_PAGE_NAME, page.getName());
        startActivity(intent);
    }

    public void showSearchPages(View view) {
        Intent intent = new Intent(this, SearchPagesActivity.class);
        startActivity(intent);
    }

    public void showAccountPage(View view) {
        int userId = page.getDonateeId();
        Intent intent = new Intent(this, AccountActivity.class)
                .putExtra(Constants.KEY_USER_ID, userId);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: the basket that is supposed to display is not displaying
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donation_page);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new ClickBackListener(this));

        Intent intent = getIntent();
        if (intent.getStringExtra(Constants.KEY_DONATION_PAGE_NAME) == null) {
            throw new RuntimeException("Missing page name");
        }

        String name = intent.getStringExtra(Constants.KEY_DONATION_PAGE_NAME);
        page = DonationPage.getPage(name);
        if (page == null) {
            return;
        }

        try {
            page.fetchPageContent();
        } catch (ServerResponseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        WebView webView = findViewById(R.id.webView);

        webView.loadData(page.getContent(), "text/html", "UTF-8");

        TextView username = findViewById(R.id.username);
        username.setText(User.getFromId(page.getDonateeId()).getUsername());

        ListView listView = findViewById(R.id.listView);
        PageProgressAdapter adapter = new PageProgressAdapter(getApplicationContext(), page);
        System.out.println(page.getBasket().getItems().size());
        listView.setAdapter(adapter);
    }
}