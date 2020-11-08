package com.example.flexyowner.ControllerClasses;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flexyowner.ModelClasses.Business;
import com.example.flexyowner.ModelClasses.BusinessOwner;
import com.example.flexyowner.ModelClasses.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductController extends AppCompatActivity {

    public Product retrieveCurrentProduct(Context context){

        final SharedPreferences pref = context.getSharedPreferences("userProfile", 0); // 0 - for private mode

        Gson gson = new Gson();
        String json = pref.getString("currentProduct", null);
        Product currentProduct = gson.fromJson(json, Product.class);

        return currentProduct;
    }

    public void storeCurrentProduct(Context context, Product currentProduct){

        SharedPreferences pref = context.getSharedPreferences("userProfile", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(currentProduct);
        editor.putString("currentProduct", json);
        editor.commit();

    }

    public List<Product> retrieveListProducts(Context context){

        final SharedPreferences pref = context.getSharedPreferences("userProfile", 0); // 0 - for private mode

        Gson gson = new Gson();
        String json = pref.getString("listProducts", null);

        Type listType = new TypeToken<ArrayList<Product>>(){}.getType();
        List<Product> listProducts = gson.fromJson(json, listType);

        return listProducts;
    }

    public void storeListProducts(Context context, List<Product> listProducts){

        SharedPreferences pref = context.getSharedPreferences("userProfile", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(listProducts);
        editor.putString("listProducts", json);
        editor.commit();

    }

}
