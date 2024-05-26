package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class HomepageActivity extends AppCompatActivity {
    private List<User> searchedUsers;
    private UsersAdapter usersAdapter;

    public void showCreatePage(View view) {
        Intent intent = new Intent(this, CreatePageActivity.class);
        startActivity(intent);
    }

    public void showSearchPages(View view) {
        Intent intent = new Intent(this, SearchPagesActivity.class);
        startActivity(intent);
    }

    public void showContribute(View view) {
        startActivity(new Intent(this, ContributeActivity.class));
    }

    public void showAccountPage(View view) {
        Intent intent = new Intent(this, AccountActivity.class)
                .putExtra(Constants.KEY_USER_ID, UserSession.getId());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: Add notification viewer
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        ListView listView = findViewById(R.id.list_view);
        try {
            searchedUsers = User.getTop();
        } catch (ServerResponseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        usersAdapter = new UsersAdapter(getApplicationContext(), searchedUsers);
        listView.setAdapter(usersAdapter);
        EditText searchBar = findViewById(R.id.editTextSearchUser);

        Button accountButton = findViewById(R.id.username);
        accountButton.setText(UserSession.getData().getUsername());

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_NULL) {
                    return false;
                }

                try {
                    String text = v.getText().toString();
                    searchedUsers = text.isEmpty() ? User.getTop() : User.searchBy(text);
                    usersAdapter = new UsersAdapter(getApplicationContext(), searchedUsers);
                    listView.setAdapter(usersAdapter);
                } catch (ServerResponseException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(HomepageActivity.this, AccountActivity.class)
                    .putExtra(Constants.KEY_USER_ID, searchedUsers.get(position).getId());
            startActivity(intent);
        });

        Button notificationButton = findViewById(R.id.notification_button);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Contribution> contributions = PendingContributionReceiver.get().dataToList();
                if (PendingContributionReceiver.get().isEmpty()) {
                    Toast.makeText(HomepageActivity.this, "No new pending contributions", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(HomepageActivity.this, PendingListActivity.class);
                startActivity(intent);
            }
        });
    }
}