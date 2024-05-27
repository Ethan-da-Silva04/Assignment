package com.example.assignment;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

public class ServerExceptionHandler {
    public static void handle(Context context, ServerResponseException e) {
//        new AlertDialog.Builder(context).setMessage(e.getMessage()).show();
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
