package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Contribution implements JSONSerializable, Searchable {
    private int id;
    private int posterId;
    private int recipientPageId;
    private Basket basket;

    private Contribution() {
        this.id = 0;
    }

    public static Contribution fromSession(int recipientPageId, Basket basket) {
        Contribution contribution = new Contribution();
        contribution.posterId = UserSession.getData().getId();
        contribution.recipientPageId = recipientPageId;
        contribution.basket = basket;
        return contribution;
    }

    public static Contribution fromAcceptAction(int id, int posterId, int recipientPageId, Basket basket) {
        Contribution contribution = new Contribution();
        contribution.id = id;
        contribution.posterId = posterId;
        contribution.recipientPageId = recipientPageId;
        contribution.basket = basket;
        return contribution;
    }

    public static Contribution fromJSONArray(JSONArray array) {
        try {
            return Contribution.fromJSONObject(array.getJSONObject(0));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static Contribution fromJSONObject(JSONObject object) {

    }

    @Override
    public JSONObject serialize() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("basket", basket.serialize());
        result.put("poster_id", posterId);
        result.put("recipient_page_id", recipientPageId);
        return result;
    }

    public ServerResponse post() throws JSONException, ServerResponseException {
        try {
            ServerResponse response = WebClient.postJSON("post_contribution.php", serialize());
            JSONObject object = (JSONObject) response.getData().get(0);
            System.out.println(object);
            return response;
        } catch (ServerResponseException e) {
            throw e;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Searchable> search() {
        try {
            return Searchable.fromQuery(
                    new SearchQuery("request_contributions"),
                    serialize()
            );
            JSONObject object = new JSONObject();
            object.put("id", id);
            return new Search<Contribution>().run(
                    new SearchQuery("request_contributions"),

            )
            return Search<Contribution>.search(Contribution::deserialize);
        } catch (ServerResponseException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static Contribution deserialize(JSONObject object) {
        return Contribution.fromJSONObject(object);
    }
}
