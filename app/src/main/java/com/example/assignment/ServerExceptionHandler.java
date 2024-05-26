package com.example.assignment;

import android.content.Context;
import android.widget.Toast;

public class ServerExceptionHandler {
    public static void handle(Context context, ServerResponseException e) {
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
