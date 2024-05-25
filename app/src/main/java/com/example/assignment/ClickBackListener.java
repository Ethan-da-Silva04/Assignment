package com.example.assignment;

import android.view.View;

public class ClickBackListener implements View.OnClickListener {
    private android.app.Activity activity;

    public ClickBackListener(android.app.Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        activity.finish();
    }
}
