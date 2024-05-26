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
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasketListViewAdapter extends ArrayAdapter<DonationItem> {
    private Basket basket;
    private Context context;
    private Map<Integer, Integer> referenceMaxima = null;

    public BasketListViewAdapter(@NonNull Context context, Basket basket) {
        super(context, R.layout.basket_list_row, basket.getItems());
        this.context = context;
        this.basket = basket;
    }

    public void setReferenceMaxima(Map<Integer, Integer> referenceMaxima) {
        this.referenceMaxima = referenceMaxima;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }

        int currentQuantity = basket.get(position).getQuantity();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(CreateBasketActivity.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.basket_list_row, null);

        TextView number = convertView.findViewById(R.id.txt_number);
        number.setText(String.valueOf(position + 1) + ".");

        EditText editAmount = convertView.findViewById(R.id.editTextAmount);
        editAmount.setText(String.valueOf(currentQuantity));

        TextView name = convertView.findViewById((R.id.txt_name));
        name.setText(basket.get(position).getName());

        Button addButton = convertView.findViewById(R.id.add_button);
        BasketListViewAdapter adapter = this;

        addButton.setOnClickListener(v -> {
            int itemId = basket.get(position).getResourceId();
            if (referenceMaxima != null && basket.get(position).getQuantity() + 1 > referenceMaxima.getOrDefault(itemId, 0)) {
                Toast.makeText(context, "Cannot exceed promised quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            basket.add(position, 1);
            editAmount.setText(String.valueOf(currentQuantity + 1));
            adapter.notifyDataSetChanged();
            basket.setAdapter(adapter);
        });

        Button removeButton = convertView.findViewById(R.id.remove_button);
        removeButton.setOnClickListener(v -> {
            basket.remove(position, 1);
            editAmount.setText(String.valueOf(currentQuantity - 1));
            adapter.notifyDataSetChanged();
            basket.setAdapter(adapter);
        });


        editAmount.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_NULL) {
                return false;
            }

            int amount = Integer.parseInt(v.getText().toString());
            int itemId = basket.get(position).getResourceId();
            if (referenceMaxima != null) {
                // items cannot be added if they are not contained within the referenceMaxima, so only need normal get
                if (amount > referenceMaxima.get(itemId)) {
                    Toast.makeText(context, "Cannot exceed promised quantity", Toast.LENGTH_SHORT).show();
                }
                basket.setQuantity(position, Math.min(referenceMaxima.get(itemId), amount));
                basket.setAdapter(adapter);
                return true;
            }
            basket.setQuantity(position, amount);
            basket.setAdapter(adapter);
            return true;
        });

        return convertView;
    }
}
