package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/* A class to represent donation pages. WARNING: page contents must be fetched before they are displayed, the server only gives them when they are asked for */
public class DonationPage implements JSONSerializable, Searchable {
    private int id;
    private String pageContent;
    private Basket basket;

    private DonationPage(int id, Basket basket) {
        this.id = id;
        this.basket = basket;
        pageContent = "";
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
            Basket basket = Basket.fromJSONArray(object.get("content"));
            return new DonationPage(id, basket);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /* This function fetches the page content, meant for when a link to a page is actually clicked */
    public void fetchPageContent() {
        try {
            JSONObject object = new JSONObject();
            object.put("id", id);
            ServerResponse response = WebClient.postJSON("request_page_content.php", object);
        } catch (ServerResponseException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject serialize() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("basket", basket.serialize());
        result.put("page_content", pageContent);
        return result;
    }

    public ServerResponse post() {
        try {
            ServerResponse response = WebClient.postJSON("post_donation_page.php", serialize());
        } catch (ServerResponseException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Searchable> search() {
        try {
            return Searchable.fromQuery(
                    new SearchQuery("request_pages"),
                    serialize()
            );
        } catch (ServerResponseException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Searchable deserialize(JSONArray array) {
        return DonationPage.fromJSONArray(array);
    }

    @Override
    public Searchable deserialize(JSONObject object) {
        return fromJSONObject(object);
    }
}
