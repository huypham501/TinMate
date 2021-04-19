package com.hcmus.tinuni.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    private ArrayList<String> arrayList;

    public AutoCompleteAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
        arrayList = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return objectFilter;
    }

    private Filter objectFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            ArrayList<String> arrayListSuggestions = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                arrayListSuggestions.addAll(arrayList);
            } else {
                String strFilterPattern = charSequence.toString().toLowerCase().trim();

                for (String str : arrayList) {
                    if (str.toLowerCase().contains(strFilterPattern)) {
                        arrayListSuggestions.add(str);
                    }
                }
            }

            filterResults.values = arrayListSuggestions;
            filterResults.count = arrayListSuggestions.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return resultValue.toString();
        }
    };
}
