package com.example.assignment;

import java.util.HashMap;
import java.util.Map;

public enum SearchTag {
    Account,
    BasketName,
    BasketItem;

    private static final Map<String, SearchTag> tagMap;

    public static boolean hasAssociatedTag(String string) {
        return tagMap.containsKey(string);
    }

    public static SearchTag getAsscoiatedTag(String string) {
        return tagMap.get(string);
    }

    static {
        tagMap = new HashMap<>();
        tagMap.put("@account", Account);
        tagMap.put("@basket", BasketName);
        tagMap.put("@item", BasketItem);
    }
}
