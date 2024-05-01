package com.example.assignment;

import androidx.annotation.NonNull;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class SearchQuery {
    private static final Set<Character> acceptedCharacters;
    private EnumSet<SearchTag> tags;
    private final String queryText;

    public SearchQuery(String raw) {
        tags = EnumSet.allOf(SearchTag.class);
        String sanitized = SearchQuery.sanitize(raw);
        String[] tokens = sanitized.split(" ");
        EnumSet<SearchTag> selectedTags = EnumSet.noneOf(SearchTag.class);
        StringBuilder textBuilder = new StringBuilder();

        for (String token : tokens) {
            if (SearchTag.hasAssociatedTag(token)) {
                selectedTags.add(SearchTag.getAsscoiatedTag(token));
            } else if (!token.contains("@")) {
                textBuilder.append(token);
            }
        }

        queryText = textBuilder.toString();

        if (!selectedTags.isEmpty()) {
            tags = selectedTags;
        }
    }

    public boolean hasTag(SearchTag tag) {
        return tags.contains(tag);
    }

    public String getQueryText() {
        return queryText;
    }

    @NonNull
    private static String sanitize(String raw) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            if (acceptedCharacters.contains(raw.charAt(i))) {
                result.append(raw.charAt(i));
            }
        }
        return result.toString();
    }

    static {
        String accepted = "@qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        acceptedCharacters = new HashSet<>();
        for (int i = 0; i < accepted.length(); i++) {
            acceptedCharacters.add(accepted.charAt(i));
        }
    }
}
