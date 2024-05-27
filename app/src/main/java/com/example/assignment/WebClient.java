package com.example.assignment;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import javax.net.ssl.TrustManager;

import okhttp3.Cookie;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLSocketFactory;
import java.lang.RuntimeException;


public class WebClient {
    private static final OkHttpClient client;
    private static final String serverURL = (Constants.DEBUG) ? "http://192.168.1.105:8000/" : "https://lamp.ms.wits.ac.za/home/s2710345/";

    private static final MediaType JSON = MediaType.get("application/json");

    private WebClient() {}

    private static Request.Builder addCookies(Request.Builder builder, @NonNull String subpath) {
        List<Cookie> cookies = client.cookieJar().loadForRequest(HttpUrl.parse(getFullPath(subpath)));

        for (Cookie cookie : cookies) {
            builder.addHeader("Cookie", cookie.toString());
        }

        return builder;
    }

    private static Request generatePostRequest(String subPath, String json) {
        RequestBody requestBody = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(getFullPath(subPath))
                .post(requestBody);

        return addCookies(builder, subPath).build();
    }

    private static Request generateGetRequest(String subPath) {
        Request.Builder builder = new Request.Builder()
                .url(getFullPath(subPath));

        return addCookies(builder, subPath).build();
    }

    private static ServerResponse makeServerRequest(Request request) throws ServerResponseException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<ServerResponse> callable = () -> {
            Response response = client.newCall(request).execute();
            return new ServerResponse(response);
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
            // I don't know why, but JavaNetCookieJar is unavailable, so I resorted to creating a rudimentary garbage CookieJar so that the session could be managed.
            CookieJar cookieJar = new CustomCookieJar();

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .cookieJar(cookieJar)
                    .hostnameVerifier((hostname, session) -> true);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static UserSession signup(String username, String password, String biography, String phoneNumber) throws ServerResponseException, JSONException {
        JSONObject object = new JSONObject()
                .put("username", username)
                .put("password", password)
                .put("biography", biography)
                .put("phone_number", phoneNumber);

        return UserSession.initialize(WebClient.postJSON("register", object).getData());
    }

    public static UserSession login(String username, String password) throws ServerResponseException, JSONException {
        JSONObject object = new JSONObject();
        object.put("username", username);
        object.put("password", password);

        return UserSession.initialize(WebClient.postJSON("login", object).getData());
    }

    static {
        client = createUnsafeClient();
    }
}
