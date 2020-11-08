package com.example.flexyowner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.flexyowner.ModelClasses.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectProduct extends Fragment {

    private FirebaseFirestore db;
    private TextView textViewTitle;
    private List<Product> productList;
    private SelectProductAdapter selectProductAdapter;
    private ListView listViewProducts;
    Product currentProduct = new Product();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_select_product, container, false);
        //Toolbar toolbar = view.get getParentFragment().getView().findViewById(R.id.toolBar);
        //toolbar.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();

        textViewTitle = view.findViewById(R.id.textViewTitle);
        listViewProducts = view.findViewById(R.id.listViewProducts);
        listViewProducts.setAdapter(selectProductAdapter);

        listViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                currentProduct = (Product) adapterView.getItemAtPosition(position);
            }
        });


        ImageView imageViewAddProduct = view.findViewById(R.id.imageViewAddProduct);

        imageViewAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddProduct.class));
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchProducts();
    }


    private void fetchProducts()
    {

        productList = new ArrayList<Product>();

        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                productList.add(document.toObject(Product.class));
                            }

                            if (productList.isEmpty())
                            {
                                Toast.makeText(getContext(), "No payment methods stored.", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                selectProductAdapter = new SelectProductAdapter(getContext(), productList);
                                listViewProducts.setAdapter(selectProductAdapter);
                            }
                        } else {
                            Log.d("FIREBASE", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
