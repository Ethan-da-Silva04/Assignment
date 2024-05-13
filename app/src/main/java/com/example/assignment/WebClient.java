package com.example.assignment;

import android.view.PixelCopy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import javax.net.ssl.TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLSocketFactory;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.KeyManagementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.lang.RuntimeException;
import org.json.JSONException;
import org.json.JSONObject;


public class WebClient {
    private static OkHttpClient client;
    private static String serverURL = "http://192.168.1.105:8000/";
    private static final MediaType JSON = MediaType.get("application/json");

    private WebClient() {}

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
                throw e;
                //System.out.println(e);
            }
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

        throw new ServerResponseException("Failed to connect to server", 500);
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

    public static ServerResponse postJSON(String subPath, JSONSerializable obj) throws ServerResponseException, JSONException {
        return postJSON(subPath, obj.serialize());
    }

    public static ServerResponse get(String subPath) throws ServerResponseException {
        return makeServerRequest(generateGetRequest(subPath));
    }

    private static OkHttpClient createUnsafeClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static UserSession signup(String username, String password, String biography, String phoneNumber) throws ServerResponseException, JSONException {
        JSONObject object = new JSONObject();
        object.put("username", username);
        object.put("password", password);
        object.put("biography", biography);
        object.put("phoneNumber", phoneNumber);

        return UserSession.initialize(WebClient.postJSON("login", object).getData());
    }

    public static UserSession login(String username, String password) throws ServerResponseException, JSONException {
        JSONObject object = new JSONObject();
        object.put("username", username);
        object.put("password", password);

        return UserSession.initialize(WebClient.postJSON("login", object).getData());
    }

    static {

        client = createUnsafeClient();
        System.out.println("Test!");
    }
}
