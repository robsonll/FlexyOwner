package com.example.flexyowner.ControllerClasses;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flexyowner.ModelClasses.Business;
import com.example.flexyowner.ModelClasses.BusinessOwner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BusinessOwnerController extends AppCompatActivity {

    public BusinessOwner retrieveUser(Context context){

        final SharedPreferences pref = context.getSharedPreferences("userProfile", 0); // 0 - for private mode

        Gson gson = new Gson();
        String json = pref.getString("User", null);
        BusinessOwner user = gson.fromJson(json, BusinessOwner.class);

        return user;
    }

    public void storeUser(Context context, BusinessOwner user){

        SharedPreferences pref = context.getSharedPreferences("userProfile", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("User", json);
        editor.commit();

    }

    public Business retrieveCurrentBusiness(Context context){

        final SharedPreferences pref = context.getSharedPreferences("userProfile", 0); // 0 - for private mode

        Gson gson = new Gson();
        String json = pref.getString("currentBusiness", null);
        Business currentBusiness = gson.fromJson(json, Business.class);

        return currentBusiness;
    }

    public void storeCurrentBusiness(Context context, Business currentBusiness){

        SharedPreferences pref = context.getSharedPreferences("userProfile", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(currentBusiness);
        editor.putString("currentBusiness", json);
        editor.commit();

    }

    public List<Business> retrieveListBusinesses(Context context){

        final SharedPreferences pref = context.getSharedPreferences("userProfile", 0); // 0 - for private mode

        Gson gson = new Gson();
        String json = pref.getString("listBusinesses", null);

        Type listType = new TypeToken<ArrayList<Business>>(){}.getType();
        List<Business> listBusinesses = gson.fromJson(json, listType);

        return listBusinesses;
    }

    public void storeListBusinesses(Context context, List<Business> listBusinesses){

        SharedPreferences pref = context.getSharedPreferences("userProfile", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(listBusinesses);
        editor.putString("listBusinesses", json);
        editor.commit();

    }

}
