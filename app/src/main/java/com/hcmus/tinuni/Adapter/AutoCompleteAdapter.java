package com.hcmus.tinuni.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    private List<String> arrayList;

    public AutoCompleteAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
        arrayList = objects;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return objectFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.auto_complete_text_item, parent, false);
        }

        TextView textViewContent = convertView.findViewById(R.id.textViewContent);

        String strContent = getItem(position);
        if (strContent != null) {
            textViewContent.setText(strContent);
        }
        return convertView;
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
