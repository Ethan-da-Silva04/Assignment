package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* A class to represent donation pages. WARNING: page contents must be fetched before they are displayed, the server only gives them when they are asked for */
public class DonationPage implements JSONSerializable {
    private static final Map<String, DonationPage> pendingPages;

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

    public void setId(int id) {
        this.id = id;
    }

    /* This function fetches the page content, meant for when a link to a page is actually clicked */
    public void fetchPageContent() throws ServerResponseException {
        if (pageContent != null) {
            return;
        }

        try {
            JSONObject object = new JSONObject()
                    .put("id", id);
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

    public String getName() { return name; }

    @Override
    public JSONObject serialize() throws JSONException {
        return new JSONObject()
                .put("id", id)
                .put("basket", basket.serialize())
                .put("page_content", pageContent)
                .put("name", name);
    }

    public ServerResponse post() throws ServerResponseException {
        try {
            ServerResponse response = WebClient.postJSON("post_donation_page.php", serialize());
            JSONObject object = response.getData().getJSONObject(0);
            this.id = (int) object.get("id");
            return response;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addSearchQueryResult(JSONObject object, Map<Integer, DonationPage> pages) throws JSONException {
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

    private static void populateWithResult(Map<Integer, DonationPage> pages, JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            addSearchQueryResult(array.getJSONObject(i), pages);
        }
    }

    public static List<DonationPage> searchBy(String queryName) throws ServerResponseException {
        List<DonationPage> result = new ArrayList<>();
        Map<Integer, DonationPage> pages = new HashMap<>();

        try {
            JSONObject queryObject = new JSONObject()
                    .put("name", queryName);
            ServerResponse response = WebClient.postJSON("search_donation_pages_by_name.php", queryObject);
            populateWithResult(pages, response.getData());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<Integer, DonationPage> entry : pages.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    public int getId() { return id; }

    public String getContent() {
        return pageContent;
    }

    static {
        pendingPages = new HashMap<>();
    }

}
