package com.example.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class ProgressBarAdapter extends ArrayAdapter<Integer> {

    public ProgressBarAdapter(Context context, ArrayList<Integer> progressList) {
        super(context, 0, progressList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            //listItemView = LayoutInflater.from(getContext()).inflate(
              //   R.layout.list_item_progress, parent, false);
        }

        ProgressBar progressBar = listItemView.findViewById(R.id.progressBar);
        Integer progress = getItem(position);
        if (progress != null) {
            progressBar.setProgress(progress);
        }

        return listItemView;
    }
}
