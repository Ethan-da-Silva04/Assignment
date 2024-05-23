package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_NULL) {
                    return false;
                }

                try {
                    searchedUsers = User.searchBy(v.getText().toString());
                    usersAdapter = new UsersAdapter(getApplicationContext(), searchedUsers);
                    listView.setAdapter(usersAdapter);
                } catch (ServerResponseException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}