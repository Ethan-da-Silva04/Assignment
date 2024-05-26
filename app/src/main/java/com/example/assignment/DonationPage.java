package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* A class to represent donation pages. WARNING: page contents must be fetched before they are displayed, the server only gives them when they are asked for */
public class DonationPage implements JSONSerializable {
    private static final Map<String, DonationPage> pendingPages;

    private int id;
    private String name;
    private String pageContent;
    private Basket basket;
    private int donateeId;

    private LocalDateTime createdAt;

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
        basket = new Basket();
        putPage(name, this);
    }

    public DonationPage(String name, String pageContent) {
        this.id = 0;
        this.name = name;
        this.pageContent = pageContent;
        this.donateeId = UserSession.getId();
        putPage(name, this);
    }

    private static void putPage(String name, DonationPage page) {
        pendingPages.putIfAbsent(name, page);
    }

    public static DonationPage getPage(String name) {
        return pendingPages.get(name);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime dateTime) {
        this.createdAt = dateTime;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    /* This function fetches the page content, meant for when a link to a page is actually clicked */
    public User fetchPageContent() throws ServerResponseException {
        if (pageContent != null) {
            return User.getFromId(donateeId);
        }

        try {
            JSONObject object = new JSONObject()
                    .put("id", id);
            ServerResponse response = WebClient.postJSON("request_page_content.php", object);
            JSONObject responseData = response.getData().getJSONObject(0);
            pageContent = (String) responseData.get("content");
            return User.fromJSONObject(responseData.getJSONObject("user"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public int getDonateeId() { return donateeId; }

    public static Set<Map.Entry<String, DonationPage>> getEntries() { return pendingPages.entrySet(); }

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

    public static DonationPage getPageFromId(int pageId) {
        for (DonationPage page : pendingPages.values()) {
            if (page.getId() == pageId) {
                return page;
            }
        }

        return null;
    }

    private static void addSearchQueryResult(JSONObject object, Map<Integer, DonationPage> pages) throws JSONException {
        System.out.println("QFIOQUHWFOIQUHWVOJNQOFIUHKSANJLKVJHWEOGFIUEWHFLKJWHELKVJHEWOVIUHWKJHFLQKWJHFDQWIOUH");
        int itemId = (int) object.get("id");
        int pageId = (int) object.get("page_id");
        int resourceId = (int) object.get("resource_id");
        int donateeId = (int) object.get("donatee_id");
        String pageName = (String) object.get("name");
        int quantityAsked = (int) object.get("quantity_asked");
        int quantityReceived = (int) object.get("quantity_received");

        pages.putIfAbsent(pageId, new DonationPage(pageId, donateeId, pageName));
        DonationPage page = pages.get(pageId);
        if (object.has("created_at")) {
            LocalDateTime toSet = LocalDateTime.parse((CharSequence) object.get("created_at"), Constants.fromFormatter);
            page.setCreatedAt(toSet);
        }

        DonationItem item = new DonationItem(itemId, Resource.getFromId(resourceId), quantityAsked, quantityReceived);
        if (!pages.get(pageId).getBasket().add(item)) {
            throw new RuntimeException("Fuck off");
        }
        System.out.println("FOIQWJFOQWIJFOQWIJFOIQWJOFIJQWGWQ: SIZE " + page.getBasket().size());
    }

    private static void populateWithResult(Map<Integer, DonationPage> pages, JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            addSearchQueryResult(array.getJSONObject(i), pages);
        }
    }

    public static List<DonationPage> listFromPageMap(Map<Integer, DonationPage> pages) {
        List<DonationPage> result = new ArrayList<>();
        result.addAll(pages.values());
        return result;
    }

    public static List<DonationPage> searchBy(String queryName) throws ServerResponseException {
        try {
            JSONObject queryObject = new JSONObject()
                    .put("name", queryName);
            ServerResponse response = WebClient.postJSON("search_donation_pages_by_name.php", queryObject);
            return listFromJSONArray(response.getData());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<DonationPage> searchBy(Basket basket) throws ServerResponseException {
        try {
            System.out.println("DATA SENT: " + basket.serialize());
            ServerResponse response = WebClient.postJSON("search_donation_pages_by_closeness.php", basket.serialize());
            return listFromJSONArray(response.getData());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<DonationPage> listFromJSONArray(JSONArray array) {
        Map<Integer, DonationPage> pages = new HashMap<>();
        try {
            populateWithResult(pages, array);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return listFromPageMap(pages);
    }

    public int getId() { return id; }

    public String getContent() {
        return pageContent;
    }

    static {
        pendingPages = new HashMap<>();
    }

}
