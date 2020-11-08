package com.example.flexyowner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flexyowner.ControllerClasses.ProductController;
import com.example.flexyowner.ModelClasses.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

public class SelectProductAdapter extends ArrayAdapter<Product> {

    Context context;
    List<Product> productsData = new ArrayList<Product>();
    private FirebaseFirestore db;

    public SelectProductAdapter(Context context, List<Product> products) {
        super(context, R.layout.select_product_adapter);

        this.productsData = products;
        this.context = context;
    }

    @Override
    public int getCount() {
        return productsData.size();
    }

    @Override
    public Product getItem(int position) {
        return productsData.get(position);
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
                    .from(parent.getContext()).inflate(R.layout.select_product_adapter,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.textViewProductName = convertView.findViewById(R.id.productName);
            viewHolder.imageViewEditProduct = convertView.findViewById(R.id.imageViewEditProduct);
            viewHolder.imageViewDeleteProduct = convertView.findViewById(R.id.imageViewDeleteProduct);

            final Product product = productsData.get(position);

            viewHolder.imageViewEditProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductController productController = new ProductController();
                    productController.storeCurrentProduct(context, product);

                    Intent intent = new Intent(context, EditProduct.class);
                    context.startActivity(intent);
                }
            });

            viewHolder.imageViewDeleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteProduct(v, product);
                }
            });

        }

        TextView textViewProductName = convertView.findViewById(R.id.productName);
        ImageView imageViewEditPaymentMethod = convertView.findViewById(R.id.imageViewEditProduct);
        textViewProductName.setText(productsData.get(position).getName());

        return convertView;
    }

    public void deleteProduct(View v, Product product){

        db = FirebaseFirestore.getInstance();

        final KProgressHUD hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Deleting Product")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        

        db.collection("products").document(product.getId())
                .delete().addOnSuccessListener(new OnSuccessListener< Void >() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Product deleted !",
                        Toast.LENGTH_SHORT).show();

                hud.dismiss();

                productsData.remove(product);
                notifyDataSetChanged();

            }
        });


    }

    private static class ViewHolder{
        TextView textViewProductName;
        ImageView imageViewEditProduct;
        ImageView imageViewDeleteProduct;
    }

}
