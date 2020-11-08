package com.example.flexyowner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flexyowner.ControllerClasses.BusinessOwnerController;
import com.example.flexyowner.ModelClasses.Business;
import com.example.flexyowner.ModelClasses.BusinessConfiguration;
import com.example.flexyowner.ModelClasses.PaymentMethod;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class SelectPaymentMethodAdapter extends ArrayAdapter<PaymentMethod> {

    Context context;
    List<PaymentMethod> paymentMethodsData = new ArrayList<PaymentMethod>();
    private FirebaseFirestore db;

    public SelectPaymentMethodAdapter(Context context, List<PaymentMethod> paymentMethods) {
        super(context, R.layout.select_payment_method_adapter);

        this.paymentMethodsData = paymentMethods;
        this.context = context;
    }

    @Override
    public int getCount() {
        return paymentMethodsData.size();
    }

    @Override
    public PaymentMethod getItem(int position) {
        return paymentMethodsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = LayoutInflater
                    .from(parent.getContext()).inflate(R.layout.select_payment_method_adapter,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.textViewPMName = convertView.findViewById(R.id.poName);
            viewHolder.checkboxPaymentMethod = convertView.findViewById(R.id.checkBoxPaymentMethod);

            final PaymentMethod paymentMethod = paymentMethodsData.get(position);

        }

        TextView textViewPaymentOptionName = convertView.findViewById(R.id.poName);
        textViewPaymentOptionName.setText(paymentMethodsData.get(position).getName());

        CheckBox checkBoxPaymentMethod = convertView.findViewById(R.id.checkBoxPaymentMethod);

        BusinessOwnerController businessOwnerController = new BusinessOwnerController();
        Business business = businessOwnerController.retrieveCurrentBusiness(getContext());
        BusinessConfiguration businessConfiguration = business.getBusinessConfiguration();

        if(businessConfiguration != null) {
            List<PaymentMethod> acceptedPaymentMethods = businessConfiguration.getAcceptedPaymentMethods();

            for (PaymentMethod acceptedPaymentMethod : acceptedPaymentMethods) {
                if (acceptedPaymentMethod.getId().equals(paymentMethodsData.get(position).getId())) {
                    checkBoxPaymentMethod.setChecked(true);
                }
            }
        }else{
            List<PaymentMethod> acceptedPaymentMethods = new ArrayList<PaymentMethod>();
        }

        return convertView;
    }


    private static class ViewHolder{
        TextView textViewPMName;
        CheckBox checkboxPaymentMethod;
    }

}
