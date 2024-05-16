package com.example.assignment;

public class SearchQuery {
    private String[] searchLocations;

    public SearchQuery(String... searchLocations) {
        this.searchLocations = searchLocations;
    }

    public String[] getSearchLocations() { return searchLocations; }
}
