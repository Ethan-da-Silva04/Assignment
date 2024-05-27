package com.example.assignment;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String KEY_DONATION_PAGE_NAME = "DonationPageName";
    public static final String KEY_CONTRIBUTION_MAP_ID = "ContributionMapId";
    public static final String KEY_PAGE_MODE = "Mode";

    public static final String KEY_USER_ID = "user_id";

    public static final DateTimeFormatter fromFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter toFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final boolean DEBUG = false;
}
