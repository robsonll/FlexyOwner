package com.example.flexyowner;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.flexyowner.ControllerClasses.BusinessOwnerController;
import com.example.flexyowner.ModelClasses.Business;
import com.example.flexyowner.ModelClasses.BusinessConfiguration;
import com.example.flexyowner.ModelClasses.PaymentMethod;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ChooseBusinessAdapter extends ArrayAdapter<Business> {

    Context context;
    List<Business> businessData = new ArrayList<Business>();
    private FirebaseFirestore db;

    public ChooseBusinessAdapter(Context context, int textViewResourceId, List<Business> businesses) {
        super(context, textViewResourceId, businesses);

        this.businessData = businesses;
        this.context = context;
    }

    @Override
    public int getCount() {
        return businessData.size();
    }

    @Override
    public Business getItem(int position) {
        return businessData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(businessData.get(position).getName());

        return label;

    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(businessData.get(position).getName());

        return label;
    }



}
