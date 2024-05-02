package com.example.assignment;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebClient {
    private static OkHttpClient client;
    private static String serverURL = "https://lamp.ms.wits.ac.za/home/s2710345/";
    private static final MediaType JSON = MediaType.get("application/json");

    private static String getFullPath(String subPath) {
        if (!subPath.endsWith(".php")) {
            return getFullPath(subPath + ".php");
        }
        System.out.println("[getFullPath]: " + serverURL + subPath);
        return serverURL + subPath;
    }

    public static ServerResponse postJSON(String subPath, String json) {
        RequestBody requestBody = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(getFullPath(subPath))
                .post(requestBody)
                .build();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Response> callable = new Callable<Response>() {
            @Override
            public Response call() {
                try {
                    Response response = client.newCall(request).execute();
                    return response;
                } catch (Exception e) {
                    System.out.println(e.toString());
                }

                return null;
            }
        };

        Future<Response> future = executor.submit(callable);
        // future.get() returns 2 or raises an exception if the thread dies, so safer
        executor.shutdown();
        try {
            return new ServerResponse(future.get());
        } catch (java.util.concurrent.ExecutionException|InterruptedException e) {
            System.out.println(e);
        }

        return null;
    }

    public static ServerResponse postJSON(String subPath, JSONObject object) {
        return postJSON(subPath, object.toString());
    }

    public static ServerResponse postJSON(String subPath, JSONSerializable obj) {
        return postJSON(subPath, obj.serialize());
    }

    static {
        client = new OkHttpClient();
    }
}
