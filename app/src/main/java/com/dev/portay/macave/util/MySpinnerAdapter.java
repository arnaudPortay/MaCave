package com.dev.portay.macave.util;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MySpinnerAdapter<T> extends ArrayAdapter<T>
{

    public MySpinnerAdapter(final Context pContext, final int pResource)
    {
        super(pContext, pResource);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater lInflater = LayoutInflater.from(getContext());
            convertView = lInflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView lTextView = convertView.findViewById(android.R.id.text1);

        if (getItem(position).getClass().equals(String.class))
        {
            lTextView.setText((String)getItem(position));
        }
        else
        {
            lTextView.setText(String.format("%d", getItem(position)));
        }
        lTextView.setTextColor(Color.BLACK);
        lTextView.setTextSize(22);

        return convertView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        return getDropDownView(position,convertView,parent);
    }
}
