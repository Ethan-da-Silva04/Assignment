package com.example.assignment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

public class PendingContributionReceiver extends Thread {
    private static final int sleepTime = 120 * 60 * 1000;

    private static PendingContributionReceiver instance;

    private Queue<Contribution> queue;

    public PendingContributionReceiver() {
        queue = new LinkedList<>();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public List<Contribution> dataToList() {
        List<Contribution> result = new ArrayList<>();
        result.addAll(queue);
        return result;
    }

    public static PendingContributionReceiver get() {
        if (instance == null) {
            instance = new PendingContributionReceiver();
            instance.start();
        }
        return instance;
    }

    public void run() {
        try {
            ServerResponse response = WebClient.get("pending_contributions.php");
            for (int i = 0; i < response.getData().length(); i++) {
                queue.addAll(Contribution.listFromJSONArray(response.getData()));
            }
            this.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ServerResponseException e) {
            throw new RuntimeException(e);
        }
    }
}
