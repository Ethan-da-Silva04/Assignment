package com.example.assignment;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PageViewAdapter extends ArrayAdapter<DonationPage> {
    private Context context;
    private List<DonationPage> pages;

    public PageViewAdapter(@NonNull Context context, List<DonationPage> pages) {
        super(context, R.layout.page_list_row, pages);
        this.context = context;
        this.pages = pages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }
        System.out.println("[PageViewAdapter.getView]: I should be called!");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LoginActivity.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.page_list_row, null);

        TextView name = convertView.findViewById(R.id.txt_name);
        TextView number = convertView.findViewById(R.id.txt_number);

        name.setText(pages.get(position).getName());
        number.setText(String.valueOf(position + 1));

        return convertView;
    }
}
