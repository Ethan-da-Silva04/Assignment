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
    private Context context;
    private LayoutInflater inflater;

    public PageProgressAdapter(Context context, DonationPage page) {
        super(context, 0, page.getBasket().getItems());
        donationItems = page.getBasket().getItems();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        for (DonationItem item : donationItems) {
            System.out.println("THESE ARE THE ITEMS :3 :" + item);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.page_progress_row, parent, false);
        DonationItem item = donationItems.get(position);

        TextView number = convertView.findViewById(R.id.txt_number);
        number.setText(String.valueOf(position + 1));

        TextView name = convertView.findViewById(R.id.txt_name);
        name.setText(item.getName() + " " + String.valueOf(item.getQuantityReceived()) + "/" + String.valueOf(item.getQuantityAsked()));

        ProgressBar progressBar = convertView.findViewById(R.id.progressBar);
        progressBar.setProgress((item.getQuantityReceived() / item.getQuantityAsked()) * 100);

        return convertView;
    }
}
