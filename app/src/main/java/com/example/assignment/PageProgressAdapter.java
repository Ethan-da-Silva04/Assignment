package com.example.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class PageProgressAdapter extends ArrayAdapter<DonationItem> {
    private List<DonationItem> donationItems;

    public PageProgressAdapter(Context context, DonationPage page) {
        super(context, 0, page.getBasket().getItems());
        donationItems = page.getBasket().getItems();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.page_progress_row, parent, false);
        }

        DonationItem item = donationItems.get(position);

        TextView number = listItemView.findViewById(R.id.txt_number);
        number.setText(String.valueOf(position + 1));

        TextView name = listItemView.findViewById(R.id.txt_name);
        name.setText(item.getName());

        ProgressBar progressBar = listItemView.findViewById(R.id.progressBar);
        progressBar.setProgress(item.getQuantityReceived() / item.getQuantityAsked());

        return listItemView;
    }
}
