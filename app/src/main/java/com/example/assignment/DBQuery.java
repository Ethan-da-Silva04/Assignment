package com.example.assignment;

import java.util.Locale;

/*
DBQueries are currently sent to the entire database, this obviously is not good if we were working
on a larger scale database. Either way we would have to sort based on edit distance at some point,
which is slow. If we expanded the scale of our app, perhaps adding location data to accounts
will help mitigate this (only look at accounts x distance away from this account).
Searching for what the user typed in exactly might prove fast, but also inconvenient for the user.
 */
public class DBQuery {
    private static final int MAX_SIZE = 256;
    private int[][] dp = new int[MAX_SIZE][MAX_SIZE];

    public DBQuery(SearchQuery query) {
    }

    private int getEditDistance(String fst, String snd) {
        /*
        Algorithm adapted from the recursion presented on:
        https://en.wikipedia.org/wiki/Levenshtein_distance
        Uses dynamic programming, runs in O(m*n)
         */
        int m = fst.length() + 1;
        int n = snd.length() + 1;

        int i = m - 1;
        for (int j = 0; j < snd.length(); j++) {
            dp[i][j] = snd.length() - j;
        }

        int j = n - 1;
        for (i = 0; i < fst.length(); i++) {
            dp[i][j] = fst.length() - i;
        }

        for (i = m - 2; i >= 0; i--) {
            for (j = n - 2; j >= 0; j--) {
                if (i < fst.length() && j < snd.length()) {
                    dp[i][j] = dp[i+1][j+1];
                } else {
                    dp[i][j] = 1 + min(dp[i+1][j], dp[i][j+1], dp[i+1][j+1]);
                }
            }
        }

        return dp[0][0];
    }

    private static int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }
}
