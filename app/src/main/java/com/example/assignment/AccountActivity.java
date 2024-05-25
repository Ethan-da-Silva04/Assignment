package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AccountActivity extends AppCompatActivity {
    public void logout(View view) {
        try {
            WebClient.get("logout.php");
            UserSession.logout();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } catch (ServerResponseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        Intent intent = getIntent();
        int userId = intent.getIntExtra("user_id", 0);
        User user = User.getFromId(userId);
        TextView rank = findViewById(R.id.txt_rank);
        rank.setText("Rank: " + String.valueOf(user.getRank()));

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new ClickBackListener(this));

        EditText editUsername = findViewById(R.id.txt_username);
        EditText editBiography = findViewById(R.id.txt_biography);
        editUsername.setText(user.getUsername());
        editBiography.setText(user.getBiography());

        Button editUsernameButton = findViewById(R.id.editUsername_button);
        Button editBiographyButton = findViewById(R.id.editBiography_button);

        editUsername.setFocusable(false);
        editUsername.setFocusableInTouchMode(false);
        editBiography.setFocusable(false);
        editBiography.setFocusableInTouchMode(false);

        if (userId != UserSession.getId()) {
            editUsernameButton.setVisibility(View.INVISIBLE);
            editBiographyButton.setVisibility(View.INVISIBLE);
            Button logoutButton = findViewById(R.id.logout_button);
            logoutButton.setVisibility(View.INVISIBLE);

            TextView title = findViewById(R.id.page_title);
            title.setText("View Account");
        } else {
            Button reportButton = findViewById(R.id.report_button);
            reportButton.setVisibility(View.INVISIBLE);

            editUsernameButton.setOnClickListener(v -> {
                editUsername.setFocusable(true);
                editUsername.setFocusableInTouchMode(true);
                editUsername.requestFocus();
            });

            editBiographyButton.setOnClickListener(v -> {
                editBiography.setFocusable(true);
                editBiography.setFocusableInTouchMode(true);
                editBiography.requestFocus();
            });

            editUsername.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_NULL) {
                    return false;
                }

                String text = v.getText().toString();

                if (text.equals(UserSession.getData().getUsername())) {
                    editUsername.setFocusable(false);
                    editUsername.setFocusableInTouchMode(false);
                    return true;
                }

                try {
                    JSONObject postObject = new JSONObject()
                            .put("new_username", text)
                            .put("id", UserSession.getId());
                    ServerResponse response = WebClient.postJSON("update_username.php", postObject);
                    UserSession.setUsername(text);
                    Toast.makeText(AccountActivity.this, "Successfully changed username!", Toast.LENGTH_SHORT).show();
                } catch (ServerResponseException e) {
                    Toast.makeText(AccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return false;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                editUsername.setFocusable(false);
                editUsername.setFocusableInTouchMode(false);

                return true;
            });

            editBiography.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_NULL) {
                    return false;
                }

                String text = v.getText().toString();

                try {
                    JSONObject postObject = new JSONObject()
                            .put("new_biography", text)
                            .put("id", UserSession.getId());
                    ServerResponse response = WebClient.postJSON("update_user_biography.php", postObject);
                    UserSession.setBiography(text);
                    Toast.makeText(AccountActivity.this, "Successfully changed biography!", Toast.LENGTH_SHORT).show();
                } catch (ServerResponseException e) {
                    Toast.makeText(AccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return false;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                editBiography.setFocusable(false);
                editBiography.setFocusableInTouchMode(false);

                return true;
            });
        }

        JSONObject postObject = null;
        try {
            postObject = new JSONObject().put("id", userId);
            ServerResponse response = WebClient.postJSON("request_history.php", postObject);
            JSONObject responseData = response.getData().getJSONObject(0);
            System.out.println("VERY IMPORTANT" + responseData);
            List<Contribution> contributionList = Contribution.listFromJSONArray(responseData.getJSONArray("contributions"));
            List<DonationPage> pageList = DonationPage.listFromJSONArray(responseData.getJSONArray("pages"));
            // TODO: Test this, and merge them together to put into list view
            // TODO: Create an interface that will go into an adapter and define how the display for the donation pages and stuff will go
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ServerResponseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
