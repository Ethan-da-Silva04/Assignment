package com.example.assignment;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* A class to represent donation pages. WARNING: page contents must be fetched before they are displayed, the server only gives them when they are asked for */
public class DonationPage implements JSONSerializable {
    private static Map<String, DonationPage> pendingPages;

    private int id;
    private String name;
    private String pageContent;
    private Basket basket;
    private int donateeId;

    public DonationPage(String name) {
        this.id = 0;
        this.name = name;
        this.donateeId = UserSession.getId();
        putPage(name, this);
    }

    public DonationPage(int id, int donateeId, String name) {
        this.id = id;
        this.donateeId = donateeId;
        this.name = name;
        // TODO: Basket should also be added here
        putPage(name, this);
        basket = new Basket();
    }

    public DonationPage(String name, String pageContent) {
        this.id = 0;
        this.name = name;
        this.pageContent = pageContent;
        this.donateeId = UserSession.getId();
        putPage(name, this);
    }

    public DonationPage(int id, String name, Basket basket) {
        this.id = id;
        this.name = name;
        this.basket = basket;
        this.donateeId = UserSession.getId();
        pageContent = "";
        putPage(name, this);
    }

    private static void putPage(String name, DonationPage page) {
        pendingPages.put(name, page);
    }

    public static DonationPage getPage(String name) {
        return pendingPages.get(name);
    }


    public static DonationPage fromJSONArray(JSONArray array) {
        try {
            return fromJSONObject((JSONObject) array.get(0));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static DonationPage fromJSONObject(JSONObject object) {
        try {
            int id = (int) object.get("id");
            //Basket basket = Basket.fromJSONArray(object.get("content"));
            return null;
            //return new DonationPage(id, basket);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    /* This function fetches the page content, meant for when a link to a page is actually clicked */
    public void fetchPageContent() throws ServerResponseException {
        if (pageContent != null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("id", id);
            ServerResponse response = WebClient.postJSON("request_page_content.php", object);
            pageContent = (String) response.getData().getJSONObject(0).get("content");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    public Basket getBasket() { return basket; }

    public String getName() {
        return name;
    }

    @Override
    public JSONObject serialize() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("basket", basket.serialize());
        result.put("page_content", pageContent);
        result.put("name", name);
        return result;
    }

    public ServerResponse post() throws ServerResponseException {
        try {
            ServerResponse response = WebClient.postJSON("post_donation_page.php", serialize());
            JSONObject object = response.getData().getJSONObject(0);
            this.id = (int) object.get("id");
            System.out.println(object);
            return response;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<DonationPage> searchBy(String queryName) throws ServerResponseException {
        List<DonationPage> result = new ArrayList<>();
        Map<Integer, DonationPage> pages = new HashMap<>();

        JSONObject queryObject = new JSONObject();
        try {
            queryObject.put("name", queryName);
            ServerResponse response = WebClient.postJSON("search_donation_pages_by_name.php", queryObject);
            JSONArray array = response.getData();

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                int itemId = (int) object.get("id");
                int pageId = (int) object.get("page_id");
                int resourceId = (int) object.get("resource_id");
                int donateeId = (int) object.get("donatee_id");
                String pageName = (String) object.get("name");
                int quantityAsked = (int) object.get("quantity_asked");
                int quantityReceived = (int) object.get("quantity_received");

                if (!pages.containsKey(pageId)) {
                    pages.put(pageId, new DonationPage(pageId, donateeId, pageName));
                }

                Basket basket = pages.get(pageId).getBasket();
                DonationItem item = new DonationItem(itemId, Resource.getFromId(resourceId), quantityAsked, quantityReceived);
                basket.add(item);
            }

            for (Map.Entry<Integer, DonationPage> entry : pages.entrySet()) {
                result.add(entry.getValue());
            }

            System.out.println("IMPORTANT " + response.getData());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        for (DonationPage page : result) {
            System.out.println(page.getName());
        }

        return result;
    }

    public String getContent() {
        return pageContent;
    }

    static {
        pendingPages = new HashMap<>();
    }

}
