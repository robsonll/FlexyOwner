package com.example.flexyowner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.flexyowner.ControllerClasses.BusinessOwnerController;
import com.example.flexyowner.ModelClasses.Business;
import com.google.firebase.firestore.auth.User;

import java.util.List;

public class ChooseBusiness extends AppCompatActivity {

    private Spinner spinnerListBusiness;
    private ChooseBusinessAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_business);

        spinnerListBusiness = findViewById(R.id.spinnerListBusiness);

        BusinessOwnerController businessOwnerController = new BusinessOwnerController();
        List<Business> listBusiness = businessOwnerController.retrieveListBusinesses(ChooseBusiness.this);

        adapter = new ChooseBusinessAdapter(ChooseBusiness.this, android.R.layout.simple_spinner_item, listBusiness);

        spinnerListBusiness.setAdapter(adapter);
        spinnerListBusiness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Business business = adapter.getItem(position);

                businessOwnerController.storeCurrentBusiness(ChooseBusiness.this, business);

                //Toast.makeText(ChooseBusiness.this, "ID: " + business.getId() + "\nName: " + business.getName(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        Button buttonProceed = findViewById(R.id.buttonProceed);

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseBusiness.this, NavigationMainActivity.class));
            }
        });


    }
}