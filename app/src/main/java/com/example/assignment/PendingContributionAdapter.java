package com.example.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PendingContributionAdapter extends ArrayAdapter<Contribution> {
    private Context context;
    private List<Contribution> contributions;
    private LayoutInflater inflater;

    public PendingContributionAdapter(@NonNull Context context, List<Contribution> contributions) {
        super(context, R.layout.contribution_history_item, contributions);
        this.contributions = contributions;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.contribution_history_item, parent, false);
        TextView number = convertView.findViewById(R.id.txt_number);
        number.setText(String.valueOf(position + 1));

        Contribution contribution = contributions.get(position);
        TextView name = convertView.findViewById(R.id.txt_name);
        String recipientPageName = DonationPage.getPageFromId(contribution.getRecipientPageId()).getName();
        name.setText("Contribution to " + recipientPageName + " on " + contribution.getCreatedAt());

        StringBuilder itemString = new StringBuilder();

        for (DonationItem entry : contribution.getBasket().getItems()) {
            StringBuilder row = new StringBuilder();
            row.append("\t-\t");
            row.append("NOT ACCEPTED: ");
            row.append(entry.getQuantity() + " " + entry.getName());
            itemString.append(row);
            itemString.append('\n');
        }

        TextView itemView = convertView.findViewById(R.id.txt_items);
        itemView.setMaxLines(contribution.getBasket().size());
        itemView.setText(itemString);

        return convertView;
    }
}
