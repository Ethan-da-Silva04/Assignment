package com.example.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class AccountHistoryAdapter extends BaseAdapter {
    public static final int CONTRIBUTION_ITEM = 0;
    public static final int DONATION_PAGE_ITEM = 1;
    private List<Object> historyItems;
    private Set<Integer> acceptedContributionEntryIds;
    private Context context;
    private LayoutInflater inflater;

    public AccountHistoryAdapter(Context context, List<Contribution> contributions, List<DonationPage> donationPages, Set<Integer> accepted) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        historyItems = mergeByDate(contributions, donationPages);
        acceptedContributionEntryIds = accepted;
    }

    private List<Object> mergeByDate(List<Contribution> contributions, List<DonationPage> pages) {
        List<Object> result = new ArrayList<>();
        contributions.sort(Comparator.comparing(Contribution::getCreatedAt).reversed());
        pages.sort(Comparator.comparing(DonationPage::getCreatedAt).reversed());

        if (contributions.isEmpty() && pages.isEmpty()) {
            return result;
        }

        int i = 0;
        int j = 0;

        while (i < contributions.size() && j < pages.size()) {
            if (contributions.get(i).getCreatedAt().compareTo(pages.get(j).getCreatedAt()) > 0) {
                result.add(contributions.get(i++));
            } else {
                result.add(pages.get(j++));
            }
        }

        while (i < contributions.size()) {
            result.add(contributions.get(i++));
        }

        while (j < pages.size()) {
            result.add(pages.get(j++));
        }

        return result;
    }

    @Override
    public int getCount() {
        return historyItems.size();
    }

    @Override
    public Object getItem(int position) {
        return historyItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getContributionView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.contribution_history_item, parent, false);
        TextView number = convertView.findViewById(R.id.txt_number);
        number.setText(String.valueOf(position + 1));

        Contribution contribution = (Contribution) historyItems.get(position);
        TextView name = convertView.findViewById(R.id.txt_name);
        String recipientPageName = DonationPage.getPageFromId(contribution.getRecipientPageId()).getName();
        name.setText("Contribution to " + recipientPageName + " on " + contribution.getCreatedAt());

        StringBuilder itemString = new StringBuilder();

        for (DonationItem entry : contribution.getBasket().getItems()) {
            StringBuilder row = new StringBuilder();
            row.append("\t-\t");
            row.append(acceptedContributionEntryIds.contains(entry.getId()) ? "ACCEPTED: " : "NOT ACCEPTED: ");
            row.append(entry.getQuantity() + " " + entry.getName());
            itemString.append(row);
            itemString.append('\n');
        }

        TextView itemView = convertView.findViewById(R.id.txt_items);
        itemView.setMaxLines(contribution.getBasket().size());
        itemView.setText(itemString);


        return convertView;
    }

    public View getDonationPageView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.donation_page_history_item, parent, false);

        TextView number = convertView.findViewById(R.id.txt_number);
        number.setText(String.valueOf(position + 1));

        DonationPage page = (DonationPage) historyItems.get(position);
        TextView name = convertView.findViewById(R.id.txt_name);
        name.setText("Donation Page: " + page.getName() + " created on " + page.getCreatedAt());

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case AccountHistoryAdapter.CONTRIBUTION_ITEM:
                return getContributionView(position, convertView, parent);
            case AccountHistoryAdapter.DONATION_PAGE_ITEM:
                return getDonationPageView(position, convertView, parent);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (historyItems.get(position) instanceof Contribution) {
            return CONTRIBUTION_ITEM;
        } else if (historyItems.get(position) instanceof  DonationPage) {
            return DONATION_PAGE_ITEM;
        }
        return -1;
    }
}
