package com.example.assignment;

import android.view.PixelCopy;

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

    private static Request generatePostRequest(String subPath, String json) {
        RequestBody requestBody = RequestBody.create(json, JSON);

        return new Request.Builder()
                .url(getFullPath(subPath))
                .post(requestBody)
                .build();
    }

    private static Request generateGetRequest(String subPath) {
        return new Request.Builder()
                .url(getFullPath(subPath))
                .build();
    }

    private static ServerResponse makeServerRequest(Request request) throws ServerResponseException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<ServerResponse> callable = () -> {
            try {
                Response response = client.newCall(request).execute();
                return new ServerResponse(response);
            } catch (IOException e) {
                System.out.println(e);
            }

            return null;
        };

        Future<ServerResponse> future = executor.submit(callable);

        executor.shutdown();
        try {
            return future.get();
        } catch (java.util.concurrent.ExecutionException|InterruptedException e) {
            if (e.getCause() instanceof ServerResponseException) {
                throw (ServerResponseException) e.getCause();
            }
            System.out.println(e);
        }

        return null;
    }

    private static String getFullPath(String subPath) {
        if (!subPath.endsWith(".php")) {
            return getFullPath(subPath + ".php");
        }
        return serverURL + subPath;
    }

    public static ServerResponse postJSON(String subPath, String json) throws ServerResponseException {
        return makeServerRequest(generatePostRequest(subPath, json));
    }

    public static ServerResponse postJSON(String subPath, JSONObject object) throws ServerResponseException{
        return postJSON(subPath, object.toString());
    }

    public static ServerResponse postJSON(String subPath, JSONSerializable obj) throws ServerResponseException {
        return postJSON(subPath, obj.serialize());
    }

    public static ServerResponse get(String subPath) throws ServerResponseException {
        return makeServerRequest(generateGetRequest(subPath));
    }

    static {
        client = new OkHttpClient();
    }
}
