package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class PendingListActivity extends AppCompatActivity {
    private List<Contribution> contributions = new ArrayList<>();
    private PendingContributionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_list);

        Button back = findViewById(R.id.back_button);
        back.setOnClickListener(new ClickBackListener(this));

        contributions = PendingContributionReceiver.get().dataToList();
        adapter = new PendingContributionAdapter(getApplicationContext(), contributions);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Contribution contribution = contributions.get(position);
            Intent intent = new Intent(PendingListActivity.this, CreateBasketActivity.class)
                    .putExtra(Constants.KEY_CONTRIBUTION_MAP_ID, contribution.getMapId());
            CreateBasketActivity.Mode.PENDING_CONTRIBUTION.putInto(intent);
            startActivity(intent);
        });
    }
}