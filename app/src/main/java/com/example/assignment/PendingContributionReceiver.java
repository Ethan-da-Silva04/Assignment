package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

public class PendingContributionReceiver extends Thread {
    private static final int sleepTime = 120 * 60 * 1000;
    private Queue<Contribution> queue;

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public List<Contribution> getData() {
        List<Contribution> result = new ArrayList<>();
        for (Contribution contribution : queue) {
            result.add(contribution);
        }
        return result;
    }

    public void run() {
        try {
            this.sleep(sleepTime);
            ServerResponse response = WebClient.get("accept_contribution.php");
            for (int i = 0; i < response.getData().length(); i++) {
                queue.add(Contribution.fromJSONObject(response.getData().getJSONObject(i)));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ServerResponseException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
