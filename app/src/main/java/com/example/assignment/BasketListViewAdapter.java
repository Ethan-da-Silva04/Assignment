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

public class BasketListViewAdapter extends ArrayAdapter<DonationItem> {
    private Basket basket;
    private Context context;

    public BasketListViewAdapter(@NonNull Context context, Basket basket) {
        super(context, R.layout.basket_list_row, basket.getItems());
        this.context = context;
        this.basket = basket;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }

        int currentQuantity = basket.get(position).getQuantity();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LoginActivity.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.basket_list_row, null);

        TextView number = convertView.findViewById(R.id.txt_number);
        number.setText(String.valueOf(position + 1) + ".");

        EditText editAmount = convertView.findViewById(R.id.editTextAmount);
        editAmount.setText(String.valueOf(currentQuantity));

        TextView name = convertView.findViewById((R.id.txt_name));
        name.setText(basket.get(position).getName());

        Button addButton = convertView.findViewById(R.id.add_button);
        BasketListViewAdapter adapter = this;

        addButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 basket.add(position, 1);
                 editAmount.setText(String.valueOf(currentQuantity + 1));
                 adapter.notifyDataSetChanged();
                 basket.setAdapter(adapter);
             }
        });

        Button removeButton = convertView.findViewById(R.id.remove_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.remove(position, 1);
                editAmount.setText(String.valueOf(currentQuantity - 1));
                adapter.notifyDataSetChanged();
                basket.setAdapter(adapter);
            }
        });


        editAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_NULL) {
                    return false;
                }

                int amount = Integer.parseInt(v.getText().toString());
                basket.setQuantity(position, amount);
                basket.setAdapter(adapter);
                return true;
            }
        });

        return convertView;
    }
}
