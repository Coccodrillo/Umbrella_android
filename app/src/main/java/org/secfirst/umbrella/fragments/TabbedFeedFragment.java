package org.secfirst.umbrella.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.SettingsActivity;
import org.secfirst.umbrella.adapters.FeedAdapter;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaRestClient;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TabbedFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String LOCATION = "location";
    public static final String COUNTRY = "country";
    public static final String FEED_SOURCES = "feed_sources";
    SwipeRefreshLayout mSwipeRefreshLayout;
    FeedAdapter feedAdapter;
    TextView noFeedSettings;
    ScrollView noFeedCard;
    ListView feedListView;
    TextView header;
    TextView refreshIntervalValue;
    TextView feedSourcesValue;
    CardView noFeedItems;
    Global global;
    private AutoCompleteTextView mAutocompleteLocation;
    private ArrayList<Address> mAddressList;
    private Address mAddress;
    private Registry mLocation, mCountry;
    private ArrayList<FeedItem> items = new ArrayList<>();

    public TabbedFeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_feed,
                container, false);
        global = ((BaseActivity) getActivity()).getGlobal();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        feedListView = (ListView) rootView.findViewById(R.id.feed_list);
        noFeedSettings = (TextView) rootView.findViewById(R.id.no_feed_settings);
        noFeedItems = (CardView) rootView.findViewById(R.id.no_feed_items);
        noFeedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        refreshIntervalValue = (TextView) rootView.findViewById(R.id.refresh_interval_value);
        feedSourcesValue = (TextView) rootView.findViewById(R.id.feed_sources_value);
        noFeedCard = (ScrollView) rootView.findViewById(R.id.no_feed_list);
        feedAdapter = new FeedAdapter(getActivity(), items);
        header = new TextView(getActivity());
        header.setTextColor(getResources().getColor(R.color.white));
        header.setGravity(Gravity.CENTER_HORIZONTAL);
        feedListView.addHeaderView(header);
        LinearLayout footer = new LinearLayout(getActivity());
        footer.setOrientation(LinearLayout.HORIZONTAL);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 50);
        footer.setLayoutParams(lp);
        feedListView.addFooterView(footer);
        feedListView.setAdapter(feedAdapter);
        feedListView.setDividerHeight(10);

        mAutocompleteLocation = (AutoCompleteTextView) rootView.findViewById(R.id.settings_autocomplete);

        mAutocompleteLocation.setAdapter(new GeoCodingAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));
        mAutocompleteLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !global.hasPasswordSet(false)) {
                    global.setPassword(getActivity());
                }

            }
        });
        LinearLayout refreshInterval = (LinearLayout) rootView.findViewById(R.id.refresh_interval);
        LinearLayout feedSources = (LinearLayout) rootView.findViewById(R.id.feed_sources);
        feedSources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedSources();
            }
        });
        refreshInterval.setVisibility(!global.isLoggedIn() ? View.GONE : View.VISIBLE);
        refreshInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.hasPasswordSet(false)) {
                    showRefresh();
                } else {
                    global.setPassword(getActivity());
                }
            }
        });

        mAutocompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (global.hasPasswordSet(false)) {
                    UmbrellaUtil.hideSoftKeyboard(getActivity());
                    if (position != 0 && mAddressList != null && mAddressList.size() >= position) {
                        mAddress = mAddressList.get(position - 1);
                        String chosenAddress = mAutocompleteLocation.getText().toString();
                        mAutocompleteLocation.setText(chosenAddress);
                        List<Registry> selLoc = null;
                        try {
                            selLoc = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, LOCATION);
                        } catch (SQLException e) {
                            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                        }
                        if (selLoc != null && selLoc.size() > 0) {
                            mLocation = selLoc.get(0);
                            mLocation.setValue(chosenAddress);
                            try {
                                global.getDaoRegistry().update(mLocation);
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                            }
                        } else {
                            mLocation = new Registry(LOCATION, chosenAddress);
                            try {
                                global.getDaoRegistry().create(mLocation);
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                            }
                        }
                        List<Registry> selISO2 = null;
                        Registry iso2;
                        try {
                            selISO2 = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "iso2");
                        } catch (SQLException e) {
                            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                        }
                        if (selISO2 != null && selISO2.size() > 0) {
                            iso2 = selISO2.get(0);
                            iso2.setValue(mAddress.getCountryCode().toLowerCase());
                            try {
                                global.getDaoRegistry().update(iso2);
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                            }
                        } else {
                            iso2 = new Registry("iso2", mAddress.getCountryCode().toLowerCase());
                            try {
                                global.getDaoRegistry().create(iso2);
                                if (isFeedSet()) getFeeds(getActivity());
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                            }
                        }
                        List<Registry> selCountry = null;
                        try {
                            selCountry = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, COUNTRY);
                        } catch (SQLException e) {
                            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                        }
                        if (selCountry != null && selCountry.size() > 0) {
                            mCountry = selCountry.get(0);
                            mCountry.setValue(mAddress.getCountryName());
                            try {
                                global.getDaoRegistry().update(mCountry);
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                            }
                        } else {
                            mCountry = new Registry(COUNTRY, mAddress.getCountryName());
                            try {
                                global.getDaoRegistry().create(mCountry);
                                if (isFeedSet()) getFeeds(getActivity());
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                            }
                        }
                    } else {
                        mAddress = null;
                    }
                } else {
                    global.setPassword(getActivity());
                }
            }
        });
        mAutocompleteLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        refreshIntervalValue.setText(global.getRefreshLabel());
        feedSourcesValue.setText(global.getSelectedFeedSourcesLabel());
        getFeeds(getActivity());
        refreshView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Registry> selLoc = null;
        try {
            selLoc = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, LOCATION);
        } catch (SQLException e) {
            if (getActivity()!=null) UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
        }
        if (selLoc!=null && selLoc.size() > 0) {
            mAutocompleteLocation.setHint(selLoc.get(0).getValue());
        } else {
            mAutocompleteLocation.setHint(global.getString(R.string.set_location));
        }
    }

    @Override
    public void onRefresh() {
        refreshFeed();
    }

    public void refreshView() {
        ArrayList<FeedItem> items = global.getFeedItems();
        feedAdapter.updateData(items);
        List<Registry> selCountry;
        String headerText = "";
        try {
            selCountry = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, COUNTRY);
            if (selCountry!=null && selCountry.size() > 0) {
                headerText = global.getString(R.string.country_selected)+": " + selCountry.get(0).getValue() + "\n";
            }
        } catch (SQLException e) {
            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
        }
        mSwipeRefreshLayout.setVisibility(isFeedSet() ? View.VISIBLE : View.GONE);
        noFeedItems.setVisibility((isFeedSet() && (items==null || items.size() == 0)) ? View.VISIBLE : View.GONE);
        headerText += global.getString(R.string.lat_updated)+": " + DateFormat.getDateTimeInstance().format(new Date(global.getFeeditemsRefreshed()));
        feedListView.setVisibility(isFeedSet() && items!=null ? View.VISIBLE : View.GONE);
        noFeedCard.setVisibility(isFeedSet() ? View.GONE : View.VISIBLE);
        noFeedCard.setVisibility( (items==null && isFeedSet()) ? View.GONE : View.VISIBLE);
        header.setText(headerText);
    }

    public void refreshFeed() {
        refreshView();
        if (isFeedSet()) {
            getFeeds(getActivity());
        } else {
            Toast.makeText(getActivity(), R.string.set_location_source_in_settings, Toast.LENGTH_SHORT).show();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public boolean getFeeds(final Context context) {
        global.setFeedItems(new ArrayList<FeedItem>());
        Dao<Registry, String> regDao = global.getDaoRegistry();
        List<Registry> selISO2 = null;
        try {
            selISO2 = regDao.queryForEq(Registry.FIELD_NAME, "iso2");
        } catch (SQLException e) {
            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
        }
        if (selISO2!=null && selISO2.size()>0) {
            List<Registry> selections;
            try {
                selections = regDao.queryForEq(Registry.FIELD_NAME, FEED_SOURCES);
                if (selections.size()>0) {
                    String separator = ",";
                    int total = selections.size() * separator.length();
                    for (Registry item : selections) {
                        total += item.getValue().length();
                    }
                    StringBuilder sb = new StringBuilder(total);
                    for (Registry item : selections) {
                        sb.append(separator).append(item.getValue());
                    }
                    String sources = sb.substring(separator.length());
                    String mUrl = "feed?country=" + selISO2.get(0).getValue() + "&sources=" + sources + "&since=0";
                    UmbrellaRestClient.get(mUrl, null, "", context, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess(statusCode, headers, response);
                            Gson gson = new GsonBuilder().create();
                            Type listType = new TypeToken<ArrayList<FeedItem>>() {
                            }.getType();
                            ArrayList<FeedItem> receivedItems = gson.fromJson(response.toString(), listType);
                            if (receivedItems != null && receivedItems.size() > 0) {
                                global.setFeedItems(receivedItems);
                                refreshView();
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            refreshView();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), R.string.no_sources_selected, Toast.LENGTH_SHORT).show();
                }
                return true;
            } catch (SQLException e) {
                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
            }
            return false;
        } else {
            noFeedCard.setVisibility(View.VISIBLE);
            feedListView.setVisibility(View.GONE);
            return false;
        }
    }

    public void showRefresh() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                getActivity());
        builderSingle.setTitle(global.getString(R.string.choose_refresh_inteval));
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice);
        int currentRefresh = global.getRefreshValue();
        int selectedIndex = 0;
        int i = 0;
        final HashMap<String, Integer> refreshValues = UmbrellaUtil.getRefreshValues(global.getApplicationContext());
        for (Object key : refreshValues.keySet()) {
            if (refreshValues.get(key).equals(currentRefresh)) {
                selectedIndex = i;

            }
            arrayAdapter.add((String) key);
            i++;
        }
        builderSingle.setNegativeButton(global.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setSingleChoiceItems(arrayAdapter, selectedIndex,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String chosen = arrayAdapter.getItem(which);
                        for (Object key : refreshValues.keySet()) {
                            Integer value = refreshValues.get(key);
                            if (key.equals(chosen)) {
                                BaseActivity baseAct = ((BaseActivity) getActivity());
                                if (baseAct.mBounded) baseAct.mService.setRefresh(value);
                                global.setRefreshValue(value);
                                refreshIntervalValue.setText(global.getRefreshLabel());
                                refreshFeed();
                                dialog.dismiss();
                            }
                        }
                    }
                });
        builderSingle.show();
    }

    public void showFeedSources() {
        final CharSequence[] items = global.getFeedSourcesArray();
        final ArrayList<Integer> selectedItems = new ArrayList<>();
        boolean[] currentSelections = new boolean[items.length];
        List<Registry> selections;
        try {
            selections = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, FEED_SOURCES);
            for (int i = 0; i < items.length; i++) {
                currentSelections[i] = false;
                for (Registry reg : selections) {
                    if (reg.getValue().equals(String.valueOf(global.getFeedSourceCodeByIndex(i)))) {
                        currentSelections[i] = true;
                        selectedItems.add(i);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(global.getString(R.string.select_feed_sources));
        builder.setMultiChoiceItems(items, currentSelections,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            selectedItems.add(indexSelected);
                        } else {
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                .setPositiveButton(global.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<Registry> selections;
                        try {
                            selections = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, FEED_SOURCES);
                            for (Registry selection : selections) {
                                global.getDaoRegistry().delete(selection);
                            }
                        } catch (SQLException e) {
                            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                        }
                        for (Integer item : selectedItems) {
                            try {
                                global.getDaoRegistry().create(new Registry(FEED_SOURCES, String.valueOf(global.getFeedSourceCodeByIndex(item))));
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                            }
                        }
                        feedSourcesValue.setText(global.getSelectedFeedSourcesLabel());
                        refreshFeed();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(global.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean isFeedSet() {
        return !global.getSelectedFeedSourcesLabel().equals("") && !global.getRefreshLabel().equals("") && !global.getChosenCountry().equals("");
    }

    private class GeoCodingAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GeoCodingAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        private List<Address> autoComplete(String input) {
            List<Address> foundGeocode = null;
            Context context = getActivity();
            try {
                foundGeocode = new Geocoder(context).getFromLocationName(input, 7);
                mAddressList = new ArrayList<>(foundGeocode);
            } catch (IOException e) {
                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
            }

            return foundGeocode;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        List<Address> list = autoComplete(constraint.toString());
                        ArrayList<String> toStrings = new ArrayList<>();
                        for (Address current : list) {
                            if (!current.getAddressLine(0).equals("")) {
                                String toAdd = current.getAddressLine(0);
                                if (current.getAddressLine(0) != null)
                                    toAdd += " " + current.getAddressLine(1);
                                if (current.getAddressLine(2) != null)
                                    toAdd += " " + current.getAddressLine(2);
                                toStrings.add(toAdd);
                            }
                        }
                        resultList = toStrings;
                        resultList.add(0, global.getString(R.string.current_location));

                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
        }
    }

}