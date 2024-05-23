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

import java.util.List;

public class UsersAdapter extends ArrayAdapter<User> {
    private List<User> users;
    private Context context;

    public UsersAdapter(@NonNull Context context, List<User> users) {
        super(context, R.layout.basket_list_row, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LoginActivity.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.user_list_row, null);

        TextView number = convertView.findViewById(R.id.txt_number);
        number.setText(String.valueOf(position + 1) + ".");

        TextView name = convertView.findViewById((R.id.txt_name));
        name.setText(users.get(position).getUsername());

        TextView rank = convertView.findViewById(R.id.txt_rank);
        rank.setText("#" + String.valueOf(users.get(position).getRank()));

        return convertView;
    }
}
